import java.util.Date;


public abstract class Transacao {
    private static long proximoID = 1;

    private long ID = 0;
    private long valor;
    private Date dataHora;

    public Transacao(long valor) {
        this.valor = valor;
    }

    public void executar() throws BancoException {
        if (ID != 0) {
            throw new IllegalStateException("Transacao "+ ID + "ja foi executada.");
        }
        efetivar()
    }
}