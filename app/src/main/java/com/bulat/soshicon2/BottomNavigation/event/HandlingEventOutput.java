package com.bulat.soshicon2.BottomNavigation.event;

import static com.bulat.soshicon2.constants.constants.*;
import static com.bulat.soshicon2.constants.constants.DATABASE;
import static com.bulat.soshicon2.constants.constants.ID;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bulat.soshicon2.asynctasks.SendQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HandlingEventOutput {
    private static final String GET_COUNT_DISTRIBUTION_PHP = "getCountDistribution.php";
    private static final String GET_DISTRIBUTION_SOSHICON_PHP = "Get_distribution_soshicon.php";

    private ArrayList<String> Title = new ArrayList<>();
    private ArrayList<String> Content = new ArrayList<>();
    private ArrayList<String> Avatar = new ArrayList<>();
    private ArrayList<String> Time = new ArrayList<>();
    private ArrayList<String> Distance = new ArrayList<>();
    private ArrayList<String> EventId = new ArrayList<>();
    private ArrayList<Boolean> IsLiked = new ArrayList<>();

    private ArrayAdapter eventBlock;

    private int countRowsEvent;

    SharedPreferences sp;

    Context context;

    public HandlingEventOutput(Context context){
        this.context = context;
    }

    public int HandlingEventOutput(ListView listView, int start, int end, boolean scroll, double latitude, double logitude) throws JSONException, ExecutionException, InterruptedException, ParseException {
        //���� ���������� �������� ��� �������� �� ��������
        if (!scroll) {
            //���������� ������ ������
            Title = new ArrayList<>();
            Content = new ArrayList<>();
            Avatar = new ArrayList<String>();
            Time = new ArrayList<String>();
            Distance = new ArrayList<String>();
            EventId = new ArrayList<String>();
            IsLiked = new ArrayList<Boolean>();

            //��������� ���������� ������� � ������� � ���������
            SendQuery sendQuery = new SendQuery(GET_COUNT_DISTRIBUTION_PHP);
            sendQuery.execute("?example=");
            try {
                countRowsEvent = Integer.parseInt(sendQuery.get());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }
        //�������� ������ �������
        String[] KeyArgs = {"start", "end", "latitude", "longitude", "user"};
        sp = context.getSharedPreferences(DATABASE, 0);
        String UserId = sp.getString(ID, "");
        String[] Args = {Integer.toString(start - 10), Integer.toString(end), Double.toString(latitude), Double.toString(logitude), UserId};


        receivingEvent Query = new receivingEvent(GET_DISTRIBUTION_SOSHICON_PHP, KeyArgs, Args);
        Query.execute();

        JSONArray Event_json = new JSONArray(Query.get());
        //��������� ���������� ������� ���������� � �������
        countRowsEvent -= 10;
        CalculateDistance calculateDistance = new CalculateDistance(logitude, latitude);
        //????????? ?????? ? ???????
        for (int i = 0; i < Event_json.length(); i++) {
            JSONObject jo = new JSONObject((String) Event_json.get(i));
            EventId.add(jo.get("id").toString());
            Content.add(jo.get("content").toString());
            Title.add(jo.get("nickname").toString());
            Avatar.add(jo.get("img").toString());
            IsLiked.add(jo.get("liked").toString().equals("true"));

            String eventTime = (String) jo.get("time");
            Time.add(new EventTime().handle(eventTime));

            String kilometers;


            if (jo.get("latitude").toString().equals("") || jo.get("longitude").toString().equals("")) {
                kilometers = null;
            } else {
                double lat = Double.parseDouble(jo.get("latitude").toString());
                double lng = Double.parseDouble(jo.get("longitude").toString());
                kilometers = calculateDistance.caclulate(lng, lat);
            }
            Distance.add(kilometers);

            //���� ���������� �������� ��� �������� �� �������� ���������� listview ������
            if (!scroll) {
                eventBlock = new EventAdapter(context, Title, Content, Avatar, Time, Distance, EventId, IsLiked);
                listView.setAdapter(eventBlock);
            }
            //���� ������� ���� �������� �������, �� ��������� Listview
            else {
                eventBlock.notifyDataSetChanged();
            }
        }
        return countRowsEvent;
    }
}
