module mbtec.baitulmal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.jetbrains.annotations;


    opens mbtec.baitulmal02 to javafx.fxml;
    opens mbtec.baitulmal02.model to javafx.base;
    opens mbtec.baitulmal02.controller to javafx.fxml;
    exports mbtec.baitulmal02;
}