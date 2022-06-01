package com.bulat.soshicon2.BottomNavigation.event;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Years;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventTime {
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
        else if (eventTime.equals(clientTime)) {
            time = "только что";
        }
        else if(year == 0 && month == 0 && day == 0 && hour  == 0){
            if (min <= 5){
                time = "только что";
            }
            if (min > 5 && min < 21 || min > 24 && min < 31 || min > 34 && min < 41 || min > 44 && min < 51 || min > 54 && min < 60) {
                time = min + " минут назад";
            }
            if (min  == 21 || min  == 31 || min  == 41 ||min  == 51){
                time = min + " минута назад";
            }
            if (min > 21 && min < 25 || min > 31 && min < 35 || min > 41 && min < 45 || min > 51 && min < 55){
                time = min + " минуты назад";
            }
        }
        else if (year == 0 && month == 0 && day == 0){
            if (hour == 1 || hour == 21){
                time = hour + " час назад";
            }
            if (hour > 1 && hour < 5 || hour > 21){
                time = hour + " часа назад";
            }
            if (hour > 4 && hour < 21){
                time = hour + " часов назад";
            }
        }
        else if (year == 0 && month == 0){
            if (day == 1){
                time = "вчера в " + eventTimeArr[3] + ":" + eventTimeArr[4] ;
            }
            if (day == 31 || day == 21){
                time = day + " день назад";
            }
            if (day > 1 && day < 5 ||  day > 21 && day < 25){
                time = day + " дня назад";
            }
            if (day > 4 &&  day < 21 || day > 24 &&  day < 31 ){
                time = day + " дней назад";
            }
        }
        System.out.println(time);
        return time;
    }
}
