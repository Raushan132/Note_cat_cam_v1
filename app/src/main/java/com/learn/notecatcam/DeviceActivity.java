package com.learn.notecatcam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.learn.notecatcam.constant.Date_Time_Constant;
import com.learn.notecatcam.constant.StorageName;
import com.learn.notecatcam.constant.StorageVariable;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DeviceActivity extends AppCompatActivity {
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    private final int GET_IMG_CODE= 101;
    private Scalar textColor = new Scalar(0, 0, 0); // black color
    private Scalar rectColor = new Scalar(255, 255, 255, 128); // semi-transparent white color (50% opacity)
    private int rectThickness = -1;
    private LocationManager locationManager;
    private Location currLocation;
    private Bitmap image_selected_bitmap;
    private Bitmap shareable_bitmap;
    ImageView imageViewDevice;
    int day=1,month=1,year=2024,hour=10,min=0;
    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startLocationUpdates();
        SharedPreferences preferences = getSharedPreferences(StorageName.NOTE_CAT_CAM.name(), MODE_PRIVATE);
        day= preferences.getInt(Date_Time_Constant.DAY.name(),1);
        month =preferences.getInt(Date_Time_Constant.MONTH.name(), 1);
        year = preferences.getInt(Date_Time_Constant.YEAR.name(), 2024);
        hour = preferences.getInt(Date_Time_Constant.HOUR.name(), 0);
        min = preferences.getInt(Date_Time_Constant.MIN.name(), 0);
        calendar = Calendar.getInstance();


        Button selectImgBtn = findViewById(R.id.select_img_btn);

        selectImgBtn.setOnClickListener(listener ->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
            intent.setType("image/*");
            startActivityForResult(intent,GET_IMG_CODE);
        });

        ImageButton deviceShareBtn = findViewById(R.id.device_share_btn);
        deviceShareBtn.setOnClickListener(l->{

            if(shareable_bitmap !=null){
              String filePath=  MediaStore.Images.Media.insertImage(this.getContentResolver(),shareable_bitmap,"image_selected",null);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(filePath));

                startActivity(Intent.createChooser(shareIntent,"Shared Image"));

            }else{
                Toast.makeText(this,"Please Select Image",Toast.LENGTH_SHORT).show();
            }


        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GET_IMG_CODE && resultCode ==RESULT_OK){

                Uri imageUri = data.getData();
                try {
                    InputStream is= getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    image_selected_bitmap =bitmap;
                    Mat newMat = putTextInImage(bitmap);
                    bitmap = Bitmap.createBitmap(newMat.width(),newMat.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(newMat,bitmap);
                    imageViewDevice = findViewById(R.id.image_view_device);
                    imageViewDevice.setImageBitmap(bitmap);
                    shareable_bitmap = bitmap;


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
//            }

//
        }
    }

    private void updateDateInImage(){

        Bitmap update_image = image_selected_bitmap;
        Utils.matToBitmap(putTextInImage(update_image),update_image);
        imageViewDevice.setImageBitmap(update_image);



    }

    public Mat putTextInImage(Bitmap bitmap){

        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);

        if(mat.size().width>2000)
        Imgproc.resize(mat,mat,new Size(0,0),0.3,0.3, Imgproc.INTER_AREA);
        else if(mat.size().width>1500)
            Imgproc.resize(mat,mat,new Size(0,0),0.6,0.6, Imgproc.INTER_AREA);
        Log.e("size Of mat:",mat.size().toString());
        Log.e("size Of mats:",mat.size().toString());
        SharedPreferences preferences = getSharedPreferences(StorageName.NOTE_CAT_CAM.name(), MODE_PRIVATE);
        String notes= preferences.getString(StorageVariable.NOTE.name(), "");
        calendar.set(year,month,day,hour,min);
        Date load_date = calendar.getTime();
        String date_time_format = preferences.getString(StorageVariable.TIME_FORMAT.name(),"dd/MM/yyyy HH:mm:ss" );
        boolean notes_empty= notes.trim().isEmpty();
        int extraHeight = notes_empty?0:30;
        // Add note rectangle
        int rectWidth = 300;
        int rectHeight = 200 ;
        int rectX = 20;
        int rectY = mat.rows() - 10 - rectHeight; // Bottom-left corner
        Mat overlay = mat.clone();



        if(currLocation!=null){
           Imgproc.rectangle(overlay, new Point(rectX, rectY+(notes_empty?30:0)), new Point(rectX + rectWidth, rectY + rectHeight), rectColor, rectThickness);

            // Blend the overlay with the original frame
            Core.addWeighted(overlay, 0.5, mat, 0.5, 0, mat);
            Imgproc.putText(mat, "Latitude:  "+String.format("%.7f",currLocation.getLatitude()) , new Point(rectX + 10, rectY + rectHeight - 140-extraHeight), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
            Imgproc.putText(mat, "Longitude: "+String.format("%.7f",currLocation.getLongitude()), new Point(rectX + 10, rectY + rectHeight - 110-extraHeight), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
            Imgproc.putText(mat, "Altitude:  "+String.format("%.7f",currLocation.getAltitude()) , new Point(rectX + 10, rectY + rectHeight - 80-extraHeight), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
            Imgproc.putText(mat, "Accuracy:  "+currLocation.getAccuracy() , new Point(rectX + 10, rectY + rectHeight - 50-extraHeight), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);

        }else{
            Imgproc.rectangle(overlay, new Point(rectX, rectY+(notes_empty?30:0)), new Point(rectX + rectWidth, rectY + rectHeight), rectColor, rectThickness);
//            Imgproc.rectangle(overlay, new Point(rectX, rectY+150-extraHeight), new Point(rectX + rectWidth, rectY + rectHeight), rectColor, rectThickness);

            // Blend the overlay with the original frame
            Core.addWeighted(overlay, 0.5, mat, 0.5, 0, mat);
            Imgproc.putText(mat, "Latitude:  "+preferences.getString(StorageVariable.LATITUDE.name(),"25.2445333" ) , new Point(rectX + 10, rectY + rectHeight - 140), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
            Imgproc.putText(mat, "Longitude: "+preferences.getString(StorageVariable.LONGITUDE.name(),"84.664717" ), new Point(rectX + 10, rectY + rectHeight - 110), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
            Imgproc.putText(mat, "Altitude:  "+preferences.getString(StorageVariable.ALTITUDE.name(),"30.1") , new Point(rectX + 10, rectY + rectHeight - 80), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
            Imgproc.putText(mat, "Accuracy:  "+preferences.getString(StorageVariable.ACCURACY.name(),"4.1") , new Point(rectX + 10, rectY + rectHeight - 50), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);


//            Imgproc.rectangle(overlay, new Point(rectX, rectY+150), new Point(rectX + rectWidth, rectY + rectHeight), rectColor, rectThickness);
//
//            // Blend the overlay with the original frame
//            Core.addWeighted(overlay, 0.5, mat, 0.5, 0, mat);
            Log.e("location:","Not Found location");
        }
        String noteText = "Time: "+ new SimpleDateFormat(date_time_format, Locale.getDefault()).format(load_date);
        Imgproc.putText(mat, noteText, new Point(rectX + 10, rectY + rectHeight - 20-extraHeight), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);
        if(!notes_empty)
            Imgproc.putText(mat,"Note: "+notes,new Point(rectX + 10, rectY + rectHeight+10-extraHeight), Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, textColor, 2);

        return mat;
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