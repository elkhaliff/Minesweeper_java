package minesweeper;

public class Main {
    public static void main(String[] args) {
        int cells = 9;
        int mines = 10; //getInt("How many mines do you want on the field? ")
        GameEngine gameEngine = new GameEngine(cells, mines); // Инициализация экземпляра класса с заданной шириной поля

        gameEngine.startGame();
    }
}
