package hash;

import java.util.ArrayList;
import java.util.List;
import model.*;

public class TabelaHash {

    private static final int CAPACIDADE_PADRAO = 1009;
    private static final double FACTOR_CARGA_MAXIMO = 0.70;

    private Entrada[] tabela;
    private int tamanho; // n = posições OCUPADAS
    private int removidos; 

    // Pra estatísticas
    private int capacidadeInicial;
    private int totalColisoes;
    private long totalSondagens;
    private int numInsercoes;
    private int maiorSondagem;
    private int numRedimensionamentos;
    private int numLimpezas;
    private int ultimasSondagens;

    public TabelaHash() {
        this(CAPACIDADE_PADRAO);
    }

    public TabelaHash(int capacidade) {
        if (capacidade < 2) {
            capacidade = CAPACIDADE_PADRAO; // Nao permitir capacidades "estranhas"
        }

        capacidade = proximoPrimo(capacidade);
        this.tamanho = 0;
        this.removidos = 0;
        this.capacidadeInicial = capacidade;
        this.tabela = criarTabelaVazia(capacidade);
        this.totalColisoes = 0;
        this.totalSondagens = 0;
        this.numInsercoes = 0;
        this.maiorSondagem = 0;
        this.numRedimensionamentos = 0;
        this.numLimpezas = 0;
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

            if (entrada.getEstado() == EstadoEntrada.VAZIO) {
                posicaoVazia = pos;
                break;
            }

            if (entrada.getEstado() == EstadoEntrada.REMOVIDO) {
                // Podemos encontrar mesmo codigo lá à frente
                if (primeiroRemovido == -1) {
                    primeiroRemovido = pos;
                }
            } else {
                if (entrada.getEstudante().getCodigo().equals(codigo)) {
                    throw new CodigoDuplicadoException(codigo);
                }
                colisoes++;
            }

            pos = (pos + passo) % tabela.length;
        }

        int destino;
        if (primeiroRemovido != -1) {
            destino = primeiroRemovido;
            removidos--; // o REMOVIDO é reaproveitado
        } else {
            destino = posicaoVazia;
        }

        if (destino == -1) {
            throw new IllegalStateException("Tabela cheia: não foi possível inserir " + codigo);
        }

        tabela[destino].setEstudante(estudante);
        tabela[destino].setEstado(EstadoEntrada.OCUPADO);
        tamanho++;

        // Aqui já contamos inserções que passaram
        numInsercoes++;
        totalSondagens += sondagens;
        totalColisoes += colisoes;
        if (sondagens > maiorSondagem) {
            maiorSondagem = sondagens;
        }

        if (getFactorCarga() > FACTOR_CARGA_MAXIMO) {
            reconstruir(proximoPrimo(2 * tabela.length));
            numRedimensionamentos++;
        } else if ((double) (tamanho + removidos) / tabela.length > FACTOR_CARGA_MAXIMO) {
            // Os REMOVIDO não contam para α, mas também não são VAZIO. Vamos diminuir pra
            // poder tornar pesquisa mais eficaz
            reconstruir(tabela.length);
            numLimpezas++;
        }
    }

    private void reconstruir(int novaCapacidade) {
        Entrada[] antiga = tabela;

        tabela = criarTabelaVazia(novaCapacidade);
        // Só os OCUPADO passam; os REMOVIDO desaparecem.
        for (int i = 0; i < antiga.length; i++) {
            if (antiga[i].getEstado() == EstadoEntrada.OCUPADO) {
                reinserir(antiga[i].getEstudante());
            }
        }
        removidos = 0;
    }

    private void reinserir(Estudante estudante) {
        int k = converterChave(estudante.getCodigo());
        int pos = h1(k);
        int passo = h2(k);

        while (tabela[pos].getEstado() != EstadoEntrada.VAZIO) {
            pos = (pos + passo) % tabela.length;
        }
        tabela[pos].setEstudante(estudante);
        tabela[pos].setEstado(EstadoEntrada.OCUPADO);
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
        tabela[pos].setEstado(EstadoEntrada.REMOVIDO);
        tamanho--;
        removidos++;
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

            if (actual.getEstado() == EstadoEntrada.VAZIO) {
                return -1;
            }

            if (actual.getEstado() == EstadoEntrada.OCUPADO
                    && codigo.equals(actual.getEstudante().getCodigo())) {
                return pos;
            }

            pos = (pos + passo) % tabela.length;
        }

        return -1;
    }

    // Para auxiliar na defesa. Podemos ou não remover
    public String descreverSondagem(String codigo) {
        if (codigo == null || codigo.isEmpty())
            throw new IllegalArgumentException("Código inválido");

        int k = converterChave(codigo);
        int pos = h1(k);
        int passo = h2(k);

        StringBuilder sb = new StringBuilder();
        sb.append("Código ").append(codigo)
          .append(": k=").append(k)
          .append(", h1=").append(pos)
          .append(", h2=").append(passo)
          .append(", m=").append(tabela.length).append('\n');

        for (int i = 0; i < tabela.length; i++) {
            Entrada actual = tabela[pos];
            sb.append("  i=").append(i)
              .append(" -> posição ").append(pos)
              .append(": ").append(actual.getEstado());

            if (actual.getEstado() == EstadoEntrada.OCUPADO) {
                String outro = actual.getEstudante().getCodigo();
                sb.append(" (").append(outro).append(')');
                if (outro.equals(codigo)) {
                    sb.append(" <- encontrado\n");
                    return sb.toString();
                }
                sb.append(" <- colisão");
            }
            sb.append('\n');

            if (actual.getEstado() == EstadoEntrada.VAZIO) {
                sb.append("  Não existe na tabela.\n");
                return sb.toString();
            }

            pos = (pos + passo) % tabela.length;
        }

        sb.append("  Não existe na tabela (tabela percorrida por completo).\n");
        return sb.toString();
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
            if (e.getEstado() == EstadoEntrada.OCUPADO) {
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

    public double getMediaSondagens() {
        if (numInsercoes == 0) {
            return 0;
        }
        return (double) totalSondagens / numInsercoes;
    }

    public int getNumRedimensionamentos() {
        return numRedimensionamentos;
    }

    public int getNumLimpezas() {
        return numLimpezas;
    }

    public int getRemovidos() {
        return removidos;
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