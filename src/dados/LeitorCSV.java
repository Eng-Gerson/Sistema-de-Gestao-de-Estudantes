package dados;

import hash.CodigoDuplicadoException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;
import model.Estudante;
import utils.EstadoValidacao;
import utils.Resposta;
import utils.Validacao;

public class LeitorCSV {

    private static final String NOME_FICHEIRO = "estudantes.csv";
    // Usado quando o CSV não está no classpath (ex.: compilado com javac -d out),
    // relativo à raiz do projecto.
    private static final Path CAMINHO_ALTERNATIVO = Path.of("src", "dados", NOME_FICHEIRO);

    // Procura o CSV ao lado de LeitorCSV.class (funciona no VS Code, que copia
    // src/dados/estudantes.csv para a pasta de saída), e depois em src/dados/.
    private BufferedReader abrirFicheiro() throws IOException {
        InputStream in = LeitorCSV.class.getResourceAsStream(NOME_FICHEIRO);
        if (in != null) {
            return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
        if (Files.exists(CAMINHO_ALTERNATIVO)) {
            return Files.newBufferedReader(CAMINHO_ALTERNATIVO, StandardCharsets.UTF_8);
        }
        throw new FileNotFoundException(NOME_FICHEIRO + " não encontrado no classpath nem em "
                + CAMINHO_ALTERNATIVO.toAbsolutePath());
    }

    public Dados lerDados() {

        Dados dados = new Dados();

        try(BufferedReader br = abrirFicheiro()) {
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
                        case EstadoValidacao.VALIDO: dados.inserir(estudante);
                        break;
                        case EstadoValidacao.INVALIDO: dados.incrementarInvalidos();
                        break;
                        default: System.out.println("Impossivel");
                        break;
                    }
                    
                    
                } catch (CodigoDuplicadoException e) {
                    dados.incrementarDuplicados();
                } catch (Exception e) {
                    dados.incrementarInvalidos();
                }
                
            }

        } catch(IOException e) {
            System.out.println("Erro ao ler dados do ficheiro estudantes.csv: " + e.getMessage() + "\n");
        }

        return dados;
    }

}

