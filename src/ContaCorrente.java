public class ContaCorrente extends Conta {
    private static final long TARIFA_MENSAL = 15;
    private final long limiteChequeEspecial;

    public ContaCorrente(int numero, Cliente titular, long limiteChequeEspecial) {
        super(numero, titular);
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    @Override
    public String getTipo() { return "Corrente"; }

    @Override
    public long getSaldoDisponivel() {
        return getSaldo() + limiteChequeEspecial;
    }

    @Override
    public void aplicarRotinaMensal() {
        debitar(TARIFA_MENSAL, TipoTransacao.TARIFA);
    }

    public long getLimiteChequeEspecial() { return limiteChequeEspecial; }
}