package com.blackteam.dsketches.Utils;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;

import com.blackteam.dsketches.Orb;
import com.blackteam.dsketches.Sketch;
import com.blackteam.dsketches.SketchesManager;
import com.blackteam.dsketches.Texture;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;


@RunWith(AndroidJUnit4.class)
public class SketchesManagerTest {

    private SketchesManager sketchesManager_;

    @Before
    public void setUp() throws Exception {
        sketchesManager_ = new SketchesManager();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testFindSketch() throws Exception {
        ArrayList<Orb> orbs = new ArrayList<>();
        Vector2 orbPos = new Vector2(0f, 0f);
        Orb.Types orbType = Orb.Types.TYPE1;
        Orb.SpecTypes orbSpecType = Orb.SpecTypes.NONE;

        Bitmap fakeBitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_4444);
        Texture fakeTexture = new Texture(fakeBitmap);

        orbs.add(new Orb(orbType, orbSpecType, orbPos, 0, 0, fakeTexture));
        orbs.add(new Orb(orbType, orbSpecType, orbPos, 0, 1, fakeTexture));
        orbs.add(new Orb(orbType, orbSpecType, orbPos, 0, 2, fakeTexture));

        // Проверка, что sketch найден.
        Sketch sketch = sketchesManager_.findSketch(orbs);
        org.junit.Assert.assertEquals(Sketch.Types.ROW_3, sketch.getType());

        // Проверка, что sketch НЕ найден.
        orbs.add(new Orb(orbType, orbSpecType, orbPos, 3, 0, fakeTexture));
        sketch = sketchesManager_.findSketch(orbs);
        org.junit.Assert.assertEquals(Sketch.Types.NONE, sketch.getType());

        orbs.clear();
    }
}