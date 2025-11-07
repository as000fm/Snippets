```xml
		<!-- https://mvnrepository.com/artifact/io.github.nstdio/rsql-parser -->
		<dependency>
			<groupId>io.github.nstdio</groupId>
			<artifactId>rsql-parser</artifactId>
			<version>2.3.4</version>
		</dependency>
```

```java
package sgbd.datasets.rsql.data;

import sgbd.fields.base.DataFieldList;

/**
 * Classe des données du filtre
 * @author Claude Toupin - 26 sept. 2025
 */
public class RSQLFilterData {
	/** La partie SQL du filtre **/
	private final String filter;

	/** Les paramètres du filtre **/
	private final DataFieldList params;

	/**
	 * Constructeur de base
	 * @param filter La partie SQL du filtre
	 * @param params Les paramètres du filtre
	 */
	public RSQLFilterData(String filter) {
		this(filter, new DataFieldList());
	}

	/**
	 * Constructeur de base
	 * @param filter La partie SQL du filtre
	 * @param params Les paramètres du filtre
	 */
	public RSQLFilterData(String filter, DataFieldList params) {
		this.filter = filter;
		this.params = params;
	}

	/**
	 * Extrait le champ filter
	 * @return un String
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * Extrait le champ params
	 * @return un DataFieldList
	 */
	public DataFieldList getParams() {
		return params;
	}
}
```

```java
package sgbd.datasets.rsql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.RSQLParserException;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.Arity;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import outils.base.OutilsBase;
import sgbd.datasets.rsql.data.RSQLFilterData;
import sgbd.exceptions.DataSetException;
import sgbd.fields.base.DataField;
import sgbd.fields.base.DataFieldList;
import sgbd.fields.types.DataFieldType;

/**
 * Implémentation de filtre RSQL/FIQL et de tri JSON:API
 * @author Claude Toupin - 26 sept. 2025
 */
public class RSQLFilter implements RSQLVisitor<RSQLFilterData, Map<String, DataFieldType>> {
	/** Liste des opérateurs supportés **/
	private final Set<ComparisonOperator> operators;

	/**
	 * Constructeur de base
	 */
	public RSQLFilter() {
		this.operators = RSQLOperators.defaultOperators();
		this.operators.add(new ComparisonOperator("=like=", Arity.of(1, 1)));
		this.operators.add(new ComparisonOperator("=notlike=", Arity.of(1, 1)));
	}

	/**
	 * Jointure des sous-sections du filtre RSQL/FIQL
	 * @param children Liste des sous-sections du filtre RSQL/FIQL
	 * @param operator Opérateur liant les sous-sections du filtre RSQL/FIQL
	 * @param param Le dictionnaire des champs pour le filtre RSQL/FIQL
	 * @return les données du filtre del la jointure des sous-sections du filtre RSQL/FIQL
	 */
	protected RSQLFilterData join(List<Node> children, String operator, Map<String, DataFieldType> param) {
		List<String> parts = new ArrayList<>();
		DataFieldList params = new DataFieldList();

		for (Node ch : children) {
			RSQLFilterData subfilter = ch.accept(this, param);
			parts.add("(" + subfilter.getFilter() + ")");
			params.addAll(subfilter.getParams());
		}

		return new RSQLFilterData(OutilsBase.toList(parts, operator), params);
	}

	/**
	 * Extrait l'argument de l'opérateur d'un champ donné
	 * @param field Nom du champ
	 * @param op Opérateur du champ
	 * @param args Liste des arguments
	 * @return la valeur de l'argument de l'opérateur extrait
	 */
	protected String getSingleArg(String field, String op, List<String> args) {
		if (args.size() != 1) {
			String msg = "L'opérateur " + op + " pour " + field + " requière 1 argument";
			throw new RSQLParserException(new DataSetException(msg));
		}

		return args.get(0);
	}

	/**
	 * Vérifie si l'opérateur n'a pas d'argument pour un champ donné
	 * @param field Nom du champ
	 * @param op Opérateur du champ
	 * @param args Liste des arguments
	 */
	protected void hasNoArgs(String field, String op, List<String> args) {
		if (args != null && !args.isEmpty()) {
			String msg = "L'opérateur " + op + " pour " + field + " ne requière aucun argument";
			throw new RSQLParserException(new DataSetException(msg));
		}
	}

	/**
	 * Traitement d'un filtre RSQL/FIQL
	 * @param filter Le filtre RSQL/FIQL à traiter
	 * @param param Le dictionnaire des champs pour le filtre RSQL/FIQL
	 * @return les données du filtre
	 * @throws DataSetException en cas d'erreur...
	 */
	public RSQLFilterData parse(String filter, Map<String, DataFieldType> param) throws DataSetException {
		if (OutilsBase.isEmpty(filter)) {
			throw new DataSetException("Le filtre RSQL/FIQL est vide");
		}

		if (OutilsBase.isEmpty(param)) {
			throw new DataSetException("Le dictionnaire des champs pour le filtre RSQL/FIQL est vide");
		}

		try {
			return new RSQLParser(operators).parse(filter).accept(this, param);
		} catch (RSQLParserException e) {
			if (e.getCause() instanceof DataSetException) {
				throw (DataSetException) e.getCause();
			}

			throw new DataSetException(e.getLocalizedMessage());
		}
	}

	/**
	 * Traitement d'un tri JSON:API
	 * @param sort Le tri JSON:API
	 * @param param Le dictionnaire des champs pour le tri JSON:API
	 * @return le SQL du tri
	 * @throws DataSetException en cas d'erreur...
	 */
	public String sort(String sort, Map<String, DataFieldType> param) throws DataSetException {
		if (OutilsBase.isEmpty(sort)) {
			throw new DataSetException("Le tri JSON:API est vide");
		}

		if (OutilsBase.isEmpty(param)) {
			throw new DataSetException("Le dictionnaire des champs pour le tri JSON:API est vide");
		}

		List<String> items = new ArrayList<>();

		for (String part : sort.split(",")) {
			part = part.trim();

			boolean desc = part.startsWith("-");

			String field = desc ? part.substring(1) : part;

			if (!param.containsKey(field)) {
				String msg = "Le champ " + field + " pour le tri est inconnu";
				throw new RSQLParserException(new DataSetException(msg));
			}

			items.add(field + (desc ? " DESC" : " ASC"));
		}

		return OutilsBase.toList(items, ", ");
	}

	/*
	 * (non-Javadoc)
	 * @see cz.jirutka.rsql.parser.ast.RSQLVisitor#visit(cz.jirutka.rsql.parser.ast.AndNode, java.lang.Object)
	 */
	@Override
	public RSQLFilterData visit(AndNode node, Map<String, DataFieldType> param) {
		return join(node.getChildren(), " AND ", param);
	}

	/*
	 * (non-Javadoc)
	 * @see cz.jirutka.rsql.parser.ast.RSQLVisitor#visit(cz.jirutka.rsql.parser.ast.OrNode, java.lang.Object)
	 */
	@Override
	public RSQLFilterData visit(OrNode node, Map<String, DataFieldType> param) {
		return join(node.getChildren(), " OR ", param);
	}

	/*
	 * (non-Javadoc)
	 * @see cz.jirutka.rsql.parser.ast.RSQLVisitor#visit(cz.jirutka.rsql.parser.ast.ComparisonNode, java.lang.Object)
	 */
	@Override
	public RSQLFilterData visit(ComparisonNode node, Map<String, DataFieldType> param) {
		String field = node.getSelector();
		String op = node.getOperator().getSymbol();

		DataFieldType type = param.get(field);

		if (type == null) {
			String msg = "Le champ " + field + " pour l'opérateur " + op + " est inconnu";
			throw new RSQLParserException(new DataSetException(msg));
		}

		List<String> args = node.getArguments();
		StringBuilder sb = new StringBuilder();

		List<String> items = new ArrayList<>();

		switch (op) {
			case "==":
				sb.append(field).append(" = ?");
				items.add(getSingleArg(field, op, args));
				break;
			case "!=":
				sb.append(field).append(" <> ?");
				items.add(getSingleArg(field, op, args));
				break;
			case "=gt=":
				sb.append(field).append(" > ?");
				items.add(getSingleArg(field, op, args));
				break;
			case "=ge=":
				sb.append(field).append(" >= ?");
				items.add(getSingleArg(field, op, args));
				break;
			case "=lt=":
				sb.append(field).append(" < ?");
				items.add(getSingleArg(field, op, args));
				break;
			case "=le=":
				sb.append(field).append(" <= ?");
				items.add(getSingleArg(field, op, args));
				break;
			case "=in=":
			case "=out=":
				if (args.isEmpty()) {
					String msg = "L'opérateur " + op + " pour " + field + " n'a pas aucun item dans la liste";
					throw new RSQLParserException(new DataSetException(msg));
				}

				sb.append(field).append(op.equals("=out=") ? " NOT IN (" : " IN (");
				sb.append(String.join(",", Collections.nCopies(args.size(), "?"))).append(")");
				items.addAll(args);
				break;
			case "=null=":
				hasNoArgs(field, op, args);
				sb.append(field).append(" IS NULL");
				break;
			case "=notnull=":
				hasNoArgs(field, op, args);
				sb.append(field).append(" IS NOT NULL");
				break;
			case "=like=":
				sb.append(field).append(" LIKE ?");
				items.add(getSingleArg(field, op, args).replace('*', '%').replace('?', '_'));
				break;
			case "=notlike=":
				sb.append(field).append(" NOT LIKE ?");
				items.add(getSingleArg(field, op, args).replace('*', '%').replace('?', '_'));
				break;
			default:
				String msg = "L'opérateur " + op + " pour " + field + " n'est pas supporté";
				throw new RSQLParserException(new DataSetException(msg));
		}

		DataFieldList params = new DataFieldList();

		for (String item : items) {
			params.add(DataField.getInstance(type, op, item));
		}

		return new RSQLFilterData(sb.toString(), params);
	}

}
```
