package com.bulat.soshicon2.BottomNavigation.event;

import static com.bulat.soshicon2.constants.constants.DATABASE;
import static com.bulat.soshicon2.constants.constants.ID;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import java.util.concurrent.ExecutionException;

public class Event extends Fragment {

    private static final String GET_COUNT_DISTRIBUTION_PHP = "getCountDistribution.php";
    private static final String GET_DISTRIBUTION_SOSHICON_PHP = "Get_distribution_soshicon.php";

    View view;
    ListView listView;

    private int countRowsEvent;
    public int start = 10;
    public int end = 100;
    double logitude;
    double latitude;
    boolean ScrollStateChanged = false;

    private ArrayAdapter eventBlock;

    SharedPreferences sp;

    private ArrayList<String> CreatorId = new ArrayList<>();
    private ArrayList<String> EventId = new ArrayList<>();
    private ArrayList<String> Title = new ArrayList<>();
    private ArrayList<String> Content = new ArrayList<>();
    private ArrayList<String> Avatar = new ArrayList<>();
    private ArrayList<String> Time = new ArrayList<>();
    private ArrayList<String> Distance = new ArrayList<>();
    private ArrayList<Boolean> IsLiked = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
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

               show(listView, start, end, false);

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
                            end = 100;
                            show(listView, start, end, false);

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
                    ScrollStateChanged = true;
                }

                //отслеживаем свайп  пользователя
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (NetCheck.StatusConnection(getContext())) {
                        LayoutInflater lnInflater = getLayoutInflater();
                        View ToastId = view.findViewById(R.id.toast_layout_root);
                        Toasts InternetToast = new Toasts(getContext(), lnInflater, ToastId);
                        InternetToast.ViewInterntEror(view);
                    } else if (ScrollStateChanged) {
                        try {
                            if (firstVisibleItem > (start + 90 )- 8) {
                                if (countRowsEvent < 1) {
                                    return;
                                } else if (countRowsEvent / 100 < 1) {
                                    start += 100;
                                    end = countRowsEvent;
                                } else {
                                    end = 100;
                                    start += 100;
                                }

                                show(listView, start, end, true);

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

    public int show(ListView listView, int start, int end, boolean scroll) throws JSONException, ExecutionException, InterruptedException, ParseException {
        //если происходит загрузка при переходе на страницу
        if (!scroll) {
            //опусташаем списки данных
            CreatorId = new ArrayList<String>();
            EventId = new ArrayList<String>();
            Title = new ArrayList<>();
            Content = new ArrayList<>();
            Avatar = new ArrayList<String>();
            Time = new ArrayList<String>();
            Distance = new ArrayList<String>();
            IsLiked = new ArrayList<Boolean>();

            //вычисляем количество записей в таблице с событиями
            SendQuery sendQuery = new SendQuery(GET_COUNT_DISTRIBUTION_PHP);
            sendQuery.execute("?example=");
            try {
                countRowsEvent = Integer.parseInt(sendQuery.get());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }
        //получаем данные события
        String[] KeyArgs = {"start", "end", "latitude", "longitude", "user"};
        sp = getContext().getSharedPreferences(DATABASE, 0);
        String UserId = sp.getString(ID, "");
        String[] Args = {Integer.toString(start - 10), Integer.toString(end), Double.toString(latitude), Double.toString(logitude), UserId};


        receivingEvent Query = new receivingEvent(GET_DISTRIBUTION_SOSHICON_PHP, KeyArgs, Args);
        Query.execute();

        JSONArray Event_json = new JSONArray(Query.get());
        //уменьшаем количество записей оставшихся в таблице
        countRowsEvent -= 10;
        //????????? ?????? ? ???????
        for (int i = 0; i < Event_json.length(); i++) {
            JSONObject jo = new JSONObject((String) Event_json.get(i));
            System.out.println(jo);
            CreatorId.add(jo.get("user_id").toString());
            EventId.add(jo.get("id").toString());
            Content.add(jo.get("content").toString());
            Title.add(jo.get("nickname").toString());
            Avatar.add("http://j911147y.beget.tech/"+jo.get("img"));
            IsLiked.add(jo.get("liked").toString().equals("true"));

            String eventTime = (String) jo.get("time");
            Time.add(new EventTime().handle(eventTime));
            if (jo.get("distance").equals("0")){
                Distance.add("менее 1 км");
            }
            else{
                Distance.add(jo.get("distance").toString() + " км");
            }


            //если происходит загрузка при переходе на страницу прогружаем listview Заново

        }
        if (!scroll)
        {
            eventBlock = new EventAdapter(getContext(), Title, Content, Avatar, Time, Distance, EventId, IsLiked, CreatorId, getActivity());
            listView.setAdapter(eventBlock);
        }
        //если функция была вызванна свайпом, то обновляем Listview
        else {
            eventBlock.notifyDataSetChanged();
        }
        return countRowsEvent;
    }
    public void ShowInternerEror(){
        LayoutInflater lnInflater = getLayoutInflater();
        View ToastId = view.findViewById(R.id.toast_layout_root);
        Toasts InternetToast = new Toasts(getContext(), lnInflater, ToastId);
        InternetToast.ViewInterntEror(view);
    }
}
