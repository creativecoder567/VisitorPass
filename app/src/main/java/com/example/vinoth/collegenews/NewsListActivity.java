package com.example.vinoth.collegenews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.vinoth.collegenews.model.News;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class NewsListActivity extends AppCompatActivity implements NewsAdapter.OnItemClickListener {

    public static final String NEWS_CONTAINER_PATH = "NewsDetail";
    private static final String TAG = "NewsListActivity";
    private RecyclerView news_rv;
    private ArrayList<News> newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseMessaging.getInstance().subscribeToTopic("global");
        news_rv = (RecyclerView) findViewById(R.id.rv);
        news_rv.setLayoutManager(new LinearLayoutManager(this));
        newsList = new ArrayList<>();
        final NewsAdapter newsAdapter = new NewsAdapter(this, newsList, this);
        news_rv.setAdapter(newsAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(NEWS_CONTAINER_PATH);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot sp) {
                newsList.clear();
                for (DataSnapshot snapshot : sp.getChildren()) {
                    String key = snapshot.getKey();
                    Log.i(TAG, "onDataChange:key " + snapshot.toString());
                    News obj = snapshot.getValue(News.class);
                    if (obj != null) {
                        obj.setId(key);
                    }
                    newsList.add(obj);

                }
                newsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        String id = newsList.get(position).getId();
        Log.i(TAG, "onItemClick: " + id);

        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.NEWS_ID, id);
        startActivity(intent);
    }
}
