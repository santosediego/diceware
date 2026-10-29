import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gerador de senhas pelo método Diceware (lista Dadoware, pt-br).
 *
 * Simula a rolagem de um dado de 6 faces usando SecureRandom (gerador
 * criptograficamente seguro), no lugar de um dado físico. Cada palavra
 * exige 5 rolagens, formando um codigo de 5 digitos (11111 a 66666).
 *
 * Uso:
 *   java Diceware.java [numeroDePalavras]
 *   (padrao: 6 palavras)
 */
public class Diceware {

    private static final int DADOS_POR_PALAVRA = 5;
    private static final int PALAVRAS_NA_LISTA = 7776; // 6^5
    private static final String ARQUIVO_LISTA = "lista-numerada.txt";
    private static final String SEPARADOR = "-";

    // Fonte de aleatoriedade segura. NUNCA usar Math.random()/Random aqui:
    // esses geradores sao previsiveis. SecureRandom.nextInt(6) tambem ja
    // trata o "modulo bias" internamente, entao cada face sai equiprovavel.
    private static final SecureRandom dado = new SecureRandom();

    public static void main(String[] args) throws IOException {
        int numeroDePalavras = 6;
        if (args.length > 0) {
            numeroDePalavras = Integer.parseInt(args[0]);
        }

        Map<String, String> lista = carregarLista();

        System.out.println("=== Diceware pt-br (Dadoware) ===");
        System.out.println("Rolando " + (numeroDePalavras * DADOS_POR_PALAVRA)
                + " dados para " + numeroDePalavras + " palavras...\n");

        List<String> palavras = new ArrayList<>();

        for (int i = 1; i <= numeroDePalavras; i++) {
            int[] rolagens = rolarDados(DADOS_POR_PALAVRA);
            String codigo = montarCodigo(rolagens);
            String palavra = lista.get(codigo);

            palavras.add(palavra);
            System.out.printf("Palavra %d:  \uD83C\uDFB2 %s  ->  %s  ->  %s%n",
                    i, formatarRolagens(rolagens), codigo, palavra);
        }

        String senha = String.join(SEPARADOR, palavras);
        double entropia = numeroDePalavras * (Math.log(PALAVRAS_NA_LISTA) / Math.log(2));

        System.out.println("\n----------------------------------------");
        System.out.println("Senha: " + senha);
        System.out.printf("Entropia: ~%.1f bits (%d palavras)%n", entropia, numeroDePalavras);
        System.out.println("----------------------------------------");
    }

    /** Rola 'quantidade' dados de 6 faces, cada um de 1 a 6. */
    private static int[] rolarDados(int quantidade) {
        int[] resultado = new int[quantidade];
        for (int i = 0; i < quantidade; i++) {
            resultado[i] = dado.nextInt(6) + 1; // 0..5 -> 1..6
        }
        return resultado;
    }

    /** Junta as faces num codigo de texto, ex.: [4,2,6,1,3] -> "42613". */
    private static String montarCodigo(int[] rolagens) {
        StringBuilder sb = new StringBuilder();
        for (int face : rolagens) {
            sb.append(face);
        }
        return sb.toString();
    }

    /** Deixa a rolagem bonita pra exibir, ex.: "4-2-6-1-3". */
    private static String formatarRolagens(int[] rolagens) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rolagens.length; i++) {
            if (i > 0) sb.append("-");
            sb.append(rolagens[i]);
        }
        return sb.toString();
    }

    /** Le o arquivo "codigo palavra" e devolve um mapa codigo -> palavra. */
    private static Map<String, String> carregarLista() throws IOException {
        Map<String, String> mapa = new HashMap<>();
        List<String> linhas = Files.readAllLines(Path.of(ARQUIVO_LISTA), StandardCharsets.UTF_8);

        for (String linha : linhas) {
            linha = linha.trim();
            if (linha.isEmpty()) continue;
            String[] partes = linha.split("\\s+", 2);
            if (partes.length == 2) {
                mapa.put(partes[0], partes[1]);
            }
        }

        if (mapa.size() != PALAVRAS_NA_LISTA) {
            throw new IllegalStateException(
                    "A lista deveria ter " + PALAVRAS_NA_LISTA
                    + " palavras, mas encontrei " + mapa.size() + ".");
        }
        return mapa;
    }
}
