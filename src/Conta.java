import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class Conta implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int numero;
    private final Cliente titular;
    private double saldo = 0;
    private final List<Transacao> extrato = new ArrayList<>();

    public Conta(int numero, Cliente titular) {
        this.numero = numero;
        this.titular = titular;
        titular.adicionarConta(this);
    }

    public abstract double getSaldoDisponivel();

    public abstract void aplicarRotinaMensal();

    public abstract String getTipo();

    public void depositar(double valor) {
        validarValor(valor);
        creditar(valor, TipoTransacao.DEPOSITO);
    }

    public void sacar(double valor) {
        validarValor(valor);
        debitar(valor, TipoTransacao.SAQUE);
    }

    public void transferir(Conta destino, double valor) {
        validarValor(valor);
        if ((destino == null) || (destino == this)) {
            throw new IllegalArgumentException("Conta de destino invalida para transferencia.");
        }
        this.debitar(valor, TipoTransacao.TRANSFERENCIA_ENVIADA);
        destino.creditar(valor, TipoTransacao.TRANSFERENCIA_RECEBIDA);
    }

    protected void creditar(double valor, TipoTransacao tipo) {
        saldo += valor;
        extrato.add(new Transacao(valor, tipo));
    }

    protected void debitar(double valor, TipoTransacao tipo) {
        if (valor <= getSaldoDisponivel()) {
            saldo -= valor;
            extrato.add(new Transacao(valor, tipo));
        } else {
            throw new IllegalArgumentException(
                    String.format("Saldo indisponivel. Disponivel: R$ %.2f", getSaldoDisponivel()));
        }
    }

    private static void validarValor(double valor) {
        if (valor <= 0 || Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new IllegalArgumentException("O valor da operacao deve ser maior que zero");
        }
    }

    public int getNumero() { return numero; }
    public Cliente getTitular() { return titular; }
    public double getSaldo() { return saldo; }
    public List<Transacao> getExtrato() { return Collections.unmodifiableList(extrato); }

    @Override
    public String toString() {
        return String.format("[%s #%d] %s | Saldo: R$ %.2f", getTipo(), numero, titular.getNome(), getSaldo());
    }
}
