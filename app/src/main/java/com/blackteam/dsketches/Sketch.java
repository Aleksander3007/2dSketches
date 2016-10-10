package com.blackteam.dsketches;

import android.util.Log;

import java.util.ArrayList;

// TODO: Перенести в отдельный файл.
public class Sketch {
    public enum Types {
        NONE,
        ROW_3
    }

    public static class Element {
        private int rowNo_;
        private int colNo_;
        private Orb.Types orbType_;

        public Element(int rowNo, int colNo, Orb.Types orbType) {
            this.rowNo_ = rowNo;
            this.colNo_ = colNo;
            this.orbType_ = orbType;
        }

        public int getRowNo() { return rowNo_; }
        public int getColNo() { return colNo_; }
        public Orb.Types getOrbType() { return orbType_; }

        public boolean isEqual(Sketch.Element elem) {
            boolean isRowEqual = (rowNo_ == elem.getRowNo());
            boolean isColEqual = (colNo_ == elem.getColNo());
            boolean isOrbTypeEqual = (
                    (orbType_ == elem.getOrbType()) ||
                    (orbType_ == Orb.Types.UNIVERSAL) ||
                    (elem.getOrbType() == Orb.Types.UNIVERSAL)
            );

            if (isRowEqual && isColEqual && isOrbTypeEqual) {
                return true;
            }
            else
                return false;
        }
    }

    private Sketch.Types type_;
    private ArrayList<Element> elements = new ArrayList<>();
    private int cost_;

    public Sketch(Sketch.Types type, int cost) {
        this.type_ = type;
        this.cost_ = cost;
    }

    public Sketch.Types getType() {
        return type_;
    }

    public int getCost() {
        return cost_;
    }

    public void add(int rowNo, int colNo, Orb.Types orbType) {
        elements.add(new Element(rowNo, colNo, orbType));
    }

    public void clear() {
        elements.clear();
    }

    public boolean isEqual(ArrayList<Sketch.Element> orbElems_) {
        if (orbElems_.size() != elements.size())
            return false;

        // Max кол-во проходов = SUM(orbElems_.size() - i), где (i = [0..elements.size()]).
        boolean isElemFound;
        for (Element orbElem : orbElems_) {
            isElemFound = false;
            for (Element sketchElem : elements) {
                if (orbElem.isEqual(sketchElem)) {
                    isElemFound = true;
                    break;
                }
            }
            if (!isElemFound) return false;
        }

        return true;
    }

    public static Sketch getNullSketch() {
        return new Sketch(Sketch.Types.NONE, 0);
    }
}
