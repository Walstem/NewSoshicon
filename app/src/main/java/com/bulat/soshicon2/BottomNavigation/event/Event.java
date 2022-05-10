package com.bulat.soshicon2.BottomNavigation.event;

import static com.bulat.soshicon2.constants.constants.*;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bulat.soshicon2.BottomNavigation.account.Account;
import com.bulat.soshicon2.R;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Event extends Fragment {
    public static final String GET_COUNT_DISTRIBUTION_PHP = "getCountDistribution.php";
    public static final String GET_DISTRIBUTION_SOSHICON_PHP = "Get_distribution_soshicon.php";
    private ArrayList<String> Title = new ArrayList<String>();
    private ArrayList<String> Content = new ArrayList<String>();
    private ArrayList<String> Avatar = new ArrayList<String>();
    private ArrayList<String> Time = new ArrayList<String>();
    private ArrayAdapter eventBlock;
    private SwipeRefreshLayout swipeRefreshLayout;
    public int start = 10;
    public int end = 10;
    private int countRowsEvent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.VISIBLE);

        View view = inflater.inflate(R.layout.event_tape, container, false);
        SharedPreferences sp = getContext().getSharedPreferences(DATABASE, 0);
        ListView listView = view.findViewById(R.id.listView);
        swipeRefreshLayout = view.findViewById(R.id.SwipeRefreshLayout);
        ImageView addEvent = view.findViewById(R.id.add);
        CircleImageView avatar = view.findViewById(R.id.avatar);
        BottomSheetDialogFragment BottomSheet = new Fragment_Add();

        File file = new File(sp.getString(SMALL_AVATAR, ""));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            avatar.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //вызываем редактор создания событий
        addEvent.setOnClickListener(v -> BottomSheet.show(getFragmentManager().beginTransaction(), "BottomShitDialog"));
        //переключаем фрагмент событий на фрагмент профиль
        avatar.setOnClickListener(v -> {
            Account account = new Account();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.nav_host_fragment_activity_main, account);
            transaction.addToBackStack(null);
            transaction.commit();
            BottomNavigationView navigationView = getActivity().findViewById(R.id.bottom_navigation);
            navigationView.setSelectedItemId(R.id.nav_account);
        });
        //прогружаем данные при запуске фрагмента
        try {
            HandlingEventOutput(view, listView, start, end, false);
        } catch (JSONException | ExecutionException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }

        //прогружаем данные при ручной перезагрузке
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    start = 10;
                    end = 10;
                    HandlingEventOutput(view, listView, start, end, false);
                } catch (JSONException | ExecutionException | InterruptedException | ParseException e) {
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //отслеживаем свайп  пользователя
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
                        HandlingEventOutput(view, listView, start, end, true);
                    }
                } catch (JSONException | ExecutionException | InterruptedException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }


    public void HandlingEventOutput(View view, ListView listView, int start, int end, boolean scroll) throws JSONException, ExecutionException, InterruptedException, ParseException {
        //если происходит загрузка при переходе на страницу
        if (!scroll) {
            //опусташаем списки данных
            Title = new ArrayList<>();
            Content = new ArrayList<>();
            Avatar = new ArrayList<String>();
            Time = new ArrayList<String>();

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
        receivingEvent Query = new receivingEvent(GET_DISTRIBUTION_SOSHICON_PHP, start-10, end);
        Query.execute();

        JSONArray Event_json = new JSONArray(Query.get());
        System.out.println(Event_json);
        //уменьшаем количество записей оставшихся в таблице
        countRowsEvent -= 10;

        //добавляем данные в массивы
        for (int i = 0; i < Event_json.length(); i++) {
            JSONObject jo = new JSONObject((String) Event_json.get(i));
            Content.add((String) jo.get("content"));
            Title.add((String) jo.get("nickname"));
            Avatar.add(jo.get("img").toString());

            String eventTime = (String) jo.get("time");
            Time.add(new EventTime().handle(eventTime));
        }

        //если происходит загрузка при переходе на страницу прогружаем listview Заново
        if (!scroll) {
            eventBlock = new EventAdapter(requireContext(), Title, Content, Avatar, Time);
            listView.setAdapter(eventBlock);
        }
        //если функция была вызванна свайпом, то обновляем Listview
        else {
            eventBlock.notifyDataSetChanged();
        }
    }
}
