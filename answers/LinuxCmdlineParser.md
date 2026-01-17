```java
import java.util.ArrayList;
import java.util.List;

public final class LinuxCmdlineParser {

    private LinuxCmdlineParser() {}

    /**
     * Parse une ligne de commande "style shell Linux" (bash/zsh) en argv[],
     * pour l'utiliser avec Runtime.exec(String[]) / ProcessBuilder.
     *
     * Supporte (subset très utile, sans exécuter le shell) :
     * - Séparateurs: espaces/tabulations (whitespace) quand on est hors quotes
     * - Quotes simples: '...' (tout est littéral jusqu’au prochain ')
     * - Quotes doubles: "..." (whitespace permis; support de certains échappements)
     * - Backslash:
     *    - hors quotes: \X => X (échappe n’importe quel char)
     *    - dans "": n’échappe que \, ", $, ` et newline (comportement bash courant)
     *
     * Ne fait PAS:
     * - expansion ($VAR, $(cmd), `cmd`)
     * - globbing (*.txt)
     * - opérateurs shell (|, >, &&, etc.)
     *
     * Si tu veux exactement le comportement du shell, il faut plutôt lancer:
     *   new ProcessBuilder("bash","-lc", commandLine)
     */
    public static String[] parse(String commandLine) {
        if (commandLine == null) throw new IllegalArgumentException("commandLine == null");

        final int len = commandLine.length();
        final List<String> args = new ArrayList<>();
        final StringBuilder cur = new StringBuilder();

        final int OUT = 0, IN_SINGLE = 1, IN_DOUBLE = 2;
        int state = OUT;

        boolean haveToken = false; // permet de capturer "" comme argument vide

        int i = 0;
        while (i < len) {
            char c = commandLine.charAt(i);

            switch (state) {
                case OUT: {
                    if (Character.isWhitespace(c)) {
                        // Fin d’argument
                        if (haveToken) {
                            args.add(cur.toString());
                            cur.setLength(0);
                            haveToken = false;
                        }
                        i++;
                        break;
                    }

                    if (c == '\'') {
                        state = IN_SINGLE;
                        haveToken = true;
                        i++;
                        break;
                    }

                    if (c == '"') {
                        state = IN_DOUBLE;
                        haveToken = true;
                        i++;
                        break;
                    }

                    if (c == '\\') {
                        haveToken = true;
                        i++;
                        if (i >= len) {
                            // En bash, backslash en fin de ligne signifie "continuation".
                            // Ici, on le traite comme un backslash littéral.
                            cur.append('\\');
                        } else {
                            cur.append(commandLine.charAt(i));
                            i++;
                        }
                        break;
                    }

                    // Char normal
                    haveToken = true;
                    cur.append(c);
                    i++;
                    break;
                }

                case IN_SINGLE: {
                    if (c == '\'') {
                        state = OUT;
                        i++;
                    } else {
                        cur.append(c);
                        haveToken = true;
                        i++;
                    }
                    break;
                }

                case IN_DOUBLE: {
                    if (c == '"') {
                        state = OUT;
                        i++;
                        break;
                    }

                    if (c == '\\') {
                        i++;
                        if (i >= len) {
                            // Backslash en fin => littéral
                            cur.append('\\');
                            haveToken = true;
                            break;
                        }

                        char next = commandLine.charAt(i);

                        // Dans bash, dans "", \ n’échappe que: \ " $ ` et newline
                        if (next == '\\' || next == '"' || next == '$' || next == '`') {
                            cur.append(next);
                            haveToken = true;
                            i++;
                        } else if (next == '\n') {
                            // continuation: on supprime les deux (\"\n)
                            i++;
                        } else {
                            // sinon, le backslash reste littéral
                            cur.append('\\').append(next);
                            haveToken = true;
                            i++;
                        }
                        break;
                    }

                    // Char normal dans ""
                    cur.append(c);
                    haveToken = true;
                    i++;
                    break;
                }

                default:
                    throw new IllegalStateException("État invalide: " + state);
            }
        }

        if (state != OUT) {
            throw new IllegalArgumentException("Quotes non fermées dans la ligne de commande.");
        }

        if (haveToken) {
            args.add(cur.toString());
        }

        return args.toArray(new String[0]);
    }

    // Quick example
    public static void main(String[] args) throws Exception {
        String cmd = "program \"text with \\\"quotes\\\" inside\"";
        String[] argv = parse(cmd);

        // argv[0] = program
        // argv[1] = text with "quotes" inside
        for(String s : argv) System.out.println(s);
    }
}
```
