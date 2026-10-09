package hash;

public class CodigoDuplicadoException extends RuntimeException {

    private final String codigo;

    public CodigoDuplicadoException(String codigo) {
        super("Código duplicado: " + codigo);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
