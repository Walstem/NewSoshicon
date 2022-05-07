package com.bulat.soshicon2.BottomNavigation;

import static com.bulat.soshicon2.constants.constants.DATABASE;
import static com.bulat.soshicon2.constants.constants.ID;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.bulat.soshicon2.event.Fragment_Add;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Event extends Fragment {
    public static final String GET_COUNT_DISTRIBUTION_PHP = "getCountDistribution.php";
    public static final String GET_DISTRIBUTION_SOSHICON_PHP = "Get_distribution_soshicon.php";
    String distributions_str;
    private ArrayList<String> Title = new ArrayList<String>();
    private ArrayList<String> Discription = new ArrayList<String>();
    private ArrayList<String> Avatars = new ArrayList<String>();
    private ArrayAdapter myAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int start = 0;
    private int end = 10;
    private int countRowsDisitibution;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.VISIBLE);

        View view = inflater.inflate(R.layout.event_tape, container, false);
        ListView listView =  view.findViewById(R.id.listView);
        swipeRefreshLayout = view.findViewById(R.id.SwipeRefreshLayout);
        ImageView addEvent = view.findViewById(R.id.add);
        CircleImageView avatar = view.findViewById(R.id.avatar);
        BottomSheetDialogFragment BottomSheet = new Fragment_Add();

        //вызываем редактор создания событий
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheet.show(getFragmentManager().beginTransaction(), "BottomShitDialog");
            }
        });
        //переключаем фрагмент событий на фрагмент профиль
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Account account = new Account();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.nav_host_fragment_activity_main, account);
                transaction.addToBackStack(null);
                transaction.commit();
                BottomNavigationView navigationView = getActivity().findViewById(R.id.bottom_navigation);
                navigationView.setSelectedItemId(R.id.nav_account);
            }
        });
        try {
            //прогружаем данные при запуске фрагмента
            GetDistribution(view, listView, start, end, false);

            //прогружаем данные при ручной перезагрузке
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    try {
                        start = 0;end = 10;
                        GetDistribution(view, listView, start, end, false);
                    } catch (JSONException | ExecutionException | InterruptedException e) {
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
                        if (visibleItemCount > start || countRowsDisitibution / 10 < 1){
                            if (countRowsDisitibution < 1) {
                                System.out.println("Записи закончились!");
                            }
                            else if(countRowsDisitibution / 10 < 1){
                                System.out.println("countRowsDisitibution1 " + countRowsDisitibution);
                                start +=10;
                                end = countRowsDisitibution;
                                GetDistribution(view, listView, start, end, true);
                            }
                            else{
                                end = 10;
                                start +=10;
                                System.out.println("countRowsDisitibution2 " + countRowsDisitibution);
                                GetDistribution(view, listView, start, end, true);
                            }
                        }
                    } catch (JSONException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
    static class MyAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> Title;
        ArrayList<String> Discription;
        ArrayList<String> Avatars;

        public MyAdapter(@NonNull Context context, ArrayList<String> Title, ArrayList<String> Discription, ArrayList<String> Avatars) {
            super(context, R.layout.row_card_event, R.id.NameMessage, Title);
            this.context = context;
            this.Title = Title;
            this.Discription = Discription;
            this.Avatars = Avatars;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_card_event, parent, false);
            TextView NameMessage = row.findViewById(R.id.NameMessage);
            TextView Content = row.findViewById(R.id.ContentMessage);
            ImageView avatar = row.findViewById(R.id.avatar);
            if (Avatars.get(position) != "null"){
                byte [] encodeByte = Base64.decode(Avatars.get(position),Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

                avatar.setImageBitmap(bitmap);
            }

            NameMessage.setText(Title.get(position));
            Content.setText(Discription.get(position));

            return row;
        }
    }
    public void GetDistribution(View view, ListView listView, int start, int end, boolean scroll) throws JSONException, ExecutionException, InterruptedException {
        //если происходит загрузка при переходе на страницу
        if (!scroll) {
            //опусташаем списки данных
            Title = new ArrayList<>();
            Discription = new ArrayList<>();
            Avatars = new ArrayList<String>();

            //вычисляем количество записей в таблице с событиями
            SendQuery sendQuery = new SendQuery(GET_COUNT_DISTRIBUTION_PHP);
            sendQuery.execute("?example=");
            try {
                distributions_str = sendQuery.get();
                countRowsDisitibution = Integer.parseInt(distributions_str);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }
        //получаем данные события
        GetDistribution Distributions = new GetDistribution(GET_DISTRIBUTION_SOSHICON_PHP, start, end);
        Distributions.execute();
        String distributions_str = Distributions.get();
        JSONArray array = new JSONArray(distributions_str);
        //уменьшаем количество записей оставшихся в таблице
        countRowsDisitibution -= 10;

        //добавляем данные в массивы
        for (int i = 0; i < array.length(); i++) {
            JSONObject jo = new JSONObject((String) array.get(i));
            Discription.add((String) jo.get("content"));
            Title.add((String) jo.get("nickname"));
            Avatars.add((String) jo.get("img").toString());
        }

        //если происходит загрузка при переходе на страницу прогружаем listview Заново
        if (!scroll) {
            myAdapter = new MyAdapter(requireContext(), Title, Discription, Avatars);
            listView.setAdapter(myAdapter);
        }
        //если функция была вызванна свайпом, то обновляем Listview
        else {
            myAdapter.notifyDataSetChanged();
        }
    }
    class GetDistribution extends AsyncTask<String, String, String>{
        String filename;
        String start, end;

        GetDistribution( String filename, int start, int end){
            this.filename = filename;
            this.start = Integer.toString(start);
            this.end = Integer.toString(end);
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://j911147y.beget.tech/" + filename);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("start", start));
            nameValuePairs.add(new BasicNameValuePair("end", end));
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //Execute and get the response.
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert response != null;
                HttpEntity entity = response.getEntity();

                int n = 0;
                char[] buffer = new char[1024 * 4];
                InputStreamReader reader = new InputStreamReader(entity.getContent(), "UTF8");
                StringWriter writer = new StringWriter();
                while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);

                httpclient.getConnectionManager().shutdown();
                return writer.toString();

            } catch (IOException e) {
                e.printStackTrace();

                httpclient.getConnectionManager().shutdown();
                return null;
            }




        }
    }
}