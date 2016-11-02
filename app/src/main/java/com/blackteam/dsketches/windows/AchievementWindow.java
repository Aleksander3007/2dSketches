package com.blackteam.dsketches.windows;

import android.util.Log;

import com.blackteam.dsketches.Achievement;
import com.blackteam.dsketches.ContentManager;
import com.blackteam.dsketches.R;
import com.blackteam.dsketches.gui.DisplayableObject;
import com.blackteam.dsketches.gui.GameButton;
import com.blackteam.dsketches.gui.GameImage;
import com.blackteam.dsketches.gui.Graphics;
import com.blackteam.dsketches.gui.ShaderProgram;
import com.blackteam.dsketches.gui.StaticText;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.util.ArrayList;

public class AchievementWindow extends Window {
    private MenuManager menuManager_;
    private ContentManager contents_;

    private GameButton closeButton_;
    private GameImage backgroundImage_;

    private class AchievementBox extends DisplayableObject {
        private GameImage bgImage_;
        private GameImage mainImage_;
        private StaticText name_;

        public AchievementBox(Achievement achievement) {
            bgImage_ = new GameImage(new Vector2(0, 0),
                    contents_.get(R.drawable.achievement_bg_noactive));
        }
    }

    private ArrayList<AchievementBox> achievementBoxes_ = new ArrayList<>();

    public AchievementWindow(MenuManager menuManager, ArrayList<Achievement> achievements) {
        this.menuManager_ = menuManager;
        closeButton_ = new GameButton();

        for (Achievement achievement : achievements) {
            achievementBoxes_.add(new AchievementBox(achievement));
        }
    }

    @Override
    public void resize(float windowWidth, float windowHeight) {
        this.pos_ = new Vector2(0, 0);
        this.size_ = new Size2(windowWidth, windowHeight);

        Size2 btnSize = new Size2(windowWidth / 3f, windowHeight / 5f);

        closeButton_.setPosition(windowWidth - btnSize.width,
                windowHeight - btnSize.height - 3 * 0.01f);

        for (int iElem = 0; iElem < achievementBoxes_.size(); iElem++) {
            achievementBoxes_.get(iElem).setPosition(0,
                    windowHeight - iElem * (btnSize.height + 0.01f));
            achievementBoxes_.get(iElem).setSize(btnSize);
        }

        backgroundImage_.setSize(windowWidth, windowHeight);
        closeButton_.setSize(btnSize);
    }

    @Override
    public void render(Graphics graphics) {
        backgroundImage_.draw(graphics);
        closeButton_.draw(graphics);
        for (AchievementBox achievementBox : achievementBoxes_) {
            achievementBox.draw(graphics);
        }
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
        this.contents_ = contents;

        backgroundImage_ = new GameImage(new Vector2(0, 0), // TODO: Magic numbers!
                contents.get(R.drawable.achievement_window_bg)
        );
        closeButton_.setTexture(contents.get(R.drawable.x_close));
    }
}
