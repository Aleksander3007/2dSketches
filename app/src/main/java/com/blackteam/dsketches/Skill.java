package com.blackteam.dsketches;

/**
 * Спец. возможность.
 */
public class Skill extends DisplayableObject {

    private SkillType type_;
    /** Количество доступных ипользований. */
    private int count_;

    public Skill(final SkillType type, final int amount, Texture texture) {
        super(texture);
        this.type_ = type;
        this.count_ = amount;
    }

    @Override
    public void dispose() {

    }

    public static int getResourceId(SkillType type) {
        switch (type) {
            case RESHUFFLE:
                return R.drawable.reshuffle;
            case GOOD_NEIGHBOUR:
                return R.drawable.good_neighbour;
            case CHASM:
                return R.drawable.chasm;
            default:
                // Такого быть не может, но всё же.
                return R.drawable.error;
        }
    }

    public SkillType getType() {
        return type_;
    }

    public void increment() {
        count_++;
    }

    public void decrement() {
        if (count_ > 0) {
            count_--;
        }
    }

    public boolean canUse() {
        return (count_ > 0);
    }
}
