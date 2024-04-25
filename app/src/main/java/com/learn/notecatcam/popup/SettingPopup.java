package com.learn.notecatcam.popup;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.learn.notecatcam.R;
import com.learn.notecatcam.constant.StorageName;
import com.learn.notecatcam.constant.StorageVariable;

public class SettingPopup {
    RadioGroup radioGroup;
    String val;
    int selectedId=-1;
    final String RADIO_BUTTON_VAL_INDEX="RADIO_BUTTON_VAL_INDEX" ;

    public  void showDialog(Context context){

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.setting_popup_view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Button saveBtn = dialog.findViewById(R.id.saveBtn);
        SharedPreferences preferences = context.getSharedPreferences(StorageName.NOTE_CAT_CAM.name(), Context.MODE_PRIVATE);
        selectedId = preferences.getInt(RADIO_BUTTON_VAL_INDEX,-1);
        if (selectedId != -1) {
            RadioButton radioButton = dialog.findViewById(selectedId);
            radioButton.setChecked(true);
        }

        saveBtn.setEnabled(false);
        saveBtn.setOnClickListener(v-> {

            preferences.edit().putString(StorageVariable.TIME_FORMAT.name(), val).putInt(RADIO_BUTTON_VAL_INDEX, selectedId).apply();

            dialog.dismiss();
        });


        dialog.show();

        radioGroup = dialog.findViewById(R.id.date_time_format);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            selectedId = radioGroup.getCheckedRadioButtonId();

            if(selectedId==-1){
                Toast.makeText(context,"Nothing selected or Change", Toast.LENGTH_SHORT).show();
            }else{
             RadioButton radioButton =  dialog.findViewById(selectedId);
             val= String.valueOf(radioButton.getText());
             saveBtn.setEnabled(true);
             Log.e("selected radio btn",val);
            }
        });
    }
}
