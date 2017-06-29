package com.example.classcomplain;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

public class imageViewActivity extends AppCompatActivity {

    ImageView imageview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        imageview = (ImageView) findViewById(R.id.imageview);

        Bundle bun = getIntent().getExtras();
        byte[] decodedString = Base64.decode(bun.getString("img"), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Bitmap ThumbImage = Bitmap.createScaledBitmap(decodedByte, 64, 64, false);
        imageview.setVisibility(View.VISIBLE);
        imageview.setImageBitmap(ThumbImage);

        PhotoViewAttacher photoAttacher;
        photoAttacher= new PhotoViewAttacher(imageview);
        photoAttacher.update();
    }
}
