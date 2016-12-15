package com.blackteam.dsketches;

import java.io.Serializable;

public class Achievement implements Serializable {
    public static final String ANY_SKETCH = "";
    public static final int ANY_SCORE = 0;
    public static final int ANY_PROFIT = 0;

    private String mName;
    private String mDescription;
    private String mSketchName = ANY_SKETCH;
    private int mScore = ANY_SCORE;
    private int mProfit = ANY_PROFIT;
    private boolean mIsEarned = false;

    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException("achievement's name is null.");
        this.mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setSketchType(String sketchName) {
        this.mSketchName = sketchName;
    }

    public void setScore(int score) {
        this.mScore = score;
    }

    public void setProfit(int profit) {
        this.mProfit = profit;
    }

    public void earn() { mIsEarned = true; }

    public boolean isEarned() { return mIsEarned; }

    public boolean equals(String sketchName, int score, int profit) {
        assert sketchName != null;
        boolean sketchIdentity = (mSketchName == ANY_SKETCH) || (sketchName.equals(mSketchName));
        boolean scoreIdentity = (mScore == ANY_SCORE) || (score >= this.mScore);
        boolean profitIdentity = (mProfit == ANY_PROFIT) || (profit >= this.mProfit);

        return (sketchIdentity && scoreIdentity && profitIdentity);
    }
}
