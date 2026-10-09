package model;

public class Entrada {
    private Estudante estudante;
    private EstadoEntrada estado;
    public Entrada(){
        estudante = null;
        estado = EstadoEntrada.VAZIO;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public EstadoEntrada getEstado() {
        return estado;
    }

    public void setEstado(EstadoEntrada estado) {
        this.estado = estado;
    }
    
    public void setEstudante(Estudante estudante) {
        this.estudante = estudante;
    }
}