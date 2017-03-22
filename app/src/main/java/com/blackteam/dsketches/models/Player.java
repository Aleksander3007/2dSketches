package com.blackteam.dsketches.models;

import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.Xml;

import com.blackteam.dsketches.GameDotsFactory;
import com.blackteam.dsketches.World;
import com.blackteam.dsketches.models.gamedots.GameDot;
import com.blackteam.dsketches.managers.ContentManager;
import com.blackteam.dsketches.utils.Vector2;
import com.blackteam.dsketches.utils.xml.XmlParceable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Класс содержит информацию игроке,
 * его достижения, кол-во очков, skills и их количество, и т.п.
 */
public class Player implements XmlParceable {

    public static final String FILE_NAME = "userData.xml";

    private static final String sTagRoot = "userData";
    private static final String sTagScore = "score";
    private static final String sTagAchievement = "achievement";
    private static final String sTagSkill = "skill";
    private static final String sTagGameDots = "gameDots";
    private static final String sTagGameDot = "gameDot";
    private static final String sTagSkills = "skills";
    private static final String sTagEarnedAchievements = "earned_achievements";

    private static final String sAttrScoreValue = "value";
    private static final String sAttrAchievementName = "name";
    private static final String sAttrSkillType = "type";
    private static final String sAttrSkillAmount = "amount";
    private static final String sAttrNumRows = "nRows";
    private static final String sAttrNumColumns = "nColumns";
    private static final String sAttrGameDotRow = "row";
    private static final String sAttrGameDotColumn = "column";
    private static final String sAttrGameDotType = "type";
    private static final String sAttrGameDotSpecType = "specType";

    /** Количество skill по умолчанию. */
    private static final int DEFAULT_SKILL_AMOUNT = 2;

    private ContentManager mContents;

    private int score_ = 0;
    private ArrayMap<Skill.Type, Skill> skills_ = new ArrayMap<>(Skill.Type.values().length);
    /** Список полученных достижений. */
    private ArrayList<String> achievements_ = new ArrayList<>();
    /** Массив игровых точек */
    private GameDot[][] gameDots_ = null;

    public Player(ContentManager contents) throws IOException, XmlPullParserException {
        this.mContents = contents;
        for (Skill.Type skillType : Skill.Type.values()) {
            skills_.put(skillType, new Skill(skillType, DEFAULT_SKILL_AMOUNT));
        }
    }

    public void setScore(int newScore) {
        score_ = newScore;
    }

    public int getScore() {
        return score_;
    }

    public void addScore(final int addingScore) {
        score_ += addingScore;
    }

    public void removeScore(final int removingScore) {
        score_ -= removingScore;
    }

    /** Получить имена заработанных достижений. */
    public ArrayList<String> getEarnedAchievementsNames() {
        return achievements_;
    }

    public Skill getSkill(final Skill.Type skillType) {
        return skills_.get(skillType);
    }

    public GameDot[][] getGameDots() {
        return gameDots_;
    }

    @Override
    public void parseData(XmlPullParser xmlParser) throws XmlPullParserException, IOException {
        // Считываем данные.
        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                switch (xmlParser.getName()) {
                    case sTagScore:
                        parseScore(xmlParser);
                        break;
                    case sTagAchievement:
                        parseEarnedAchievements(xmlParser);
                        break;
                    case sTagSkill:
                        parseSkills(xmlParser);
                        break;
                    case sTagGameDots:
                        parseGameDots(xmlParser);
                        break;
                    case sTagGameDot:
                        parseGameDot(xmlParser);
                        break;
                    default:
                        // отстальные тэги игнорируем.
                }
            }

            eventType = xmlParser.next();
        }

        Log.i("Player", "read is completed.");
    }

    private void parseScore(XmlPullParser xmlParser) {
        score_ = Integer.parseInt(xmlParser.getAttributeValue(null, sAttrScoreValue));
        Log.i("Player", String.format("score = %d", score_));
    }

    private void parseEarnedAchievements(XmlPullParser xmlParser) {
        String achievementName = xmlParser.getAttributeValue(null, sAttrAchievementName);
        if (achievementName != null && !achievementName.equals(""))
            achievements_.add(achievementName);
    }

    private void parseSkills(XmlPullParser xmlParser) {
        Skill.Type skillType = Skill.convertToType(xmlParser.getAttributeValue(null, sAttrSkillType));
        int skillAmount = Integer.parseInt(xmlParser.getAttributeValue(null, sAttrSkillAmount));

        if (skills_.containsKey(skillType))
            skills_.get(skillType).setAmount(skillAmount);
        else
            skills_.put(skillType, new Skill(skillType, skillAmount));
    }

    private void parseGameDots(XmlPullParser xmlParser) {
        try {
            int nRows = Integer.parseInt(xmlParser.getAttributeValue(null, sAttrNumRows));
            int nColumns = Integer.parseInt(xmlParser.getAttributeValue(null, sAttrNumColumns));
            gameDots_ = new GameDot[nRows][nColumns];
        }
        // Если вдруг не указаны размеры, то значит инициализируем default значениями.
        catch (NumberFormatException nfEx) {
            gameDots_ = new GameDot[World.DEFAULT_NUM_ROWS][World.DEFAULT_NUM_COLUMNS];
        }
    }

    private void parseGameDot(XmlPullParser xmlParser) {
        int iRow = Integer.parseInt(xmlParser.getAttributeValue(null, sAttrGameDotRow));
        int iCol = Integer.parseInt(xmlParser.getAttributeValue(null, sAttrGameDotColumn));
        GameDot.Types gameDotType = GameDot.convertToType(
                xmlParser.getAttributeValue(null, sAttrGameDotType));
        String gameDotSpecType = xmlParser.getAttributeValue(null, sAttrGameDotSpecType);

        gameDots_[iRow][iCol] = GameDotsFactory.createDot(gameDotType, gameDotSpecType,
                iRow, iCol, new Vector2(0, 0),
                mContents);
    }


    @Override
    public String parseXmlString() throws IOException {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();
        xmlSerializer.setOutput(stringWriter);
        xmlSerializer.startDocument("UTF-8", true);

        xmlSerializer.startTag(null, sTagRoot);

        addScoreToXml(xmlSerializer);
        addGameDotsToXml(xmlSerializer);
        addSkillsToXml(xmlSerializer);
        addEarnedAchievements(xmlSerializer);

        xmlSerializer.endTag(null, sTagRoot);

        xmlSerializer.flush();

        return stringWriter.toString();
    }

    /**
     * Добавить в xml-сериализатор тэг с информацией об полученных игровых очках.
     */
    private void addScoreToXml(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag(null, sTagScore);
        xmlSerializer.attribute(null, sAttrScoreValue, String.valueOf(score_));
        xmlSerializer.endTag(null, sTagScore);
    }

    /**
     * Добавить в xml-сериализатор тэг с информацией об расположении игровых точек.
     */
    private void addGameDotsToXml(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag(null, sTagGameDots);
        if (gameDots_ != null) {
            xmlSerializer.attribute(null, sAttrNumRows, String.valueOf(gameDots_.length));
            xmlSerializer.attribute(null, sAttrNumColumns,
                    String.valueOf(String.valueOf(gameDots_[0].length)));

            for (int iRow = 0; iRow < gameDots_.length; iRow++) {
                for (int iCol = 0; iCol < gameDots_[iRow].length; iCol++) {
                    xmlSerializer.startTag(null, sTagGameDot);
                    xmlSerializer.attribute(null, sAttrGameDotType,
                            String.valueOf(gameDots_[iRow][iCol].getType()));
                    xmlSerializer.attribute(null, sAttrGameDotSpecType,
                            String.valueOf(gameDots_[iRow][iCol].getName()));
                    xmlSerializer.attribute(null, sAttrGameDotRow, String.valueOf(iRow));
                    xmlSerializer.attribute(null, sAttrGameDotColumn, String.valueOf(iCol));
                    xmlSerializer.endTag(null, sTagGameDot);
                }
            }
        }
        else {
            xmlSerializer.attribute(null, sAttrNumRows,
                    String.valueOf(World.DEFAULT_NUM_ROWS));
            xmlSerializer.attribute(null, sAttrNumColumns,
                    String.valueOf(String.valueOf(World.DEFAULT_NUM_COLUMNS)));
        }
        xmlSerializer.endTag(null, sTagGameDots);
    }

    /**
     * Добавить в xml-сериализатор тэг с информацией об навыках.
     */
    private void addSkillsToXml(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag(null, sTagSkills);
        for (Skill skill : skills_.values()) {
            xmlSerializer.startTag(null, sTagSkill);
            xmlSerializer.attribute(null, sAttrSkillType, String.valueOf(skill.getType()));
            xmlSerializer.attribute(null, sAttrSkillAmount, String.valueOf(skill.getAmount()));
            xmlSerializer.endTag(null, sTagSkill);
        }
        xmlSerializer.endTag(null, sTagSkills);
    }

    /**
     * Добавить в xml-сериализатор тэг с информацией об полученных достижениях.
     */
    private void addEarnedAchievements(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag(null, sTagEarnedAchievements);
        for (String achievement : achievements_) {
            xmlSerializer.startTag(null, sTagAchievement);
            xmlSerializer.attribute(null, sAttrAchievementName, achievement);
            xmlSerializer.endTag(null, sTagAchievement);
        }
        xmlSerializer.endTag(null, sTagEarnedAchievements);
    }

    public void earnAchievement(String achievementName) {
        achievements_.add(achievementName);
    }
}
