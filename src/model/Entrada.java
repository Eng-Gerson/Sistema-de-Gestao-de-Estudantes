enum Estado{
    VAZIO,OCUPADO,REMOVIDO;
}
public class Entrada {
    private Estudante estudante;
    private Estado estado;
    public Entrada(){
        estudante = null;
        estado = Estado.VAZIO;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public Estado getEstado() {
        return estado;
    }
}