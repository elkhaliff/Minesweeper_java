package minesweeper;

import java.awt.*;
import java.util.Scanner;

public class GameField {
    private final int rows; // Количество строк
    private final int cols; // Количество столбцов

    private final String cloudCell = "."; // неисследованное облако
    private final String miningCell = "X";        // заминированная ячейка
    private final String flagCell = "*";          // установленный флаг (возможно мина)
    private final String emptyCell = "/";         // исследованная - пустая ячейка
    private final String workCell = "#";          // рабочая ячейка для повторного исследования

    private final String [][] fieldMap;
    private final String [][] minMap;
    private int cntSetX; // Общее количество установленных мин

    /**
     * Конструктор, в качестве параметра длинна стороны
     */
    public GameField(int cells) {
        this.rows = cells;
        this.cols = cells;
        // Инициализация массива рабочей области
        fieldMap = new String[rows][cols];
        minMap = new String[rows][cols];
        cntSetX = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                fieldMap[i][j] = cloudCell;
                minMap[i][j] = cloudCell;
            }
        }
    }

    /**
     * Проверка содержимого ячейки поля на необходимый тип
     */
    private boolean isTypeCell(Point point, String type) {
        return (type.equals(fieldMap[point.x][point.y]));
    }

    /**
     * Проверка на незаполненность
     */
    private boolean isEmpty(Point point) {
        return (!miningCell.equals(minMap[point.x][point.y]));
    }

    /**
     * Проверка на наличие цифры в клетке
     */
    private boolean isNumberCell(Point point) {
        return !isTypeCell(point, cloudCell) && !isTypeCell(point, flagCell) &&
                !isTypeCell(point, workCell) && !isTypeCell(point, emptyCell);
    }
    /**
     * Есть ли свободные ячейки (для возможности установки мины)
     */
    private boolean isEmptyCells() {
        return (rows*cols - cntSetX) != 0;
    }

    /**
     * Установка одной мины
     */
    public boolean setMine(Point point) {
        if (isEmpty(point)) {
            minMap[point.x][point.y] = miningCell;
            cntSetX++;
            return true;
        } else return false;
    }

    /**
     *  Установка определенного кол-ва мин random
     */
    public void initMines(int mines) {
        boolean doIt = true;
        for (int i = 0; i < mines; i++) {
            while (doIt && isEmptyCells()) { // Попытка найти место для одной мины
                int x = (int) (Math.random() * 9);
                int y = (int) (Math.random() * 9);
                doIt = !setMine(new Point(x, y));
            }
        }
    }

    /**
     * Получение данных о трех ячейках строки (исключая выход за пределы поля)
     */
    private int checkRow(Point point, boolean markWork) {
        if (point.x < 0 || point.x > rows ) return 0; // такой ячейки - нет
        var out = 0;
        for (int c = point.y; c < point.y + 2; c++) {
            if (c < 0 || c > cols) continue; // такой ячейки - нет
            if (isEmpty(point)) {
                if (markWork && (isTypeCell(point, cloudCell) || (isTypeCell(point, flagCell)) && !isTypeCell(point, emptyCell)))
                    fieldMap[point.x][point.y] = workCell; // покажем, что данная ячейка пуста, и вокруг можно исследовать
            } else out ++;
        }
        return out;
    }

    /**
     * Количество мин вокруг указанной ячейки (8 ячеек, исключая выход за пределы поля)
     */
    private int viewAround(Point point, boolean markWork) {
        return checkRow(new Point(point.x - 1, point.y - 1), markWork) +      // проверяем строку выше (все позиции)
                checkRow(new Point(point.x, point.y - 1), markWork) +            // проверяем текущую строку
                checkRow(new Point(point.x + 1, point.y - 1), markWork);      // проверяем строку ниже (все позиции)
    }

    /**
     *  Установка\снятие флага на предполагаемое место мины
     */
    private void setUnsetFlag(Point point) {
        fieldMap[point.x][point.y] = isTypeCell(point, flagCell) ? cloudCell : flagCell;
    }

    /**
     * Проверка всего поля на отсутствие мин
     */
    private void testEmpty() {
        var isWork = true;
        while (isWork) {
            isWork = false;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Point point = new Point(i, j);
                    if (isTypeCell(point, workCell)) {
                        testEmptyCell(point);
                        isWork = true;
                    }
                }
            }
        }
    }

    /**
     * Проверка ячейки на отсутствие мины
     */
    private boolean testEmptyCell(Point point) {
        boolean empty = isEmpty(point);
        if (!empty) return false;

        Integer around = viewAround(point, false);
        if (empty && (around != 0)) fieldMap[point.x][point.y] = around.toString();
        else if (around == 0) {
            viewAround(point, true);
            fieldMap[point.x][point.y] = emptyCell;
        }
        return true;
    }

    /**
     * Ход сапёра и проверка
     */
    public int makeTurn() {
//        print("Set/unset mines marks or claim a cell as free: ");
//        String input = readLine()!!.split(" ")
/*
        // По заданию - ввод идет столбец - строка (x - y)
        int col = input[0].toInt();
        int row = input[1].toInt();
        int type = input[2];

        Point point = new Point(row, col);
        // There is a number here!
        if (isNumberCell(point)) return 1;

        switch (type) {
            case "mine" -> setUnsetFlag(point);
            case "free" -> testEmptyCell(point) ? testEmpty() : return 2;
        }
*/
        return 0;
    }

    /**
     * Проверка на завершение игры
     */
    public int checkFlags() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Point point = new Point(i, j);
                if (!isEmpty(point) && !isTypeCell(point, flagCell)) return 1;
            }
        }
        return 0;
    }

    /**
     * Раскрытие всех мин, удаление флагов - при взрыве и проигрыше
     */
    public void viewAllMine() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Point point = new Point(i, j);
                if (isTypeCell(point, flagCell)) {
                    fieldMap[i][j] = emptyCell;
                }
                if (!isEmpty(point)) {
                    fieldMap[i][j] = miningCell;
                }
            }
        }
    }

    /**
     * Формирование строки из игрового поля данного класса
     * (в частности - получаем возможность вывода на печать)
     */
    @Override
    public String toString() {
        StringBuilder border = new StringBuilder(" ");
        border.append(" |");
        for (int i = 1; i < cols+1; i++) {
            border.append(" "); border.append(i); // печатаем строку цифр
        }
        border.append("|\n");
        border.append("—|");
        for (int i = 1; i < cols+1; i++) {
            border.append("—"); // печатаем линейку
        }
        border.append("|\n");

        StringBuilder outStr = new StringBuilder();
        outStr.append(border);
        for (int i = 0; i < rows; i++) {
            outStr.append(String.format("%s|", i+1)); // порядковый номер
            for (int j = 0; j < cols; j++) {
                outStr.append(fieldMap[i][j]);
            }
            outStr.append("\n");
        }
        return outStr.toString();
    }

    public static void print(String string) { System.out.print(string); }

    public static String getString() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}