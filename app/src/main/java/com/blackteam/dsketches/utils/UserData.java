package com.blackteam.dsketches.utils;

import android.content.Context;
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

/**
 * Чтение, запись и хранение пользовательских данных.
 */
public class UserData {
    private static Context context_;
    private static final String fileName_ = "userData.xml";

    public static int score_;

    public static void load(Context context) throws IOException, XmlPullParserException {
        context_ = context;
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName_);
            readFile(fileInputStream);
        }
        // Если файл не создан необходимо его создать.
        catch (FileNotFoundException ex) {
            createFile(context);
        }
    }

    private static void createFile(Context context) {
        try {
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter stringWriter = new StringWriter();
            xmlSerializer.setOutput(stringWriter);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, "userData");
            xmlSerializer.startTag(null, "score");
            xmlSerializer.attribute(null, "value", "0");
            xmlSerializer.endTag(null, "score");
            xmlSerializer.startTag(null, "gameDots");
            // TODO: Сохранять игровое состояние.
            xmlSerializer.endTag(null, "gameDots");
            xmlSerializer.startTag(null, "skills");
            // TODO: Сохранять кол-во оставшихся skill-ов.
            xmlSerializer.endTag(null, "skills");
            xmlSerializer.startTag(null, "earned_achievements");
            xmlSerializer.endTag(null, "earned_achievements");
            xmlSerializer.endTag(null, "userData");
            xmlSerializer.flush();
            String dataWrite = stringWriter.toString();

            FileOutputStream fileOutputStream = context.openFileOutput(fileName_, Context.MODE_PRIVATE);
            fileOutputStream.write(dataWrite.getBytes());
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            // Такого быть не может, т.к. если файла не существует openFileOutput() создаст файл.
            e.printStackTrace();
            Log.i("FileNotFoundException", e.getMessage());
            Toast.makeText(context_, "FileNotFoundException: error write user data to file.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("IOException", e.getMessage());
            Toast.makeText(context_, "IOException: error write user data to file.", Toast.LENGTH_LONG).show();
        }
    }

    private static void readFile(FileInputStream fileInputStream) throws IOException, XmlPullParserException {
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
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                Log.i("userData", "End tag " + xmlPullParser.getName());
            }

            eventType = xmlPullParser.next();
        }
    }
}
