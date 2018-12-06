package com.polarbearr.cinemasearch;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class GreenToast {
    public static void setCustomToast(Context context, int resource, String text){
        TextView toastView = new TextView(context);

        int bytes;
        if(text != null){
            toastView.setText("'" + text + "'" + context.getText(resource));
            bytes = toastView.getText().toString().getBytes().length;
            toastView.setWidth(bytes * 20);
        } else{
            toastView.setText(resource);
            bytes = toastView.getText().toString().getBytes().length;
            toastView.setWidth(bytes * 17);
        }

        toastView.setGravity(Gravity.CENTER);
        toastView.setHeight(120);
        toastView.setBackgroundColor(context.getColor(R.color.colorPrimary));
        toastView.setTextColor(Color.WHITE);
        toastView.setTextSize(15);

        final Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.show();
    }
}
