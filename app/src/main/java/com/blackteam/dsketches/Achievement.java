package com.blackteam.dsketches;

public class Achievement {
    public static final Sketch.Types ANY_SKETCH_TYPE = Sketch.Types.NONE;
    public static final int ANY_SCORE = 0;
    public static final int ANY_PROFIT = 0;

    private String name_;
    private Sketch.Types sketchType_ = ANY_SKETCH_TYPE;
    private int score_ = ANY_SCORE;
    private int profit_ = ANY_PROFIT;

    public String getName() {
        return name_;
    }

    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException("achievement's name is null.");
        this.name_ = name;
    }

    public void setSketchType(Sketch.Types sketchType_) {
        this.sketchType_ = sketchType_;
    }

    public void setScore(int score) {
        this.score_ = score;
    }

    public void setProfit(int profit) {
        this.profit_ = profit;
    }

    public boolean equals(Sketch.Types sketchType, int score, int profit) {
        boolean sketchIdentity = (this.sketchType_ == ANY_SKETCH_TYPE) || (sketchType == this.sketchType_);
        boolean scoreIdentity = (this.score_ == ANY_SCORE) || (score >= this.score_);
        boolean profitIdentity = (this.profit_ == ANY_PROFIT) || (profit >= this.profit_);

        return (sketchIdentity && scoreIdentity && profitIdentity);
    }
}
