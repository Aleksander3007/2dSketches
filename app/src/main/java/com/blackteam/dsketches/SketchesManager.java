package com.blackteam.dsketches;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Управление скетчами.
 */
public class SketchesManager {

    // TODO: Как действовать если скетч_1 перекрывает скетч_2 какой из них учитывать.
    // Вариант 1. Оба.
    // Вариант 2. Выставлять приоритет для каждого скетча.
    // Вариант 3. Порядок должен быть учитан в xml-файле.

    // Нулевой скетч.
    public static final Sketch SKETCH_NULL = Sketch.getNullSketch();

    private ArrayList<Sketch> mSketches = new ArrayList<>();

    public SketchesManager(Context context) throws XmlPullParserException, IOException {
        loadContent(context);
    }

    public void loadContent(Context context) throws XmlPullParserException, IOException {
        Sketch sketch = null;
        XmlResourceParser xpp = context.getResources().getXml(R.xml.sketches);
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (xpp.getName().equals("sketch")) {
                    String name = xpp.getAttributeValue(null, "name");
                    int cost = xpp.getAttributeIntValue(null, "cost", 0);
                    sketch = new Sketch(name, cost);
                }
                else if (xpp.getName().equals("elem")) {
                    int rowNo = xpp.getAttributeIntValue(null, "row", 0);
                    int colNo = xpp.getAttributeIntValue(null, "column", 0);
                    GameDot.Types dotType = GameDot.convertToType(xpp.getAttributeValue(null, "dotType"));
                    sketch.add(rowNo, colNo, dotType);
                }
            }
            else if (eventType == XmlPullParser.END_TAG) {
                if (xpp.getName().equals("sketch")) {
                    mSketches.add(sketch);
                }
            }
            eventType = xpp.next();
        }
        xpp.close();

        Log.i("SketchesManager", "Sketches xml is read.");
    }

    public Sketch findSketch(List<GameDot> gameDots) {
        if (gameDots == null)
            return SKETCH_NULL;

        // Ищём подходящий sketch.
        ArrayList<Sketch.Element> normalDots  = normalize(gameDots);
        for (Sketch sketch : mSketches) {
            if (sketch.isEqual(normalDots)) {
                return sketch;
            }
        }

        return SKETCH_NULL;
    }

    private ArrayList<Sketch.Element> normalize(List<GameDot> gameDots) {
        // 1. Ищем минимумы.
        int minRowNo = gameDots.get(0).getRowNo();
        int minColNo = gameDots.get(0).getColNo();
        for (GameDot gameDot : gameDots) {
            if (gameDot.getRowNo() < minRowNo)
                minRowNo = gameDot.getRowNo();
            if (gameDot.getColNo() < minColNo)
                minColNo = gameDot.getColNo();
        }

        // Создаем список нормализованных элементов.
        ArrayList<Sketch.Element> normalDots = new ArrayList<>(gameDots.size());
        for (GameDot gameDot : gameDots) {
            Sketch.Element elem = new Sketch.Element(
                    gameDot.getRowNo() - minRowNo,
                    gameDot.getColNo() - minColNo,
                    gameDot.getType()
            );
            normalDots.add(elem);
        }

        return normalDots;
    }

    public ArrayList<Sketch> getSketches() {
        return mSketches;
    }
}
