package com.blackteam.dsketches;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Панель спец. возможностей.
 */
public class SkillsPanel {
    private ArrayList<Skill> skills_ = new ArrayList<>();
    private Skill selectedSkill_;

    public SkillsPanel(Context context) {
        loadContent(context);
    }

    public void init(final Vector2 pos, final Size2 rectSize) {
        float skillWidth = rectSize.width / (skills_.size() * 2);
        for (int iSkill = 0; iSkill < skills_.size(); iSkill++) {
            skills_.get(iSkill).setPosition(
                    pos.x + (iSkill + 1) * skillWidth,
                    pos.y
            );
            skills_.get(iSkill).setSize(skillWidth, rectSize.height);
        }
        selectedSkill_ = null;
    }

    public boolean hit(Vector2 coords) {
        for (Skill skill : skills_) {
            if (skill.hit(coords)) {
                if (skill.canUse()) {
                    Log.i("SkillsPanel", "skill canUse()");
                    selectedSkill_ = skill;
                    return true;
                }
            }
        }
        selectedSkill_ = null;

        return false;
    }

    public void useSelectedSkill() {
        selectedSkill_.decrement();
        selectedSkill_ = null;
    }

    private void loadContent(Context context) {
        for (SkillType skillType : SkillType.values()) {
            skills_.add(new Skill(skillType, 100, new Texture(context, Skill.getResourceId(skillType))));
        }
    }

    public void draw(float[] mvpMatrix, ShaderProgram shader) {
        for (Skill skill : skills_) {
            skill.draw(mvpMatrix, shader);
        }
    }

    public void applySelectedSkill(World world) {
        switch (selectedSkill_.getType()) {

            case RESHUFFLE:
                Log.i("World", "RESHUFFLE skill.");
                for (int iRow = 0; iRow < world.getNumRows(); iRow++) {
                    for (int iCol = 0; iCol < world.getNumCols(); iCol++) {
                        int randomRow = (int) (Math.random() * world.getNumRows());
                        int randomCol = (int) (Math.random() * world.getNumCols());

                        OrbType tempOrbType = world.getOrb(iRow, iCol).getType();
                        OrbSpecType tempOrbSpecType = world.getOrb(iRow, iCol).getSpecType();

                        world.createOrb(world.getOrb(randomRow, randomCol).getType(),
                                world.getOrb(randomRow, randomCol).getSpecType(),
                                iRow, iCol
                        );

                        world.createOrb(tempOrbType,
                                tempOrbSpecType,
                                randomRow, randomCol
                        );
                    }
                }
                break;
            case GOOD_NEIGHBOUR:
                Log.i("World", "GOOD_NEIGHBOUR skill.");
                break;
            case CHASM:
                Log.i("World", "CHASM skill.");
                break;
        }

        useSelectedSkill();
    }
}
