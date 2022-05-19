package com.bulat.soshicon2.BottomNavigation.event;

import android.location.Location;

import java.text.DecimalFormat;

public class CalculateDistance {
    double startLatitude;
    double startLongitude;

    CalculateDistance(double startLatitude, double startLongitude) {
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
    }

    public String caclulate(double endLatitude, double endLongitude) {
        Location locStart = new Location("");
        locStart.setLatitude(startLatitude);
        locStart.setLongitude(startLongitude);

        Location locEnd = new Location("");
        locEnd.setLatitude(endLatitude);
        locEnd.setLongitude(endLongitude);


        int result = Math.round(locStart.distanceTo(locEnd));
        if (result < 1000){
            return result + " μ";
        }
        else{
            return Math.round(result/1000) + " κμ";
        }
    }
}

