package com.bulat.soshicon2.BottomNavigation.event;

import static com.bulat.soshicon2.constants.constants.*;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.SQLUtils.SQLUtils;
import com.bulat.soshicon2.Toasts.Toasts;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.bulat.soshicon2.checks.NetCheck;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class AddEvent extends BottomSheetDialogFragment implements LocationListener {
    int selected_id = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomshit_add_event, container, false);
    }

    LocationManager locationManager;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onViewCreated(@NonNull View MainView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(MainView, savedInstanceState);


        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) MainView.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        CoordinatorLayout layout = getDialog().findViewById(R.id.bottom_sheet_layout);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);

        ImageView cancelEvent = MainView.findViewById(R.id.cancel);
        ImageView add = MainView.findViewById(R.id.add);
        EditText editText = (EditText) MainView.findViewById(R.id.ed_add);

        SharedPreferences sp = getActivity().getSharedPreferences(DATABASE, Context.MODE_PRIVATE);
        TextView name = MainView.findViewById(R.id.username);
        name.setText(sp.getString(U_NICKNAME, ""));

        add.setOnClickListener(view -> {
            if (NetCheck.StatusConnection(getContext())) {
                LayoutInflater lnInflater = getLayoutInflater();
                View ToastId = view.findViewById(R.id.toast_layout_root);
                Toasts InternetToast  = new Toasts(getContext(), lnInflater, ToastId);
                InternetToast.ViewInterntEror(view);
            }
            else{
                String content = editText.getText().toString();

                if(!content.equals("")){
                    String pattern = "yyyy-MM-dd-HH-mm";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                    SharedPreferences sp1 = getContext().getSharedPreferences(DATABASE, 0);
                    String user_id  = sp1.getString(ID, "");
                    String nickname = sp1.getString(U_NICKNAME, "");
                    String Message = editText.getText().toString();
                    String time = simpleDateFormat.format(new Date());

                    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

                    Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                    String latitude = Double.toString(location.getLatitude());
                    String longitude =  Double.toString(location.getLongitude());

                    String urlArgs = new SQLUtils(user_id, Message, nickname, time, latitude, longitude).input_event();

                    SendQuery sendQuery = new SendQuery("input_event.php");
                    sendQuery.execute(urlArgs);
                    try {
                        String answer = sendQuery.get();
                        if (answer.equals("true")) {
                            //здесть будет выводиться сообщение об успешном создании события
                            closeBottomSheet(bottomSheetBehavior);
                        }
                        else {
                            LayoutInflater lnInflater = getLayoutInflater();
                            View ToastId = view.findViewById(R.id.toast_layout_root);
                            Toasts InternetToast  = new Toasts(getContext(), lnInflater, ToastId);
                            InternetToast.ViewErorAddEvent(view);
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        LayoutInflater lnInflater = getLayoutInflater();
                        View ToastId = view.findViewById(R.id.toast_layout_root);
                        Toasts InternetToast  = new Toasts(getContext(), lnInflater, ToastId);
                        InternetToast.ViewErorAddEvent(view);
                    }
                }
            }
        });
        cancelEvent.setOnClickListener(v -> closeBottomSheet(bottomSheetBehavior));

    }
    private void closeBottomSheet(BottomSheetBehavior bottomSheetBehavior) {
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        System.out.println(location.getAltitude());
    }
}