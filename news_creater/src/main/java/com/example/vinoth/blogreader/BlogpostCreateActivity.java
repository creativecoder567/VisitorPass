package com.example.vinoth.blogreader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vinoth.blogreader.model.News;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BlogpostCreateActivity extends AppCompatActivity {

    private static final int GALLERY = 100;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1231;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("images");
    DatabaseReference newsContainermyRef;
    private ImageView pickimagebtn;
    private Uri Imageuri;
    private ProgressDialog mprogressDialog;
    private EditText etTitle;
    private EditText etDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogpost);
        newsContainermyRef = FirebaseDatabase.getInstance().getReference(MainActivity.NEWS_CONTAINER_PATH);
        pickimagebtn = (ImageView) findViewById(R.id.ibtnadd);
        etTitle = (EditText) findViewById(R.id.etBlogTitel);
        etDesc = (EditText) findViewById(R.id.etdesc);

        mprogressDialog = new ProgressDialog(this);

        Toast.makeText(this, "give permission manually todo", Toast.LENGTH_SHORT).show();

    }

    public void pickupimage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == GALLERY) && (resultCode == RESULT_OK)) {
            File file = new File(this.getCacheDir(), "cropped.jpg");
            Uri destination = Uri.fromFile(file);
            Uri source = data.getData();
            Crop.of(source, destination).withAspect(2, 1).start(this);
        }
        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }


    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            Imageuri = Crop.getOutput(result);
            // Imageuri= Uri.fromFile(new File(Crop.getOutput(result).getPath()));
            pickimagebtn.setImageURI(Imageuri);


        } else if (resultCode == Activity.RESULT_CANCELED) {
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void uploaddatas(View view) {
        mprogressDialog.setMessage("uploading.....");
        mprogressDialog.show();
        storageReference.child(etTitle.getText().toString()).putFile(Imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadURI = taskSnapshot.getDownloadUrl();
                String downloadUrl = String.valueOf(downloadURI);
                DatabaseReference daRef = newsContainermyRef.push();
                News blogPost = new News(etTitle.getText().toString(), etDesc.getText().toString(), downloadUrl);
                final SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                blogPost.setCreateAt(serverFormat.format(new Date()));

                daRef.setValue(blogPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(BlogpostCreateActivity.this, "added", Toast.LENGTH_SHORT).show();
                        finish();
                        mprogressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mprogressDialog.dismiss();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("failed", "filll");
                Toast.makeText(getApplicationContext(), "Image failed", Toast.LENGTH_LONG).show();
            }
        });
    }

}
