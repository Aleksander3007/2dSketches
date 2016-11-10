package com.blackteam.dsketches;

import android.util.Log;

import com.blackteam.dsketches.gui.Graphics;
import com.blackteam.dsketches.utils.Size2;
import com.blackteam.dsketches.utils.Vector2;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Панель спец. возможностей.
 */
public class SkillsPanel {
    private ArrayList<Skill> skills_ = new ArrayList<>();
    private Skill selectedSkill_;

    public SkillsPanel(ContentManager contents) {
        loadContent(contents);
    }

    public void init(final Vector2 pos, final Size2 rectSize) {
        float skillWidth = rectSize.width / ((skills_.size() * 2) + 1);
        float skillHeight = rectSize.height;

        // Ширина всех skills.
        float skillsWidth = (2 * skills_.size() + 1) * (skillWidth);
        // Выставляем по середине контейнера.
        float startX = pos.x + (rectSize.width / 2 - skillsWidth / 2);
        int iSkill = 0;
        for (Skill skill : skills_) {
            skill.init(
                    new Vector2(startX + (2 * iSkill + 1) * (skillWidth),
                            pos.y),
                    new Size2(skillWidth, skillHeight)

            );
            iSkill++;
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
        selectedSkill_.use();
        selectedSkill_ = null;
    }

    private void loadContent(ContentManager contents) {
        for (SkillType skillType : SkillType.values()) {
            skills_.add(new Skill(skillType, 10, contents));
        }
    }

    public void draw(Graphics graphics) {
        for (Skill skill : skills_) {
            skill.draw(graphics);
        }
    }

    public void applySelectedSkill(World world) {
        switch (selectedSkill_.getType()) {
            case RESHUFFLE:
                applySkillReshuffle(world);
                break;
            case FRIENDS:
                applySkillFriends(world);
                break;
            case CHASM:
                applySkillChasm(world);
                break;
        }

        useSelectedSkill();
    }

    private void applySkillChasm(World world) {
        Log.i("World", "CHASM skill.");
        CopyOnWriteArrayList<GameDot> dots = new CopyOnWriteArrayList<>();
        // Последние два ряда.
        for (int iRow = 0; iRow < 2; iRow++) {
            for (int iCol = 0; iCol < world.getNumCols(); iCol++) {
                dots.add(world.getDot(iRow, iCol));
            }
        }
        world.deleteDots(dots);
    }

    private void applySkillFriends(World world) {
        Log.i("World", "FRIENDS skill.");
        ArrayList<Integer> randomDotNumbers = new ArrayList<>();
        // У трёх разные случайных dots.
        int iFriend = 0;
        while (iFriend < 3) {
            int randomDotNo = (int) (Math.random() * (world.getNumRows() * world.getNumCols() - 1));
            boolean isFriend = false;
            // Должны быть три разных.
            for (int existRandomDotNo : randomDotNumbers) {
                if (existRandomDotNo == randomDotNo) {
                    isFriend = true;
                    break;
                }
            }

            if (!isFriend) {
                int randomRow = randomDotNo / world.getNumCols();
                int randomCol = randomDotNo % world.getNumCols();
                // Должны быть не универсальными.
                if (world.getDot(randomRow, randomCol).getType() != GameDot.Types.UNIVERSAL) {
                    world.createDot(GameDot.Types.UNIVERSAL,
                            world.getDot(randomRow, randomCol).getSpecType(),
                            randomRow, randomCol
                    );
                    iFriend++;
                }
            }
        }
    }

    private void applySkillReshuffle(World world) {
        Log.i("World", "RESHUFFLE skill.");
        for (int iRow = 0; iRow < world.getNumRows(); iRow++) {
            for (int iCol = 0; iCol < world.getNumCols(); iCol++) {
                int randomRow = (int) (Math.random() * world.getNumRows());
                int randomCol = (int) (Math.random() * world.getNumCols());

                GameDot.Types tempOrbType = world.getDot(iRow, iCol).getType();
                GameDot.SpecTypes tempOrbSpecType = world.getDot(iRow, iCol).getSpecType();

                world.createDot(world.getDot(randomRow, randomCol).getType(),
                        world.getDot(randomRow, randomCol).getSpecType(),
                        iRow, iCol
                );

                world.createDot(tempOrbType,
                        tempOrbSpecType,
                        randomRow, randomCol
                );
            }
        }
    }
}
