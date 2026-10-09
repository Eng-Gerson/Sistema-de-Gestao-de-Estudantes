package utils;

public class Resposta {
    private String mensagem;
    private EstadoValidacao estado;

    public Resposta(String mensagem, EstadoValidacao estado) {
        this.mensagem = mensagem;
        this.estado = estado;
    }

    public String getMensagem() {
        return mensagem;
    }

    public EstadoValidacao getEstado() {
        return estado;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public void setEstado(EstadoValidacao estado) {
        this.estado = estado;
    }

}
