package com.blackteam.dsketches;

import android.content.Context;
import android.content.Intent;
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

    public static final String TAG = AchievementsManager.class.getSimpleName();

    private static final String sTagAchievement = "achievement";
    private static final String sTagCondition = "condition";

    private static final String sAttrAchievementName = "name";
    private static final String sAttrAchievementDescription = "description";
    private static final String sAttrConditionName = "name";
    private static final String sAttrConditionSketch = "sketch";
    private static final String sAttrConditionScore = "score";
    private static final String sAttrConditionProfit = "profit";
    private static final String sAttrConditionValue = "value";

    private ArrayList<Achievement> mAchievements = new ArrayList<>();
    private Context mContext;
    private Player mPlayer;

    public AchievementsManager(Player player, Context context) throws XmlPullParserException, IOException {
        this.mPlayer = player;
        this.mContext = context;
        loadContent(context);
    }

    private void loadContent(Context context) throws XmlPullParserException, IOException {
        readFile(context);
        findEarnedAchievements();
    }

    private void readFile(Context context) throws XmlPullParserException, IOException {
        Achievement achievement = new Achievement();
        XmlResourceParser xmlResParser = context.getResources().getXml(R.xml.achievements);
        int eventType = xmlResParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (xmlResParser.getName().equals(sTagAchievement)) {
                    parseAchievement(xmlResParser, achievement);
                }
                else if (xmlResParser.getName().equals(sTagCondition)) {
                    parseCondition(xmlResParser, achievement);

                }
            }
            else if (eventType == XmlPullParser.END_TAG) {
                if (xmlResParser.getName().equals(sTagAchievement)) {
                    mAchievements.add(achievement);
                }
            }

            eventType = xmlResParser.next();
        }
        xmlResParser.close();
        Log.i(TAG, "Xml is read.");
    }

    /**
     * Распарсить Achievement через xmlResParser.
     * @param xmlResParser XML парсер.
     * @param achievement итоговый.
     */
    private void parseAchievement(XmlResourceParser xmlResParser, Achievement achievement) {
        achievement = new Achievement();
        achievement.setName(xmlResParser.getAttributeValue(null, sAttrAchievementName));
        achievement.setDescription(xmlResParser.getAttributeValue(null, sAttrAchievementDescription));
    }

    /**
     * Распарсить Условия для achievement через xmlResParser.
     * @param xmlResParser XML парсер.
     * @param achievement итоговый.
     */
    private void parseCondition(XmlResourceParser xmlResParser, Achievement achievement) {
        String conditionName = xmlResParser.getAttributeValue(null, sAttrConditionName);
        if (TextUtils.equals(conditionName, sAttrConditionSketch)) {
            String sketchType = xmlResParser.getAttributeValue(null, sAttrConditionValue);
            achievement.setSketchType(sketchType);
        } else if (TextUtils.equals(conditionName, sAttrConditionScore)) {
            int score = xmlResParser.getAttributeIntValue(null, sAttrConditionValue, Achievement.ANY_SCORE);
            achievement.setScore(score);
        } else if (TextUtils.equals(conditionName, sAttrConditionProfit)) {
            int profit = xmlResParser.getAttributeIntValue(null, sAttrConditionValue, Achievement.ANY_PROFIT);
            achievement.setProfit(profit);
        }
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
        final String sketchType = (String) info.get(World.DATA_SKETCH_TYPE);
        final int profit = (int)info.get(World.DATA_SKETCH_PROFIT);
        final int score = mPlayer.getScore();

        Log.i(TAG, "(profit, sketch, score) = " +
                        "(" +
                        String.valueOf(profit) + "," +
                        sketchType + "," +
                        String.valueOf(score) +
                        ")"
        );

        for (Achievement achievement : mAchievements) {
            boolean isAchievement = achievement.equals(
                    sketchType, Achievement.ANY_SCORE, Achievement.ANY_PROFIT) ||
                    achievement.equals(Achievement.ANY_SKETCH, score, Achievement.ANY_PROFIT) ||
                    achievement.equals(Achievement.ANY_SKETCH, Achievement.ANY_SCORE, profit);

            if (isAchievement) {
                Log.i(TAG, achievement.getName());

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
