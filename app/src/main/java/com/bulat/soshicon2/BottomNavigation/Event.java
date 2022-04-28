package com.bulat.soshicon2.BottomNavigation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.Registration.RegistrationFinish;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.bulat.soshicon2.event.Fragment_Add;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Event extends Fragment {
    String distributions_str;
    private ArrayList<String> Title = new ArrayList<String>();
    private ArrayList<String> Discription = new ArrayList<String>();
    private ArrayAdapter myAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int lastFirstVisibleItem = 0;
    private int value = 0;
    private int countRowsDisitibution;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.VISIBLE);

        View view = inflater.inflate(R.layout.event_tape, container, false);
        ListView listView =  view.findViewById(R.id.listView);
        swipeRefreshLayout = view.findViewById(R.id.SwipeRefreshLayout);
        ImageView addEvent = view.findViewById(R.id.add);
        CircleImageView avatar = view.findViewById(R.id.avatar);
        BottomSheetDialogFragment BottomSheet = new Fragment_Add();


        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheet.show(getFragmentManager().beginTransaction(), "BottomShitDialog");
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Account account = new Account();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.nav_host_fragment_activity_main, account);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        try {
            GetDistribution(view, listView, 0, false);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    try {
                        GetDistribution(view, listView, 0, false);
                    } catch (JSONException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    try {
                        if (countRowsDisitibution == 0) {
                            System.out.println("������ �����������!");
                        }
                        else if(countRowsDisitibution / 10 < 1){
                            value = countRowsDisitibution;
                            countRowsDisitibution = 0;
                            GetDistribution(view, listView, value, true);
                        }
                        else{
                            value+=10;
                            GetDistribution(view, listView, value, true);
                            countRowsDisitibution =- 10;
                        }
                    } catch (JSONException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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

        public MyAdapter(@NonNull Context context, ArrayList<String> Title, ArrayList<String> Discription) {
            super(context, R.layout.row_card_event, R.id.NameMessage, Title);
            this.context = context;
            this.Title = Title;
            this.Discription = Discription;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_card_event, parent, false);
            TextView NameMessage = row.findViewById(R.id.NameMessage);
            TextView Content = row.findViewById(R.id.ContentMessage);

            NameMessage.setText(Title.get(position));
            Content.setText(Discription.get(position));


            return row;
        }
    }
    public void GetDistribution(View view, ListView listView, int value, boolean scroll) throws JSONException, ExecutionException, InterruptedException {
        if (!scroll){
            Title = new ArrayList<>();
            Discription = new ArrayList<>();

            value = 0;
            SendQuery sendQuery = new SendQuery("getCountDistribution.php");
            sendQuery.execute("?name=");
            try {
                distributions_str = sendQuery.get();
                countRowsDisitibution = Integer.parseInt(distributions_str) - 10;
                System.out.println("countRowsDisitibution " + countRowsDisitibution);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }
        SendQuery sendQuery = new SendQuery("Get_distribution_soshicon.php");
        sendQuery.execute("?start=" + value);
        String distributions_str = sendQuery.get();
        if (!distributions_str.equals("[]")){
            JSONArray array = new JSONArray(distributions_str);

            for (int i=0; i < array.length(); i++){
                JSONObject jo = new JSONObject((String) array.get(i));
                Discription.add((String) jo.get("content"));
                Title.add((String) jo.get("nickname"));
            }
            if (!scroll){
                myAdapter = new MyAdapter(requireContext(), Title, Discription);
                listView.setAdapter(myAdapter);
            }
            else{
                myAdapter.notifyDataSetChanged();
            }
        }
    }
}