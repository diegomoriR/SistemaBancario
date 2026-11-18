import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


public class Banco implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, Cliente> clientes = new LinkedHashMap<>();   
    private final Map<Integer, Conta> contas = new LinkedHashMap<>();      
    private int proximoNumero = 1001;                                     

    private transient boolean alterado;

    

    public synchronized Cliente cadastrarCliente(String nome, String CPF) {
        Cliente novo = new Cliente(nome, CPF);
        if (clientes.containsKey(novo.getCPF())) {
            throw new IllegalStateException("Já existe cliente com o CPF " + Cliente.formatarCPF(novo.getCPF()));
        }
        clientes.put(novo.getCPF(), novo);
        alterado = true;
        return novo;
    }

    public synchronized ContaCorrente abrirContaCorrente(String CPF, double limite) {
        if (limite < 0) {
            throw new IllegalArgumentException("O limite do cheque especial não pode ser negativo.");
        }
        ContaCorrente c = new ContaCorrente(proximoNumero++, buscarCliente(CPF), limite);
        contas.put(c.getNumero(), c);
        alterado = true;
        return c;
    }

    public synchronized ContaPoupanca abrirContaPoupanca(String CPF) {
        ContaPoupanca c = new ContaPoupanca(proximoNumero++, buscarCliente(CPF));
        contas.put(c.getNumero(), c);
        alterado = true;
        return c;
    }


    public synchronized Cliente buscarCliente(String CPF) {
        Cliente c = clientes.get(Cliente.normalizarCPF(CPF));
        if (c == null) throw new NoSuchElementException("Cliente com CPF " + CPF + " não encontrado.");
        return c;
    }

    public synchronized Conta buscarConta(int numero) {
        Conta c = contas.get(numero);
        if (c == null) throw new NoSuchElementException("Conta número " + numero + " não encontrada.");
        return c;
    }

    public synchronized List<Conta> listarContas() { return new ArrayList<>(contas.values()); }

    public synchronized List<Cliente> listarClientes() { return new ArrayList<>(clientes.values()); }

    public synchronized List<Integer> numerosDasContas() { return new ArrayList<>(contas.keySet()); }

    public synchronized List<Transacao> extrato(int numero) {
        return new ArrayList<>(buscarConta(numero).getExtrato());
    }

    public synchronized double saldoTotal() {
        double total = 0;
        for (Conta c : contas.values()) total += c.getSaldo();
        return total;
    }


    public synchronized double depositar(int numero, double valor) {
        Conta c = buscarConta(numero);
        c.depositar(valor);
        alterado = true;
        return c.getSaldo();
    }

    public synchronized double sacar(int numero, double valor) {
        Conta c = buscarConta(numero);
        c.sacar(valor);
        alterado = true;
        return c.getSaldo();
    }

    public synchronized void transferir(int origem, int destino, double valor) {
        buscarConta(origem).transferir(buscarConta(destino), valor);
        alterado = true;
    }

    public synchronized List<String> fechamentoMensal() {
        List<String> falhas = new ArrayList<>();
        for (Conta conta : contas.values()) {
            try {
                conta.aplicarRotinaMensal();
            } catch (RuntimeException e) {
                falhas.add("Conta #" + conta.getNumero() + " (" + conta.getTitular().getNome() + "): " + e.getMessage());
            }
        }
        alterado = true;
        return falhas;
    }


    public synchronized boolean precisaSalvar() { return alterado; }

    synchronized void marcarSalvo() { alterado = false; }
}
