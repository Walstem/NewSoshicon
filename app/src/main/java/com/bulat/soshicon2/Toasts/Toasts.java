package com.bulat.soshicon2.Toasts;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bulat.soshicon2.R;

public class Toasts {
    private Context context;
    private LayoutInflater infalter;
    private View ToastId;
    
    public Toasts(Context context, LayoutInflater infalter, View ToastId){
        this.context = context;
        this.infalter = infalter;
        this.ToastId = ToastId;
    }
    //Сообщение об отсутствии интернета
    public void ViewInterntEror(View view) {
        View layout = infalter.inflate(R.layout.toast_internet_message,(ViewGroup) ToastId);
        android.widget.Toast toast = new android.widget.Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(android.widget.Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
    public void ViewErorAddEvent(View view) {
        View layout = infalter.inflate(R.layout.toast_eror_add_event,(ViewGroup) ToastId);
        android.widget.Toast toast = new android.widget.Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(android.widget.Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
    
}
