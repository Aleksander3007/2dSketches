package com.blackteam.dsketches;

import android.util.Log;

import com.blackteam.dsketches.gui.DisplayableObject;
import com.blackteam.dsketches.gui.Texture;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

/**
 * Линия при выделении игровых точек.
 */
public class TouchLine extends DisplayableObject {
    public TouchLine(Size2 size, Texture texture) {
        super(texture);
    }

    public TouchLine(GameDot gameDot1, GameDot gameDot2, Size2 size, ContentManager contents) {
        super(contents.get(R.drawable.touch_line));
        setSize(size.width, size.height);
        setPosition(calculatePos(gameDot1, gameDot2));
        setRotationDeg(calculateRotationDeg(gameDot1, gameDot2));
    }

    private Vector2 calculatePos(GameDot gameDot1, GameDot gameDot2) {
        GameDot startGameDot;
        // Если выделение по вертикали.
        if (gameDot2.getY() != gameDot1.getY()) {
            // Если выделение идёт снизу вверх.
            if (gameDot2.getY() > gameDot1.getY()) {
                Log.i("World.hit", "bottom to top");
                startGameDot = gameDot1;
            }
            // Если выделение идёт сверху вниз.
            else {
                Log.i("World.hit", "top to bottom");
                startGameDot = gameDot2;
            }

            return new Vector2(
                    startGameDot.getX() + (startGameDot.getWidth() / 2) + (height_ / 2),
                    startGameDot.getY() + (startGameDot.getHeight() / 2)
            );
        }
        // По горизонтали.
        else {
            // Если выделение идёт слево направо.
            if (gameDot2.getX() > gameDot1.getX()) {
                Log.i("World.hit", "left to right");
                startGameDot = gameDot1;
            }
            // Если выделение идёт справо налево.
            else {
                Log.i("World.hit", "right to left");
                startGameDot = gameDot2;
            }

            return new Vector2(
                    startGameDot.getX() + (startGameDot.getWidth() / 2),
                    startGameDot.getY() + (startGameDot.getHeight() / 2) - (height_ / 2)
            );
        }
    }

    private float calculateRotationDeg(GameDot gameDot1, GameDot gameDot2) {
        // Если выделение идёт снизу вверх.
        if (gameDot2.getY() != gameDot1.getY()) {
            return 90;
        }
        else {
            return 0;
        }
    }
}
