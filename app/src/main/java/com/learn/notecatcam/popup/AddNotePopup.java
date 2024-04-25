package com.learn.notecatcam.popup;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.learn.notecatcam.R;
import com.learn.notecatcam.constant.StorageName;
import com.learn.notecatcam.constant.StorageVariable;

public class AddNotePopup {

    public void showDialog(Context context){

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.add_note_popup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        Button add_note_save_btn = dialog.findViewById(R.id.add_note_save_btn);
        Button add_note_cancel_btn = dialog.findViewById(R.id.add_note_cancel_btn);
        EditText add_note_txt = dialog.findViewById(R.id.add_note_txt);

        add_note_save_btn.setOnClickListener(l->{
            SharedPreferences preferences = context.getSharedPreferences(StorageName.NOTE_CAT_CAM.name(), Context.MODE_PRIVATE);
            SharedPreferences.Editor edit= preferences.edit();
            edit.putString(StorageVariable.NOTE.name(), add_note_txt.getText().toString());
            edit.apply();
            dialog.dismiss();
        });

        add_note_cancel_btn.setOnClickListener(l->{
            dialog.dismiss();
        });


        dialog.show();

    }
}
