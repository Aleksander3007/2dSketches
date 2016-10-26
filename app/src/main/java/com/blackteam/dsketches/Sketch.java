package com.blackteam.dsketches;

import java.util.ArrayList;

public class Sketch {
    // TODO: Должен тип браться из файла, а не жестко здесь зашит.
    public enum Types {
        NONE,
        ROW_3, // TODO: Удалить.
        ROW_5
    }

    public static class Element {
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

    public void add(int rowNo, int colNo, GameDot.Types dotType) {
        elements.add(new Element(rowNo, colNo, dotType));
    }

    public void clear() {
        elements.clear();
    }

    public boolean isEqual(ArrayList<Sketch.Element> dotElems) {
        if (dotElems.size() != elements.size())
            return false;

        // Max кол-во проходов = SUM(orbElems_.size() - i), где (i = [0..elements.size()]).
        boolean isElemFound;
        for (Element dotElem : dotElems) {
            isElemFound = false;
            for (Element sketchElem : elements) {
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
        return new Sketch(Sketch.Types.NONE, 0);
    }
}
