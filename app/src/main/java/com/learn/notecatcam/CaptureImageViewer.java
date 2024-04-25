package com.learn.notecatcam;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class CaptureImageViewer extends AppCompatActivity {

    private int currentImageIndex = 0; // Index of the currently displayed image
    private ImageView imageView;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    File directory;
    File[] imageFiles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image_viewer);
        imageView = findViewById(R.id.capture_image_view);

        //path of directory
        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Get list of image files in the folder
        imageFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".png"));
        currentImageIndex=imageFiles.length>0?imageFiles.length-1:0;
        CapturedImage();
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // Create GestureDetector
        gestureDetector = new GestureDetector(this, new SwipeListener());



        // Set onTouchListener to detect pinch and swipe gestures
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isMultiple= event.getPointerCount()>1;
                if(isMultiple)
                    scaleGestureDetector.onTouchEvent(event);
                else
                    gestureDetector.onTouchEvent(event);

                return true;
            }
        });

        ImageButton shareBtn = findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(l->{


           Bitmap bitmap = BitmapFactory.decodeFile( imageFiles[currentImageIndex].getPath());
           String stringPath = MediaStore.Images.Media.insertImage(this.getContentResolver(),bitmap,imageFiles[currentImageIndex].getName(),null);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(stringPath));

            // Start the activity with the share intent
            startActivity(Intent.createChooser(shareIntent, "Share Image"));



        });

    }

    private void CapturedImage() {

        // Check if there are any image files
        if (imageFiles != null && imageFiles.length > 0) {
            // Get the first image file
            File firstImage = imageFiles[currentImageIndex];

            // Load the first image into ImageView

            Glide.with(this).load(firstImage).into(imageView);
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private float scaleFactor = 1.0f;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 10.0f)); // Limit zooming between 10% and 1000%

            // Apply scale to the ImageView
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
            return true;
        }
    }

    // GestureDetector.OnGestureListener implementation to handle swipe gestures
    private class SwipeListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX < 0) {
                    // Swipe left
                    if (currentImageIndex > 0) {
                        currentImageIndex--;
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFiles[currentImageIndex].getAbsolutePath());
                        imageView.setImageBitmap(bitmap);
                        imageView.setScaleX(1.0f);
                        imageView.setScaleY(1.0f);

                    }
                } else {
                    // Swipe right
                    if (currentImageIndex < imageFiles.length - 1) {
                        currentImageIndex++;
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFiles[currentImageIndex].getAbsolutePath());
                        imageView.setImageBitmap(bitmap);
                        imageView.setScaleX(1.0f);
                        imageView.setScaleY(1.0f);
                    }
                }
                return true;
            }
            return false;
        }
    }

}