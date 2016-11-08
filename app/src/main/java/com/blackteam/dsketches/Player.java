package com.blackteam.dsketches;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Класс содержит информацию игроке,
 * его достижения, кол-во очков, skills и их количество, и т.п.
 */
public class Player {
    private static Context context_;
    private static final String fileName_ = "userData.xml";

    private int score_ = 0;
    private ArrayMap<SkillType, Integer> skills_ = new ArrayMap<>();
    /** Список полученных достижений. */
    private ArrayList<String> achievements_ = new ArrayList<>();
    // private Sketch.Types lastSketchType_;

    public Player(Context context) throws IOException, XmlPullParserException {
        load(context);
    }

    public void setScore(int newScore) {
        score_ = newScore;
    }

    public int getScore() {
        return score_;
    }

    public void addScore(int addingScore) {
        score_ += addingScore;
    }

    public ArrayList<String> getEarnedAchievementsNames() {
        return achievements_;
    }

    public void load(Context context) throws IOException, XmlPullParserException {
        context_ = context;
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName_);
            readFile(fileInputStream);
        }
        // Если файл не создан необходимо его создать.
        catch (FileNotFoundException ex) {
            writeFile(context);
        }
    }

    public void save() {
        writeFile(context_);
        Log.i("Player", "save is completed.");
    }

    private void writeFile(Context context) {
        try {
            String dataWrite = createXmlData();
            FileOutputStream fileOutputStream = context.openFileOutput(fileName_, Context.MODE_PRIVATE);
            fileOutputStream.write(dataWrite.getBytes());
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            // Такого быть не может, т.к. если файла не существует, openFileOutput() создаст файл.
            e.printStackTrace();
            Log.i("FileNotFoundException", e.getMessage());
            Toast.makeText(context_, "FileNotFoundException: error write user data to file.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("IOException", e.getMessage());
            Toast.makeText(context_, "IOException: error write user data to file.", Toast.LENGTH_LONG).show();
        }
    }

    private String createXmlData() throws IOException {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();
        xmlSerializer.setOutput(stringWriter);
        xmlSerializer.startDocument("UTF-8", true);
        xmlSerializer.startTag(null, "userData");

        xmlSerializer.startTag(null, "score");
        xmlSerializer.attribute(null, "value", String.valueOf(score_));
        xmlSerializer.endTag(null, "score");

        xmlSerializer.startTag(null, "gameDots");
        // TODO: Сохранять игровое состояние.
        xmlSerializer.endTag(null, "gameDots");

        xmlSerializer.startTag(null, "skills");
        // TODO: Сохранять кол-во оставшихся skill-ов.
        xmlSerializer.endTag(null, "skills");

        xmlSerializer.startTag(null, "earned_achievements");
        for (String achievement : achievements_) {
            xmlSerializer.startTag(null, "achievement");
            xmlSerializer.attribute(null, "name", achievement);
            xmlSerializer.endTag(null, "achievement");
        }
        xmlSerializer.endTag(null, "earned_achievements");

        xmlSerializer.endTag(null, "userData");
        xmlSerializer.flush();
        String dataXml = stringWriter.toString();

        return dataXml;
    }

    private void readFile(FileInputStream fileInputStream) throws IOException, XmlPullParserException {
        // Считываем данные из файла в строку.
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        char[] inputBuffer = new char[fileInputStream.available()];
        inputStreamReader.read(inputBuffer);
        String data = new String(inputBuffer);
        inputStreamReader.close();
        fileInputStream.close();

        // Распарсиваем строку как xml.
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xmlPullParser = factory.newPullParser();
        xmlPullParser.setInput(new StringReader(data));

        // Считываем данные.
        int eventType = xmlPullParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (xmlPullParser.getName().equals("score")) {
                    score_ = Integer.parseInt(xmlPullParser.getAttributeValue(null, "value"));
                    Log.i("Player", String.format("score = %d", score_));
                } else if (xmlPullParser.getName().equals("achievement")) {
                    String achievementName = xmlPullParser.getAttributeValue(null, "name");
                    if (achievementName != null && !achievementName.equals(""))
                        achievements_.add(achievementName);
                }
            }

            eventType = xmlPullParser.next();
        }

        Log.i("Player", "read is completed.");
    }

    public void earnAchievement(String achievementName) {
        achievements_.add(achievementName);
    }
}
