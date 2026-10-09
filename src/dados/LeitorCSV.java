package dados;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import model.Estudante;
import utils.Estado;
import utils.Resposta;
import utils.Validacao;

public class LeitorCSV {
    
    public Dados lerDados() {

        Dados dados = new Dados();

        try(BufferedReader br = new BufferedReader(new FileReader("dados/estudantes.csv"))) {
            br.readLine();
            String linha;
            StringTokenizer st;
            Estudante estudante;

            while((linha = br.readLine()) != null) {
                dados.incrementarLidas();

                st = new StringTokenizer(linha, ",");

                try {
                    estudante = new Estudante(
                    st.nextToken(), 
                    st.nextToken(), 
                    st.nextToken(), 
                    Integer.parseInt(st.nextToken()), 
                    Integer.parseInt(st.nextToken()), 
                    st.nextToken(), 
                    Double.parseDouble(st.nextToken())
                    );

                    Resposta resposta = Validacao.validarEstudante(estudante);

                    switch(resposta.getEstado()) {
                        case Estado.VALIDO: dados.inserir(estudante);
                        break;
                        case Estado.INVALIDO: dados.incrementarInvalidos();
                        break;
                        default: System.out.println("Impossivel");
                        break;
                    }
                    
                    
                } catch (Exception e) {
                    dados.incrementarInvalidos();
                }
                
            }

        } catch(IOException e) {
            System.out.println("Erro ao ler dados do ficheiro estudantes.csv\n");
        }

        return dados;
    }

}

