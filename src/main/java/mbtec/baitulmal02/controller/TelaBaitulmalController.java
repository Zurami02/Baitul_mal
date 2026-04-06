package mbtec.baitulmal02.controller;


import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import mbtec.baitulmal02.dao.ContaPrincipalDAO;
import mbtec.baitulmal02.dao.MovimentoDAO;
import mbtec.baitulmal02.dao.RelatorioDAO;
import mbtec.baitulmal02.model.*;
import mbtec.baitulmal02.service.MovimentoService;
import mbtec.baitulmal02.utilitario.AlertaUtil;
import mbtec.baitulmal02.utilitario.TipoMovimento;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class TelaBaitulmalController implements Initializable {
    @FXML
    private TableColumn<Movimento, Integer> colunaCodigoMovimento;

    @FXML
    private TableColumn<Movimento, String> colunaConsumoContribuinte;

    @FXML
    private TableColumn<Movimento, String> colunaDataMovimento;

    @FXML
    private TableColumn<Movimento, String> colunaTipoMovimento;

    @FXML
    private TableColumn<Movimento, BigDecimal> colunaValorMovimento;

    @FXML
    private TableColumn<Movimento, BigDecimal> colunaValorTotalMovimento;

    @FXML
    private ComboBox<TipoMovimento> combomboxTipos;

    @FXML
    private DatePicker dataPickerInicial;

    @FXML
    private DatePicker dataPickerMovimento;

    @FXML
    private DatePicker datapickerfinal;

    @FXML
    private Label lblConsumoContribuicao;

    @FXML
    private Label lblObservacao;

    @FXML
    private Label lbSaldoAtual;

    @FXML
    private TableView<Movimento> tableviewBaitulMal;

    @FXML
    private TextField txtConsumoContribuinte;

    @FXML
    private TextField txtObservacao;

    @FXML
    private TextArea txtObservacaoLeitura;

    @FXML
    private TextField txtPesquisa;

    @FXML
    private TextField txtValor;

    private final MovimentoService movimentoService = new MovimentoService();
    private Movimento movimento = new Movimento();
    private List<Movimento> movimentoList = new ArrayList<>();
    private final MovimentoDAO movimentoDAO = new MovimentoDAO();
    private ObservableList<Movimento> movimentoObservableList;
    private FilteredList<Movimento> movimentoFilteredList;
    private List<TipoMovimento> tipoList = new ArrayList<>();
    private ObservableList<TipoMovimento> tipoObservableList;
    private final RelatorioDAO tipoDAO = new RelatorioDAO();

    @FXML
    void btnAdicionarTiposImagem(MouseEvent event) throws IOException {
        //openPaginas(event, "/mbtec/baitulmal02/tipo.fxml");
    }

    @FXML
    void btnAdicionarTipos(ActionEvent event) throws IOException {
//        carregarCombboxTipo();
    }

    @FXML
    void btnExtrato(ActionEvent event) throws IOException {
        LocalDate dataInicial = dataPickerInicial.getValue();
        LocalDate dataFinal = datapickerfinal.getValue();

        if (dataInicial == null || dataFinal == null || dataInicial.isAfter(dataFinal)){
            AlertaUtil.mostrarErro("Falha", "Por favor verifique as datas e data inicial nao pode ser > data final");
            return;
        }
        openPaginas(event, "/mbtec/baitulmal02/extrato.fxml", dataInicial, dataFinal);
    }

    @FXML
    void btnAtualizar(ActionEvent event) {
        Movimento movimentoSelecionado = tableviewBaitulMal.getSelectionModel().getSelectedItem();
        BigDecimal valor = new BigDecimal(String.valueOf(txtValor.getText()));
        if (movimentoSelecionado == null) {
            AlertaUtil.mostrarErro("Erro na atualizacao de Dados",
                    "Selecione um movimento para atualizar!");
            return;
        }

        // Atualiza com base nos campos preenchidos na tela
        movimentoSelecionado.setData(dataPickerMovimento.getValue().toString());
        movimentoSelecionado.setValor(valor);

        // Define tipo (pode vir de ComboBox ou campo)
        movimentoSelecionado.setTipo(String.valueOf(combomboxTipos.getValue()));

        // Atualiza relação com consumo ou contribuição
        if (movimentoSelecionado.getTipo().equalsIgnoreCase(TipoMovimento.SAIDA.getDescricao())) {
            Consumo c = movimentoSelecionado.getConsumo();
            c.setDescricao(txtConsumoContribuinte.getText());
            c.setValorConsumo(valor);
            c.setData(dataPickerMovimento.getValue().toString());
            c.setObservacao(txtObservacao.getText());
            movimentoSelecionado.setConsumo(c);
            carregarTableviewMovimento();
            limparCampos();

        } else if (movimentoSelecionado.getTipo().equalsIgnoreCase(TipoMovimento.ENTRADA.getDescricao())) {
            Contribuicao cb = movimentoSelecionado.getContribuicao();
            cb.setContribuinte(txtConsumoContribuinte.getText());
            cb.setValorContribuicao(valor);
            cb.setData(dataPickerMovimento.getValue().toString());
            movimentoSelecionado.setContribuicao(cb);
            carregarTableviewMovimento();
            limparCampos();
            atualizarSaldoAtual();
        }

        // Chama o serviço
        MovimentoService service = new MovimentoService();
        boolean atualizado = service.atualizarMovimento(movimentoSelecionado);

        if (atualizado) {
            AlertaUtil.mostrarInfo("Atualizacao de Dados","Movimento atualizado com sucesso!");
            carregarTableviewMovimento(); // atualiza a tabela
            limparCampos();
            listaBase();
            atualizarSaldoAtual();
        } else {
            AlertaUtil.mostrarErro("Erro ao atualizar o movimento!",
                    "Por favor verifique os dados inseridos");
        }
    }

    @FXML
    void btnExcluir(ActionEvent event) {
        Movimento movimentoSelecionado = tableviewBaitulMal.getSelectionModel().getSelectedItem();

        if (movimentoSelecionado == null) {
            AlertaUtil.mostrarErro("Seleção inválida", "Por favor, selecione um movimento para excluir.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Exclusão");
        alert.setHeaderText("Excluir Movimento");
        alert.setContentText("Tem certeza que deseja excluir este movimento?");
        Optional<ButtonType> resultado = alert.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean sucesso = movimentoService.excluirMovimento(movimentoSelecionado);

            if (sucesso) {
                //AlertaUtil.mostrarInfo("Sucesso",
                // "Movimento excluído e saldo atualizado com sucesso!");
                carregarTableviewMovimento();
                listaBase();
                atualizarSaldoAtual();
                limparCampos();
            } else {
                AlertaUtil.mostrarErro("Erro", "Não foi possível excluir o movimento.");
            }
        }
    }

    @FXML
    void btnInserir(ActionEvent event) {
        TipoMovimento tipoSelecionado = combomboxTipos.getValue();
        String valorTexto = txtValor.getText();

        // Evita NumberFormatException
        if (valorTexto == null || valorTexto.trim().isEmpty() || tipoSelecionado == null) {
            AlertaUtil.mostrarErro("Campo sem dados!","Certifique que preencheu todos os campos");
            return;
        }

        String tipo = tipoSelecionado.getDescricao();

        BigDecimal valor = new BigDecimal(String.valueOf(txtValor.getText()));
        LocalDate data = dataPickerMovimento.getValue();
        if (tipo.equalsIgnoreCase(TipoMovimento.SAIDA.getDescricao())) {
            if (validarEntradadedadosConsumo()) {
                Consumo c = new Consumo();
                c.setDescricao(txtConsumoContribuinte.getText());
                c.setValorConsumo(valor);
                c.setData(String.valueOf(data));
                c.setObservacao(txtObservacao.getText());

                movimentoService.registrarConsumo(c);
                carregarTableviewMovimento();
                limparCampos();
                atualizarSaldoAtual();
            }
        } else if (tipo.equalsIgnoreCase(TipoMovimento.ENTRADA.getDescricao())) {
            if (validarEntradadedadosContribuicao()) {
                Contribuicao co = new Contribuicao();
                co.setContribuinte(txtConsumoContribuinte.getText());
                co.setValorContribuicao(valor);
                co.setData(String.valueOf(data));
                co.setObservacao(txtObservacao.getText());

                movimentoService.registrarContribuicao(co);
                carregarTableviewMovimento();
                listaBase();
                limparCampos();
                atualizarSaldoAtual();
            }
        }
    }

    private void limparCampos() {
        txtConsumoContribuinte.clear();
        txtObservacao.clear();
        combomboxTipos.setValue(null);
        dataPickerMovimento.setValue(null);
        txtValor.clear();
        txtObservacao.clear();
    }

    /* util usado acima */
    private double parseDoubleSafe(String text) throws NumberFormatException {
        if (text == null) throw new NumberFormatException("null");
        return Double.parseDouble(text.trim().replace(",", "."));
    }

    @FXML
    void btnPesquisar(ActionEvent event) {
        String pesquisa = txtPesquisa.getText();
        List<Movimento> resultadoMov = movimentoDAO.buscarPorTipoData(pesquisa);
        tableviewBaitulMal.getItems().clear();
        limparCampos();
        if (!resultadoMov.isEmpty()) {
            tableviewBaitulMal.getItems().addAll(resultadoMov);
        } else {
            AlertaUtil.mostrarAviso("Movimento nao encontrado!", "Por favor verifique e redigite");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarCombboxTipo();
        listaBase();
        listenerTableViewTipo();
        carregarTableviewMovimento();
        pesquisarPorNome();
        atualizarSaldoAtual();
        tableViewListener();
    }

    private void listenerTableViewTipo() {

    }

    public void carregarCombboxTipo() {

        tipoObservableList = FXCollections.observableArrayList(TipoMovimento.values());
        //combomboxTipos.setItems(tipoObservableList);

        // Define um filtro dinâmico
        FilteredList<TipoMovimento> tiposFiltrados = new FilteredList<>(tipoObservableList, p -> true);

        combomboxTipos.setItems(tiposFiltrados);

        // Adiciona um listener para o editor de texto do ComboBox
        combomboxTipos.setEditable(true);
        combomboxTipos.getEditor().textProperty().addListener((obs,
                                                               oldValue, newValue) -> {
            final String filtro = newValue.toLowerCase();

            // Aplica filtro
            tiposFiltrados.setPredicate(tipo -> {
                if (filtro == null || filtro.isEmpty()) {
                    return true;
                }
                return tipo.getDescricao().toLowerCase().contains(filtro);
            });

            // Mostra o menu dropdown automaticamente
            if (!combomboxTipos.isShowing()) {
                combomboxTipos.show();
            }
        });

        // Corrige o comportamento de seleção para manter o objeto real
        combomboxTipos.setConverter(new StringConverter<TipoMovimento>() {
            @Override
            public String toString(TipoMovimento tipo) {
                return tipo != null ? tipo.getDescricao() : "";
            }

            @Override
            public TipoMovimento fromString(String string) {
                return tipoObservableList.stream()
                        .filter(t -> t.getDescricao().equalsIgnoreCase(string))
                        .findFirst().orElse(null);
            }
        });
    }

    public void carregarTableviewMovimento() {
        colunaCodigoMovimento.setCellValueFactory(new PropertyValueFactory<>("idmovimento"));
        colunaConsumoContribuinte.setCellValueFactory(cellData -> {
            Movimento m = cellData.getValue();
            if (m.getTipo().equalsIgnoreCase("Saida") && m.getConsumo() != null) {
                return new SimpleObjectProperty<>(m.getConsumo().getDescricao());
            } else if (m.getTipo().equalsIgnoreCase("Entrada") && m.getContribuicao() != null) {
                return new SimpleObjectProperty<>(m.getContribuicao().getContribuinte());
            } else {
                return new SimpleObjectProperty<>("Sem dados encontrados");
            }
        });
        colunaTipoMovimento.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colunaDataMovimento.setCellValueFactory(new PropertyValueFactory<>("data"));
        colunaValorMovimento.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colunaValorMovimento.setCellFactory(tc -> new TableCell<Movimento, BigDecimal>() {
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
        colunaValorTotalMovimento.setCellValueFactory(new PropertyValueFactory<>("saldoResultante"));
    }

    private void pesquisarPorNome(){
        //Listener para txtProcuraNome
        txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> movimentoFilteredList.setPredicate(movimentoItem->{
            if (newValue == null || newValue.isBlank()) {
                return true;
            }

            if (movimentoItem.getTipo() != null &&
                    movimentoItem.getTipo().toLowerCase().contains(newValue)) {
                return true;
            }

            if (movimentoItem.getConsumo() != null) {
                return movimentoItem.getConsumo().getDescricao() != null &&
                        movimentoItem.getConsumo().getDescricao()
                                .toLowerCase()
                                .contains(newValue.toLowerCase());
            } else if (movimentoItem.getContribuicao() != null) {
                return movimentoItem.getContribuicao().getContribuinte() != null &&
                        movimentoItem.getContribuicao().getContribuinte()
                                .toLowerCase()
                                .contains(newValue.toLowerCase());
            }

            return false;
        }));

    }

    private void listaBase(){
        movimentoList = movimentoDAO.listar();
        movimentoObservableList = FXCollections.observableArrayList(movimentoList);

        movimentoFilteredList = new FilteredList<>(movimentoObservableList, p -> true);
        tableviewBaitulMal.setItems(movimentoFilteredList);
    }

    public void openPaginas(Event event, String pagina,
                            LocalDate dataInicial, LocalDate dataFinal) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(pagina));
        Parent root = loader.load();

        RelatorioController rc = loader.getController();
        rc.setDatas(dataInicial, dataFinal);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("ISLAM_PARA_TODOS");
        stage.centerOnScreen();

        stage.showAndWait(); // sempre por último
    }

    //Os 2 metodos verifica a validacao dos dados digitado pelo usuario
    private boolean validarEntradadedadosConsumo() {
        StringBuilder erro = new StringBuilder();

        if (txtConsumoContribuinte.getText().trim().isEmpty()) {
            erro.append("Consumo/Contribuinte inválido!\n");
        }
        if (dataPickerMovimento.getValue() == null) {
            erro.append("Data inválida!\n");
        }
        if (txtValor.getText().trim().isEmpty()) {
            erro.append("Valor inválido!\n");
        }

        if (erro.length() > 0) {
            AlertaUtil.mostrarErro("Erro no Cadastro", erro.toString());
            return false;
        }
        return true;
    }

    private boolean validarEntradadedadosContribuicao() {
        StringBuilder erro = new StringBuilder();

        if (txtConsumoContribuinte.getText().trim().isEmpty()) {
            erro.append("Contribuinte inválido!\n");
        }
        if (dataPickerMovimento.getValue() == null) {
            erro.append("Data inválida!\n");
        }
        if (txtValor.getText().trim().isEmpty()) {
            erro.append("Valor inválido!\n");
        }

        if (erro.length() > 0) {
            AlertaUtil.mostrarErro("Erro no Cadastro", erro.toString());
            return false;
        }
        return true;
    }

    public void atualizarSaldoAtual() {
        ContaPrincipalDAO contaDAO = new ContaPrincipalDAO();
        ContaPrincipal contaAtualizada = contaDAO.buscarConta();

        lbSaldoAtual.setText(String.format("%.2f", contaAtualizada.getSaldo_atual()));
    }

    //Listener para preencher campos de textos (Combobox, txtValor )
    public void tableViewListener() {
        tableviewBaitulMal.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSelection) -> {
            if (newSelection == null) return;

            // Atualiza valor
            txtValor.setText(String.valueOf(newSelection.getValor()));

            // Atualiza  bComboBox
            String tipoSelecionado = newSelection.getTipo();
            if (tipoSelecionado != null) {
                String tipoNorm = normalizar(tipoSelecionado);
                combomboxTipos.getItems().stream()
                        .filter(t -> normalizar(t.getDescricao()).equals(tipoNorm))
                        .findFirst()
                        .ifPresentOrElse(combomboxTipos::setValue, () -> combomboxTipos.setValue(null));
            } else {
                combomboxTipos.setValue(null);
            }

            // Atualiza DatePicker
            String dataStr = newSelection.getData();
            if (dataStr != null) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate data = LocalDate.parse(dataStr, formatter);
                    dataPickerMovimento.setValue(data);
                } catch (DateTimeParseException e) {
                    System.err.println("Formato de data inválido: " + dataStr);
                }
            }

            if (newSelection.getContribuicao() != null ) {

                txtObservacaoLeitura.setText(newSelection.getContribuicao().getObservacao());
                txtConsumoContribuinte.setText(newSelection.getContribuicao().getContribuinte());

            } else if (newSelection.getConsumo() != null) {
                txtObservacaoLeitura.setText(newSelection.getConsumo().getObservacao());
                txtConsumoContribuinte.setText(newSelection.getConsumo().getDescricao());
            }else {
                txtObservacaoLeitura.setText("");
                txtConsumoContribuinte.setText("");
            }
        });
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "") // remove acentos
                .toLowerCase();
    }
}
