import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String nome;
    private final String CPF;            
    private final List<Conta> contas = new ArrayList<>();

    public Cliente(String nome, String CPF) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do cliente não pode ser vazio.");
        }
        this.nome = nome.trim();
        this.CPF = normalizarCPF(CPF);
    }

    /**
     * Remove pontos/traço e valida os dígitos verificadores.
     * Assim "529.982.247-25" e "52998224725" são o mesmo cliente.
     */
    public static String normalizarCPF(String cpf) {
        if (cpf == null) throw new IllegalArgumentException("CPF não informado.");
        String d = cpf.replaceAll("\\D", "");
        if (d.length() != 11 || d.chars().distinct().count() == 1) {
            throw new IllegalArgumentException("CPF inválido: " + cpf);
        }
        for (int pos = 9; pos <= 10; pos++) {
            int soma = 0;
            for (int i = 0; i < pos; i++) soma += (d.charAt(i) - '0') * (pos + 1 - i);
            int dv = (soma * 10) % 11 % 10;
            if (dv != d.charAt(pos) - '0') throw new IllegalArgumentException("CPF inválido: " + cpf);
        }
        return d;
    }

    public static String formatarCPF(String d) {
        return d.substring(0, 3) + "." + d.substring(3, 6) + "." + d.substring(6, 9) + "-" + d.substring(9);
    }

    void adicionarConta(Conta conta) {      // só o pacote (Conta) chama
        contas.add(conta);
    }

    public String getNome() { return nome; }
    public String getCPF() { return CPF; }
    public List<Conta> getContas() { return Collections.unmodifiableList(contas); }

    @Override
    public String toString() {
        return "Cliente: " + nome + " CPF: " + formatarCPF(CPF);
    }
}
