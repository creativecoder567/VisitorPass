package com.example.vinoth.blogreader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vinoth.blogreader.model.News;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BlogPostDetailActivity extends AppCompatActivity {

    public static final String BLOG_KEY = "blog key";
    DatabaseReference reference;
    private ImageView imageView;
    private TextView title;
    private TextView desc;
    private News mNewsDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post_detail);

        imageView = (ImageView) findViewById(R.id.ivblogimag);
        title = (TextView) findViewById(R.id.tvblogtitle);
        desc = (TextView) findViewById(R.id.tvblogdesc);
        String key = getIntent().getStringExtra(BLOG_KEY);
        reference = FirebaseDatabase.getInstance().getReference(MainActivity.NEWS_CONTAINER_PATH);
        reference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mNewsDetail = dataSnapshot.getValue(News.class);
                if (mNewsDetail != null) {
                    String dourl = mNewsDetail.getBannerUrl();
                    title.setText(mNewsDetail.getTitle());
                    desc.setText(mNewsDetail.getBody());
                    mNewsDetail.setId(dataSnapshot.getKey());
                    Picasso.with(BlogPostDetailActivity.this).load(dourl).placeholder(R.drawable.place_holder_big).fit().into(imageView);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            onDelete();
        } else if (id == R.id.action_notification) {
            onSentNotification();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDelete() {
        if (mNewsDetail != null) {
            String id = mNewsDetail.getId();
            reference.child(id).removeValue().addOnSuccessListener(
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getBaseContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(BlogPostDetailActivity.this, "failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
    }

    public void onSentNotification() {

        if (mNewsDetail != null)
            sendPost(mNewsDetail);
        else
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();


    }

    public void sendPost(final News news) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Authorization", "key=AAAAQoJCZ3Y:APA91bHGuQqn-YcdL5Po-rkujf_KLxW4vilzZ2Y3eZzll3TfxYuw8V6-85_k-VskL2CkWOq9lpObBY40IvHQXTcWkdSWstCaZJxt6qjyVAZf6VOBba8KUEHtypIAaJ6GiCbtPbTpayAba4QmViIHkrVyFU4bYtqRuw");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    String title = news.getTitle();
                    String body = news.getBody();
                    if (title.length() > 20)
                        title = title.substring(0, 20);
                    if (body.length() > 30)
                        body = body.substring(0, 30);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("to", "/topics/news");
                    JSONObject notificationJson = new JSONObject();
                    notificationJson.put("body", body);
                    notificationJson.put("title", title);
                    notificationJson.put("sound", "default");
                    notificationJson.put("news_id", news.getId());
                    jsonParam.put("notification", notificationJson);
                    jsonParam.put("data", notificationJson);
                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());
                    os.flush();
                    os.close();
                    int resCode = conn.getResponseCode();
                    Log.i("STATUS:", String.valueOf(resCode));
                    Log.i("MSG:", conn.getResponseMessage());
                    if (resCode == HttpURLConnection.HTTP_OK)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BlogPostDetailActivity.this, "Notification Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    else
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BlogPostDetailActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    conn.disconnect();
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BlogPostDetailActivity.this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
