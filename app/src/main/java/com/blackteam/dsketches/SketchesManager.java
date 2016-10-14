package com.blackteam.dsketches;

import com.blackteam.dsketches.Orb;
import com.blackteam.dsketches.Sketch;

import java.util.ArrayList;

/**
 * Управление скетчами.
 */
public class SketchesManager {

    // TODO: Как действовать если скетч_1 перекрывает скетч_2 какой из них учитывать.
    // Вариант 1. Оба.
    // Вариант 2. Выставлять приоритет для каждого скетча.

    // Нулевой скетч.
    public static final Sketch SKETCH_NULL_ = Sketch.getNullSketch();

    private ArrayList<Sketch> sketches_ = new ArrayList<>();

    public SketchesManager() {
        // TODO: 1. Чтение из файла всех sketches.
        // TODO: STUB: Генерация sketch.
        Sketch sketchElemRow3 = new Sketch(Sketch.Types.ROW_3, 20);
        for (int iElem = 0; iElem < 3; iElem++) {
            sketchElemRow3.add(0, iElem, Orb.Types.UNIVERSAL);
        }
        sketches_.add(sketchElemRow3);

        Sketch sketchElemRow5 = new Sketch(Sketch.Types.ROW_5, 50);
        for (int iElem = 0; iElem < 5; iElem++) {
            sketchElemRow5.add(0, iElem, Orb.Types.UNIVERSAL);
        }
        sketches_.add(sketchElemRow5);
    }

    public Sketch findSketch(ArrayList<Orb> orbs) {
        if (orbs == null)
            return SKETCH_NULL_;

        // Ищём подходящий sketch.
        ArrayList<Sketch.Element> normalOrbs  = normalize(orbs);
        for (Sketch sketch : sketches_) {
            if (sketch.isEqual(normalOrbs)) {
                return sketch;
            }
        }

        return SKETCH_NULL_;
    }

    private ArrayList<Sketch.Element> normalize(ArrayList<Orb> orbs) {
        // 1. Ищем минимумы.
        int minRowNo = orbs.get(0).getRowNo();
        int minColNo = orbs.get(0).getColNo();
        for (Orb orb : orbs) {
            if (orb.getRowNo() < minRowNo)
                minRowNo = orb.getRowNo();
            if (orb.getColNo() < minColNo)
                minColNo = orb.getColNo();
        }

        // Создаем список нормализованных элементов.
        ArrayList<Sketch.Element> normalOrbs = new ArrayList<>(orbs.size());
        for (Orb orb : orbs) {
            Sketch.Element elem = new Sketch.Element(
                    orb.getRowNo() - minRowNo,
                    orb.getColNo() - minColNo,
                    orb.getType()
            );
            normalOrbs.add(elem);
        }

        return normalOrbs;
    }
}
