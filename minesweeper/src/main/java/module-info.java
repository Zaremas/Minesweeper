module com.example.minesweeper {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires validatorfx;

    opens com.example.minesweeper to javafx.fxml;
    exports com.example.minesweeper;
}