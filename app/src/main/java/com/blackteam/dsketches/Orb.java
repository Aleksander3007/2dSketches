package com.blackteam.dsketches;

public class Orb extends DisplayableObject {
    private OrbType type_;
    private OrbSpecType specType_;
    private int rowNo_;
    private int colNo_;

    public Orb(OrbType orbType, OrbSpecType orbSpecType, Vector2 pos,
               int rowNo, int colNo, Texture texture) {
        this(pos, texture);
        this.type_ = orbType;
        this.specType_ = orbSpecType;
        this.rowNo_ = rowNo;
        this.colNo_ = colNo;
    }

    public boolean hit(Vector2 coords) {
        return ((coords.x >= pos_.x) && (coords.x <= (pos_.x + width_))) &&
                ((coords.y >= pos_.y) && (coords.y <= (pos_.y + height_)));
    }

    public int getColNo() {
        return colNo_;
    }

    public int getRowNo() {
        return rowNo_;
    }

    public OrbType getType() {
        return this.type_;
    }

    public void setType(OrbType orbType) {
        this.type_ = orbType;
    }

    public OrbSpecType getSpecType() {
        return this.specType_;
    }

    public static int getResourceId(OrbType type, OrbSpecType specType) {
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

    @Override
    public void dispose() {

    }

    private Orb(Vector2 pos, Texture texture) {
        super(pos, texture);
    }
}
