package com.bulat.soshicon2.BottomNavigation.chat;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Years;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatTime {
    public String handle(String eventTime) throws ParseException {

        String[] eventTimeArr = eventTime.split("-");
        String time = "";
        String pattern = "yyyy-MM-dd-HH-mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String clientTime = simpleDateFormat.format(new Date());

        Date d1 = simpleDateFormat.parse(eventTime);
        Date d2 = simpleDateFormat.parse(clientTime);

        DateTime dt1 = new DateTime(d1);
        DateTime dt2 = new DateTime(d2);

        int year = Years.yearsBetween(dt1, dt2).getYears();
        int month = Months.monthsBetween(dt1, dt2).getMonths();
        int day = Days.daysBetween(dt1, dt2).getDays();
        int hour = Hours.hoursBetween(dt1, dt2).getHours();
        int min = Minutes.minutesBetween(dt1, dt2).getMinutes();


        if (eventTime == ""){
            time = "";
        }
        else if(year == 0 && month == 0 && day == 0 && hour  == 0){
            time = hour + ":" + min;
        }
        else if (year == 0 && month == 0 && day == 0){
            time = hour + ":" + min;
        }
        else if (year == 0 && month == 0){
            time = day + "." + month;
        }
        else{
            time = day + "." + month;
        }
        System.out.println(time);
        return time;
    }
}
