package com.blackteam.dsketches;

import android.support.test.runner.AndroidJUnit4;

import com.blackteam.dsketches.models.gamedots.GameDot;
import com.blackteam.dsketches.managers.ContentManager;
import com.blackteam.dsketches.utils.Vector2;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

@RunWith(AndroidJUnit4.class)
public class WorldTest {

    private ContentManager fakeContents_ = new ContentManager(null);

    private class WorldTestClass extends World {

        public WorldTestClass() {
            super(fakeContents_, null);
        }

        public void addSelectedOrbs(Set<GameDot> selectedGameDot) {
            setSelectedDots(selectedGameDot);
        }
    }

    public int getProfit(WorldTestClass worldTestClass, Set<GameDot> selectedGameDot) {
        worldTestClass.addSelectedOrbs(selectedGameDot);
        worldTestClass.update();
        int profit = worldTestClass.getProfitByDots();
        worldTestClass.removeSelection();
        return profit;
    }

    @Test
    public void testGetProfitByOrbs() {
        WorldTestClass worldTestClass = new WorldTestClass();
        Set<GameDot> selectedGameDot = new HashSet<>();
        int profit = -1;

        Vector2 fakePos = new Vector2(0, 0);
        //Bitmap fakedBitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_4444);
        //Texture fakedTexture = new Texture(fakedBitmap);

        final int ORB_COST = 10;

        // Проверка, что при выделении меньше трёх, profit = 0.
        selectedGameDot.clear();
        selectedGameDot.add(new GameDot(GameDot.Types.TYPE1, GameDot.SpecTypes.NONE, fakePos, 0, 0, fakeContents_));
        selectedGameDot.add(new GameDot(GameDot.Types.TYPE1, GameDot.SpecTypes.NONE, fakePos, 1, 0, fakeContents_));
        profit = getProfit(worldTestClass, selectedGameDot);
        Assert.assertEquals("Выделено меньше трёх", 0, profit);

        // Проверка, когда выделены три.
        for (GameDot.Types orbType : GameDot.Types.values()) {
            int rowNo = (int) (World.DEFAULT_NUM_ROWS * Math.random());
            selectedGameDot.clear();
            selectedGameDot.add(new GameDot(orbType, GameDot.SpecTypes.NONE, fakePos, rowNo, 0, fakeContents_));
            selectedGameDot.add(new GameDot(orbType, GameDot.SpecTypes.NONE, fakePos, rowNo + 1, 0, fakeContents_));
            selectedGameDot.add(new GameDot(orbType, GameDot.SpecTypes.NONE, fakePos, rowNo + 2, 0, fakeContents_));
            profit = getProfit(worldTestClass, selectedGameDot);
            Assert.assertEquals("Выделено три GameDot типа " + orbType.toString(), 3 * ORB_COST, profit);
        }

        // Проверка, когда выделены три разных. - Не может быть, т.к. теперь проверяется при выделении.
        /*selectedGameDot.clear();
        selectedGameDot.add(new GameDot(GameDot.Types.TYPE1, GameDot.SpecTypes.NONE, fakePos, 0, 0, fakeContents_));
        selectedGameDot.add(new GameDot(GameDot.Types.TYPE2, GameDot.SpecTypes.NONE, fakePos, 1, 0, fakeContents_));
        selectedGameDot.add(new GameDot(GameDot.Types.TYPE2, GameDot.SpecTypes.NONE, fakePos, 2, 0, fakeContents_));
        profit = getProfit(worldTestClass, selectedGameDot);
        Assert.assertEquals("Выделено три GameDot разного типа", 0, profit);*/

        // Проверка универсального типа.
        selectedGameDot.clear();
        selectedGameDot.add(new GameDot(GameDot.Types.TYPE1, GameDot.SpecTypes.NONE, fakePos, 0, 0, fakeContents_));
        selectedGameDot.add(new GameDot(GameDot.Types.UNIVERSAL, GameDot.SpecTypes.NONE, fakePos, 1, 0, fakeContents_));
        selectedGameDot.add(new GameDot(GameDot.Types.TYPE1, GameDot.SpecTypes.NONE, fakePos, 2, 0, fakeContents_));
        profit = getProfit(worldTestClass, selectedGameDot);
        Assert.assertEquals("Проверка универсального типа", 3 * ORB_COST, profit);

        // Проверка если не все соседи.
        /*
         * Это учитывается во время выделения элементов.
         */

        // Проверка спец. типов.
        /* Невозможна, т.к. для спец. типов необходимо создать уровень, который содержит GameDot для
         * создания которых требуется Context.
         */

        // Проверка с подсчетом sketch.
        selectedGameDot.clear();
        selectedGameDot.add(new GameDot(GameDot.Types.TYPE1, GameDot.SpecTypes.NONE, fakePos, 0, 0, fakeContents_));
        selectedGameDot.add(new GameDot(GameDot.Types.TYPE1, GameDot.SpecTypes.NONE, fakePos, 0, 1, fakeContents_));
        selectedGameDot.add(new GameDot(GameDot.Types.TYPE1, GameDot.SpecTypes.NONE, fakePos, 0, 2, fakeContents_));
        selectedGameDot.add(new GameDot(GameDot.Types.TYPE1, GameDot.SpecTypes.NONE, fakePos, 0, 3, fakeContents_));
        selectedGameDot.add(new GameDot(GameDot.Types.TYPE1, GameDot.SpecTypes.NONE, fakePos, 0, 4, fakeContents_));
        profit = getProfit(worldTestClass, selectedGameDot);
        Assert.assertEquals("Sketch ROW_5", 5 * ORB_COST + 50, profit);
    }
}
