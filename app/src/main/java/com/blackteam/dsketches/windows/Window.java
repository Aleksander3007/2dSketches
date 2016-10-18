package com.blackteam.dsketches.windows;

import com.blackteam.dsketches.Loadable;
import com.blackteam.dsketches.gui.ShaderProgram;
import com.blackteam.dsketches.utils.Vector2;

public abstract class Window implements Loadable {
    protected boolean isVisible_;

    public abstract void resize(final float windowWidth, final float windowHeight);
    public abstract void render(float[] mvpMatrix, final ShaderProgram shader);
    public abstract void touchUp(Vector2 worldCoords);
    public void show() { setVisible(); }
    public boolean isVisible() { return isVisible_; }
    public void setVisible() { isVisible_ = true; }
    public void setInvisible() { isVisible_ = false; }
}
