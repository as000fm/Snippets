```java
import java.util.ArrayList;
import java.util.List;

public final class WindowsCmdlineParser {

    private WindowsCmdlineParser() {}

    /**
     * Analyse une ligne de commande Windows (String) en argv[] utilisable avec
     * Runtime.getRuntime().exec(String[]) / ProcessBuilder.
     *
     * Règles implémentées (style standard Windows / runtime C de Microsoft) :
     * - Les espaces (et tabulations, etc.) séparent les arguments quand on n’est pas entre guillemets.
     * - Les guillemets doubles regroupent le texte (incluant les espaces).
     * - Les antislashs avant un guillemet sont traités de façon spéciale :
     *     * 2n antislashs + "  => n antislashs, et le guillemet bascule le mode « entre guillemets »
     *     * 2n+1 antislashs + " => n antislashs, et un guillemet littéral (caractère ")
     *
     * Ça marche bien aussi pour lancer "powershell.exe ..." (le parsing *à PowerShell*
     * se fait après qu’il a reçu argv[]).
     */
    public static String[] parse(String commandLine) {
        if (commandLine == null) throw new IllegalArgumentException("commandLine == null");

        final int len = commandLine.length();
        final List<String> args = new ArrayList<>();

        int i = 0;

        while (true) {
            // Saute les blancs au début
            while (i < len && Character.isWhitespace(commandLine.charAt(i))) i++;
            if (i >= len) break;

            StringBuilder arg = new StringBuilder();
            boolean inQuotes = false;
            int backslashes = 0;

            while (i < len) {
                char c = commandLine.charAt(i);

                if (c == '\\') {
                    backslashes++;
                    i++;
                    continue;
                }

                if (c == '"') {
                    // Traite les antislashs accumulés juste avant le guillemet
                    if (backslashes > 0) {
                        int pairs = backslashes / 2;
                        for (int k = 0; k < pairs; k++) arg.append('\\');

                        if ((backslashes & 1) == 1) {
                            // Nombre impair d’antislashs => guillemet échappé (littéral)
                            arg.append('"');
                            backslashes = 0;
                            i++;
                            continue;
                        }
                        // Nombre pair => le guillemet bascule inQuotes
                        backslashes = 0;
                        inQuotes = !inQuotes;
                        i++;
                        continue;
                    } else {
                        // Aucun antislash => le guillemet bascule inQuotes
                        inQuotes = !inQuotes;
                        i++;
                        continue;
                    }
                }

                // Tout autre caractère : on « flush » d’abord les antislashs
                if (backslashes > 0) {
                    for (int k = 0; k < backslashes; k++) arg.append('\\');
                    backslashes = 0;
                }

                // Hors guillemets, un blanc termine l’argument
                if (!inQuotes && Character.isWhitespace(c)) {
                    break;
                }

                arg.append(c);
                i++;
            }

            // Flush des antislashs de fin (pas devant un guillemet)
            if (backslashes > 0) {
                for (int k = 0; k < backslashes; k++) arg.append('\\');
            }

            args.add(arg.toString());

            // Avance après les blancs qui ont terminé l’argument (s’il y en a)
            while (i < len && Character.isWhitespace(commandLine.charAt(i))) i++;
        }

        return args.toArray(new String[0]);
    }

    // Exemple rapide
    public static void main(String[] args) throws Exception {
        String cmd = "program \"text with \\\"quotes\\\" inside\"";
        String[] argv = parse(cmd);

        // argv[0] = program
        // argv[1] = text with "quotes" inside
        Process p = Runtime.getRuntime().exec(argv);
        p.waitFor();
    }
}
```

Si tu veux, je peux aussi traduire/adapter les identifiants (ex. `backslashes` → `nbAntislashs`) tout en gardant un style “dev Québec” propre.
