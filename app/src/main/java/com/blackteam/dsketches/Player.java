package com.blackteam.dsketches;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.blackteam.dsketches.utils.Vector2;

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
import java.util.Map;

/**
 * Класс содержит информацию игроке,
 * его достижения, кол-во очков, skills и их количество, и т.п.
 */
public class Player {
    private static final String fileName_ = "userData.xml";

    private ContentManager contents_;

    private int score_ = 0;
    /** Список skills с доступным количеством. */
    private ArrayMap<SkillType, Integer> skills_ = new ArrayMap<>(SkillType.values().length);
    /** Список полученных достижений. */
    private ArrayList<String> achievements_ = new ArrayList<>();
    /** Массив игровых точек */
    private GameDot[][] gameDots_ = null;

    public Player(ContentManager contents) throws IOException, XmlPullParserException {
        this.contents_ = contents;
        for (SkillType skillType : SkillType.values()) {
            skills_.put(skillType, 2); // Начальное состояние.
        }
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

    /** Получить имена заработанных достижений. */
    public ArrayList<String> getEarnedAchievementsNames() {
        return achievements_;
    }

    public ArrayMap<SkillType, Integer> getSkills() {
        return skills_;
    }

    public void setSkill(SkillType skillType, int skillAmount) {
        skills_.setValueAt(skills_.indexOfKey(skillType), skillAmount);
    }

    public GameDot[][] getGameDots() {
        return gameDots_;
    }

    public void load(Context context) throws IOException, XmlPullParserException {
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName_);
            readFile(fileInputStream);
        }
        // Если файл не создан необходимо его создать.
        catch (FileNotFoundException ex) {
            writeFile(context);
        }
    }

    public void save(Context context) {
        writeFile(context);
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
            Toast.makeText(context, "FileNotFoundException: error write user data to file.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("IOException", e.getMessage());
            Toast.makeText(context, "IOException: error write user data to file.", Toast.LENGTH_LONG).show();
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
        xmlSerializer.attribute(null, "nRows", String.valueOf(gameDots_.length));
        xmlSerializer.attribute(null, "nColumns", String.valueOf(String.valueOf(gameDots_[0].length)));
        for (int iRow = 0; iRow < gameDots_.length; iRow++) {
            for (int iCol = 0; iCol < gameDots_[iRow].length; iCol++) {
                xmlSerializer.startTag(null, "gameDot");
                xmlSerializer.attribute(null, "type", String.valueOf(gameDots_[iRow][iCol].getType()));
                xmlSerializer.attribute(null, "specType", String.valueOf(gameDots_[iRow][iCol].getSpecType()));
                xmlSerializer.attribute(null, "row", String.valueOf(iRow));
                xmlSerializer.attribute(null, "column", String.valueOf(iCol));
                xmlSerializer.endTag(null, "gameDot");
            }
        }
        xmlSerializer.endTag(null, "gameDots");

        xmlSerializer.startTag(null, "skills");
        for (Map.Entry<SkillType, Integer> skillEntry : skills_.entrySet()) {
            xmlSerializer.startTag(null, "skill");
            xmlSerializer.attribute(null, "type", String.valueOf(skillEntry.getKey()));
            xmlSerializer.attribute(null, "amount", String.valueOf(skillEntry.getValue()));
            xmlSerializer.endTag(null, "skill");
        }
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
                }
                else if (xmlPullParser.getName().equals("achievement")) {
                    String achievementName = xmlPullParser.getAttributeValue(null, "name");
                    if (achievementName != null && !achievementName.equals(""))
                        achievements_.add(achievementName);
                }
                else if (xmlPullParser.getName().equals("skill")) {
                    SkillType skillType = Skill.convertToType(xmlPullParser.getAttributeValue(null, "type"));
                    int skillCount = Integer.parseInt(xmlPullParser.getAttributeValue(null, "amount"));
                    skills_.setValueAt(skills_.indexOfKey(skillType), skillCount);
                }
                else if (xmlPullParser.getName().equals("gameDots")) {
                    try {
                        int nRows = Integer.parseInt(xmlPullParser.getAttributeValue(null, "nRows"));
                        int nColumns = Integer.parseInt(xmlPullParser.getAttributeValue(null, "nColumns"));
                        gameDots_ = new GameDot[nRows][nColumns];
                    }
                    // Если вдруг не указаны размеры, то значит инициализируем default значениями.
                    catch (NumberFormatException nfEx) {
                        gameDots_ = new GameDot[World.DEFAULT_NUM_ROWS][World.DEFAULT_NUM_COLUMNS];
                    }

                }
                else if (xmlPullParser.getName().equals("gameDot")) {
                    int iRow = Integer.parseInt(xmlPullParser.getAttributeValue(null, "row"));;
                    int iCol = Integer.parseInt(xmlPullParser.getAttributeValue(null, "column"));
                    GameDot.Types gameDotType = GameDot.convertToType(
                            xmlPullParser.getAttributeValue(null, "type"));
                    GameDot.SpecTypes gameDotSpecType = GameDot.convertToSpecType(
                            xmlPullParser.getAttributeValue(null, "specType"));

                    gameDots_[iRow][iCol] = new GameDot(gameDotType, gameDotSpecType,
                            new Vector2(0, 0), iRow, iCol, contents_);
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
