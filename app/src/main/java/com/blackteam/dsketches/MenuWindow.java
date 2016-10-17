package com.blackteam.dsketches;

import android.content.Context;
import android.util.Log;

import com.blackteam.dsketches.gui.GameButton;
import com.blackteam.dsketches.gui.GameImage;
import com.blackteam.dsketches.gui.ShaderProgram;
import com.blackteam.dsketches.gui.Texture;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

public class MenuWindow {
    private World world_;
    private MainWindow mainWindow_;

    private GameButton closeButton_;
    private GameButton restartBtn_;
    private GameButton achievementButton_;
    // private GameButton hallOfFameButton_;
    // private GameButton settingButton_;
    private GameButton exitButton_;
    private GameImage backgroundImage_;


    private boolean isVisible_;

    public MenuWindow(World world, MainWindow mainWindow) {
        this.world_ = world;
        this.mainWindow_ = mainWindow;

        closeButton_ = new GameButton();
        restartBtn_ = new GameButton();
        achievementButton_ = new GameButton();
        exitButton_ = new GameButton();
    }

    // TODO: Бред, что передаём windowWidth, windowHeight а позицию нет.
    // либо передавть еще позицию, либо вообще убрать размеры.
    public void init(final float windowWidth, final float windowHeight) {


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

    public void render(float[] mvpMatrix, final ShaderProgram shader) {
        if (isVisible_) {
            backgroundImage_.draw(mvpMatrix, shader);
            closeButton_.draw(mvpMatrix, shader);
            restartBtn_.draw(mvpMatrix, shader);
            achievementButton_.draw(mvpMatrix, shader);
            exitButton_.draw(mvpMatrix, shader);
        }
    }

    public boolean isVisible() { return isVisible_; }
    public void setVisible() { isVisible_ = true; }
    public void setInvisible() { isVisible_ = false; }

    public void touchUp(Vector2 worldCoords) {
        if (closeButton_.hit(worldCoords)) {
            Log.i("MenuWindow", "closeButton is clicked.");
            setInvisible();
        }
        else if (restartBtn_.hit(worldCoords)) {
            Log.i("MenuWindow", "restartButton is clicked.");
            world_.createLevel();
            mainWindow_.reset();
        }
    }

    public void show() {
        setVisible();
    }

}
