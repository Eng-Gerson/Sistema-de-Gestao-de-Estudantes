package utils;

public class Resposta {
    private String mensagem;
    private Estado estado;

    public Resposta(String mensagem, Estado estado) {
        this.mensagem = mensagem;
        this.estado = estado;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

}
