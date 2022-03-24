package minesweeper;

import java.util.Scanner;

/**
 * Основной класс приложения
 *  @author Andrey Zotov aka OldFox
 */
public class GameEngine {
    private GameField gameField;

    /**
     * Конструктор, в качестве параметра длинна стороны
     */
    public GameEngine(int cells, int mines) {
        // Инициализация массива рабочей области (минного поля)
        gameField = new GameField(cells);
        gameField.initMines(mines);
    }

    /**
     * Процесс игры
     */
    public void startGame() {
        gameField.viewAllMine();
        System.out.println(gameField);
    }
}