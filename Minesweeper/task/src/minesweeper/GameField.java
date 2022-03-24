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
        boolean doIt;
        for (int i = 0; i < mines; i++) {
            doIt = true;
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
        if (point.x < 0 || point.x > rows - 1) return 0; // такой ячейки - нет
        var out = 0;
        for (int c = point.y; c < point.y + 3; c++) {
            if (c < 0 || c > cols - 1) continue; // такой ячейки - нет
            Point pointTest = new Point(point.x, c);
            if (isEmpty(pointTest)) {
                if (markWork && (isTypeCell(pointTest, cloudCell) || (isTypeCell(pointTest, flagCell)) && !isTypeCell(pointTest, emptyCell)))
                    fieldMap[pointTest.x][pointTest.y] = workCell; // покажем, что данная ячейка пуста, и вокруг можно исследовать
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
    public void testEmpty() {
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
     * Проходная функция заполнения статистики по всем клеткам поля
     */
    public void checkAll() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Point point = new Point(i, j);
                int around = viewAround(point, false);
                if (isEmpty(point) && (around != 0)) fieldMap[i][j] = String.format("%d", around);
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
        if (empty && (around != 0))
            fieldMap[point.x][point.y] = around.toString();
        else {
            viewAround(point, true);
            fieldMap[point.x][point.y] = emptyCell;
        }
        return true;
    }

    /**
     * Ход сапёра и проверка
     */
    public int makeTurn() {
        String[] input = getString("Set/unset mines marks or claim a cell as free: ").split(" ");

        // По заданию - ввод идет столбец - строка (x - y)
        Point point = new Point(Integer.parseInt(input[1]) - 1, Integer.parseInt(input[0]) - 1);
        String type = input[2];
        // There is a number here!
        if (isNumberCell(point)) return 1;

        switch (type) {
            case "mine": { setUnsetFlag(point); break; }
            case "free": {
                if (testEmptyCell(point))
                    testEmpty();
                else
                    return 2;
                break;
            }
        }
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
        StringBuilder border = new StringBuilder("");
        border.append(" |");
        for (int i = 1; i < cols+1; i++) {
            border.append(i); // печатаем строку цифр
        }
        border.append("|\n");
        StringBuilder border1 = new StringBuilder("");
        border1.append("-|");
        for (int i = 1; i < cols+1; i++) {
            border1.append("-"); // печатаем линейку
        }
        border1.append("|\n");

        StringBuilder outStr = new StringBuilder();
        outStr.append(border)
                .append(border1);
        for (int i = 0; i < rows; i++) {
            outStr.append(String.format("%d|", i+1)); // порядковый номер
            for (int j = 0; j < cols; j++) {
                outStr.append(fieldMap[i][j]);
            }
            outStr.append("|\n");
        }
        outStr.append(border1);
        return outStr.toString();
    }

    private void print(String string) { System.out.print(string); }

    private String getString(String string) {
        Scanner scanner = new Scanner(System.in);
        print(string);
        return scanner.nextLine();
    }
}