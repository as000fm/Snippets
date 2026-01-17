```java
	/**
	 * Analyse une ligne de commande Windows (String) en arguments utilisable par execute
	 * @param commandLine La ligne de commande Windows à analyser
	 * @return les arguments utilisable par execute
	 * @throws Exception en cas d'erreur...
	 */
	protected String[] windowsCmdlineParser(String commandLine) throws Exception {
		if (OutilsBase.isEmpty(commandLine)) {
			throw new Exception("La ligne de commande est vide");
		}

		final int len = commandLine.length();
		final List<String> args = new ArrayList<>();

		int i = 0;

		while (true) {
			// Saute les blancs au début
			while (i < len && Character.isWhitespace(commandLine.charAt(i))) {
				i++;
			}

			if (i >= len) {
				break;
			}

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

						for (int k = 0; k < pairs; k++) {
							arg.append('\\');
						}

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
					for (int k = 0; k < backslashes; k++) {
						arg.append('\\');
					}

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
				for (int k = 0; k < backslashes; k++) {
					arg.append('\\');
				}
			}

			args.add(arg.toString());

			// Avance après les blancs qui ont terminé l’argument (s’il y en a)
			while (i < len && Character.isWhitespace(commandLine.charAt(i))) {
				i++;
			}
		}

		return args.toArray(new String[0]);
	}

	/**
	 * Analyse une ligne de commande "style shell Unix/Linux bash/zsh" (String) en arguments utilisable par execute
	 * @param commandLine La ligne de commande Windows à analyser
	 * @return les arguments utilisable par execute
	 * @throws Exception en cas d'erreur...
	 */
	protected String[] unixCmdlineParser(String commandLine) throws Exception {
		if (OutilsBase.isEmpty(commandLine)) {
			throw new Exception("La ligne de commande est vide");
		}

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
					throw new Exception("État invalide: " + state);
			}
		}

		if (state != OUT) {
			throw new Exception("Guillemets non fermées dans la ligne de commande.");
		}

		if (haveToken) {
			args.add(cur.toString());
		}

		return args.toArray(new String[0]);
	}
	
	/**
	 * Analyse une ligne de commande selon l'environnement courant Windows ou Unix/Linux et l'exécute
	 * @param commandLine La ligne de commande à analyser
	 * @return le code de retour d'exécution
	 * @throws Exception en cas d'erreur...
	 */
	protected int execCmdLine(String commandLine) throws Exception {
		return execute(OutilsCommun.isWindows() ? windowsCmdlineParser(commandLine) : unixCmdlineParser(commandLine));
	}
	
	/**
	 * Analyse une ligne de commande pour l'environnement Windows et l'exécute
	 * @param commandLine La ligne de commande à analyser
	 * @return le code de retour d'exécution
	 * @throws Exception en cas d'erreur...
	 */
	protected int execWindowsCmdLine(String commandLine) throws Exception {
		return execute(windowsCmdlineParser(commandLine));
	}
	
	/**
	 * Analyse une ligne de commande pour l'environnement Unix/Linux et l'exécute
	 * @param commandLine La ligne de commande à analyser
	 * @return le code de retour d'exécution
	 * @throws Exception en cas d'erreur...
	 */
	protected int execUnixCmdLine(String commandLine) throws Exception {
		return execute(unixCmdlineParser(commandLine));
	}
```
