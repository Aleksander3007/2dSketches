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

    private Skill.Type mType;
    /** Количество доступных ипользований. */
    private int mAmount;

    /**
     * Конструктор.
     * @param type Тип skill.
     * @param amount Кол-во доступных ипользований.
     */
    public Skill(final Skill.Type type, final int amount) {
        super();
        this.mType = type;
        this.mAmount = amount;
    }

    public Skill.Type getType() {
        return mType;
    }

    public int getAmount() {
        return mAmount;
    }

    public void setAmount(final int amount) {
        this.mAmount = amount;
    }

    public void add() {
        mAmount++;
    }

    public void use() {
        if (mAmount > 0) {
            mAmount--;
        }
    }

    public boolean canUse() {
        return (mAmount > 0);
    }

    public static Skill.Type convertToType(String skillTypeStr) {
        return Enum.valueOf(Skill.Type.class, skillTypeStr);
    }
}
