package mbtec.baitulmal02.service;


import mbtec.baitulmal02.dao.ConsumoDAO;
import mbtec.baitulmal02.dao.ContaPrincipalDAO;
import mbtec.baitulmal02.dao.ContribuicaoDAO;
import mbtec.baitulmal02.dao.MovimentoDAO;
import mbtec.baitulmal02.model.Consumo;
import mbtec.baitulmal02.model.ContaPrincipal;
import mbtec.baitulmal02.model.Contribuicao;
import mbtec.baitulmal02.model.Movimento;
import mbtec.baitulmal02.utilitario.TipoMovimento;

import java.math.BigDecimal;

public class MovimentoService {
    private final ContaPrincipalDAO contaDAO = new ContaPrincipalDAO();
    private final ConsumoDAO consumoDAO = new ConsumoDAO();
    private final ContribuicaoDAO contribuicaoDAO = new ContribuicaoDAO();
    private final MovimentoDAO movimentoDAO = new MovimentoDAO();

    public void registrarConsumo(Consumo c) {
        System.out.println("Consumo invocado!");
        ContaPrincipal conta = contaDAO.buscarConta();
        //BigDecimal novoSaldo = conta.getSaldo_atual() - c.getValorConsumo();
        BigDecimal novoSaldo = conta.getSaldo_atual().subtract(c.getValorConsumo());

        consumoDAO.inserirConsumo(c);

        Movimento mov = new Movimento();
        mov.setTipo(TipoMovimento.SAIDA.getDescricao());
        mov.setValor(c.getValorConsumo());
        mov.setData(c.getData());
        mov.setSaldoResultante(novoSaldo);
        mov.setConsumo(c);

        int idMov = movimentoDAO.inserirMovimento(mov);
        contaDAO.atualizarSaldo(novoSaldo, idMov);
    }

    public void registrarContribuicao(Contribuicao contribuicao) {
        System.out.println("Contribuicao invocada!");
        ContaPrincipal conta = contaDAO.buscarConta();
        BigDecimal novoSaldo = conta.getSaldo_atual().add(contribuicao.getValorContribuicao());

        contribuicaoDAO.inserirContribuicao(contribuicao); // retorna id gerado

        Movimento mov = new Movimento();
        mov.setTipo(TipoMovimento.ENTRADA.getDescricao());
        mov.setValor(contribuicao.getValorContribuicao());
        mov.setData(contribuicao.getData());
        mov.setSaldoResultante(novoSaldo);
        mov.setContribuicao(contribuicao);

        int idMov = movimentoDAO.inserirMovimento(mov);
        contaDAO.atualizarSaldo(novoSaldo, idMov);
    }

    /**
     * O metodo reage e atualiza saldo disponivel, quando ha uma exclusao de movimentos (saida ou entrada)
     * @param movimento
     * @return
     */
    public boolean excluirMovimento(Movimento movimento) {
        if (movimento == null) return false;

        System.out.println("Excluindo movimento: " + movimento);

        ContaPrincipal conta = contaDAO.buscarConta();
        BigDecimal novoSaldo = conta.getSaldo_atual();

        //Corrige saldo conforme tipo
        if (movimento.getTipo().equalsIgnoreCase(TipoMovimento.SAIDA.getDescricao())) {
            novoSaldo = novoSaldo.subtract(movimento.getValor()); // reverte o acréscimo
            if (movimento.getContribuicao() != null) {
                contribuicaoDAO.excluirContribuicao(movimento.getContribuicao().getIdcontribuicao());
            }
        } else if (movimento.getTipo().equalsIgnoreCase(TipoMovimento.ENTRADA.getDescricao())) {
            novoSaldo = novoSaldo.add(movimento.getValor()); // reverte o desconto
            if (movimento.getConsumo() != null) {
                consumoDAO.excluirConsumo(movimento.getConsumo().getIdconsumo());
            }
        }

        boolean sucesso = movimentoDAO.excluir(movimento);

        if (sucesso) {
            contaDAO.atualizarSaldo(novoSaldo, movimento.getIdmovimento());
            return true;
        } else {
            System.out.println("Falha ao excluir movimento");
            return false;
        }
    }

    public boolean atualizarMovimento(Movimento movimentoAtualizado) {
       try {

           Movimento movimentoOriginal = movimentoDAO.buscarPorId(movimentoAtualizado.getIdmovimento());
           if (movimentoOriginal == null) {
               System.out.println("Movimento não encontrado!");
               return false;
           }
           if (movimentoAtualizado.getTipo().equalsIgnoreCase(TipoMovimento.SAIDA.getDescricao())
                   && movimentoAtualizado.getConsumo() == null) {
               System.out.println("Erro: SAÍDA sem consumo!");
               return false;
           }

           if (movimentoAtualizado.getTipo().equalsIgnoreCase(TipoMovimento.ENTRADA.getDescricao())
                   && movimentoAtualizado.getContribuicao() == null) {
               System.out.println("Erro: ENTRADA sem contribuição!");
               return false;
           }

           ContaPrincipal conta = contaDAO.buscarConta();
           BigDecimal saldoAtual = conta.getSaldo_atual();
           BigDecimal novoSaldo = saldoAtual;

           // Se mudou de tipo, remover o registro anterior
           if (!movimentoOriginal.getTipo().equalsIgnoreCase(movimentoAtualizado.getTipo())) {
               System.out.println("Tipo de movimento alterado — limpando vínculos antigos...");

               if (movimentoOriginal.getConsumo() != null) {
                   consumoDAO.excluirConsumo(movimentoOriginal.getConsumo().getIdconsumo());
                   movimentoDAO.limparConsumo(movimentoOriginal.getIdmovimento());
               }

               if (movimentoOriginal.getContribuicao() != null) {
                   contribuicaoDAO.excluirContribuicao(movimentoOriginal.getContribuicao().getIdcontribuicao());
                   movimentoDAO.limparContribuicao(movimentoOriginal.getIdmovimento());
               }
           }

           // Atualiza conforme o novo tipo
           if (movimentoAtualizado.getTipo().equalsIgnoreCase(TipoMovimento.SAIDA.getDescricao())) {
               if (movimentoAtualizado.getConsumo().getIdconsumo() == 0) {
                   // novo consumo — inserir e obter o id gerado
                   int novoId = consumoDAO.inserirConsumo(movimentoAtualizado.getConsumo());
                   movimentoAtualizado.getConsumo().setIdconsumo(novoId);
               } else {
                   consumoDAO.editar(movimentoAtualizado.getConsumo());
               }
               novoSaldo = saldoAtual.subtract(movimentoAtualizado.getValor());

               movimentoDAO.editar(movimentoAtualizado);
               contaDAO.atualizarSaldo(novoSaldo, movimentoAtualizado.getIdmovimento());

           } else if (movimentoAtualizado.getTipo().equalsIgnoreCase(TipoMovimento.ENTRADA.getDescricao())) {
               if (movimentoAtualizado.getContribuicao().getIdcontribuicao() == 0) {
                   int novoId = contribuicaoDAO.inserirContribuicao(movimentoAtualizado.getContribuicao());
                   movimentoAtualizado.getContribuicao().setIdcontribuicao(novoId);
               } else {
                   contribuicaoDAO.editar(movimentoAtualizado.getContribuicao());
               }
               novoSaldo = saldoAtual.add(movimentoAtualizado.getValor());

               movimentoDAO.editar(movimentoAtualizado);
               contaDAO.atualizarSaldo(novoSaldo, movimentoAtualizado.getIdmovimento());
           }

           System.out.println("Movimento e saldo atualizados com sucesso. Novo saldo: " + novoSaldo);
           return true;
       }
       catch (Exception e)
       {
           System.out.println("Falha na atualizacao de movimento: "+e.getMessage());
           return false;
       }
    }


}
