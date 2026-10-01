import java.util.Date;


public abstract class Transacao {
    private static long proximoID = 1;

    private long ID = 0;
    private long valor;
    private String tipoTransacao;
    private Date dataHora;

    public Transacao(long valor, String tipoTransacao) {
        this.valor = valor;
        this.tipoTransacao = tipoTransacao;
    }

    @Override   
    public String toString() {
        return 
    }
}