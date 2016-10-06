package com.blackteam.dsketches;

import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class AchievementsManager implements Observer{
    @Override
    public void update(Observable observable, Object data) {
        final ArrayList<Orb> selectedOrbs = (ArrayList<Orb>) (data);

        Log.i("Achievement", String.valueOf(selectedOrbs.size()));

        if (selectedOrbs.size() >= 3) {
            Log.i("Achievement", ">3");
        }
    }
}
