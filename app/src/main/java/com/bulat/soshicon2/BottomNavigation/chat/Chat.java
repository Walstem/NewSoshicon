package com.bulat.soshicon2.BottomNavigation.chat;

import static com.bulat.soshicon2.constants.constants.DATABASE;
import static com.bulat.soshicon2.constants.constants.ID;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bulat.soshicon2.R;
import com.bulat.soshicon2.Toasts.Toasts;
import com.bulat.soshicon2.asynctasks.SendQuery;
import com.bulat.soshicon2.checks.NetCheck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Chat extends Fragment {

    private static final String GET_COUNT_CHATS_PHP = "GetCountChats.php";
    private static final String GET_CHATS = "get_chats.php";

    View view;
    ListView listView;

    private int countRowsEvent;
    public int start = 10;
    public int end = 10;
    boolean ScrollStateChanged = false;

    private ArrayAdapter eventBlock;

    SharedPreferences sp;

    private ArrayList<String> ChatsId = new ArrayList<>();
    private ArrayList<String> User_Id = new ArrayList<>();
    private ArrayList<String> AnotherUserId = new ArrayList<>();
    private ArrayList<String> Nickname = new ArrayList<>();
    private ArrayList<String> LastMessage = new ArrayList<>();
    private ArrayList<String> Avatar = new ArrayList<>();
    private ArrayList<String> TimeChat = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.chat, container, false);

        sp = getContext().getSharedPreferences(DATABASE, 0);

        if (NetCheck.StatusConnection(getContext())) {
            ShowInternerEror();
        } else {
            //переписать
            listView = view.findViewById(R.id.listView);
            SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.SwipeRefreshLayout);


            //прогружаем данные при запуске фрагмента
            try {

               show(listView, start, end, false);

            } catch (JSONException | ExecutionException | InterruptedException | ParseException e) {
                e.printStackTrace();
            }

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

                //отслеживаем свайп  пользовател€
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (NetCheck.StatusConnection(getContext())) {
                        LayoutInflater lnInflater = getLayoutInflater();
                        View ToastId = view.findViewById(R.id.toast_layout_root);
                        Toasts InternetToast = new Toasts(getContext(), lnInflater, ToastId);
                        InternetToast.ViewInterntEror(view);
                    } else if (ScrollStateChanged) {
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
            ChatsId = new ArrayList<>();
            User_Id = new ArrayList<>();
            AnotherUserId = new ArrayList<>();
            LastMessage = new ArrayList<>();
            Avatar = new ArrayList<>();
            TimeChat = new ArrayList<>();
            Nickname = new ArrayList<>();


            //вычисл€ем количество записей в таблице с событи€ми
            SendQuery sendQuery = new SendQuery(GET_COUNT_CHATS_PHP);
            sendQuery.execute("?user_id=" + sp.getString(ID, ""));
            try {
                countRowsEvent = Integer.parseInt(sendQuery.get());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }
        //получаем данные откликов
        String[] KeyArgs = {"start", "end", "user"};
        String UserId = sp.getString(ID, "");
        String[] Args = {Integer.toString(start - 10), Integer.toString(end), UserId};


        ChatReceiving Query = new ChatReceiving(GET_CHATS, KeyArgs, Args);
        Query.execute();

        JSONArray Event_json = new JSONArray(Query.get());
        //уменьшаем количество записей оставшихс€ в таблице
        countRowsEvent -= 10;
        //????????? ?????? ? ???????
        String textContent;
        for (int i = 0; i < Event_json.length(); i++) {
            JSONObject jo = new JSONObject((String) Event_json.get(i));
            System.out.println(jo);
            ChatsId.add(jo.get("chat_id").toString());
            User_Id.add(jo.get("user_id").toString());
            AnotherUserId.add(jo.get("another_user_id").toString());
            Nickname.add(jo.get("nickname").toString());
            Avatar.add("http://j911147y.beget.tech/" + jo.get("img"));

            String Time = jo.get("time").toString();
            TimeChat.add(new ChatTime().handle(Time));

            textContent = jo.get("last_message").toString();
            if(textContent.equals("null")){
                LastMessage.add("Ќачинайте общение");
            }
            else if (textContent.length() > 33){
                textContent = textContent.substring(0, 33);
                LastMessage.add(textContent + "...");
            }
            else{
                LastMessage.add(textContent);
            }


            //если происходит загрузка при переходе на страницу прогружаем listview «аново

        }
        if (!scroll)
        {
            eventBlock = new ChatAdapter(getContext(),view,  ChatsId, User_Id, AnotherUserId, Nickname, Avatar,TimeChat, LastMessage, getActivity());
            listView.setAdapter(eventBlock);
        }
        //если функци€ была вызванна свайпом, то обновл€ем Listview
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
