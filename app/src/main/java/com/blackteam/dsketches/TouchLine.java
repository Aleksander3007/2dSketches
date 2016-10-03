package com.blackteam.dsketches;

import android.util.Log;

/**
 * Линия при выделении Orbs.
 */
public class TouchLine extends DisplayableObject {
    public TouchLine(Size2 size, Texture texture) {
        super(texture);
    }

    public TouchLine(Orb orb1, Orb orb2, Size2 size, Texture texture) {
        super(texture);
        setPosition(new Vector2(0,0));
        /*setSize(size.width, size.height);
        Vector2 pos = calculatePos(orb1, orb2);
        Log.i("TouchLine.pos.x", String.valueOf(pos.x));
        Log.i("TouchLine.pos.y", String.valueOf(pos.y));
        setPosition(pos);
        setRotationDeg(calculateRotationDeg(orb1, orb2));*/
    }

    public static int getResourceId() {
        return R.drawable.touch_line;
    }

    @Override
    public void dispose() {
        // TODO: Auto-generated method stub
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
                    startOrb.getX() + (startOrb.getWidth() / 2) - (width_ / 2),
                    startOrb.getY() + startOrb.getHeight()  - (height_ / 2)
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
                    startOrb.getX() + (startOrb.getWidth() / 2),
                    startOrb.getY() + (startOrb.getHeight() / 2) - (height_ / 2)
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
