package com.bulat.soshicon2.BottomNavigation.event;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bulat.soshicon2.BottomNavigation.event.EventAdapter;
import com.bulat.soshicon2.R;
import com.bulat.soshicon2.Toasts.Toasts;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.bulat.soshicon2.checks.NetCheck;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Event extends Fragment implements LocationListener {
    View view;
    ListView listView;
    public static final String GET_COUNT_DISTRIBUTION_PHP = "getCountDistribution.php";
    public static final String GET_DISTRIBUTION_SOSHICON_PHP = "Get_distribution_soshicon.php";
    final int PERMISSION_ID = 44;

    private ArrayList<String> Title = new ArrayList<String>();
    private ArrayList<String> Content = new ArrayList<String>();
    private ArrayList<String> Avatar = new ArrayList<String>();
    private ArrayList<String> Time = new ArrayList<String>();
    private ArrayList<String> Distance = new ArrayList<String>();
    private ArrayList<String> EventId = new ArrayList<String>();
    private ArrayAdapter eventBlock;
    public int start = 10;
    public int end = 10;
    private int countRowsEvent;
    double logitude;
    double latitude;
    public Criteria criteria;
    public String bestProvider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.VISIBLE);
        view = inflater.inflate(R.layout.event_tape, container, false);

        if (NetCheck.StatusConnection(getContext())) {
            LayoutInflater lnInflater = getLayoutInflater();
            View ToastId = view.findViewById(R.id.toast_layout_root);
            Toasts InternetToast = new Toasts(getContext(), lnInflater, ToastId);
            InternetToast.ViewInterntEror(view);
        } else {
            //переписать
            listView = view.findViewById(R.id.listView);
            SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.SwipeRefreshLayout);
            ImageView addEvent = view.findViewById(R.id.add);
            BottomSheetDialogFragment BottomSheet = new AddEvent();

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);

            Location location = getLastKnownLocation();
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            if (location != null) {
                latitude = location.getLatitude();
                logitude = location.getLongitude();
            } else {
                //This is what you need:
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);

            }
            //вызываем редактор создани€ событий
            addEvent.setOnClickListener(v -> BottomSheet.show(getFragmentManager().beginTransaction(), "BottomShitDialog"));

            //прогружаем данные при запуске фрагмента
            try {
                HandlingEventOutput(listView, start, end, false);
            } catch (JSONException | ExecutionException | InterruptedException | ParseException e) {
                e.printStackTrace();
            }
            //прогружаем данные при ручной перезагрузке
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (NetCheck.StatusConnection(getContext())) {
                        LayoutInflater lnInflater = getLayoutInflater();
                        View ToastId = view.findViewById(R.id.toast_layout_root);
                        Toasts InternetToast = new Toasts(getContext(), lnInflater, ToastId);
                        InternetToast.ViewInterntEror(view);
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        try {
                            start = 10;
                            end = 10;
                            HandlingEventOutput(listView, start, end, false);
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

                //отслеживаем свайп  пользовател€
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
                                HandlingEventOutput(listView, start, end, true);
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

    LocationManager mLocationManager;

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    public void HandlingEventOutput( ListView listView, int start, int end, boolean scroll) throws JSONException, ExecutionException, InterruptedException, ParseException {
        //если происходит загрузка при переходе на страницу
        if (!scroll) {
            //опусташаем списки данных
            Title = new ArrayList<>();
            Content = new ArrayList<>();
            Avatar = new ArrayList<String>();
            Time = new ArrayList<String>();
            Distance = new ArrayList<String>();
            EventId = new ArrayList<String>();

            //вычисл€ем количество записей в таблице с событи€ми
            SendQuery sendQuery = new SendQuery(GET_COUNT_DISTRIBUTION_PHP);
            sendQuery.execute("?example=");
            try {
                countRowsEvent = Integer.parseInt(sendQuery.get());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }
        //получаем данные событи€
        String[] KeyArgs = {"start", "end", "latitude", "longitude"};
        String[] Args = { Integer.toString(start-10),  Integer.toString(end), Double.toString(latitude), Double.toString(logitude)};


        receivingEvent Query = new receivingEvent(GET_DISTRIBUTION_SOSHICON_PHP, KeyArgs, Args);
        Query.execute();

        JSONArray Event_json = new JSONArray(Query.get());
        System.out.println(Event_json);
        //уменьшаем количество записей оставшихс€ в таблице
        countRowsEvent -= 10;
        CalculateDistance calculateDistance = new CalculateDistance(logitude, latitude);
        //????????? ?????? ? ???????
        for (int i = 0; i < Event_json.length(); i++) {
            JSONObject jo = new JSONObject((String) Event_json.get(i));
            EventId.add(jo.get("id").toString());
            Content.add(jo.get("content").toString());
            Title.add( jo.get("nickname").toString());
            Avatar.add(jo.get("img").toString());
            System.out.println(jo.get("latitude").toString());
            System.out.println(jo.get("longitude").toString());
            String eventTime = (String) jo.get("time");
            String kilometers;
            Time.add(new EventTime().handle(eventTime));

            if (jo.get("latitude").toString().equals("") || jo.get("longitude").toString().equals("")){
                kilometers = null;
            }
            else{
                double latitude = Double.parseDouble(jo.get("latitude").toString());
                double longitude = Double.parseDouble(jo.get("longitude").toString());
                kilometers = calculateDistance.caclulate(longitude, latitude);
            }
            Distance.add(kilometers);
        }


        //если происходит загрузка при переходе на страницу прогружаем listview «аново
        if (!scroll) {
            eventBlock = new EventAdapter(requireContext(), Title, Content, Avatar, Time, Distance, EventId);
            listView.setAdapter(eventBlock);
        }
        //если функци€ была вызванна свайпом, то обновл€ем Listview
        else {
            eventBlock.notifyDataSetChanged();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        System.out.println(location.getLongitude());
    }
}
