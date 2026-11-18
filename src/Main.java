import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    private static final Path ARQUIVO_DADOS = Path.of("banco.dat");
    private static final long INTERVALO_AUTO_SAVE_SEG = 30;

    public static void main(String[] args) {
        Persistencia persistencia = new Persistencia(ARQUIVO_DADOS);
        Banco banco = persistencia.carregar();
        System.out.printf("Dados carregados: %d cliente(s), %d conta(s).%n",
                banco.listarClientes().size(), banco.listarContas().size());

        persistencia.iniciarAutoSave(banco, INTERVALO_AUTO_SAVE_SEG);

        new Menu(banco, persistencia, new Scanner(System.in)).executar();
    }
}
