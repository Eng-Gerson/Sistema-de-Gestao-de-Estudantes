public class Estudante {
    private String codigo;
    private String nome;
    private String curso;
    private int ano;
    private int idade;
    private String sexo;
    private double media;

    public Estudante(String codigo, String nome, String curso, int ano, int idade, String sexo, double media) {
        this.codigo = codigo;
        this.nome = nome;
        this.curso = curso;
        this.ano = ano;
        this.idade = idade;
        this.sexo = sexo;
        this.media = media;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCurso() {
        return curso;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getSexo() {
        return sexo;
    }

    public double getMedia() {
        return media;
    }

    public void setMedia(double media) {
        this.media = media;
    }

    @Override
    public java.lang.String toString() {
        return "Estudante{" +
                "codigo='" + codigo + '\'' +
                ", nome='" + nome + '\'' +
                ", curso='" + curso + '\'' +
                ", ano=" + ano +
                ", idade=" + idade +
                ", sexo='" + sexo + '\'' +
                ", media=" + media +
                '}';
    }

}