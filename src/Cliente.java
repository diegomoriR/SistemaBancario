public class Cliente {
    private final String nome;
    private final String CPF;

    public Cliente(String nome, String CPF) {
        this.nome = nome;
        this.CPF = CPF;
    }

    public String getNome() {
        return nome;
    }

    public String getCPF() {
        return CPF;
    }

    @Override 
    public String toString() {
        return "Cliente: "+ nome +" CPF: " + CPF;   
    }
}