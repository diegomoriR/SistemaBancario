import java.io.Serial;

public class ContaPoupanca extends Conta {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final double TAXA_MENSAL = 0.005;

    public ContaPoupanca(int numero, Cliente titular) {
        super(numero, titular);
    }

    @Override
    public String getTipo() { return "Poupança"; }

    @Override
    public double getSaldoDisponivel() {
        return getSaldo();
    }

    @Override
    public void aplicarRotinaMensal() {
        double rendimento = getSaldo() * TAXA_MENSAL;
        if (rendimento > 0.0) {
            creditar(rendimento, TipoTransacao.RENDIMENTO);
        }
    }
}
