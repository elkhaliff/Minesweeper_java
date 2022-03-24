package minesweeper;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int cells = 9;
        GameEngine gameEngine = new GameEngine(cells); // Инициализация экземпляра класса с заданной шириной поля

        gameEngine.startGame();
    }
}
