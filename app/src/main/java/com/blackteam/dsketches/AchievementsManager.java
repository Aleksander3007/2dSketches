package com.blackteam.dsketches;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class AchievementsManager implements Observer {

    public ArrayList<Achievement> achievements_ = new ArrayList<>();

    public void loadContent(Context context) throws XmlPullParserException, IOException {
        Achievement achievement = new Achievement();
        XmlResourceParser xpp = context.getResources().getXml(R.xml.achievements);
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (xpp.getName().equals("achievement")) {
                    achievement = new Achievement();
                    achievement.setName(xpp.getAttributeValue(null, "name"));
                }
                else if (xpp.getName().equals("condition")) {
                    String conditionName = xpp.getAttributeValue(null, "name");
                    if (TextUtils.equals(conditionName, "sketch")) {
                        String sketchType = xpp.getAttributeValue(null, "value");
                        achievement.setSketchType(Enum.valueOf(Sketch.Types.class, sketchType));
                    }
                    else if (TextUtils.equals(conditionName, "score")) {
                        int score = xpp.getAttributeIntValue(null, "value", Achievement.ANY_SCORE);
                        achievement.setScore(score);
                    }
                    else if (TextUtils.equals(conditionName, "profit")) {
                        int profit = xpp.getAttributeIntValue(null, "value", Achievement.ANY_PROFIT);
                        achievement.setProfit(profit);
                    }
                }
            }
            else if (eventType == XmlPullParser.END_TAG) {
                if (xpp.getName().equals("achievement")) {
                    achievements_.add(achievement);
                }
            }

            eventType = xpp.next();
        }
        Log.i("AchievementsManager", "Xml is readed.");
    }

    @Override
    public void update(Observable observable, Object data) {
        final Sketch.Types sketchType = (Sketch.Types) data;

        Log.i("Achievement", "update");
        // TODO: Ачивки могут быть не только за sketch, но еще, например, за набранное.
        // количество очков в сумме, за раз, и т.д.

        for (Achievement achievement : achievements_) {
            if (achievement.equals(sketchType, 0, 0)) {
                Log.i("Achievement", achievement.getName());

                // TODO: Необходимо проверять есть ли уже такая ачивка.
                // Если нет, добавляем, и выводим достижение, что получили ачивку.
            }
        }
    }
}
