package model;

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

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    
    public void setEstudante(Estudante estudante) {
        this.estudante = estudante;
    }
}