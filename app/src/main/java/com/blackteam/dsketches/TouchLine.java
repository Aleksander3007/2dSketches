package com.blackteam.dsketches;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by Aleksander on 26.09.2016.
 */
public class TouchLine extends DisplayableObject {
    public final static int WIDTH = Orb.WIDTH;
    public final static int HEIGHT = (Orb.WIDTH / 4);

    public TouchLine(Vector2 pos, Bitmap texture) {
        super(pos, texture);
    }

    public TouchLine(Vector2 pos, float rotationDeg, Bitmap texture) {
        super(pos, rotationDeg, texture);
    }

    public TouchLine(Orb orb1, Orb orb2, Bitmap texture) {
        super(new Vector2(0, 0), 0, texture);

        this.pos_ = calculatePos(orb1, orb2);
        this.rotationDeg_ =  calculateRotationDeg(orb1, orb2);
    }

    public void onDraw(Canvas canvas)
    {
        canvas.drawBitmap(texture_, getX(), getY(), null);
        //canvas.rotate(rotationDeg_);
        //canvas.restoreToCount(1);
    }

    @Override
    public void dispose() {
        // TODO: Auto-generated method stub
    }

    @Override
    public int getWidth() {
        return TouchLine.WIDTH; /* (Orb.WIDTH / 2) + (Orb.WIDTH / 2) */
    }

    @Override
    public int getHeight() {
        return TouchLine.HEIGHT;
    }

    private Vector2 calculatePos(Orb orb1, Orb orb2) {
        Orb startOrb;
        // Если выделение идёт снизу вверх.
        if (orb2.getY() != orb1.getY()) {
            // Если выделение идёт снизу вверх.
            if (orb2.getY() > orb1.getY()) {
                Log.i("World.hit", "bottom to top");
                startOrb = orb1;
            }
            // Если выделение идёт сверху вниз.
            else {
                Log.i("World.hit", "top to bottom");
                startOrb = orb2;
            }

            return new Vector2(
                    startOrb.getX() + (Orb.WIDTH / 2) - (TouchLine.WIDTH / 2),
                    startOrb.getY() + Orb.HEIGHT  - (TouchLine.HEIGHT / 2)
            );
        }
        else {
            // Если выделение идёт слево направо.
            if (orb2.getX() > orb1.getX()) {
                Log.i("World.hit", "left to right");
                startOrb = orb1;
            }
            // Если выделение идёт справо налево.
            else {
                Log.i("World.hit", "right to left");
                startOrb = orb2;
            }

            return new Vector2(
                    startOrb.getX() + (Orb.WIDTH / 2),
                    startOrb.getY() + (Orb.HEIGHT / 2) - (TouchLine.HEIGHT / 2)
            );
        }
    }

    private float calculateRotationDeg(Orb orb1, Orb orb2) {
        // Если выделение идёт снизу вверх.
        if (orb2.getY() != orb1.getY()) {
            return 90;
        }
        else {
            return 0;
        }
    }
}
