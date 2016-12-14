package com.blackteam.dsketches;

/**
 * Спец. возможность.
 */
public class Skill {

    public enum Type {
        RESHUFFLE, // "Перемешать" ("Перетасовка").
        FRIENDS, // "Обратить три случайных в спец. orb" ("Друзья").
        CHASM // "Убрать два нижних ряда" ("Пропасть").
    }

    /** Стоимость skill в игровых очках. */
    public static final int COST_POINTS = 150;
    /** Стоимость skill в реальных долларах США. */
    public static final float COST_REAL_MONEY = 1;

    private Skill.Type type_;
    /** Количество доступных ипользований. */
    private int amount_;

    /**
     * Конструктор.
     * @param type Тип skill.
     * @param amount Кол-во доступных ипользований.
     */
    public Skill(final Skill.Type type, final int amount) {
        super();
        this.type_ = type;
        this.amount_ = amount;
    }

    public Skill.Type getType() {
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

    public static Skill.Type convertToType(String skillTypeStr) {
        return Enum.valueOf(Skill.Type.class, skillTypeStr);
    }
}
