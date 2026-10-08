package utils;

import model.Estudante;

public class Validacao {

    public static Resposta validarEstudante(Estudante estudante) {

        Resposta resposta;
        boolean invalido = estudante.getNome().trim().equals("")
            ||
            estudante.getCodigo().trim().equals("")
            ||
            estudante.getCurso().trim().equals("")
            ||
            (!estudante.getSexo().trim().equalsIgnoreCase("M") && !estudante.getSexo().trim().equalsIgnoreCase("F"))
            || estudante.getAno() > 4
            || estudante.getAno() < 1
            || estudante.getIdade() < 17
            || estudante.getIdade() > 70
            || estudante.getMedia() > 20
            || estudante.getMedia() < 0;

        if (invalido) {
            resposta = new Resposta("Os dados do estudante são inválidos", Estado.INVALIDO);
        } else {
            resposta = new Resposta("Os dados do estudante são válidos", Estado.VALIDO);
        }

        return resposta;
    }
    
}
