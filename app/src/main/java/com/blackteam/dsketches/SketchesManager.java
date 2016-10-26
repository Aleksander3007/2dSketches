package com.blackteam.dsketches;

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
            sketchElemRow3.add(0, iElem, GameDot.Types.UNIVERSAL);
        }
        sketches_.add(sketchElemRow3);

        Sketch sketchElemRow5 = new Sketch(Sketch.Types.ROW_5, 50);
        for (int iElem = 0; iElem < 5; iElem++) {
            sketchElemRow5.add(0, iElem, GameDot.Types.UNIVERSAL);
        }
        sketches_.add(sketchElemRow5);
    }

    public Sketch findSketch(ArrayList<GameDot> gameDots) {
        if (gameDots == null)
            return SKETCH_NULL_;

        // Ищём подходящий sketch.
        ArrayList<Sketch.Element> normalDots  = normalize(gameDots);
        for (Sketch sketch : sketches_) {
            if (sketch.isEqual(normalDots)) {
                return sketch;
            }
        }

        return SKETCH_NULL_;
    }

    private ArrayList<Sketch.Element> normalize(ArrayList<GameDot> gameDots) {
        // 1. Ищем минимумы.
        int minRowNo = gameDots.get(0).getRowNo();
        int minColNo = gameDots.get(0).getColNo();
        for (GameDot gameDot : gameDots) {
            if (gameDot.getRowNo() < minRowNo)
                minRowNo = gameDot.getRowNo();
            if (gameDot.getColNo() < minColNo)
                minColNo = gameDot.getColNo();
        }

        // Создаем список нормализованных элементов.
        ArrayList<Sketch.Element> normalDots = new ArrayList<>(gameDots.size());
        for (GameDot gameDot : gameDots) {
            Sketch.Element elem = new Sketch.Element(
                    gameDot.getRowNo() - minRowNo,
                    gameDot.getColNo() - minColNo,
                    gameDot.getType()
            );
            normalDots.add(elem);
        }

        return normalDots;
    }
}
