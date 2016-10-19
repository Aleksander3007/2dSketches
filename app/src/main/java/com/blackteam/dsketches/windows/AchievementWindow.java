package com.blackteam.dsketches.windows;

import android.util.Log;

import com.blackteam.dsketches.ContentManager;
import com.blackteam.dsketches.Loadable;
import com.blackteam.dsketches.R;
import com.blackteam.dsketches.World;
import com.blackteam.dsketches.gui.GameButton;
import com.blackteam.dsketches.gui.GameImage;
import com.blackteam.dsketches.gui.ShaderProgram;
import com.blackteam.dsketches.gui.Texture;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

public class AchievementWindow extends Window {
    private MenuManager menuManager_;

    private GameButton closeButton_;
    private GameImage backgroundImage_;
    private GameImage achievementBgNoActive_;
    private GameImage achievementBgActive_;

    public AchievementWindow(MenuManager menuManager) {
        this.menuManager_ = menuManager;
        closeButton_ = new GameButton();
    }

    @Override
    public void resize(float windowWidth, float windowHeight) {
        this.pos_ = new Vector2(0, 0);
        this.size_ = new Size2(windowWidth, windowHeight);

        Size2 btnSize = new Size2(windowWidth / 3f, windowHeight / 5f);

        closeButton_.setPosition(windowWidth - btnSize.width,
                windowHeight - btnSize.height - 3 * 0.01f);

        backgroundImage_.setSize(windowWidth, windowHeight);
        closeButton_.setSize(btnSize);
    }

    @Override
    public void render(float[] mvpMatrix, ShaderProgram shader) {
        backgroundImage_.draw(mvpMatrix, shader);
        closeButton_.draw(mvpMatrix, shader);
    }

    @Override
    public void touchUpHandle(Vector2 worldCoords) {
        if (closeButton_.hit(worldCoords)) {
            Log.i("AchievementWindow", "closeButton is clicked.");
            menuManager_.close(MenuManager.MenuTypes.ACHIEVEMENT);
        }
    }

    @Override
    public void loadContent(ContentManager contents) {
        backgroundImage_ = new GameImage(new Vector2(0, 0), // TODO: Magic numbers!
                contents.get(R.drawable.achievement_window_bg)
        );
        closeButton_.setTexture(contents.get(R.drawable.x_close));
    }
}
