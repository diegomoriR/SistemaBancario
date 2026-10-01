import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Cliente {
    private final String nome;
    private final String CPF;
    private final List<Conta> contas = new ArrayList<>();

    public Cliente(String nome, String CPF) {
        this.nome = nome;
        this.CPF = CPF;
    }

    public void adicionarConta(Conta conta) {
        contas.add(conta);
    }

    public String getNome() { return nome; }
    public String getCPF() { return CPF; }
    public List<Conta> getContas() { return Collections.unmodifiableList(contas); }

    @Override 
    public String toString() {
        return "Cliente: "+ nome +" CPF: " + CPF;   
    }
}