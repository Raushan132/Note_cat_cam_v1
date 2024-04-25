package com.learn.notecatcam.popup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.learn.notecatcam.DeviceActivity;
import com.learn.notecatcam.FakeNoteCam;
import com.learn.notecatcam.R;
import com.learn.notecatcam.constant.Date_Time_Constant;
import com.learn.notecatcam.constant.StorageName;
import com.learn.notecatcam.constant.StorageVariable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Device_popup {

    String date_Val="dd-MM-yyyy";
    String time_val= "HH:MM";

    public void showDialog(Context context){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.device_popup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        SharedPreferences preferences = context.getSharedPreferences(StorageName.NOTE_CAT_CAM.name(), Context.MODE_PRIVATE);

        date_Val = preferences.getString("DATE","dd-MM-yyyy");
        time_val = preferences.getString("TIME","HH:MM");
        String notes = preferences.getString(StorageVariable.NOTE.name(), "Add Notes");

        /* Select Notes in Edit Text change the notes */
        EditText editNotes = dialog.findViewById(R.id.change_note_txt);
        editNotes.setText(notes);

        Button deviceSetNoteBtn = dialog.findViewById(R.id.device_set_note_btn);
        deviceSetNoteBtn.setOnClickListener(l->{
            preferences.edit().putString(StorageVariable.NOTE.name(), editNotes.getText().toString()).apply();
            Toast.makeText(context,"Note Updated",Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                editNotes.setFocusable(View.NOT_FOCUSABLE);
            }
        });


        /* Select date  in Text View */
        TextView changeDateBtn = dialog.findViewById(R.id.change_date_txt);
        changeDateBtn.setText(date_Val);

        changeDateBtn.setOnClickListener(l->{
            Dialog date_dialog = new Dialog(context);
            date_dialog.setContentView(R.layout.date_picker_popup);
            date_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            DatePicker datePicker = date_dialog.findViewById(R.id.datePicker);
            Button set_date_btn = date_dialog.findViewById(R.id.set_date_btn);
            set_date_btn.setOnClickListener(date_View->{
                int day= datePicker.getDayOfMonth();
                int month= datePicker.getMonth();
                int year = datePicker.getYear();
                preferences.edit().putInt(Date_Time_Constant.DAY.name(),day)
               .putInt(Date_Time_Constant.MONTH.name(),month)
                .putInt(Date_Time_Constant.YEAR.name(), year)
                        .apply();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month,day);
                Date date = calendar.getTime();
                String updatedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date);

                preferences.edit().putString("DATE",updatedDate).apply();


                Log.e("Date:-",date.toString());
                changeDateBtn.setText(updatedDate);
                date_Val = updatedDate;

                date_dialog.dismiss();
            });

            /*cancel button inside date picker functionality*/
            Button dateCancelBtn = date_dialog.findViewById(R.id.cancel_date_btn);
            dateCancelBtn.setOnClickListener(cancel->date_dialog.dismiss());

            date_dialog.show();

        });

       /*Select time in text view*/

        TextView changeTimeBtn = dialog.findViewById(R.id.change_time_txt);
        changeTimeBtn.setText(time_val);

        changeTimeBtn.setOnClickListener(time_view->{
            Dialog timeDialog = new Dialog(context);
            timeDialog.setContentView(R.layout.time_picker_popup);
            timeDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            Button set_time_btn = timeDialog.findViewById(R.id.set_time_btn);
            set_time_btn.setOnClickListener(l->{
                TimePicker time= timeDialog.findViewById(R.id.time_picker);
                int hour=time.getHour();
                int min=time.getMinute();
                preferences.edit().putInt(Date_Time_Constant.HOUR.name(), hour).putInt(Date_Time_Constant.MIN.name(), min).apply();
                String updateTime = (hour<10?"0"+hour:hour)+":"+(min<10?"0"+min:min);
                changeTimeBtn.setText(updateTime);
                preferences.edit().putString("TIME",updateTime).apply();
                time_val = updateTime;

                Log.e("time:-", hour +":"+ min);

                timeDialog.dismiss();
            });

            Button cancelTimeBtn = timeDialog.findViewById(R.id.cancel_time_btn);
            cancelTimeBtn.setOnClickListener(time-> timeDialog.dismiss());


            timeDialog.show();
        });


        /*select camera to open*/
        CardView cameraCard = dialog.findViewById(R.id.camera_card);
        cameraCard.setOnClickListener(cardView->{
            if(!date_Val.equals("dd-MM-yyyy") && !time_val.equals("HH:MM")){

                context.startActivities(new Intent[]{new Intent(context, FakeNoteCam.class)});

            }else{
                Toast.makeText(context,"Set Date and Time",Toast.LENGTH_SHORT).show();
            }
        });
       /*select device to open device activity*/
        CardView deviceCard = dialog.findViewById(R.id.device_card);
        deviceCard.setOnClickListener(cardView->{
            if(!date_Val.equals("dd-MM-yyyy") && !time_val.equals("HH:MM")){

                context.startActivities(new Intent[]{new Intent(context, DeviceActivity.class)});

            }else{
                Toast.makeText(context,"Set Date and Time",Toast.LENGTH_SHORT).show();
            }
        });




        dialog.show();

    }

}
