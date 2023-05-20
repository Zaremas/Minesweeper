package com.example.minesweeper;
/*
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
 */
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

class Square {
    boolean opened;
    boolean mine;
    boolean flagged;
    int number;
    Button button;

    Square() {
        opened = false;
        mine = false;
        flagged = false;
        number = 0;
    }

    void setDefault() {
        opened = false;
        mine = false;
        flagged = false;
        number = 0;
    }

    void setButton(Button button) {
        this.button = button;
    }

    void setMine() {
        mine = true;
    }

    void flag() {
        flagged = true;
    }

    void unflag() {
        flagged = false;
    }

    void open() {
        opened = true;
        button.setText(String.valueOf(number));
        button.setDisable(true);
    }
}

public class Minesweeper extends Application {
    private static int gridSize;
    private static int numMines;
    //static Timeline time;
    //static Label timeLabel;
    int difficulty = 1;
    /*
    time = new Timeline();
    timeLabel = new Label();
        timeLabel.setText(getTime(stage));
        time.setCycleCount(Timeline.INDEFINITE);
        time.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {try {timeLabel.setText(getTime(stage));} catch (IOException ex) {}}));
*/

    private Square[][] squares;

    @Override
    public void start(Stage primaryStage) {

        selectDifficulty(primaryStage);

    }

    private void startgame(Stage primaryStage){
        int buttonsize = 20;
        if (difficulty == 1){
            gridSize = 10;
            numMines = 10;
            buttonsize = 38;
        }
        else if (difficulty == 2){
            gridSize = 20;
            numMines = 50;
            buttonsize = 30;
        }else if (difficulty == 3){
            gridSize = 30;
            numMines = 100;
            buttonsize = 25;
        }

        squares = new Square[gridSize][gridSize];

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(2);
        gridPane.setVgap(2);

        // Initialize buttons
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Square square = new Square();
                Button button = new Button();
                button.setMinSize(buttonsize, buttonsize);
                button.setMaxSize(buttonsize, buttonsize);
                int finalRow = row;
                int finalCol = col;
                square.setButton(button);
                button.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        handleButtonClick(finalRow, finalCol, square);
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        if (!square.opened) {
                            flag(finalRow, finalCol, button);
                        }
                    }
                });

                squares[row][col] = square;
                gridPane.add(button, col, row);
            }
        }
        BorderPane bp = new BorderPane();
        bp.setTop(new Label("Left click to open a cell, Right click to flag a cell"));
        bp.setBottom(gridPane);

        Scene scene = new Scene(bp);
        primaryStage.setTitle("Minesweeper");
        primaryStage.setScene(scene);
        primaryStage.show();

        generateMines();

        calculateNumbers();
    }

    private void selectDifficulty(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        Label label = new Label("Select difficulty level:");
        label.setAlignment(Pos.CENTER);
        borderPane.setTop(label);
        Button easy = new Button("Easy");
        easy.setTextFill(Color.GREEN);
        borderPane.setLeft(easy);
        easy.setOnMouseClicked(event -> {
            difficulty = 1;
            startgame(primaryStage);});

        Button medium = new Button("Medium");
        medium.setTextFill(Color.YELLOW);
        borderPane.setCenter(medium);
        medium.setOnMouseClicked(event -> {
            difficulty = 2;
            startgame(primaryStage);});


        Button difficult = new Button("Difficult");
        difficult.setTextFill(Color.RED);
        borderPane.setRight(difficult);
        difficult.setOnMouseClicked(event -> {
            difficulty = 3;
            startgame(primaryStage);});

        Scene scene = new Scene(borderPane);
        primaryStage.setTitle("Select difficulty");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void generateMines() {
        int count = 0;
        while (count < numMines) {
            int row = (int) (Math.random() * gridSize);
            int col = (int) (Math.random() * gridSize);
            if (!(squares[row][col].mine || squares[row][col].opened)) {
                squares[row][col].setMine();
                count++;
            }
        }
    }

    private void calculateNumbers() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (squares[row][col].mine) {
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            if (isValidCell(row + i, col + j)) {
                                squares[row + i][col + j].number++;
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleButtonClick(int row, int col, Square square) {
        if (square.mine) {
            square.button.setDisable(true);
            square.button.setText("X");
            showAlert("Game Over", "You clicked on a mine!");
            resetGame();
        } else {
            square.open();
            if (square.number == 0) {
                square.button.setText("");
                revealAdjacentCells(row, col);
            }
        }
    }

    private void flag(int row, int col, Button button) {
        Square square = squares[row][col];
        if (!square.flagged) {
            square.flag();
            button.setText("⚐");
            button.setTextFill(Color.RED);
        } else {
            square.unflag();
            button.setText("");
        }
    }

    private void revealAdjacentCells(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (isValidCell(newRow, newCol) && !squares[newRow][newCol].opened && !squares[newRow][newCol].mine) {
                    handleButtonClick(newRow, newCol, squares[newRow][newCol]);
                }
            }
        }
    }

    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < gridSize && col >= 0 && col < gridSize;
    }

    private void resetGame() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Square square = squares[row][col];
                square.button.setText("");
                square.button.setDisable(false);
                square.setDefault();
            }
        }
        generateMines();
        calculateNumbers();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
