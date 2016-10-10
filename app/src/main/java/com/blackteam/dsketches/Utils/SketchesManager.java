package com.blackteam.dsketches.Utils;

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
        Sketch sketch3ElemRow = new Sketch(Sketch.Types.ROW_3, 20);
        sketch3ElemRow.add(0, 0, Orb.Types.UNIVERSAL);
        sketch3ElemRow.add(0, 1, Orb.Types.UNIVERSAL);
        sketch3ElemRow.add(0, 2, Orb.Types.UNIVERSAL);

        sketches_.add(sketch3ElemRow);
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
