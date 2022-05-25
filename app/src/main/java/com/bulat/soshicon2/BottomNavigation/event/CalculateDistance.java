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

        System.out.println("start" + startLatitude + startLongitude );
        System.out.println("end" + endLatitude + endLongitude );

        int result = Math.round(locStart.distanceTo(locEnd));
        return  result + " метров";

    }
}
