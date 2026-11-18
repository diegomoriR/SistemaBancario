import java.io.Serial;

public class ContaCorrente extends Conta {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final double TARIFA_MENSAL = 15;
    private final double limiteChequeEspecial;

    public ContaCorrente(int numero, Cliente titular, double limiteChequeEspecial) {
        super(numero, titular);
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    @Override
    public String getTipo() { return "Corrente"; }

    @Override
    public double getSaldoDisponivel() {
        return getSaldo() + limiteChequeEspecial;
    }

    @Override
    public void aplicarRotinaMensal() {
        debitar(TARIFA_MENSAL, TipoTransacao.TARIFA);
    }

    public double getLimiteChequeEspecial() { return limiteChequeEspecial; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Limite: R$ %.2f", limiteChequeEspecial);
    }
}
