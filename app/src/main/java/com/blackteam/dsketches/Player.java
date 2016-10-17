package com.blackteam.dsketches;

import android.support.v4.util.ArrayMap;

/**
 * Класс содержит информацию игроке,
 * его достижения, кол-во очков, skills и их количество, и т.п.
 */
public class Player {
    private int score_;
    private ArrayMap<SkillType, Integer> skills_ = new ArrayMap<>();
    //private ArrayMap<Achievement, Boolean> achievements = new ArrayMap<>();
    // private Sketch.Types lastSketchType_;

    public Player() {
        this.score_ = 0;
    }

    public void setScore(int newScore) {
        score_ = newScore;
    }

    public int getScore() {
        return score_;
    }

    public void addScore(int addingScore) {
        score_ += addingScore;
    }
}
