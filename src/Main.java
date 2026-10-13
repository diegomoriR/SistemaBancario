
import java.math.BigDecimal;
import java.util.NoSuchElementException;

public class Main {

    public static void main(String[] args) {
        Banco banco = new Banco();
        ContaCorrente cc;
        ContaPoupanca cp;

        titulo("1. Cadastro de clientes e contas");
        try {
            banco.cadastrarCliente("Diego Mori", "529.982.247-25");
            banco.cadastrarCliente("Maria Souza", "111.444.777-35");
            cc = banco.abrirContaCorrente("529.982.247-25", (long)500.00);
            cp = banco.abrirContaPoupanca("111.444.777-35");
            System.out.println(cc);
            System.out.println(cp);
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
            System.out.println("Falha no setup: " + e.getMessage());
            return; // sem setup não há demo
        }

        titulo("2. Operações válidas");
        executar("Depósito R$ 1000 na corrente", () -> cc.depositar((long)1000.00));
        executar("Saque R$ 200 da corrente", () -> cc.sacar((long)200.00));
        executar("Transferência R$ 300 corrente -> poupança", () -> cc.transferir(cp, (long)300.00));

        titulo("3. Erros tratados com try/catch");
        executar("Saque R$ 5000 na poupança", () -> cp.sacar((long)5000.00));
        executar("Depósito de valor negativo", () -> cc.depositar((long)-50));
        executar("Transferir para a própria conta", () -> cc.transferir(cc, (long)10));
        executar("Buscar conta inexistente", () -> banco.buscarConta(9999));
        executar("Cliente com CPF duplicado", () -> banco.cadastrarCliente("Diego Rodrigues", "529.982.247-25"));
        executar("Abrir conta p/ CPF não cadastrado", () -> banco.abrirContaPoupanca("390.533.447-05"));

        titulo("4. try / catch / finally");
        try {
            System.out.println("Tentando sacar R$ 99999 da corrente...");
            cc.sacar((long)99999.00);
            System.out.println("Esta linha não executa: a exceção pula direto para o catch.");
        } catch (IllegalStateException e) {
            System.out.println("catch -> " + e.getMessage());
        } finally {
            System.out.println("finally -> executa sempre (ex.: fechar recurso, registrar log).");
        }

        titulo("5. Polimorfismo: fechamento mensal (tarifa vs. rendimento)");
        banco.fechamentoMensal();
        for (Conta c : banco.listarContas()) System.out.println(c);

        titulo("6. Extratos");
        System.out.println("Poupança:");
        cp.getExtrato().forEach(System.out::println);
        System.out.println("Corrente:");
        cc.getExtrato().forEach(System.out::println);
    }

    /** Executa uma operação e mostra o resultado, tratando cada tipo de exceção nativa. */
    private static void executar(String descricao, Runnable operacao) {
        try {
            operacao.run();
            System.out.println("[OK]   " + descricao);
        } catch (IllegalArgumentException e) {
            System.out.println("[ARGUMENTO INVÁLIDO] " + descricao + " -> " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("[ESTADO INVÁLIDO]    " + descricao + " -> " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("[NÃO ENCONTRADO]     " + descricao + " -> " + e.getMessage());
        } catch (Exception e) { // rede de segurança: qualquer outra coisa inesperada
            System.out.println("[ERRO INESPERADO]    " + descricao + " -> " + e);
        }
    }

    private static void titulo(String t) {
        System.out.println("\n=== " + t + " ===");
    }
}