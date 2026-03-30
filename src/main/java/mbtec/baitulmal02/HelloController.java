package mbtec.baitulmal02;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import mbtec.baitulmal02.DB.ConexaoSQLite;

import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class HelloController {
    public static void main(String[] args) {
        Connection conn = ConexaoSQLite.getConnection();
        if (conn != null) {
            System.out.println("Conexão estabelecida com sucesso!");
        } else {
            System.out.println("Erro ao conectar.");
        }
        Function<String, Integer> fn = n->n.length();

        System.out.println(fn.apply("Mitumba"));

        Predicate<Integer> predicate = n->n<2*n;
        System.out.println(predicate.test(3));

        Consumer<String> cm = n-> System.out.println(n+" Zulo");
        cm.accept("Filho");
    }

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}