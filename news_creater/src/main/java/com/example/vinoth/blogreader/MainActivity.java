package com.example.vinoth.blogreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.vinoth.blogreader.model.News;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NewsAdapter.OnItemClickListener {

    public static final String NEWS_CONTAINER_PATH = "NewsDetail";
    private RecyclerView mBlogRecycler;
    private DatabaseReference databaseReference;
    private int i = 0;
    private List<News> newsList = new ArrayList<>();
    private String TAG = "MainActivity";
    private DatabaseReference newsContainermyRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication(), BlogpostCreateActivity.class));
            }
        });
        mBlogRecycler = (RecyclerView) findViewById(R.id.recyclerview);
        mBlogRecycler.setHasFixedSize(true);
        mBlogRecycler.setLayoutManager(new LinearLayoutManager(this));
        mBlogRecycler.setItemAnimator(new DefaultItemAnimator());
        final NewsAdapter newsAdapter = new NewsAdapter(this, newsList, this);
        mBlogRecycler.setAdapter(newsAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        newsContainermyRef = database.getReference(NEWS_CONTAINER_PATH);
        // newsContainermyRef.push().setValue(new News());
        newsContainermyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot sp) {
                newsList.clear();
                for (DataSnapshot snapshot : sp.getChildren()) {
                    String key = snapshot.getKey();
                    News news = snapshot.getValue(News.class);
                    if (news != null) {
                        news.setId(key);
                    }
                    newsList.add(news);

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
    protected void onStart() {
        super.onStart();


    }

    @Override
    public void onItemClick(int position) {
        String id = newsList.get(position).getId();
        Intent intent = new Intent(this, BlogPostDetailActivity.class);
        intent.putExtra(BlogPostDetailActivity.BLOG_KEY, id);
        startActivity(intent);
    }

    @Override
    public void onDelete(int position) {
        String id = newsList.get(position).getId();
        newsContainermyRef.child(id).removeValue();

    }

    @Override
    public void onSentNotification(int position) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
