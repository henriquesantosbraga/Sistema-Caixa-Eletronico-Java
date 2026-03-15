package Sistemas;

import javax.swing.JOptionPane;
import java.util.*;
import java.io.*;

public class CaixaEletronico {
    private int[] Notas;
    private int[] ValoresNotas;
    private int TotalInicial;
    private List<Integer> Saques;

    public CaixaEletronico() {
        this.notas = new int[7];
        this.valoresNotas = new int[]{2, 5, 10, 20, 50, 100, 200};
        this.saques = new ArrayList<>();
        this.totalInicial = 0;
    }

    public void iniciar() {
        int opcao;
        do {
            opcao = menuPrincipal();
            
            switch (opcao) {
                case 1:
                    carregarNotas();
                    break;
                case 2:
                    retirarNotas();
                    break;
                case 3:
                    estatistica();
                    break;
                case 9:
                    JOptionPane.showMessageDialog(null, 
                        "Encerrando o sistema...", "Caixa Eletrônico", JOptionPane.INFORMATION_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } while (opcao != 9);
    }

     private int menuPrincipal() {
        String input = JOptionPane.showInputDialog(null,
            "=== CAIXA ELETRÔNICO ===\n\n" +
            "1 - Carregar Notas\n" +
            "2 - Retirar Notas\n" +
            "3 - Estatística\n" +
            "9 - Fim\n\n" +
            "Escolha uma opção:",
            "Menu Principal",
            JOptionPane.QUESTION_MESSAGE);
        
        if (input == null) return 9; // Cancelar encerra
        
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void carregarNotas() {
        for (int i = 0; i < notas.length; i++) {
            notas[i] = 100;
            totalInicial += valoresNotas[i] * 100;
        }
        
        JOptionPane.showMessageDialog(null,
            "Notas carregadas com sucesso!\n" +
            "Total inicial no caixa: R$ " + totalInicial + ",00",
            "Carregar Notas",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void retirarNotas() {
        if (!caixaCarregado()) {
            JOptionPane.showMessageDialog(null, "Carregue as notas primeiro!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
    }

    int retiradas = 0;
    StringBuilder historico = new StringBuilder();
        
    while (retiradas < 100 && temNotasDisponiveis()) {
        String input = JOptionPane.showInputDialog(null,
            "--- RETIRADA DE NOTAS ---\n" +
            "Retiradas realizadas: " + retiradas + "/100\n" +
            "Saldo disponível: R$ " + calcularSaldoTotal() + ",00\n\n" +
           "Digite o valor do saque (ou 0 para sair):",
            "Saque",
            JOptionPane.QUESTION_MESSAGE);
            
        if (input == null) break; // Cancelar sai do loop
            
        try {
            int valor = Integer.parseInt(input);
                
            if (valor == 0) {
                break;
                }
                
            if (!validarValor(valor)) {
                JOptionPane.showMessageDialog(null,
                    "VALOR SOLICITADO INVÁLIDO\n" +
                    "O valor deve estar entre R$ 2,00 e R$ 3.000,00",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                
            if (valor > calcularSaldoTotal()) {
                JOptionPane.showMessageDialog(null,
                    "EXCEDEU O LIMITE DO CAIXA\n" +
                    "Saldo disponível: R$ " + calcularSaldoTotal() + ",00", "Erro", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                
            if (calcularSaque(valor)) {
                saques.add(valor);
                retiradas++;
                historico.append("Saque ").append(retiradas).append(": R$ ").append(valor).append(",00\n");
            } else {
                JOptionPane.showMessageDialog(null,
                    "VALOR SOLICITADO NÃO PODE SER SACADO\n\n" + getNotasDisponiveisString(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                    "Valor inválido! Digite apenas números.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
    }
    
    if (retiradas > 0) {
        JOptionPane.showMessageDialog(null, "Operação de saques finalizada!\n\n" + "Total de saques realizados: " + retiradas + "\n" + historico.toString(), "Resumo dos Saques", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private boolean caixaCarregado() {
        for (int nota : notas) {
            if (nota > 0) return true;
        }
        return false;
    }

    private boolean temNotasDisponiveis() {
        for (int nota : notas) {
            if (nota > 0) return true;
        }
        return false;
    }

    private boolean validarValor(int valor) {
        return valor >= 2 && valor <= 3000;
    }

    private int calcularSaldoTotal() {
        int total = 0;
        for (int i = 0; i < notas.length; i++) {
            total += notas[i] * valoresNotas[i];
        }
        return total;
    }

    private boolean calcularSaque(int valor) {
        int[] notasUsadas = new int[notas.length];
        int valorRestante = valor;
        
        // Tenta usar as notas maiores primeiro
        for (int i = notas.length - 1; i >= 0; i--) {
            if (notas[i] > 0) {
                int quantidadeNecessaria = Math.min(valorRestante / valoresNotas[i], notas[i]);
                notasUsadas[i] = quantidadeNecessaria;
                valorRestante -= quantidadeNecessaria * valoresNotas[i];
            }
        }
        
        // Se conseguiu formar o valor exato
        if (valorRestante == 0) {
            // Atualiza o caixa
            for (int i = 0; i < notas.length; i++) {
                notas[i] -= notasUsadas[i];
            }
            
            exibirNotasSaque(notasUsadas);
            return true;
        }
        
        return false;
    }

    private void exibirNotasSaque(int[] notasUsadas) {
        StringBuilder mensagem = new StringBuilder("Notas entregues:\n\n");
        
        for (int i = 0; i < notasUsadas.length; i++) {
            if (notasUsadas[i] > 0) {
                mensagem.append("• ")
                       .append(notasUsadas[i])
                       .append(" nota(s) de R$ ")
                       .append(valoresNotas[i])
                       .append(",00\n");
            }
        }
        
        JOptionPane.showMessageDialog(null,
            mensagem.toString(),
            "Saque Realizado",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private String getNotasDisponiveisString() {
        StringBuilder mensagem = new StringBuilder("Cédulas disponíveis no caixa:\n\n");
        
        for (int i = 0; i < notas.length; i++) {
            if (notas[i] > 0) {
                mensagem.append("• R$ ")
                       .append(valoresNotas[i])
                       .append(",00: ")
                       .append(notas[i])
                       .append(" cédulas\n");
            }
        }
        
        return mensagem.toString();
    }

    private void estatistica() {
        if (saques.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Nenhum saque realizado.",
                "Estatísticas",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Cálculos estatísticos
        int totalSaques = saques.stream().mapToInt(Integer::intValue).sum();
        double mediaSaques = totalSaques / (double) saques.size();
        int saldoFinal = calcularSaldoTotal();
        int totalSobra = totalInicial - totalSaques;
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== ESTATÍSTICAS ===\n\n");
        stats.append("• Valor total inicial: R$ ").append(totalInicial).append(",00\n");
        stats.append("• Média dos saques: R$ ").append(String.format("%.2f", mediaSaques)).append("\n");
        stats.append("• Valor total dos saques: R$ ").append(totalSaques).append(",00\n");
        stats.append("• Quantidade de saques: ").append(saques.size()).append("\n");
        stats.append("• Valor total das sobras: R$ ").append(saldoFinal).append(",00");
        
        JOptionPane.showMessageDialog(null,
            stats.toString(),
            "Estatísticas",
            JOptionPane.INFORMATION_MESSAGE);
        
        // Salvar em arquivo
        salvarResumo(totalSaques, mediaSaques, saldoFinal);
    }

    private void salvarResumo(int totalSaques, double mediaSaques, int saldoFinal) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("Resumo.txt"))) {
            writer.println("=== RESUMO DO CAIXA ELETRÔNICO ===");
            writer.println("Valor total inicial: R$ " + totalInicial + ",00");
            writer.println("Média dos saques: R$ " + String.format("%.2f", mediaSaques));
            writer.println("Valor total dos saques: R$ " + totalSaques + ",00");
            writer.println("Quantidade de saques: " + saques.size());
            writer.println("Valor total das sobras: R$ " + saldoFinal + ",00");
            
            JOptionPane.showMessageDialog(null,
                "Resumo salvo no arquivo 'Resumo.txt'",
                "Arquivo Gerado",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao salvar arquivo: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        CaixaEletronico caixa = new CaixaEletronico();
        caixa.iniciar();
    }
}
