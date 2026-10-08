package dados;

import hash.TabelaHash;
import model.Estudante;

public class Dados {
    
    private final TabelaHash tabela;
    private int invalidos;
    private int lidas;

    public Dados() {
        this.tabela = new TabelaHash();
        this.invalidos = 0;
    }

    public void inserir(Estudante estudante) {
        this.tabela.inserir(estudante);
    }

    public void incrementarInvalidos() {
        this.invalidos++;
    }

    public TabelaHash getTabelaHash() {
        return this.tabela;
    }

    public int getInvalidos() {
        return this.invalidos;
    }

    public void incrementarLidas() {
        this.lidas++;
    }

    public int getLidas() {
        return this.lidas;
    }

    public int getValidos() {
        return this.tabela.getTamanho();
    }

}
