import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class Menu {

    private static class FimDaEntrada extends RuntimeException {
        @java.io.Serial private static final long serialVersionUID = 1L;
    }

    private final Banco banco;
    private final Persistencia persistencia;
    private final Scanner in;

    public Menu(Banco banco, Persistencia persistencia, Scanner in) {
        this.banco = banco;
        this.persistencia = persistencia;
        this.in = in;
    }

    public void executar() {
        while (true) {
            mostrarOpcoes();
            int op;
            try {
                op = lerInt("Escolha uma opção: ");
            } catch (FimDaEntrada e) {
                op = 0;
            }
            try {
                switch (op) {
                    case 1 -> cadastrarCliente();
                    case 2 -> abrirContaCorrente();
                    case 3 -> abrirContaPoupanca();
                    case 4 -> depositar();
                    case 5 -> sacar();
                    case 6 -> transferir();
                    case 7 -> verExtrato();
                    case 8 -> listarContas();
                    case 9 -> listarClientes();
                    case 10 -> fechamentoMensal();
                    case 11 -> salvarAgora();
                    case 0 -> {
                        salvarAgora();
                        System.out.println("Até logo!");
                        return;
                    }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (FimDaEntrada e) {
                salvarAgora();
                return;
            } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
                System.out.println(">> Erro: " + e.getMessage());
            }
        }
    }

    private void mostrarOpcoes() {
        System.out.println();
        System.out.println("=========== SISTEMA BANCÁRIO ===========");
        System.out.println(" Último salvamento: " + persistencia.descricaoUltimoSalvamento()
                + (banco.precisaSalvar() ? " (há alterações pendentes)" : ""));
        System.out.println("----------------------------------------");
        System.out.println("  1 - Cadastrar cliente");
        System.out.println("  2 - Abrir conta corrente");
        System.out.println("  3 - Abrir conta poupança");
        System.out.println("  4 - Depositar");
        System.out.println("  5 - Sacar");
        System.out.println("  6 - Transferir");
        System.out.println("  7 - Ver extrato");
        System.out.println("  8 - Listar contas");
        System.out.println("  9 - Listar clientes");
        System.out.println(" 10 - Fechamento mensal (tarifa/rendimento)");
        System.out.println(" 11 - Salvar agora");
        System.out.println("  0 - Salvar e sair");
        System.out.println("========================================");
    }

    

    private void cadastrarCliente() {
        String nome = lerTexto("Nome: ");
        String cpf = lerTexto("CPF: ");
        System.out.println(">> Cadastrado: " + banco.cadastrarCliente(nome, cpf));
    }

    private void abrirContaCorrente() {
        String cpf = lerTexto("CPF do titular: ");
        banco.buscarCliente(cpf);   // falha cedo, antes de pedir o limite
        double limite = lerValor("Limite do cheque especial (R$): ");
        System.out.println(">> Conta aberta: " + banco.abrirContaCorrente(cpf, limite));
    }

    private void abrirContaPoupanca() {
        String cpf = lerTexto("CPF do titular: ");
        System.out.println(">> Conta aberta: " + banco.abrirContaPoupanca(cpf));
    }

    private void depositar() {
        int numero = lerInt("Número da conta: ");
        banco.buscarConta(numero);
        double valor = lerValor("Valor do depósito (R$): ");
        System.out.printf(">> Depósito realizado. Novo saldo: R$ %.2f%n", banco.depositar(numero, valor));
    }

    private void sacar() {
        int numero = lerInt("Número da conta: ");
        Conta c = banco.buscarConta(numero);
        System.out.printf("   Disponível: R$ %.2f%n", c.getSaldoDisponivel());
        double valor = lerValor("Valor do saque (R$): ");
        System.out.printf(">> Saque realizado. Novo saldo: R$ %.2f%n", banco.sacar(numero, valor));
    }

    private void transferir() {
        int origem = lerInt("Conta de origem: ");
        banco.buscarConta(origem);
        int destino = lerInt("Conta de destino: ");
        banco.buscarConta(destino);
        double valor = lerValor("Valor (R$): ");
        banco.transferir(origem, destino, valor);
        System.out.println(">> Transferência realizada.");
        System.out.println("   " + banco.buscarConta(origem));
        System.out.println("   " + banco.buscarConta(destino));
    }

    private void verExtrato() {
        int numero = lerInt("Número da conta: ");
        Conta c = banco.buscarConta(numero);
        List<Transacao> extrato = banco.extrato(numero);
        System.out.println(c);
        if (extrato.isEmpty()) {
            System.out.println("   (sem movimentações)");
        } else {
            extrato.forEach(t -> System.out.println("   " + t));
        }
    }

    private void listarContas() {
        List<Conta> contas = banco.listarContas();
        if (contas.isEmpty()) {
            System.out.println("Nenhuma conta cadastrada.");
            return;
        }
        contas.forEach(System.out::println);
        System.out.printf("Total em custódia: R$ %.2f%n", banco.saldoTotal());
    }

    private void listarClientes() {
        List<Cliente> clientes = banco.listarClientes();
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        for (Cliente cl : clientes) {
            System.out.println(cl);
            if (cl.getContas().isEmpty()) System.out.println("   (sem contas)");
            cl.getContas().forEach(c -> System.out.println("   " + c));
        }
    }

    private void fechamentoMensal() {
        String resp = lerTexto("Aplicar tarifas e rendimentos em TODAS as contas? (s/n): ");
        if (!resp.equalsIgnoreCase("s")) {
            System.out.println("Cancelado.");
            return;
        }
        List<String> falhas = banco.fechamentoMensal();
        banco.listarContas().forEach(System.out::println);
        if (!falhas.isEmpty()) {
            System.out.println("Contas que não puderam ser processadas:");
            falhas.forEach(f -> System.out.println("   " + f));
        }
    }

    private void salvarAgora() {
        try {
            persistencia.salvar(banco);
            System.out.println(">> Dados salvos em " + persistencia.getArquivo().toAbsolutePath());
        } catch (IOException e) {
            System.out.println(">> ERRO ao salvar: " + e.getMessage());
        }
    }

    // ---------------- Leitura de entrada ----------------

    private String lerTexto(String rotulo) {
        System.out.print(rotulo);
        if (!in.hasNextLine()) throw new FimDaEntrada();
        return in.nextLine().trim();
    }

    private int lerInt(String rotulo) {
        while (true) {
            String s = lerTexto(rotulo);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro.");
            }
        }
    }

    private double lerValor(String rotulo) {
        while (true) {
            String s = lerTexto(rotulo).replace("R$", "").trim();
            if (s.contains(",")) s = s.replace(".", "").replace(",", ".");
            try {
                double v = Double.parseDouble(s);
                if (Double.isNaN(v) || Double.isInfinite(v)) throw new NumberFormatException();
                return Math.round(v * 100) / 100.0;   // arredonda para centavos
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Exemplo: 150,00");
            }
        }
    }
}
