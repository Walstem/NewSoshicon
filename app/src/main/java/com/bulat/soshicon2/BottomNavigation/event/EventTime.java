package com.bulat.soshicon2.BottomNavigation.event;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventTime {
    public String handle(String eventTime) {

        String pattern = "yyyy-MM-dd-HH-mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String clientTime = simpleDateFormat.format(new Date());

        String time = "";
        String[] eventTimeArr = eventTime.split("-");
        String[] clientTimeArr = clientTime.split("-");

        int[] eventTimeArrInt = {0, 0, 0, 0, 0};
        int[] clientTimeArrInt = {0, 0, 0, 0, 0};

        for (int j = 0; j < 5; j++) {
            eventTimeArrInt[j] = Integer.parseInt(eventTimeArr[j]);
            clientTimeArrInt[j] = Integer.parseInt(clientTimeArr[j]);
        }
        if (eventTime == ""){
            time = "";
        }
        else if (eventTime.equals(clientTime)) {
            time = "только что";
        } else if (eventTimeArrInt[0] == clientTimeArrInt[0] && eventTimeArrInt[1] == clientTimeArrInt[1] && eventTimeArrInt[2] == clientTimeArrInt[2] && eventTimeArrInt[3] == clientTimeArrInt[3] && clientTimeArrInt[4] - eventTimeArrInt[4]  < 5){
            time = "только что";
        }
        else if (eventTimeArrInt[0] == clientTimeArrInt[0] && eventTimeArrInt[1] == clientTimeArrInt[1] && eventTimeArrInt[2] == clientTimeArrInt[2] && eventTimeArrInt[3] == clientTimeArrInt[3] && clientTimeArrInt[4] - eventTimeArrInt[4] < 21 || clientTimeArrInt[4] - eventTimeArrInt[4] > 25 ){
            time = clientTimeArrInt[4] - eventTimeArrInt[4] + " минут назад";
        }
        else if (eventTimeArrInt[0] == clientTimeArrInt[0] && eventTimeArrInt[1] == clientTimeArrInt[1] && eventTimeArrInt[2] == clientTimeArrInt[2] && eventTimeArrInt[3] == clientTimeArrInt[3] && clientTimeArrInt[4] - eventTimeArrInt[4]  == 21 ){
            time = clientTimeArrInt[4] - eventTimeArrInt[4] + " минута назад";
        }
        else if (eventTimeArrInt[0] == clientTimeArrInt[0] && eventTimeArrInt[1] == clientTimeArrInt[1] && eventTimeArrInt[2] == clientTimeArrInt[2] && eventTimeArrInt[3] == clientTimeArrInt[3] && clientTimeArrInt[4] - eventTimeArrInt[4] > 21 || clientTimeArrInt[4] - eventTimeArrInt[4] < 25 ){
            time = clientTimeArrInt[4] - eventTimeArrInt[4] + " минуты назад";
        }
        else if (eventTimeArrInt[0] == clientTimeArrInt[0] && eventTimeArrInt[1] == clientTimeArrInt[1] && eventTimeArrInt[2] == clientTimeArrInt[2] && clientTimeArrInt[3] - eventTimeArrInt[3] == 1 || clientTimeArrInt[3] - eventTimeArrInt[3] == 21){
            time = clientTimeArrInt[3] - eventTimeArrInt[3] + " час назад";
        }
        else if (eventTimeArrInt[0] == clientTimeArrInt[0] && eventTimeArrInt[1] == clientTimeArrInt[1] && eventTimeArrInt[2] == clientTimeArrInt[2] && clientTimeArrInt[3] - eventTimeArrInt[3] > 1 && clientTimeArrInt[3] - eventTimeArrInt[3] < 5 || clientTimeArrInt[3] - eventTimeArrInt[3] > 21){
            time = clientTimeArrInt[3] - eventTimeArrInt[3] + " часа назад";
        }
        else if (eventTimeArrInt[0] == clientTimeArrInt[0] && eventTimeArrInt[1] == clientTimeArrInt[1] && eventTimeArrInt[2] == clientTimeArrInt[2] && clientTimeArrInt[3] - eventTimeArrInt[3] > 4 && clientTimeArrInt[3] - eventTimeArrInt[3] < 21){
            time = clientTimeArrInt[3] - eventTimeArrInt[3] + " часов назад";
        }
        else if (eventTimeArrInt[0] == clientTimeArrInt[0] && eventTimeArrInt[1] == clientTimeArrInt[1] && clientTimeArrInt[2] - eventTimeArrInt[2] == 1 ||  clientTimeArrInt[2] - eventTimeArrInt[2] == 31 || clientTimeArrInt[2] - eventTimeArrInt[2] == 21){
            time = clientTimeArrInt[3] - eventTimeArrInt[3] + " день назад";
        }
        else if (eventTimeArrInt[0] == clientTimeArrInt[0] && eventTimeArrInt[1] == clientTimeArrInt[1] && clientTimeArrInt[2] - eventTimeArrInt[2] > 1 && clientTimeArrInt[2] - eventTimeArrInt[2] < 5 ||  clientTimeArrInt[2] - eventTimeArrInt[2] > 21 && clientTimeArrInt[2] - eventTimeArrInt[2] < 25){
            time = clientTimeArrInt[3] - eventTimeArrInt[3] + " дня назад";
        }
        else if (eventTimeArrInt[0] == clientTimeArrInt[0] && eventTimeArrInt[1] == clientTimeArrInt[1] && clientTimeArrInt[2] - eventTimeArrInt[2] > 4 &&  clientTimeArrInt[2] - eventTimeArrInt[2] < 21 || clientTimeArrInt[2] - eventTimeArrInt[2] > 24 &&  clientTimeArrInt[2] - eventTimeArrInt[2] < 31 ){
            time = clientTimeArrInt[3] - eventTimeArrInt[3] + " дней назад";
        }
        else {
            time = eventTimeArrInt[0] + "г. " +  eventTimeArrInt[1] + "/" +  eventTimeArrInt[2] + " " + eventTimeArr[3] + ":" + eventTimeArr[4];
        }
        System.out.println(time);
        return time;
    }
}
