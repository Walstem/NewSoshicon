package com.bulat.soshicon2.BottomNavigation.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bulat.soshicon2.R;
import com.bulat.soshicon2.Toasts.Toasts;

import com.bulat.soshicon2.checks.NetCheck;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.json.JSONException;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;

public class Event extends Fragment {
    View view;
    ListView listView;

    private int countRowsEvent;
    public int start = 10;
    public int end = 10;
    double logitude;
    double latitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setSelectedItemId(R.id.event_id);
        navBar.setVisibility(View.VISIBLE);
        view = inflater.inflate(R.layout.event_tape, container, false);

        if (NetCheck.StatusConnection(getContext())) {
            ShowInternerEror();
        } else {
            //переписать
            listView = view.findViewById(R.id.listView);
            SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.SwipeRefreshLayout);
            ImageView addEvent = view.findViewById(R.id.add);

            //определяем локацию
            EventLocation location = new EventLocation(getContext(), getActivity());
            latitude = location.getLatitude();
            logitude = location.getLogitude();

            //прогружаем данные при запуске фрагмента
            try {
                HandlingEventOutput handlingEventOutput = new HandlingEventOutput(getContext());
                handlingEventOutput.HandlingEventOutput(listView, start, end, false, latitude, logitude);
            } catch (JSONException | ExecutionException | InterruptedException | ParseException e) {
                e.printStackTrace();
            }
            //вызываем редактор создания событий
            BottomSheetDialogFragment BottomSheet = new AddEvent();
            addEvent.setOnClickListener(v -> BottomSheet.show(getFragmentManager().beginTransaction(), "BottomShitDialog"));

            //прогружаем данные при ручной перезагрузке
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (NetCheck.StatusConnection(getContext())) {
                        ShowInternerEror();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    else {
                        try {
                            start = 10;
                            end = 10;
                            HandlingEventOutput handlingEventOutput = new HandlingEventOutput(getContext());
                            countRowsEvent = handlingEventOutput.HandlingEventOutput(listView, start, end, false, latitude, logitude);
                        } catch (JSONException | ExecutionException | InterruptedException | ParseException e) {
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });

            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                //отслеживаем свайп  пользователя
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (NetCheck.StatusConnection(getContext())) {
                        LayoutInflater lnInflater = getLayoutInflater();
                        View ToastId = view.findViewById(R.id.toast_layout_root);
                        Toasts InternetToast = new Toasts(getContext(), lnInflater, ToastId);
                        InternetToast.ViewInterntEror(view);
                    } else {
                        try {
                            if (firstVisibleItem > start - 8) {
                                if (countRowsEvent < 1) {
                                    return;
                                } else if (countRowsEvent / 10 < 1) {
                                    start += 10;
                                    end = countRowsEvent;
                                } else {
                                    end = 10;
                                    start += 10;
                                }
                                HandlingEventOutput handlingEventOutput = new HandlingEventOutput(getContext());
                                countRowsEvent = handlingEventOutput.HandlingEventOutput(listView, start, end, false, latitude, logitude);;
                            }
                        } catch (JSONException | ExecutionException | InterruptedException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        return view;
    }
    public void ShowInternerEror(){
        LayoutInflater lnInflater = getLayoutInflater();
        View ToastId = view.findViewById(R.id.toast_layout_root);
        Toasts InternetToast = new Toasts(getContext(), lnInflater, ToastId);
        InternetToast.ViewInterntEror(view);
    }
}
