package com.blackteam.dsketches;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.blackteam.dsketches.Utils.Vector2;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class WorldTest {

    private class WorldTestClass extends World {

        public WorldTestClass() {
            super();
        }

        public void addSelectedOrbs(ArrayList<Orb> selectedOrb) {
            setSelectedOrbs(selectedOrb);
        }
    }

    public int getProfit(WorldTestClass worldTestClass, ArrayList<Orb> selectedOrb) {
        worldTestClass.addSelectedOrbs(selectedOrb);
        worldTestClass.update();
        int profit = worldTestClass.getProfitByOrbs();
        worldTestClass.removeSelection();
        return profit;
    }

    @Test
    public void testGetProfitByOrbs() {
        WorldTestClass worldTestClass = new WorldTestClass();
        ArrayList<Orb> selectedOrb = new ArrayList<Orb>();
        int profit = -1;

        Vector2 facePos = new Vector2(0, 0);
        Bitmap fakedBitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_4444);
        Texture fakedTexture = new Texture(fakedBitmap);

        final int ORB_COST = 10;

        // Проверка, что при выделении меньше трёх, profit = 0.
        selectedOrb.clear();
        selectedOrb.add(new Orb(Orb.Types.TYPE1, Orb.SpecTypes.NONE, facePos, 0, 0, fakedTexture));
        selectedOrb.add(new Orb(Orb.Types.TYPE1, Orb.SpecTypes.NONE, facePos, 1, 0, fakedTexture));
        profit = getProfit(worldTestClass, selectedOrb);
        Assert.assertEquals("Выделено меньше трёх", 0, profit);

        // Проверка, когда выделены три.
        for (Orb.Types orbType : Orb.Types.values()) {
            int rowNo = (int) (World.DEFAULT_NUM_ROWS * Math.random());
            selectedOrb.clear();
            selectedOrb.add(new Orb(orbType, Orb.SpecTypes.NONE, facePos, rowNo, 0, fakedTexture));
            selectedOrb.add(new Orb(orbType, Orb.SpecTypes.NONE, facePos, rowNo + 1, 0, fakedTexture));
            selectedOrb.add(new Orb(orbType, Orb.SpecTypes.NONE, facePos, rowNo + 2, 0, fakedTexture));
            profit = getProfit(worldTestClass, selectedOrb);
            Assert.assertEquals("Выделено три Orb типа " + orbType.toString(), 3 * ORB_COST, profit);
        }

        // Проверка, когда выделены три разных.
        selectedOrb.clear();
        selectedOrb.add(new Orb(Orb.Types.TYPE1, Orb.SpecTypes.NONE, facePos, 0, 0, fakedTexture));
        selectedOrb.add(new Orb(Orb.Types.TYPE2, Orb.SpecTypes.NONE, facePos, 1, 0, fakedTexture));
        selectedOrb.add(new Orb(Orb.Types.TYPE2, Orb.SpecTypes.NONE, facePos, 2, 0, fakedTexture));
        profit = getProfit(worldTestClass, selectedOrb);
        Assert.assertEquals("Выделено три Orb разного типа", 0, profit);

        // Проверка универсального типа.
        selectedOrb.clear();
        selectedOrb.add(new Orb(Orb.Types.TYPE1, Orb.SpecTypes.NONE, facePos, 0, 0, fakedTexture));
        selectedOrb.add(new Orb(Orb.Types.UNIVERSAL, Orb.SpecTypes.NONE, facePos, 1, 0, fakedTexture));
        selectedOrb.add(new Orb(Orb.Types.TYPE1, Orb.SpecTypes.NONE, facePos, 2, 0, fakedTexture));
        profit = getProfit(worldTestClass, selectedOrb);
        Assert.assertEquals("Проверка универсального типа", 3 * ORB_COST, profit);

        // Проверка если не все соседи.
        /*
         * Это учитывается во время выделения элементов.
         */

        // Проверка спец. типов.
        /* Невозможна, т.к. для спец. типов необходимо создать уровень, который содержит Orb для
         * создания которых требуется Context.
         */

        // Проверка с подсчетом sketch.
        selectedOrb.clear();
        selectedOrb.add(new Orb(Orb.Types.TYPE1, Orb.SpecTypes.NONE, facePos, 0, 0, fakedTexture));
        selectedOrb.add(new Orb(Orb.Types.TYPE1, Orb.SpecTypes.NONE, facePos, 0, 1, fakedTexture));
        selectedOrb.add(new Orb(Orb.Types.TYPE1, Orb.SpecTypes.NONE, facePos, 0, 2, fakedTexture));
        selectedOrb.add(new Orb(Orb.Types.TYPE1, Orb.SpecTypes.NONE, facePos, 0, 3, fakedTexture));
        selectedOrb.add(new Orb(Orb.Types.TYPE1, Orb.SpecTypes.NONE, facePos, 0, 4, fakedTexture));
        profit = getProfit(worldTestClass, selectedOrb);
        Assert.assertEquals("Sketch ROW_5", 5 * ORB_COST + 50, profit);
    }
}
