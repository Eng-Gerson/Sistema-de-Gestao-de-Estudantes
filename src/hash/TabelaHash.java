package hash;

import java.util.ArrayList;
import java.util.List;
import model.*;

public class TabelaHash {

    private static final int CAPACIDADE_PADRAO = 1009;
    private static final double FACTOR_CARGA_MAXIMO = 0.70;

    private Entrada[] tabela;
    private int tamanho; // n = posições OCUPADAS

    // Pra estatísticas
    private int capacidadeInicial;
    private int totalColisoes;
    private long totalSondagens;
    private int numInsercoes;
    private int maiorSondagem;
    private int numRedimensionamentos;
    private int ultimasSondagens; // sondagens pra ver se ajudam na Estatistica

    public TabelaHash() {
        this(CAPACIDADE_PADRAO);
    }

    public TabelaHash(int capacidade) {
        if (capacidade < 2) {
            capacidade = CAPACIDADE_PADRAO; // Nao permitir capacidades "estranhas"
        }

        capacidade = proximoPrimo(capacidade);
        this.tamanho = 0;
        this.capacidadeInicial = capacidade;
        this.tabela = criarTabelaVazia(capacidade);
        this.totalColisoes = 0;
        this.totalSondagens = 0;
        this.numInsercoes = 0;
        this.maiorSondagem = 0;
        this.numRedimensionamentos = 0;
        this.ultimasSondagens = 0;
    }

    public void inserir(Estudante estudante) {
        if (estudante == null)
            throw new IllegalArgumentException("Estudante nulo");

        String codigo = estudante.getCodigo();
        if (codigo == null || codigo.isEmpty())
            throw new IllegalArgumentException("Código inválido");

        int k = converterChave(codigo);
        int pos = h1(k);
        int passo = h2(k);

        int primeiroRemovido = -1;
        int posicaoVazia = -1;
        int sondagens = 0;
        int colisoes = 0;

        // h(k, i) = (h1(k) + i * h2(k)) mod m
        for (int i = 0; i < tabela.length; i++) {
            Entrada entrada = tabela[pos];
            sondagens++;

            if (entrada.getEstado() == Estado.VAZIO) {
                posicaoVazia = pos;
                break;
            }

            if (entrada.getEstado() == Estado.REMOVIDO) {
                // Podemos encontrar mesmo codigo lá à frente
                if (primeiroRemovido == -1) {
                    primeiroRemovido = pos;
                }
            } else {
                if (entrada.getEstudante().getCodigo().equals(codigo)) {
                    throw new IllegalArgumentException("Código duplicado: " + codigo);
                }
                colisoes++;
            }

            pos = (pos + passo) % tabela.length;
        }

        int destino;
        if (primeiroRemovido != -1) {
            destino = primeiroRemovido;
        } else {
            destino = posicaoVazia;
        }

        if (destino == -1) {
            throw new IllegalStateException("Tabela cheia: não foi possível inserir " + codigo);
        }

        tabela[destino].setEstudante(estudante);
        tabela[destino].setEstado(Estado.OCUPADO);
        tamanho++;

        // Aqui já contamos inserções que passaram
        numInsercoes++;
        totalSondagens += sondagens;
        totalColisoes += colisoes;
        if (sondagens > maiorSondagem) {
            maiorSondagem = sondagens;
        }

        if (getFactorCarga() > FACTOR_CARGA_MAXIMO) {
            redimensionar();
        }
    }

    private void redimensionar() {
        Entrada[] antiga = tabela;
        int novaCapacidade = proximoPrimo(2 * antiga.length);

        tabela = criarTabelaVazia(novaCapacidade);
        // Só os OCUPADO passam; os REMOVIDO desaparecem.
        for (int i = 0; i < antiga.length; i++) {
            if (antiga[i].getEstado() == Estado.OCUPADO) {
                reinserir(antiga[i].getEstudante());
            }
        }

        numRedimensionamentos++;
    }

    private void reinserir(Estudante estudante) {
        int k = converterChave(estudante.getCodigo());
        int pos = h1(k);
        int passo = h2(k);

        // A tabela nova não tem REMOVIDO, por isso basta VAZIO.
        while (tabela[pos].getEstado() != Estado.VAZIO) {
            pos = (pos + passo) % tabela.length;
        }
        tabela[pos].setEstudante(estudante);
        tabela[pos].setEstado(Estado.OCUPADO);
    }

    public Estudante pesquisar(String codigo) {
        int pos = procurarPosicao(codigo);
        if (pos == -1) {
            return null;
        }
        return tabela[pos].getEstudante();
    }

    public boolean remover(String codigo) {
        int pos = procurarPosicao(codigo);
        if (pos == -1) {
            return false;
        }

        tabela[pos].setEstudante(null);
        tabela[pos].setEstado(Estado.REMOVIDO);
        tamanho--;
        return true;
    }

    private int procurarPosicao(String codigo) {
        if (codigo == null || codigo.isEmpty())
            throw new IllegalArgumentException("Código inválido");

        int k = converterChave(codigo);
        int pos = h1(k);
        int passo = h2(k);
        ultimasSondagens = 0;

        for (int i = 0; i < tabela.length; i++) {
            Entrada actual = tabela[pos];
            ultimasSondagens++;

            if (actual.getEstado() == Estado.VAZIO) {
                return -1;
            }

            if (actual.getEstado() == Estado.OCUPADO
                    && codigo.equals(actual.getEstudante().getCodigo())) {
                return pos;
            }

            pos = (pos + passo) % tabela.length;
        }

        return -1;
    }

    // Funcoes hash
    private int converterChave(String codigo) {
        int k = 0;

        for (int i = 0; i < codigo.length(); i++) {
            k = 31 * k + codigo.charAt(i);
        }
        return k & 0x7FFFFFFF;
    }

    private int h1(int k) {
        return k % tabela.length;
    }

    private int h2(int k) {
        return 1 + (k % (tabela.length - 1));
    }

    // Auxiliares
    private Entrada[] criarTabelaVazia(int capacidade) {
        Entrada[] tabela = new Entrada[capacidade];
        for (int i = 0; i < capacidade; i++) {
            tabela[i] = new Entrada();
        }
        return tabela;
    }

    private int proximoPrimo(int valor) {
        if (isPrimo(valor)) {
            return valor;
        }
        if (valor < 2)
            return 2;
        int candidato = (valor % 2 == 0) ? (valor + 1) : valor + 2;

        while (!isPrimo(candidato)) {
            candidato += 2;
        }

        return candidato;
    }

    private boolean isPrimo(int number) {
        if (number < 2)
            return false;
        if (number % 2 == 0)
            return number == 2;

        if (number % 3 == 0)
            return number == 3;

        for (int i = 5; i * i <= number; i += 6) {
            if (number % i == 0 || number % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    public List<Estudante> listar() {
        List<Estudante> lista = new ArrayList<>();
        for(Entrada e : tabela) {
            if (e.getEstado() == Estado.OCUPADO) {
                lista.add(e.getEstudante());
            }
        }
        return lista;
    }

    public int getTamanho() {
        return tamanho;
    }

    public int getCapacidadeInicial() {
        return capacidadeInicial;
    }

    public int getTotalColisoes() {
        return totalColisoes;
    }

    public long getTotalSondagens() {
        return totalSondagens;
    }

    public int getNumInsercoes() {
        return numInsercoes;
    }

    public int getMaiorSondagem() {
        return maiorSondagem;
    }

    public int getNumRedimensionamentos() {
        return numRedimensionamentos;
    }

    public int getUltimasSondagens() {
        return ultimasSondagens;
    }

    public int getCapacidade() {
        return tabela.length;
    }

    public double getFactorCarga() {
        return (double) tamanho / tabela.length;
    }

}