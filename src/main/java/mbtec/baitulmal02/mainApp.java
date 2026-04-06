package mbtec.baitulmal02;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class mainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(mainApp.class.getResource("telaBaitulmal.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("ISLAM  PARA TODOS");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {

            event.consume();
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Confirmação");
            alerta.setHeaderText(null);
            alerta.setContentText("Tem certeza que deseja sair do sistema?");
            ButtonType btnSim = new ButtonType("Sim");
            ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
            alerta.getButtonTypes().setAll(btnSim, btnNao);
            Optional<ButtonType> resultado = alerta.showAndWait();
            if (resultado.isPresent() && resultado.get() == btnSim) {
                stage.close();
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}