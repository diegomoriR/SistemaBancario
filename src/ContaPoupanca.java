public class ContaPoupanca extends Conta {
    private static final long TAXA_MENSAL = (long) 0.005; // 0,5% a.m.

    public ContaPoupanca(int numero, Cliente titular) {
        super(numero, titular);
    }

    @Override
    public String getTipo() { return "Poupança"; }

    @Override
    public long getSaldoDisponivel() {
        return getSaldo(); // poupança não tem limite: só saca o que tem
    }

    @Override
    public void aplicarRotinaMensal() {
        long rendimento = getSaldo() * TAXA_MENSAL;
        if (rendimento > 0.0) {
            creditar(rendimento, TipoTransacao.RENDIMENTO);
        }
    }
}
