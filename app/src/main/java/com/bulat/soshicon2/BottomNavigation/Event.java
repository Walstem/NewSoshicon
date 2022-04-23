package com.bulat.soshicon2.BottomNavigation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.bulat.soshicon2.event.Fragment_Add;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Event extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ArrayList<String> Title = new ArrayList<String>();
    private ArrayList<String> Discription = new ArrayList<String>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private int value = 0;

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
        BottomSheetDialogFragment BottomShit = new Fragment_Add();

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomShit.show(getFragmentManager().beginTransaction(), "BottomShitDialog");
            }
        });
        try {
            GetDistribution(view, listView, 0);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    try {
                        GetDistribution(view, listView, 0);
                    } catch (JSONException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);
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
    public void GetDistribution(View view, ListView listView, int value) throws JSONException, ExecutionException, InterruptedException {
        SendQuery sendQuery = new SendQuery("Get_distribution_soshicon.php");
        sendQuery.execute("?start=" + value);
        String distributions_str = sendQuery.get();

        JSONArray array = new JSONArray(distributions_str);

        ArrayList<String> Title = new ArrayList<String>();
        ArrayList<String> Discription = new ArrayList<String>();
        for (int i=0; i < array.length(); i++){
            JSONObject jo = new JSONObject((String) array.get(i));
            Discription.add((String) jo.get("content"));
            Title.add((String) jo.get("nickname"));
        }
        MyAdapter myAdapter = new MyAdapter(requireContext(), Title, Discription);
        listView.setAdapter(myAdapter);
    }
}