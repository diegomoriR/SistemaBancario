import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Banco {
    private final Map<String, Cliente> clientes = new LinkedHashMap<>();   // chave: CPF
    private final Map<Integer, Conta> contas = new LinkedHashMap<>();      // chave: nº da conta
    private int proximoNumero = 1001;

    public Cliente cadastrarCliente(String nome, String CPF) {
        Cliente novo = new Cliente(nome, CPF); // lança IllegalArgumentException se nome/CPF inválidos
        if (clientes.containsKey(novo.getCPF())) {
            throw new IllegalStateException("Já existe cliente com o CPF " + novo.getCPF());
        }
        clientes.put(novo.getCPF(), novo);
        return novo;
    }

    public ContaCorrente abrirContaCorrente(String CPF, long limite) {
        if (limite < 0) {
            throw new IllegalArgumentException("O limite do cheque especial não pode ser negativo.");
        }
        ContaCorrente c = new ContaCorrente(proximoNumero++, buscarCliente(CPF), limite);
        contas.put(c.getNumero(), c);
        return c;
    }

    public ContaPoupanca abrirContaPoupanca(String CPF) {
        ContaPoupanca c = new ContaPoupanca(proximoNumero++, buscarCliente(CPF));
        contas.put(c.getNumero(), c);
        return c;
    }

    public Cliente buscarCliente(String CPF) {
        Cliente c = clientes.get(CPF);
        if (c == null) throw new NoSuchElementException("Cliente com CPF " + CPF + " não encontrado.");
        return c;
    }

    public Conta buscarConta(int numero) {
        Conta c = contas.get(numero);
        if (c == null) throw new NoSuchElementException("Conta número " + numero + " não encontrada.");
        return c;
    }

    /** Polimorfismo em ação: o loop não sabe (nem precisa saber) qual é o tipo de cada conta. */
    public void fechamentoMensal() {
        for (Conta conta : contas.values()) {
            conta.aplicarRotinaMensal();
        }
    }

    public Iterable<Conta> listarContas() { return contas.values(); }
}
