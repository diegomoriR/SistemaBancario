import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


public class Transacao {
    private static long proximoID = 1;

    private long ID = 0;
    private long valor;
    private TipoTransacao tipo;
    private LocalDateTime dataHora;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Transacao(long valor, TipoTransacao tipo) {
        this.valor = valor;
        this.tipo = tipo;
        this.dataHora = LocalDateTime.now();
    }

    @Override   
    public String toString() {
        return String.format("%s | %-22s | R$ %10s", dataHora.format(FMT), tipo, valor);
    }
}