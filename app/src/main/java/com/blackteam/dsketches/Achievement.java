package com.blackteam.dsketches;

import java.io.Serializable;

public class Achievement implements Serializable {
    public static final String ANY_SKETCH = "";
    public static final int ANY_SCORE = 0;
    public static final int ANY_PROFIT = 0;

    private String name_;
    private String sketchName_ = ANY_SKETCH;
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

    public void setSketchType(String sketchName) {
        this.sketchName_ = sketchName;
    }

    public void setScore(int score) {
        this.score_ = score;
    }

    public void setProfit(int profit) {
        this.profit_ = profit;
    }

    public boolean equals(String sketchName, int score, int profit) {
        assert sketchName != null;
        boolean sketchIdentity = (sketchName_ == ANY_SKETCH) || (sketchName.equals(sketchName_));
        boolean scoreIdentity = (score_ == ANY_SCORE) || (score >= this.score_);
        boolean profitIdentity = (profit_ == ANY_PROFIT) || (profit >= this.profit_);

        return (sketchIdentity && scoreIdentity && profitIdentity);
    }
}
