package com.blackteam.dsketches;

import android.content.Context;

public class MenuWindow {
     private GameButton closeButton_;
     private GameButton restartBtn_;
    // private GameButton achievementButton_;
    // private GameButton settingButton_;
    // private GameButton exitButton_;
    private GameImage backgroundImage_;


    private boolean isVisible_;

    // TODO: Бред, что передаём windowWidth, windowHeight а позицию нет.
    // либо передавть еще позицию, либо вообще убрать размеры.
    public void init(Context context, final float windowWidth, final float windowHeight) {
        backgroundImage_ = new GameImage(new Vector2(0,0),
                new Texture(context, R.drawable.menu_window)
        );

        closeButton_ = new GameButton(new Vector2(windowWidth - windowWidth / 3f,
                windowHeight - windowHeight / 5f),
                new Texture(context, R.drawable.x_close)
        );

        restartBtn_ = new GameButton(new Vector2(windowWidth / 3f,
                windowHeight - 2f * windowHeight / 5f),
                new Texture(context, R.drawable.restart_btn)
        );

        backgroundImage_.setSize(windowWidth, windowHeight);
        closeButton_.setSize(windowWidth / 3f, windowHeight / 5f);
        restartBtn_.setSize(windowWidth / 3f, windowHeight / 5f);
    }

    public void render(float[] mvpMatrix, final ShaderProgram shader) {
        if (isVisible_) {
            backgroundImage_.draw(mvpMatrix, shader);
            closeButton_.draw(mvpMatrix, shader);
            restartBtn_.draw(mvpMatrix, shader);
        }
    }

    public boolean isVisible() { return isVisible_; }
    public void setVisible() { isVisible_ = true; }
    public void setInvisible() { isVisible_ = false; }

    public void touchUp(Vector2 worldCoords) {
        if (closeButton_.hit(worldCoords)) {
            this.setInvisible();
        }
    }
}
