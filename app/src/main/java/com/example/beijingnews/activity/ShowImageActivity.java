package com.example.beijingnews.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beijingnews.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ShowImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        String urlImage = getIntent().getStringExtra("imgUrl");
        PhotoView photoView = findViewById(R.id.photoview);

        final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);

        Picasso.get()
                .load(urlImage)
                .into(photoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        attacher.update();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

    }
}
