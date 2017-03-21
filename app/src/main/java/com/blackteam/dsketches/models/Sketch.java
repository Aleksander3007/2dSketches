package com.blackteam.dsketches.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.blackteam.dsketches.models.gamedots.GameDot;

import java.io.Serializable;
import java.util.ArrayList;

public class Sketch implements Serializable {
    public static class Element implements Serializable {
        private int mRowNo;
        private int mColNo;
        private GameDot.Types mDotType;

        public Element(int rowNo, int colNo, GameDot.Types dotType) {
            this.mRowNo = rowNo;
            this.mColNo = colNo;
            this.mDotType = dotType;
        }

        public int getRowNo() { return mRowNo; }
        public int getColNo() { return mColNo; }
        public GameDot.Types getDotType() { return mDotType; }

        public boolean isEqual(Sketch.Element elem) {
            boolean isRowEqual = (mRowNo == elem.getRowNo());
            boolean isColEqual = (mColNo == elem.getColNo());
            boolean isDotTypeEqual = (
                    (mDotType == elem.getDotType()) ||
                    (mDotType == GameDot.Types.UNIVERSAL) ||
                    (elem.getDotType() == GameDot.Types.UNIVERSAL)
            );

            if (isRowEqual && isColEqual && isDotTypeEqual) {
                return true;
            }
            else
                return false;
        }
    }

    private String mName;
    private ArrayList<Element> mElements = new ArrayList<>();
    private int mCost;

    public Sketch(String name, int cost) {
        this.mName = name;
        this.mCost = cost;
    }

    public String getName() {
        return mName;
    }

    public int getCost() {
        return mCost;
    }

    public void add(int rowNo, int colNo, GameDot.Types dotType) {
        mElements.add(new Element(rowNo, colNo, dotType));
    }

    public void clear() {
        mElements.clear();
    }

    public boolean isEqual(ArrayList<Sketch.Element> dotElems) {
        if (dotElems.size() != mElements.size())
            return false;

        // Max кол-во проходов = SUM(orbElems_.size() - i), где (i = [0..mElements.size()]).
        boolean isElemFound;
        for (Element dotElem : dotElems) {
            isElemFound = false;
            for (Element sketchElem : mElements) {
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
        for (Element dotElem : mElements) {
            if (dotElem.getRowNo() == rowNo && dotElem.getColNo() == colNo) {
                isSketchDot = true;
                break;
            }
        }
        return isSketchDot;
    }
}
