import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Persistencia {
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Path arquivo;
    private volatile LocalDateTime ultimoSalvamento;   
    private ScheduledExecutorService agendador;

    public Persistencia(Path arquivo) {
        this.arquivo = arquivo;
    }

    public Banco carregar() {
        if (!Files.exists(arquivo)) {
            System.out.println("Nenhum arquivo de dados encontrado. Iniciando banco vazio.");
            return new Banco();
        }
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(arquivo)))) {
            return (Banco) in.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            Path backup = arquivo.resolveSibling(arquivo.getFileName() + ".corrompido");
            try {
                Files.move(arquivo, backup, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignorada) {
                // se nem mover der, segue com banco vazio mesmo
            }
            System.out.println("AVISO: não foi possível ler " + arquivo + " (" + e + ").");
            System.out.println("O arquivo antigo foi guardado como " + backup + ". Iniciando banco vazio.");
            return new Banco();
        }
    }

 
    public void salvar(Banco banco) throws IOException {
        Path tmp = arquivo.resolveSibling(arquivo.getFileName() + ".tmp");
        synchronized (banco) {
            try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(tmp)))) {
                out.writeObject(banco);
            }
            try {
                Files.move(tmp, arquivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
            banco.marcarSalvo();
        }
        ultimoSalvamento = LocalDateTime.now();
    }

    public void iniciarAutoSave(Banco banco, long intervaloSegundos) {
        agendador = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "auto-save");
            t.setDaemon(true);   // daemon: não impede o programa de encerrar
            return t;
        });
        agendador.scheduleAtFixedRate(() -> {
            try {
                if (banco.precisaSalvar()) salvar(banco);
            } catch (Exception e) {
                System.err.println("\n[auto-save] falha ao salvar: " + e.getMessage());
            }
        }, intervaloSegundos, intervaloSegundos, TimeUnit.SECONDS);
    }

    public void pararAutoSave() {
        if (agendador != null) agendador.shutdown();
    }

    public String descricaoUltimoSalvamento() {
        LocalDateTime t = ultimoSalvamento;
        return t == null ? "ainda não salvo nesta sessão" : "às " + t.format(HORA);
    }

    public Path getArquivo() { return arquivo; }
}
