package com.learn.notecatcam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.learn.notecatcam.constant.StorageName;
import com.learn.notecatcam.constant.StorageVariable;
import com.learn.notecatcam.popup.AddNotePopup;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteCam extends CameraActivity {


    private Mat rgbaFrame;
    private Scalar textColor = new Scalar(0, 0, 0); // black color
    private Scalar rectColor = new Scalar(255, 255, 255, 128); // semi-transparent white color (50% opacity)
    private int rectThickness = -1;
    private LocationManager locationManager;
    private Location currLocation;
    CameraBridgeViewBase cameraBridgeViewBase;
    String notes="";
    int camera_Front_Or_Back = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_cam);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        startLocationUpdates();
        SharedPreferences preferences = getSharedPreferences(StorageName.NOTE_CAT_CAM.name(), MODE_PRIVATE);
        cameraBridgeViewBase = findViewById(R.id.cameraView);

        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {

            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                rgbaFrame= inputFrame.rgba();

                if(camera_Front_Or_Back==1){
                    Core.flip(rgbaFrame,rgbaFrame,1);
                }


                notes= preferences.getString(StorageVariable.NOTE.name(), "");
                String date_time_format = preferences.getString(StorageVariable.TIME_FORMAT.name(),"dd/MM/yyyy HH:mm:ss" );
                boolean notes_empty= notes.trim().isEmpty();
                int extraHeight = notes_empty?0:30;
                // Add note rectangle
                int rectWidth = 300;
                int rectHeight = 200 ;
                int rectX = 20;
                int rectY = rgbaFrame.rows() - 10 - rectHeight; // Bottom-left corner
                Mat overlay = rgbaFrame.clone();



                if(currLocation!=null){
                    Imgproc.rectangle(overlay, new Point(rectX, rectY+(notes_empty?30:0)), new Point(rectX + rectWidth, rectY + rectHeight), rectColor, rectThickness);

                    // Blend the overlay with the original frame
                    Core.addWeighted(overlay, 0.5, rgbaFrame, 0.5, 0, rgbaFrame);
                    Imgproc.putText(rgbaFrame, "Latitude:  "+String.format("%.7f",currLocation.getLatitude()) , new Point(rectX + 10, rectY + rectHeight - 140-extraHeight), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
                    Imgproc.putText(rgbaFrame, "Longitude: "+String.format("%.7f",currLocation.getLongitude()), new Point(rectX + 10, rectY + rectHeight - 110-extraHeight), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
                    Imgproc.putText(rgbaFrame, "Altitude:  "+String.format("%.7f",currLocation.getAltitude()) , new Point(rectX + 10, rectY + rectHeight - 80-extraHeight), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
                    Imgproc.putText(rgbaFrame, "Accuracy:  "+currLocation.getAccuracy() , new Point(rectX + 10, rectY + rectHeight - 50-extraHeight), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);

                }else{
                    Imgproc.rectangle(overlay, new Point(rectX, rectY+150-extraHeight), new Point(rectX + rectWidth, rectY + rectHeight), rectColor, rectThickness);

                    // Blend the overlay with the original frame
                    Core.addWeighted(overlay, 0.5, rgbaFrame, 0.5, 0, rgbaFrame);
                    Log.e("location:","Not Found location");
                }
                String noteText = "Time: "+ new SimpleDateFormat(date_time_format, Locale.getDefault()).format(new Date());
                Imgproc.putText(rgbaFrame, noteText, new Point(rectX + 10, rectY + rectHeight - 20-extraHeight), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
                if(!notes_empty)
                    Imgproc.putText(rgbaFrame,"Note: "+notes,new Point(rectX + 10, rectY + rectHeight+10-extraHeight), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);

                return rgbaFrame;
            }
        });
        ImageButton captureBtn = findViewById(R.id.capBtn);
        captureBtn.setOnClickListener(view -> {

            Bitmap bitmap = Bitmap.createBitmap(rgbaFrame.cols(), rgbaFrame.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rgbaFrame, bitmap);

            if (rgbaFrame!=null) {
                saveImageToGallery(this, bitmap);


            } else {
                Log.e("MainActivity", "Failed to save image");
            }

            if(currLocation!=null){
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(StorageVariable.LONGITUDE.name(), String.format("%.7f",currLocation.getLongitude()));
                editor.putString(StorageVariable.LATITUDE.name(), String.format("%.7f",currLocation.getLatitude()));
                editor.putString(StorageVariable.ALTITUDE.name(), String.format("%.7f",currLocation.getAltitude()));
                editor.putString(StorageVariable.ACCURACY.name(), String.valueOf(currLocation.getAccuracy()));
                editor.apply();
            }

        });

        ImageButton addNoteBtn = findViewById(R.id.add_note_btn);
        addNoteBtn.setOnClickListener(l->{
              new AddNotePopup().showDialog(this);


        });

        ImageButton flipCam = findViewById(R.id.flip_cam_btn);

        flipCam.setOnClickListener(l->{
             swapCamera();
        });
        lastCaptureImage();

        ImageView imageView = findViewById(R.id.recent_img_capture);
        imageView.setOnClickListener(l->{
            Log.e("here I am","yes working");
            startActivity(new Intent(NoteCam.this, CaptureImageViewer.class));
        });


        if(OpenCVLoader.initDebug()){
            cameraBridgeViewBase.enableView();
        }


    }

    private void swapCamera(){
        camera_Front_Or_Back^=1;
        cameraBridgeViewBase.disableView();
        cameraBridgeViewBase.setCameraIndex(camera_Front_Or_Back);
        cameraBridgeViewBase.enableView();
    }

    private void lastCaptureImage() {
        // Path to your folder containing images
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Get list of image files in the folder
        File[] imageFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".png"));

        // Check if there are any image files
        if (imageFiles != null && imageFiles.length > 0) {
            // Get the first image file
            File firstImage = imageFiles[imageFiles.length-1];

            // Load the first image into ImageView
            ImageView imageView = findViewById(R.id.recent_img_capture);
            Glide.with(this).load(firstImage).centerCrop().into(imageView);
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);
    }


    private void saveImageToGallery(Context context, Bitmap bitmap) {
        // Get the directory for saving images
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(directory, "OpenCV_Image_" + new Date().getTime() + ".jpg");

        try {
            // Create the file output stream
            FileOutputStream fos = new FileOutputStream(imageFile);

            // Compress the bitmap and write it to the file
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // Close the file output stream
            fos.close();

            // Trigger the media scanner to index the saved image
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(imageFile));
            context.sendBroadcast(mediaScanIntent);

            Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show();
            lastCaptureImage();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private void  startLocationUpdates() {

        try {

            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, location -> {
                        currLocation= location;
                    }
                    ,null);


        } catch (SecurityException e) {
            Log.e("Location Update", "Permission denied", e);
        }

    }
}