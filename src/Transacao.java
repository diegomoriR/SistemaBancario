import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transacao implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final double valor;
    private final TipoTransacao tipo;
    private final LocalDateTime dataHora;   // LocalDateTime é Serializable

    public Transacao(double valor, TipoTransacao tipo) {
        this.valor = valor;
        this.tipo = tipo;
        this.dataHora = LocalDateTime.now();
    }

    public double getValor() { return valor; }
    public TipoTransacao getTipo() { return tipo; }
    public LocalDateTime getDataHora() { return dataHora; }

    @Override
    public String toString() {
        return String.format("%s | %-22s | R$ %10.2f", dataHora.format(FMT), tipo, valor);
    }
}
