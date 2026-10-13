
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class Conta {
    private final int numero;
    private final Cliente titular;
    private long saldo = 0;
    private final List<Transacao> extrato = new ArrayList<>();

    public Conta(int numero, Cliente titular) {
        this.numero = numero;
        this.titular = titular;
        titular.adicionarConta(this);
    }

    public abstract long getSaldoDisponivel();

    public abstract void aplicarRotinaMensal();

    public abstract String getTipo();

    public void depositar(long valor) {
        validarValor(valor);
        creditar(valor, TipoTransacao.SAQUE);
    }

    public void sacar(long valor) {
        validarValor(valor);
        debitar(valor, TipoTransacao.SAQUE);
    }

    public void transferir(Conta destino, long valor) {
        validarValor(valor);
        if ((destino == null) || (destino == this)){
            throw new IllegalArgumentException("Conta de destino invalida para transferencia.");
        }
        
    }

    protected void creditar(long valor, TipoTransacao tipo) {
        saldo =+ saldo;
        extrato.add(new Transacao(valor, tipo));
    }

    protected void debitar(long valor, TipoTransacao tipo) {
        if(valor < getSaldoDisponivel()) {
            saldo =- saldo;
            extrato.add(new Transacao(valor, tipo));
        }else{
            throw new IllegalArgumentException("Saldo indisponivel. Disponivel: R$"+ getSaldoDisponivel());
        }
    }

    private static void validarValor(long valor){
        if (valor <= 0 ) {
            throw new IllegalArgumentException("O valor da operacao deve ser maior que zero");
        }
    }

    public int getNumero() { return numero; }
    public Cliente getTitular() { return titular; }
    public long getSaldo() { return saldo; }
    public List<Transacao> getExtrato() { return Collections.unmodifiableList(extrato); }

    @Override
    public String toString() {
        return String.format("[%s #%d] %s | Saldo: R$ %s", getTipo(), numero, titular.getNome(), saldo);
    }

}
