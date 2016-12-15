package com.blackteam.dsketches;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.blackteam.dsketches.gui.AchievementToast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class AchievementsManager implements Observer {

    private ArrayList<Achievement> mAchievements = new ArrayList<>();
    private Context mContext;
    private Player mPlayer;

    public AchievementsManager(Player player, Context context) throws XmlPullParserException, IOException {
        this.mPlayer = player;
        this.mContext = context;
        loadContent(context);
    }

    public void loadContent(Context context) throws XmlPullParserException, IOException {
        readFile(context);
        findEarnedAchievements();
    }

    private void readFile(Context context) throws XmlPullParserException, IOException {
        Achievement achievement = new Achievement();
        XmlResourceParser xmlResParser = context.getResources().getXml(R.xml.achievements);
        int eventType = xmlResParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (xmlResParser.getName().equals("achievement")) {
                    achievement = new Achievement();
                    achievement.setName(xmlResParser.getAttributeValue(null, "name"));
                    achievement.setDescription(xmlResParser.getAttributeValue(null, "description"));
                }
                else if (xmlResParser.getName().equals("condition")) {
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
            }
            else if (eventType == XmlPullParser.END_TAG) {
                if (xmlResParser.getName().equals("achievement")) {
                    mAchievements.add(achievement);
                }
            }

            eventType = xmlResParser.next();
        }
        xmlResParser.close();
        Log.i("AchievementsManager", "Xml is read.");
    }

    private void findEarnedAchievements() {
        for (String achievementName : mPlayer.getEarnedAchievementsNames()) {
            for (Achievement achievement : mAchievements) {
                if (achievementName.equals(achievement.getName())) {
                    achievement.earn();
                    break;
                }
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        ArrayMap<String, Object> info = (ArrayMap<String, Object>) data;
        final String sketchType = (String) info.get("SketchType");
        final int profit = (int)info.get("Profit");
        final int score = mPlayer.getScore();

        Log.i("Achievement", "(profit, sketch, score) = " +
                        "(" +
                        String.valueOf(profit) + "," +
                        sketchType + "," +
                        String.valueOf(score) +
                        ")"
        );

        Log.i("Achievement", "update");

        for (Achievement achievement : mAchievements) {
            boolean isAchievement = achievement.equals(
                    sketchType, Achievement.ANY_SCORE, Achievement.ANY_PROFIT) ||
                    achievement.equals(Achievement.ANY_SKETCH, score, Achievement.ANY_PROFIT) ||
                    achievement.equals(Achievement.ANY_SKETCH, Achievement.ANY_SCORE, profit);

            if (isAchievement) {
                Log.i("Achievement", achievement.getName());

                if (!achievement.isEarned()) {
                    achievement.earn();
                    mPlayer.earnAchievement(achievement.getName());
                    AchievementToast.makeText(mContext, achievement.getName()).show();
                }
            }
        }
    }

    public ArrayList<Achievement> getAchiviements() {
        return mAchievements;
    }
}
