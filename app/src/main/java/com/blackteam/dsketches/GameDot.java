package com.blackteam.dsketches;

import com.blackteam.dsketches.gui.DisplayableObject;
import com.blackteam.dsketches.gui.Texture;
import com.blackteam.dsketches.utils.Vector2;

public class GameDot extends DisplayableObject {
    public enum Types {
        TYPE1,
        TYPE2,
        TYPE3,
        UNIVERSAL
    }

    public enum SpecTypes {
        NONE,
        DOUBLE,
        TRIPLE,
        AROUND_EATER,
        ROWS_EATER,
        COLUMNS_EATER
    }

    private GameDot.Types type_;
    private GameDot.SpecTypes specType_;
    private int rowNo_;
    private int colNo_;

    public GameDot(GameDot.Types dotType, GameDot.SpecTypes dotSpecType, Vector2 pos,
                   int rowNo, int colNo, Texture texture) {
        this(pos, texture);
        this.type_ = dotType;
        this.specType_ = dotSpecType;
        this.rowNo_ = rowNo;
        this.colNo_ = colNo;
    }

    public int getColNo() {
        return colNo_;
    }

    public int getRowNo() {
        return rowNo_;
    }

    public GameDot.Types getType() {
        return this.type_;
    }

    public void setType(GameDot.Types dotType) {
        this.type_ = dotType;
    }

    public GameDot.SpecTypes getSpecType() {
        return this.specType_;
    }

    public static int getResourceId(GameDot.Types type, GameDot.SpecTypes specType) {
        switch (type) {
            case TYPE1:
                switch (specType) {
                    case NONE:
                        return R.drawable.blue_orb_3;
                    case DOUBLE:
                        return R.drawable.blue_orb_3;
                    case TRIPLE:
                        return R.drawable.blue_orb_3;
                    case AROUND_EATER:
                        return R.drawable.blue_orb_3;
                    case ROWS_EATER:
                        return R.drawable.blue_orb_3;
                    case COLUMNS_EATER:
                        return R.drawable.blue_orb_3;
                }
            case TYPE2:
                switch (specType) {
                    case NONE:
                        return R.drawable.green_bubble_3;
                    case DOUBLE:
                        return R.drawable.green_bubble_3;
                    case TRIPLE:
                        return R.drawable.green_bubble_3;
                    case AROUND_EATER:
                        return R.drawable.green_bubble_3;
                    case ROWS_EATER:
                        return R.drawable.green_bubble_3;
                    case COLUMNS_EATER:
                        return R.drawable.green_bubble_3;
                }
            case TYPE3:
                switch (specType) {
                    case NONE:
                        return R.drawable.red_orb;
                    case DOUBLE:
                        return R.drawable.red_double_orb;
                    case TRIPLE:
                        return R.drawable.red_orb;
                    case AROUND_EATER:
                        return R.drawable.red_orb;
                    case ROWS_EATER:
                        return R.drawable.red_rows_eater_orb;
                    case COLUMNS_EATER:
                        return R.drawable.red_orb;
                }
            case UNIVERSAL:
                switch (specType) {
                    case NONE:
                        return R.drawable.universal;
                    case DOUBLE:
                        return R.drawable.universal;
                    case TRIPLE:
                        return R.drawable.universal;
                    case AROUND_EATER:
                        return R.drawable.universal;
                    case ROWS_EATER:
                        return R.drawable.universal;
                    case COLUMNS_EATER:
                        return R.drawable.universal;
                }
        }

        return R.drawable.universal;
    }

    public void setSize(float size) {
        setSize(size, size);
    }

    private GameDot(Vector2 pos, Texture texture) {
        super(pos, texture);
    }
}
