package com.blackteam.dsketches.windows;

import android.util.Log;

import com.blackteam.dsketches.AchievementsManager;
import com.blackteam.dsketches.ContentManager;
import com.blackteam.dsketches.Game;
import com.blackteam.dsketches.MenuWindow;
import com.blackteam.dsketches.Player;
import com.blackteam.dsketches.World;
import com.blackteam.dsketches.gui.Graphics;
import com.blackteam.dsketches.gui.ShaderProgram;
import com.blackteam.dsketches.utils.Vector2;

import java.util.TreeMap;

/**
 * Управление всеми окнами.
 */
public class MenuManager {
    public enum MenuTypes {
        MAIN,
        ACHIEVEMENT
    }

    // TODO: Тут ArrayMap<Menu.Types, Menu> menus; - Упорядоченный список по слоям.
    private TreeMap<MenuTypes, Window> menus_ = new TreeMap<>();
    private MenuWindow mainMenu_;
    private AchievementWindow achievementMenu_;
    private Game game_;
    private World world_;
    private ContentManager contents_;
    private AchievementsManager achievementManager_;

    private float menuWidth_;
    private float menuHeight_;

    // TODO: По идеи, при вызове show() должен создаваться объект конкретного меню,
    /*
        show() {
            new <конкретный-объект>(); или loadContent?
            <конкретный-объект>.resize();

        }
     */

    public MenuManager(Game game, World world, Player player, AchievementsManager achievementManager) {
        this.game_ = game;
        this.world_ = world;
        this.achievementManager_ = achievementManager;
        /*
        mainMenu_ = new MenuWindow(world, game, this);
        achievementMenu_ = new AchievementWindow();
        achievementMenu_.setInvisible();
        mainMenu_.setInvisible();*/

        //menus_.put(MenuTypes.MAIN, mainMenu_);
        //menus_.put(MenuTypes.ACHIEVEMENT, achievementMenu_);
    }

    public void setContent(ContentManager contents) {
        this.contents_ = contents;
    }

    public void resizeMenus(final float menuWidth, final float menuHeight) {
        menuWidth_ = menuWidth;
        menuHeight_ = menuHeight;
    }

    public void renderMenus(Graphics graphics) {
        for (Window menu : menus_.values()) {
            menu.render(graphics);
        }
    }

    public boolean menusTouchUpHandle(Vector2 worldCoords) {
        for (Window menu : menus_.descendingMap().values()) {
            if (menu.hit(worldCoords)) {
                menu.touchUpHandle(worldCoords);
                return true;
            }
        }

        return false;
    }

    public void show(final MenuTypes menuType) {
        Window menu = createMenu(menuType);
        menu.loadContent(contents_);
        menu.resize(menuWidth_, menuHeight_);
        menus_.put(menuType, menu);
        Log.i("MenuManager", "show");
    }

    public void close(final MenuTypes menuType) {
        if (menus_.containsKey(menuType))
            menus_.remove(menuType);
    }

    public Window createMenu(final MenuTypes menuType) {
        switch (menuType) {
            case MAIN:
                return new MenuWindow(world_, game_, this);
            case ACHIEVEMENT:
                return new AchievementWindow(this, achievementManager_.getAchiviements());
            default:
                return null;
        }
    }
}
