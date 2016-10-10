package com.blackteam.dsketches;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class SketchTest {
    @Test
    public void testIsEqual() throws Exception {
        final int FAKED_SKETCH_COST = 0;
        Sketch sketch = new Sketch(Sketch.Types.ROW_3, FAKED_SKETCH_COST);
        sketch.add(0, 0, Orb.Types.TYPE1);
        sketch.add(0, 1, Orb.Types.TYPE1);
        sketch.add(0, 2, Orb.Types.TYPE1);

        // Проверка, что sketch найден.
        ArrayList<Sketch.Element> sketchElems = new ArrayList<>();
        sketchElems.add(new Sketch.Element(0, 0, Orb.Types.TYPE1));
        sketchElems.add(new Sketch.Element(0, 1, Orb.Types.TYPE1));
        sketchElems.add(new Sketch.Element(0, 2, Orb.Types.TYPE1));
        Assert.assertTrue(sketch.isEqual(sketchElems));

        // Проверка, что sketch НЕ найден.
        sketchElems.clear();
        sketchElems.add(new Sketch.Element(0, 0, Orb.Types.TYPE1));
        sketchElems.add(new Sketch.Element(1, 1, Orb.Types.TYPE1));
        sketchElems.add(new Sketch.Element(0, 2, Orb.Types.TYPE1));
        Assert.assertFalse(sketch.isEqual(sketchElems));

        // Проверка на универсальный тип Orb в элементах.
        sketchElems.clear();
        sketchElems.add(new Sketch.Element(0, 0, Orb.Types.UNIVERSAL));
        sketchElems.add(new Sketch.Element(0, 1, Orb.Types.UNIVERSAL));
        sketchElems.add(new Sketch.Element(0, 2, Orb.Types.UNIVERSAL));
        Assert.assertTrue(sketch.isEqual(sketchElems));

        // Проверка на универсальный тип Orb в sketch.
        sketch.clear();
        sketch.add(0, 0, Orb.Types.UNIVERSAL);
        sketch.add(1, 1, Orb.Types.UNIVERSAL);
        sketch.add(0, 2, Orb.Types.UNIVERSAL);
        sketchElems.clear();
        sketchElems.add(new Sketch.Element(0, 0, Orb.Types.TYPE1));
        sketchElems.add(new Sketch.Element(1, 1, Orb.Types.UNIVERSAL));
        sketchElems.add(new Sketch.Element(0, 2, Orb.Types.TYPE1));
        Assert.assertTrue(sketch.isEqual(sketchElems));
    }
}