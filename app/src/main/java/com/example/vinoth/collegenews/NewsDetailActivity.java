package com.example.vinoth.collegenews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vinoth.collegenews.model.News;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.example.vinoth.collegenews.NewsListActivity.NEWS_CONTAINER_PATH;


public class NewsDetailActivity extends AppCompatActivity {
    public static final String NEWS_ID = "news_id";

    public static final String NEWS_TITLE = "NEWS_TITLE";
    public static final String NEWS_BODY = "NEWS_BODY";
    public static final String NEWS_BANNER_URL = "NEWS_BANNER_URL";
    private static final String TAG = NewsDetailActivity.class.getSimpleName();
    private TextView news_title_tv;
    private TextView news_body_tv;
    private ImageView app_bar_image;
    private TextView news_create_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        news_title_tv = findViewById(R.id.news_title_tv);
        news_create_tv = findViewById(R.id.news_create_tv);
        news_body_tv = findViewById(R.id.news_body_tv);
        app_bar_image = findViewById(R.id.app_bar_image);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (getIntent() != null) {
            String title = getIntent().getStringExtra(NEWS_TITLE);
            String body = getIntent().getStringExtra(NEWS_BODY);
            String banner_Url = getIntent().getStringExtra(NEWS_BANNER_URL);
            String newsId = getIntent().getStringExtra(NEWS_ID);

            if (TextUtils.isEmpty(title)) {
                DatabaseReference myRef = database.getReference(NEWS_CONTAINER_PATH).child(newsId);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        News news = dataSnapshot.getValue(News.class);
                        if (news != null)
                            loadData(news);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            } else {
                News news = new News();
                news.setBody(body);
                news.setTitle(title);
                news.setBannerUrl(banner_Url);
                loadData(news);
            }


        }


    }

    private void loadData(News news) {
        if (news != null) {

            String bannerUrl = news.getBannerUrl();
            String body = news.getBody();
            String title = news.getTitle();
            String createAt = news.getCreateAt();
            setTitle("Detail");
            if (!TextUtils.isEmpty(body)) {
                news_body_tv.setText(body);
            }
            if (!TextUtils.isEmpty(bannerUrl)) {
                Picasso.with(this)
                        .load(bannerUrl)
                        .into(app_bar_image);
            }
            if (!TextUtils.isEmpty(title)) {
                news_title_tv.setText(title);
            }
            if (!TextUtils.isEmpty(createAt)) {
                final SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                try {
                    String updated_at = "Created: " + DateUtils.getRelativeDateTimeString(this, serverFormat.parse(createAt).getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
                    news_create_tv.setText(updated_at);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }


        }
    }


}
