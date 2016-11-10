package com.blackteam.dsketches;

import com.blackteam.dsketches.gui.DisplayableObject;
import com.blackteam.dsketches.gui.Graphics;
import com.blackteam.dsketches.gui.NumberLabel;
import com.blackteam.dsketches.gui.StaticText;
import com.blackteam.dsketches.gui.TextureRegion;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

/**
 * Спец. возможность.
 */
public class Skill {
    private static final int SKILLS_CONTENT_ID_ = R.drawable.skills;
    private static final int AMOUNT_CONTENT_ID_ = R.drawable.numbers;

    /** Ширина текстуры. */
    public static final int TEX_WIDTH = 256;
    /** Высота текстуры. */
    public static final int TEX_HEIGHT = 256;

    private SkillType type_;
    /** Количество доступных ипользований. */
    private int count_;

    private DisplayableObject mainObject_;
    private NumberLabel amountLabel_;

    /**
     * Конструктор. Не забываем после вызвать метод init() для установки позиции и размера.
     * @param type Тип skill.
     * @param amount Кол-во доступных ипользований.
     * @param contents Content Manager.
     */
    public Skill(final SkillType type, final int amount, ContentManager contents) {
        super();
        this.type_ = type;
        this.count_ = amount;

        mainObject_ = new DisplayableObject(new TextureRegion(
                contents.get(SKILLS_CONTENT_ID_),
                getTexturePosition(type),
                new Size2(TEX_WIDTH, TEX_HEIGHT)
        ));
        amountLabel_ = new NumberLabel(contents.get(AMOUNT_CONTENT_ID_));
        amountLabel_.setValue(amount);
    }

    public void init(Vector2 pos, Size2 size) {
        mainObject_.setPosition(pos);
        mainObject_.setSize(size);
        float amountLabelSize = (size.width <= size.height) ? size.width / 2 : size.height / 2;
        Vector2 amountLabelPos = new Vector2(
                mainObject_.getPosition().x + mainObject_.getWidth() - amountLabelSize,
                mainObject_.getPosition().y - (mainObject_.getHeight() - amountLabelSize)
        );
        amountLabel_.init(amountLabelPos, new Size2(amountLabelSize, amountLabelSize));
    }

    public static Vector2 getTexturePosition(SkillType type) {
        int x = 0;

        switch (type) {
            case RESHUFFLE:
                x = 0;
                break;
            case FRIENDS:
                x = Skill.TEX_WIDTH;
                break;
            case CHASM:
                x = 2 * Skill.TEX_WIDTH;
                break;
        }

        return new Vector2(x, 0);
    }

    public SkillType getType() {
        return type_;
    }

    public void add() {
        count_++;
        amountLabel_.setValue(count_);
    }

    public void use() {
        if (count_ > 0) {
            count_--;
            amountLabel_.setValue(count_);
        }
    }

    public boolean canUse() {
        return (count_ > 0);
    }

    public void draw(Graphics graphics) {
        mainObject_.draw(graphics);
        amountLabel_.render(graphics);
    }

    public boolean hit(Vector2 coords) {
        return mainObject_.hit(coords);
    }
}
