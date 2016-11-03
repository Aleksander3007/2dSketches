package com.blackteam.dsketches;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class SketchTest {
    @Test
    public void testIsEqual() throws Exception {
        final String FAKED_SKETCH_NAME = "SKETCH_NAME";
        final int FAKED_SKETCH_COST = 0;
        Sketch sketch = new Sketch(FAKED_SKETCH_NAME, FAKED_SKETCH_COST);
        sketch.add(0, 0, GameDot.Types.TYPE1);
        sketch.add(0, 1, GameDot.Types.TYPE1);
        sketch.add(0, 2, GameDot.Types.TYPE1);

        // Проверка, что sketch найден.
        ArrayList<Sketch.Element> sketchElems = new ArrayList<>();
        sketchElems.add(new Sketch.Element(0, 0, GameDot.Types.TYPE1));
        sketchElems.add(new Sketch.Element(0, 1, GameDot.Types.TYPE1));
        sketchElems.add(new Sketch.Element(0, 2, GameDot.Types.TYPE1));
        Assert.assertTrue(sketch.isEqual(sketchElems));

        // Проверка, что sketch НЕ найден.
        sketchElems.clear();
        sketchElems.add(new Sketch.Element(0, 0, GameDot.Types.TYPE1));
        sketchElems.add(new Sketch.Element(1, 1, GameDot.Types.TYPE1));
        sketchElems.add(new Sketch.Element(0, 2, GameDot.Types.TYPE1));
        Assert.assertFalse(sketch.isEqual(sketchElems));

        // Проверка на универсальный тип GameDot в элементах.
        sketchElems.clear();
        sketchElems.add(new Sketch.Element(0, 0, GameDot.Types.UNIVERSAL));
        sketchElems.add(new Sketch.Element(0, 1, GameDot.Types.UNIVERSAL));
        sketchElems.add(new Sketch.Element(0, 2, GameDot.Types.UNIVERSAL));
        Assert.assertTrue(sketch.isEqual(sketchElems));

        // Проверка на универсальный тип GameDot в sketch.
        sketch.clear();
        sketch.add(0, 0, GameDot.Types.UNIVERSAL);
        sketch.add(1, 1, GameDot.Types.UNIVERSAL);
        sketch.add(0, 2, GameDot.Types.UNIVERSAL);
        sketchElems.clear();
        sketchElems.add(new Sketch.Element(0, 0, GameDot.Types.TYPE1));
        sketchElems.add(new Sketch.Element(1, 1, GameDot.Types.UNIVERSAL));
        sketchElems.add(new Sketch.Element(0, 2, GameDot.Types.TYPE1));
        Assert.assertTrue(sketch.isEqual(sketchElems));
    }
}