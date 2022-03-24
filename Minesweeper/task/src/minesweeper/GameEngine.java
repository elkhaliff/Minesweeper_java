package minesweeper;

import java.util.Scanner;

/**
 * Основной класс приложения
 *  @author Andrey Zotov aka OldFox
 */
public class GameEngine {
    private final GameField gameField;

    /**
     * Конструктор, в качестве параметра длинна стороны
     */
    public GameEngine(int cells) {
        // Инициализация массива рабочей области (минного поля)
        gameField = new GameField(cells);
        int mines = getInt("How many mines do you want on the field? ");
        gameField.initMines(mines);
    }

    /**
     * Процесс игры
     */
    public void startGame() {
        printResult();
        int out, checkGame;
        while (true) { // Цикл получения координат - ожидание хода, проверка результатов
            out = gameField.makeTurn(); // Запрашиваем ход игрока, устанавливаем ход на доску
            switch (out) {
                case 0: {
                    checkGame = gameField.checkFlags();
                    printResult();
                    if (checkGame == 0) {
                        println("Congratulations! You found all the mines!");
                        return;
                    }
                    break;
                }
                case 1: {
                    println("There is a number here!");
                    break;
                }
                case 2: {
                    gameField.viewAllMine();
                    printResult();
                    println("You stepped on a mine and failed!");
                    return;
                }
            }
        }
    }

    private void printResult() { System.out.println(gameField); }

    private void println(String string) { System.out.println(string); }

    private void print(String string) { System.out.print(string); }

    private int getInt(String string) {
        Scanner scanner = new Scanner(System.in);
        print(string);
        return scanner.nextInt();
    }
}