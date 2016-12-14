package com.blackteam.dsketches;

/**
 * Спец. возможность.
 */
public class Skill {
    /** Стоимость skill в игровых очках. */
    public static final int COST_POINTS = 150;
    /** Стоимость skill в реальных долларах США. */
    public static final float COST_REAL_MONEY = 1;

    private SkillType type_;
    /** Количество доступных ипользований. */
    private int amount_;

    /**
     * Конструктор.
     * @param type Тип skill.
     * @param amount Кол-во доступных ипользований.
     */
    public Skill(final SkillType type, final int amount) {
        super();
        this.type_ = type;
        this.amount_ = amount;
    }

    public SkillType getType() {
        return type_;
    }

    public int getAmount() {
        return amount_;
    }

    public void setAmount(final int amount) {
        this.amount_ = amount;
    }

    public void add() {
        amount_++;
    }

    public void use() {
        if (amount_ > 0) {
            amount_--;
        }
    }

    public boolean canUse() {
        return (amount_ > 0);
    }

    public static SkillType convertToType(String skillTypeStr) {
        return Enum.valueOf(SkillType.class, skillTypeStr);
    }
}
