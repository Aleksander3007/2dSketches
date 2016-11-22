package com.blackteam.dsketches;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.Serializable;
import java.util.ArrayList;

public class Sketch implements Serializable {
    public static class Element implements Serializable {
        private int rowNo_;
        private int colNo_;
        private GameDot.Types dotType_;

        public Element(int rowNo, int colNo, GameDot.Types dotType) {
            this.rowNo_ = rowNo;
            this.colNo_ = colNo;
            this.dotType_ = dotType;
        }

        public int getRowNo() { return rowNo_; }
        public int getColNo() { return colNo_; }
        public GameDot.Types getDotType() { return dotType_; }

        public boolean isEqual(Sketch.Element elem) {
            boolean isRowEqual = (rowNo_ == elem.getRowNo());
            boolean isColEqual = (colNo_ == elem.getColNo());
            boolean isDotTypeEqual = (
                    (dotType_ == elem.getDotType()) ||
                    (dotType_ == GameDot.Types.UNIVERSAL) ||
                    (elem.getDotType() == GameDot.Types.UNIVERSAL)
            );

            if (isRowEqual && isColEqual && isDotTypeEqual) {
                return true;
            }
            else
                return false;
        }
    }

    private String name_;
    private ArrayList<Element> elements_ = new ArrayList<>();
    private int cost_;

    public Sketch(String name, int cost) {
        this.name_ = name;
        this.cost_ = cost;
    }

    public String getName() {
        return name_;
    }

    public int getCost() {
        return cost_;
    }

    public void add(int rowNo, int colNo, GameDot.Types dotType) {
        elements_.add(new Element(rowNo, colNo, dotType));
    }

    public void clear() {
        elements_.clear();
    }

    public boolean isEqual(ArrayList<Sketch.Element> dotElems) {
        if (dotElems.size() != elements_.size())
            return false;

        // Max кол-во проходов = SUM(orbElems_.size() - i), где (i = [0..elements_.size()]).
        boolean isElemFound;
        for (Element dotElem : dotElems) {
            isElemFound = false;
            for (Element sketchElem : elements_) {
                if (dotElem.isEqual(sketchElem)) {
                    isElemFound = true;
                    break;
                }
            }
            if (!isElemFound) return false;
        }

        return true;
    }

    public static Sketch getNullSketch() {
        return new Sketch("null", 0);
    }

    public Bitmap getImage(final int bitmapSize) {
        Paint noActivePaint  = new Paint();
        noActivePaint.setColor(0xFFDDDDDD);
        Paint activePaint = new Paint();
        activePaint.setColor(Color.BLACK);

        Bitmap bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_4444);
        bitmap.eraseColor(0);
        Canvas canvas = new Canvas(bitmap);

        final int MAX_ELEM_ = 6;
        final float radius = (float) bitmapSize / (2f * MAX_ELEM_);
        for (int iRow = 0; iRow < MAX_ELEM_; iRow++) {
            for (int iCol = 0; iCol < MAX_ELEM_; iCol++) {
                if (isSketchDot(iRow, iCol))
                    canvas.drawCircle(radius + 2 * radius * iCol,
                            radius + 2 * radius * (MAX_ELEM_ - 1 - iRow),
                        radius, activePaint);
                else
                    canvas.drawCircle(radius + 2 * radius * iCol,
                            radius + 2 * radius * (MAX_ELEM_ - 1 - iRow),
                            radius, noActivePaint);
            }
        }
        return bitmap;
    }

    private boolean isSketchDot(final int rowNo, final int colNo) {
        boolean isSketchDot = false;
        for (Element dotElem : elements_) {
            if (dotElem.getRowNo() == rowNo && dotElem.getColNo() == colNo) {
                isSketchDot = true;
                break;
            }
        }
        return isSketchDot;
    }
}
