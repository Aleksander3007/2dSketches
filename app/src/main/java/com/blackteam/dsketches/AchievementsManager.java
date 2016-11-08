package com.blackteam.dsketches;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.blackteam.dsketches.gui.AchievementToast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class AchievementsManager implements Observer {

    public ArrayList<Achievement> achievements_ = new ArrayList<>();
    public Context context_;

    public AchievementsManager(Context context) throws XmlPullParserException, IOException {
        this.context_ = context;
        loadContent(context);
    }

    public void loadContent(Context context) throws XmlPullParserException, IOException {
        Achievement achievement = new Achievement();
        XmlResourceParser xmlResParser = context.getResources().getXml(R.xml.achievements);
        int eventType = xmlResParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (xmlResParser.getName().equals("achievement")) {
                    achievement = new Achievement();
                    achievement.setName(xmlResParser.getAttributeValue(null, "name"));
                    boolean isEarned = xmlResParser.getAttributeBooleanValue(null, "isEarned", false);
                    if (isEarned) achievement.earn();
                } else if (xmlResParser.getName().equals("condition")) {
                    String conditionName = xmlResParser.getAttributeValue(null, "name");
                    if (TextUtils.equals(conditionName, "sketch")) {
                        String sketchType = xmlResParser.getAttributeValue(null, "value");
                        achievement.setSketchType(sketchType);
                    } else if (TextUtils.equals(conditionName, "score")) {
                        int score = xmlResParser.getAttributeIntValue(null, "value", Achievement.ANY_SCORE);
                        achievement.setScore(score);
                    } else if (TextUtils.equals(conditionName, "profit")) {
                        int profit = xmlResParser.getAttributeIntValue(null, "value", Achievement.ANY_PROFIT);
                        achievement.setProfit(profit);
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (xmlResParser.getName().equals("achievement")) {
                    achievements_.add(achievement);
                }
            }

            eventType = xmlResParser.next();
        }
        xmlResParser.close();
        Log.i("AchievementsManager", "Xml is read.");

        // TODO: В отдельный класс SavedInfo.


        // 1. Пытаемся открыть файл savedInfo.
        // 2. Если его нет, то создаём.
        // 3. И тогда всем achievement присваиваем isEarned = false;
    }

    @Override
    public void update(Observable observable, Object data) {
        final String sketchType = (String) data;

        Log.i("Achievement", "update");

        for (Achievement achievement : achievements_) {
            if (achievement.equals(sketchType, 0, 0)) {
                Log.i("Achievement", achievement.getName());

                if (!achievement.isEarned()) {
                    AchievementToast.makeText(context_, achievement.getName()).show();
                }
            }
        }
    }

    public ArrayList<Achievement> getAchiviements() {
        return achievements_;
    }
}
