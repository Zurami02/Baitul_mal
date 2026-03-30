package mbtec.baitulmal02.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mbtec.baitulmal02.dao.TipoDAO;
import mbtec.baitulmal02.model.Tipo;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class TipoController implements Initializable {
    @FXML
    private TableColumn<Tipo, Integer> colunaCodigoTipo;

    @FXML
    private TableColumn<Tipo, String> colunaDescricaoTipo;

    @FXML
    private TableView<Tipo> tableViewTipo;

    @FXML
    private TextField txtPesuisaTipo;

    @FXML
    private TextField txtTipoMovimento;

    private List<Tipo> tipoList = new ArrayList<>();
    private ObservableList<Tipo> tipoObservableList;
    private Tipo tipo = new Tipo();
    private final TipoDAO tipoDAO = new TipoDAO();

    @FXML
    void btnAdicionarTiposImagem(MouseEvent event) {
        //Nao usado; duplicata
    }

    @FXML
    void btnAtualizar(ActionEvent event) {
        tipo = tableViewTipo.getSelectionModel().getSelectedItem();
        if (tipo != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação de Atualização");
            alert.setHeaderText("Você está prestes a atualizar o Tipo!");
            alert.setContentText("Tem certeza que deseja atualizar o Tipo: " + tipo.getDescricaoTipo() + "?");

            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                // Dados atuais
                int idAtual = tipo.getIdtipo();
                String tipoAtual = tipo.getDescricaoTipo();

                // Dados novos do formulário
                String tipoNova = txtTipoMovimento.getText().trim();

                // Verifica se houve alteração
                boolean houveAlteracao =
                        !tipoAtual.equals(tipoNova);

                if (houveAlteracao) {
                    // Atualiza o objeto
                    tipo.setDescricaoTipo(tipoNova);

                    // Salva no banco
                    tipoDAO.editar(tipo);
                    carregarTableViewTipoMovimento();
                    limparCampos();
                } else {
                    // Nenhuma alteração detectada
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Nenhuma Alteração");
                    info.setHeaderText(null);
                    info.setContentText("Nenhuma alteração foi feita no tipo " + tipoAtual);
                    info.show();
                }
            }
        }
    }

    @FXML
    void btnAdicionarTipos(MouseEvent event) {
        //Nao usado; duplicata
    }

    @FXML
    void btnCancelar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void btnExcluir(ActionEvent event) {
        tipo = tableViewTipo.getSelectionModel().getSelectedItem();
        if (tipo != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmacao de exclusao");
            alert.setHeaderText("Voce esta preste a excluir o Tipo!");
            alert.setContentText("Tem certeza que deseja excluir " + tipo.getDescricaoTipo() + "?");

            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                tipoDAO.remover(tipo);
                carregarTableViewTipoMovimento();
                limparCampos();
            }
        } else {
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Erro na exclusao de dados!");
            infoAlert.setHeaderText("Nenhuma selecao de Tipo foi feita");
            infoAlert.setContentText("Selecione o Tipo na tabela.");
            infoAlert.show();
        }
    }

    @FXML
    void btnInserir(ActionEvent event) {

        if (validarEntradadedados()) {
            tipo = new Tipo();
            tipo.setDescricaoTipo(txtTipoMovimento.getText());
            tipoDAO.inserir(tipo);
            limparCampos();
            carregarTableViewTipoMovimento();

        }
    }

    @FXML
    void btnPesquisar(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarTableViewTipoMovimento();
        tableViewTipoListener();
    }

    private void carregarTableViewTipoMovimento() {
        colunaCodigoTipo.setCellValueFactory(new PropertyValueFactory<>("idtipo"));
        colunaDescricaoTipo.setCellValueFactory(new PropertyValueFactory<>("descricaoTipo"));

        //Listener para txtProcurar
        txtPesuisaTipo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                carregarTableViewTipoMovimento();
            }
        });

        tipoList = tipoDAO.listar();

        tipoObservableList = FXCollections.observableArrayList(tipoList);
        tableViewTipo.setItems(tipoObservableList);
    }

    private void limparCampos() {
        txtTipoMovimento.clear();
    }

    private void tableViewTipoListener() {
        tableViewTipo.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtTipoMovimento.setText(newSelection.getDescricaoTipo());
            }
        });
    }

    //Validar entrada de Dados no Cadastro
    private boolean validarEntradadedados() {
        String erroMessage = "";
        if (txtTipoMovimento.getText() == null || txtTipoMovimento.getText().isEmpty()) {
            erroMessage += "Tipo invalido!\n";
        }
        if (erroMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no Cadastro");
            alert.setHeaderText("O campo vazio, por favor verifique!");
            alert.setContentText(erroMessage);
            alert.show();
            return false;
        }
    }

}
