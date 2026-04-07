package mbtec.baitulmal02.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mbtec.baitulmal02.dao.MovimentoDAO;
import mbtec.baitulmal02.dao.RelatorioDAO;
import mbtec.baitulmal02.model.Movimento;
import mbtec.baitulmal02.utilitario.AlertaUtil;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RelatorioController implements Initializable {
    @FXML
    private TableColumn<Movimento, Integer> colunaCodigoTipo;

    @FXML
    private TableColumn<Movimento, String> colunaData;

    @FXML
    private TableColumn<Movimento, String> colunaDescricaoExtrato;

    @FXML
    private TableColumn<Movimento, BigDecimal> colunaMontante;

    @FXML
    private TableColumn<Movimento, String> colunaMovimentoTipo;

    @FXML
    private TableColumn<Movimento, BigDecimal> colunaSaldoDisponivel;

    @FXML
    private TableView<Movimento> tableViewExtrato;

    private LocalDate data_incial_service;
    private LocalDate data_final_service;

    private boolean estadoTabela;



    private Movimento movimento = new Movimento();
    private List<Movimento> movimentoList = new ArrayList<>();
    private final MovimentoDAO movimentoDAO = new MovimentoDAO();
    private ObservableList<Movimento> movimentoObservableList;
    private FilteredList<Movimento> movimentoFilteredList;
    private final RelatorioDAO relatorioDAO = new RelatorioDAO();

    @FXML
    void btnImprimirImagem(MouseEvent event) {
        //Nao usado; duplicata
    }

    @FXML
    void btnCancelar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void btnImprimir(ActionEvent event) {
        if (estadoTabela){
            AlertaUtil.mostrarErro("Falha ao imprimir!","Sem dados para imprimir");
            return;
        }
        AlertaUtil.mostrarInfo("", "NA boa?");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void carregarTableviewMovimento() {

        colunaCodigoTipo.setCellValueFactory(new PropertyValueFactory<>("idmovimento"));
        colunaDescricaoExtrato.setCellValueFactory(cellData -> {
            movimento = cellData.getValue();
            if (movimento.getTipo().equalsIgnoreCase("Saida") && movimento.getConsumo() != null) {
                return new SimpleObjectProperty<>(movimento.getConsumo().getDescricao());
            } else if (movimento.getTipo().equalsIgnoreCase("Entrada") && movimento.getContribuicao() != null) {
                return new SimpleObjectProperty<>(movimento.getContribuicao().getContribuinte());
            } else {
                return new SimpleObjectProperty<>("Sem dados encontrados");
            }
        });
        colunaMovimentoTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colunaData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colunaMontante.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colunaMontante.setCellFactory(tc -> new TableCell<Movimento, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal valor, boolean empty) {
                super.updateItem(valor, empty);
                if (empty || valor == null) {
                    setText(null);
                } else {
                    setText(String.format("MZN %.2f", valor));
                }
            }
        });
        colunaSaldoDisponivel.setCellValueFactory(new PropertyValueFactory<>("saldoResultante"));

        //Responsavel em preencher dados na tabela
        movimentoList = movimentoDAO.listarPorPeriodo(data_incial_service, data_final_service);
        movimentoObservableList = FXCollections.observableArrayList(movimentoList);

        movimentoFilteredList = new FilteredList<>(movimentoObservableList, p -> true);
        tableViewExtrato.setItems(movimentoFilteredList);
        if (movimentoList.isEmpty()) {
            estadoTabela = true;
            Label message = new Label("Sem dados encontrados. As datas selecionadas sem movimentos");
            message.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: red;");
            tableViewExtrato.setPlaceholder(message);
        }
    }

    public void setDatas(LocalDate dataInicial, LocalDate dataFinal){
        this.data_incial_service = dataInicial;
        this.data_final_service = dataFinal;
        carregarTableviewMovimento();
    }

}
