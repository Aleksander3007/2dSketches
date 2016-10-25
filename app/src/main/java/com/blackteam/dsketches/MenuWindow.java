package com.blackteam.dsketches;

import android.util.Log;

import com.blackteam.dsketches.gui.GameButton;
import com.blackteam.dsketches.gui.GameImage;
import com.blackteam.dsketches.gui.ShaderProgram;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;
import com.blackteam.dsketches.windows.MenuManager;
import com.blackteam.dsketches.windows.Window;

public class MenuWindow extends Window {
    private World world_;
    private Game game_;
    private MenuManager menuManager_;

    private GameButton closeButton_;
    private GameButton restartBtn_;
    private GameButton achievementButton_;
    // private GameButton hallOfFameButton_;
    // private GameButton settingButton_;
    private GameButton exitButton_;
    private GameImage backgroundImage_;

    public MenuWindow(World world, Game game, MenuManager menuManager) {
        this.world_ = world;
        this.game_ = game;
        this.menuManager_ = menuManager;

        closeButton_ = new GameButton();
        restartBtn_ = new GameButton();
        achievementButton_ = new GameButton();
        exitButton_ = new GameButton();
    }

    // TODO: Бред, что передаём windowWidth, windowHeight а позицию нет.
    // либо передавть еще позицию, либо вообще убрать размеры.
    @Override
    public void resize(final float windowWidth, final float windowHeight) {
        this.pos_ = new Vector2(0, 0);
        this.size_ = new Size2(windowWidth, windowHeight);

        Size2 btnSize = new Size2(windowWidth / 3f, windowHeight / 5f);

        closeButton_.setPosition(windowWidth - btnSize.width,
                windowHeight - btnSize.height - 3 * 0.01f);

        restartBtn_.setPosition(btnSize.width,
                windowHeight - 2f * btnSize.height + 2 * 0.01f);

        achievementButton_.setPosition(btnSize.width,
                windowHeight - 3f * btnSize.height + 0.01f);

        exitButton_.setPosition(btnSize.width,
                windowHeight - 4f * btnSize.height);

        backgroundImage_.setSize(windowWidth, windowHeight);
        closeButton_.setSize(btnSize);
        restartBtn_.setSize(btnSize);
        achievementButton_.setSize(btnSize);
        exitButton_.setSize(btnSize);
    }

    public void loadContent(ContentManager contents) {
        backgroundImage_ = new GameImage(new Vector2(0, 0),
                contents.get(R.drawable.menu_window)
        );
        closeButton_.setTexture(contents.get(R.drawable.x_close));
        restartBtn_.setTexture(contents.get(R.drawable.restart_btn));
        achievementButton_.setTexture(contents.get(R.drawable.achievement_btn));
        exitButton_.setTexture(contents.get(R.drawable.exit_btn));
    }

    @Override
    public void render(float[] mvpMatrix, final ShaderProgram shader) {
        backgroundImage_.draw(mvpMatrix, shader);
        closeButton_.draw(mvpMatrix, shader);
        restartBtn_.draw(mvpMatrix, shader);
        achievementButton_.draw(mvpMatrix, shader);
        exitButton_.draw(mvpMatrix, shader);
    }

    @Override
    public void touchUpHandle(Vector2 worldCoords) {
        if (closeButton_.hit(worldCoords)) {
            Log.i("MenuWindow", "closeButton is clicked.");
            menuManager_.close(MenuManager.MenuTypes.MAIN);
        }
        else if (restartBtn_.hit(worldCoords)) {
            Log.i("MenuWindow", "restartButton is clicked.");
            world_.createLevel();
            game_.reset();
        }
        else if (achievementButton_.hit(worldCoords)) {
            Log.i("MenuWindow", "achievementButton is clicked.");
            menuManager_.show(MenuManager.MenuTypes.ACHIEVEMENT);
        }
    }
}
