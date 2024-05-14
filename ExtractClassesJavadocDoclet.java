package outils.javadoc.doclets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.sun.source.doctree.AuthorTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.ErroneousTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.doctree.VersionTree;
import com.sun.source.util.DocTrees;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import outils.javadoc.data.ClassJavadocData;
import outils.javadoc.data.ConstructorJavadocData;
import outils.javadoc.data.ExceptionJavadocData;
import outils.javadoc.data.FieldJavadocData;
import outils.javadoc.data.MethodJavadocData;
import outils.javadoc.data.ParameterJavadocData;
import outils.javadoc.types.ClassesJavadocTypes;
import outils.javadoc.types.FieldsJavadocTypes;

/**
 * Doclet d'extraction du javadoc des classes
 * @author Claude Toupin - 11 mai 2024
 */
public class ExtractClassesJavadocDoclet implements Doclet {

	/**
	 * Classe des données des types des paramètres et du type de retour d'une méthode ou d'un constructeur
	 * @author Claude Toupin - 13 mai 2024
	 */
	protected class JavadocTypesData {
		/** Type java générique **/
		private final String genericType;

		/** Types java des paramètres **/
		private final List<String> paramsTypesList;

		/** Type de retour java **/
		private final String returnType;

		/**
		 * Constructeur de base
		 * @param javadocTypes Valeur des types en format javadoc de la méthode ou d'un constructeur
		 */
		public JavadocTypesData(String javadocTypes) {
			this.paramsTypesList = new ArrayList<>();

			if (javadocTypes == null) {
				this.genericType = null;
				this.returnType = null;
			} else {
				int start;
				int end;

				start = javadocTypes.indexOf('<');
				end = javadocTypes.indexOf('>');

				this.genericType = ((start != -1) && (end != -1)) ? javadocTypes.substring(start, end) : null;

				start = javadocTypes.indexOf('(');
				end = javadocTypes.indexOf(')');

				if ((start != -1) && (end != -1)) {
					if ((end - start) >= 2) {
						String[] params = javadocTypes.substring(start + 1, end).split(",");

						for (String param : params) {
							paramsTypesList.add(param);
						}
					}

					if ((end + 1) < javadocTypes.length()) {
						this.returnType = javadocTypes.substring(end + 1);
					} else {
						this.returnType = null;
					}
				} else {
					this.returnType = javadocTypes;
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "MethodParamsReturnTypeData [genericType=" + genericType + ", paramsTypesList=" + paramsTypesList + ", returnType=" + returnType + "]";
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			JavadocTypesData other = (JavadocTypesData) obj;
			return Objects.equals(genericType, other.genericType) && Objects.equals(paramsTypesList, other.paramsTypesList) && Objects.equals(returnType, other.returnType);
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return Objects.hash(genericType, paramsTypesList, returnType);
		}

		/**
		 * Extrait le champ genericType
		 * @return un String
		 */
		public String getGenericType() {
			return genericType;
		}

		/**
		 * Extrait le champ paramsTypesList
		 * @return un List<String>
		 */
		public List<String> getParamsTypesList() {
			return paramsTypesList;
		}

		/**
		 * Extrait le champ returnType
		 * @return un String
		 */
		public String getReturnType() {
			return returnType;
		}
	}

	/** Liste des classes extraites **/
	private static final List<ClassJavadocData> CLASSES_JAVADOC_LIST = new ArrayList<>();

	/** Dictionnaire des classes extraites **/
	private static final Map<String, ClassJavadocData> CLASSES_JAVADOC_DICT = new HashMap<>();

	/** Journalisation des messages **/
	private Reporter reporter;

	/** Arborescences de la documentation **/
	private DocTrees docTrees;

	/**
	 * Traitement d'un élément de la documentation de type classe donné
	 * @param element L'élément de la documentation de type classe à traiter
	 * @return la classe extraite (i.e. class, enum, interface ou annotation)
	 */
	protected ClassJavadocData processClassElement(Element element) {
		ClassJavadocData classJavadocData = null;

		if (element != null) {
			switch (element.asType().getKind()) {
				case DECLARED:
					ClassesJavadocTypes classType;

					switch (element.getKind()) {
						case CLASS:
							classType = ClassesJavadocTypes.CLASS;
							break;
						case ENUM:
							classType = ClassesJavadocTypes.ENUM;
							break;
						case INTERFACE:
							classType = ClassesJavadocTypes.INTERFACE;
							break;
						case ANNOTATION_TYPE:
							classType = ClassesJavadocTypes.ANNOTATION;
							break;
						default:
							classType = null;
							reporter.print(Diagnostic.Kind.ERROR, element, "Pas de traitement pour l'élément de processClassElement de type " + element.getKind());
							break;
					}

					if (classType != null) {
						classJavadocData = new ClassJavadocData(element.asType().toString(), element.getSimpleName().toString(), classType);

						DocCommentTree docCommentTree = docTrees.getDocCommentTree(element);

						if (docCommentTree != null) {
							classJavadocData.setComment(processFullBodyComment(element, docCommentTree.getFullBody()));
							classJavadocData.setAuthor(processBlockTags(element, docCommentTree.getBlockTags(), DocTree.Kind.AUTHOR));
							classJavadocData.setVersion(processBlockTags(element, docCommentTree.getBlockTags(), DocTree.Kind.VERSION));
						}

						for (Element enclosedElement : element.getEnclosedElements()) {
							if (enclosedElement != null) {
								FieldJavadocData fieldJavadocData;
								
								switch (enclosedElement.asType().getKind()) {
									case DECLARED:
										switch (enclosedElement.getKind()) {
											case FIELD:
												fieldJavadocData = processFieldElement(enclosedElement, FieldsJavadocTypes.FIELD);

												if (fieldJavadocData != null) {
													classJavadocData.getFieldsList().add(fieldJavadocData.getName());
													classJavadocData.getFieldsDict().put(fieldJavadocData.getName(), fieldJavadocData);
												}
												break;
											case ENUM_CONSTANT:
												fieldJavadocData = processFieldElement(enclosedElement, FieldsJavadocTypes.ENUM);

												if (fieldJavadocData != null) {
													classJavadocData.getFieldsList().add(fieldJavadocData.getName());
													classJavadocData.getFieldsDict().put(fieldJavadocData.getName(), fieldJavadocData);
												}
												break;
											default:
												ClassJavadocData encloseClassJavadocData = processClassElement(enclosedElement);

												if (encloseClassJavadocData != null) {
													classJavadocData.getInnerClassesList().add(encloseClassJavadocData.getName());
													classJavadocData.getInnerClassesDict().put(encloseClassJavadocData.getName(), encloseClassJavadocData);
												}
												break;
										}
										break;
									case EXECUTABLE:
										String signature;

										switch (enclosedElement.getKind()) {
											case CONSTRUCTOR:
												ConstructorJavadocData constructorJavadocData = processConstructorElement(element.getSimpleName().toString(), enclosedElement);

												if (constructorJavadocData != null) {
													signature = constructorJavadocData.asSignature();
													classJavadocData.getConstructorsList().add(signature);
													classJavadocData.getConstructorsDict().put(signature, constructorJavadocData);
												}
												break;
											case METHOD:
												MethodJavadocData methodJavadocData = processMethodElement(enclosedElement);

												if (methodJavadocData != null) {
													signature = methodJavadocData.asSignature();
													classJavadocData.getMethodsList().add(signature);
													classJavadocData.getMethodsDict().put(signature, methodJavadocData);
												}
												break;
											default:
												reporter.print(Diagnostic.Kind.ERROR, enclosedElement, "Pas de traitement pour le sous-élément de processClassElement de type " + enclosedElement.getKind());
												break;
										}
										break;
									default:
										FieldsJavadocTypes fieldType;

										switch (enclosedElement.getKind()) {
											case ENUM:
												fieldType = FieldsJavadocTypes.ENUM;
												break;
											case FIELD:
												fieldType = FieldsJavadocTypes.FIELD;
												break;
											default:
												fieldType = null;
												reporter.print(Diagnostic.Kind.ERROR, enclosedElement, "Pas de traitement pour le sous-élément de processClassElement de type " + enclosedElement.getKind());
												break;
										}

										if (fieldType != null) {
											fieldJavadocData = processFieldElement(enclosedElement, fieldType);

											if (fieldJavadocData != null) {
												classJavadocData.getFieldsList().add(fieldJavadocData.getName());
												classJavadocData.getFieldsDict().put(fieldJavadocData.getName(), fieldJavadocData);
											}
										}
										break;
								}
							}
						}
					}
					break;
				default:
					reporter.print(Diagnostic.Kind.ERROR, element, "Pas de traitement pour l'élément de processClassElement de type " + element.asType().getKind());
					break;
			}
		}

		return classJavadocData;
	}

	/**
	 * Traitement d'un élément de la documentation de type constructeur donné
	 * @param name Nom du constructeur à traiter
	 * @param element L'élément de la documentation de type constructeur à traiter
	 * @return le constructeur extrait
	 */
	protected ConstructorJavadocData processConstructorElement(String name, Element element) {
		ConstructorJavadocData constructorJavadocData = null;

		if (element != null) {
			JavadocTypesData javadocTypesData = new JavadocTypesData(element.asType().toString());

			constructorJavadocData = new ConstructorJavadocData(name);

			DocCommentTree docCommentTree = docTrees.getDocCommentTree(element);

			if (docCommentTree != null) {
				constructorJavadocData.setComment(processFullBodyComment(element, docCommentTree.getFullBody()));

				for (ParameterJavadocData parameterJavadocData : processParametersBlockTags(element, javadocTypesData, docCommentTree.getBlockTags())) {
					constructorJavadocData.getParametersList().add(parameterJavadocData.getName());
					constructorJavadocData.getParametersDict().put(parameterJavadocData.getName(), parameterJavadocData);
				}

				for (ExceptionJavadocData exceptionJavadocData : processThrowsBlockTags(element, docCommentTree.getBlockTags())) {
					constructorJavadocData.getExceptionsList().add(exceptionJavadocData.getName());
					constructorJavadocData.getExceptionsDict().put(exceptionJavadocData.getName(), exceptionJavadocData);
				}
			}
		}

		return constructorJavadocData;
	}

	/**
	 * Traitement d'un élément de la documentation de type méthode donné
	 * @param element L'élément de la documentation de type méthode à traiter
	 * @return la méthode extraite
	 */
	protected MethodJavadocData processMethodElement(Element element) {
		MethodJavadocData methodJavadocData = null;

		if (element != null) {
			JavadocTypesData javadocTypesData = new JavadocTypesData(element.asType().toString());

			methodJavadocData = new MethodJavadocData(element.getSimpleName().toString(), javadocTypesData.getReturnType());

			DocCommentTree docCommentTree = docTrees.getDocCommentTree(element);

			if (docCommentTree != null) {
				methodJavadocData.setComment(processFullBodyComment(element, docCommentTree.getFullBody()));

				for (ParameterJavadocData parameterJavadocData : processParametersBlockTags(element, javadocTypesData, docCommentTree.getBlockTags())) {
					methodJavadocData.getParametersList().add(parameterJavadocData.getName());
					methodJavadocData.getParametersDict().put(parameterJavadocData.getName(), parameterJavadocData);
				}

				for (ExceptionJavadocData exceptionJavadocData : processThrowsBlockTags(element, docCommentTree.getBlockTags())) {
					methodJavadocData.getExceptionsList().add(exceptionJavadocData.getName());
					methodJavadocData.getExceptionsDict().put(exceptionJavadocData.getName(), exceptionJavadocData);
				}

				methodJavadocData.setReturnTypeComment(processBlockTags(element, docCommentTree.getBlockTags(), DocTree.Kind.RETURN));
			}
		}

		return methodJavadocData;
	}

	/**
	 * Traitement d'un élément de la documentation de type champ donné
	 * @param element L'élément de la documentation de type champ à traiter
	 * @param fieldType Le type champ à traiter
	 * @return le champ extrait
	 */
	protected FieldJavadocData processFieldElement(Element element, FieldsJavadocTypes fieldType) {
		FieldJavadocData fieldJavadocData = null;

		if (element != null) {
			JavadocTypesData javadocTypesData = new JavadocTypesData(element.asType().toString());

			fieldJavadocData = new FieldJavadocData(element.getSimpleName().toString(), javadocTypesData.getReturnType(), fieldType);

			DocCommentTree docCommentTree = docTrees.getDocCommentTree(element);

			if (docCommentTree != null) {
				fieldJavadocData.setComment(processFullBodyComment(element, docCommentTree.getFullBody()));
			}
		}

		return fieldJavadocData;
	}

	/**
	 * Traitement d'un item en erreur d'un élément donné
	 * @param element L'élément de la documentation à traiter
	 * @param erroneousTree L'erreur à traiter
	 * @return le texte traité de l'erreur
	 */
	protected String processErroneousTree(Element element, ErroneousTree erroneousTree) {
		switch (erroneousTree.getDiagnostic().getCode()) {
			case "compiler.err.dc.bad.lt":
				return "<";
			case "compiler.err.dc.bad.gt":
				return ">";
			case "compiler.err.dc.malformed.html":
				// Ignore !!!
				return "";
			default:
				reporter.print(Diagnostic.Kind.WARNING, element, "Pas de traitement pour l'élément de processErroneousTree " + erroneousTree.getDiagnostic().getCode() + " pour " + erroneousTree.getDiagnostic().toString());
				return "";
		}
	}

	/**
	 * Traitement des parties du texte d'un commentaire
	 * @param parts Les parties de texte à traiter
	 * @return le texte du commentaire traité
	 */
	protected String processCommentParts(String[] parts) {
		StringBuilder sb = new StringBuilder();

		if (parts != null) {
			for (String part : parts) {
				if (sb.length() != 0) {
					if (sb.charAt(sb.length() - 1) != '.') {
						sb.append('.');
					}

					sb.append(' ');
				}

				sb.append(part.trim());
			}
		}

		return sb.toString();
	}

	/**
	 * Traitement d'un commentaire multilignes donné
	 * @param comment Le commentaire à traiter
	 * @return le texte du commentaire traité
	 */
	protected String processComment(String comment) {
		if ((comment != null) && !comment.isEmpty()) {
			if (comment.contains("\r\n")) {
				comment = processCommentParts(comment.split("\r\n"));
			}

			if (comment.contains("\n\r")) { // Peu probable...
				comment = processCommentParts(comment.split("\n\r"));
			}

			if (comment.contains("\n")) {
				comment = processCommentParts(comment.split("\n"));
			}

			if (comment.contains("\r")) { // Peu probable...
				comment = processCommentParts(comment.split("\r"));
			}
		}

		return comment;
	}

	/**
	 * Traitement d'une liste de lignes de commentaires d'un élément donné
	 * @param element L'élément de la documentation à traiter
	 * @param list La liste de lignes de commentaires à traiter
	 * @return les commentaires sous forme d'une seule ligne
	 */
	protected String processFullBodyComment(Element element, List<? extends DocTree> list) {
		String comment = null;

		if ((list != null) && !list.isEmpty()) {
			StringBuilder sb = new StringBuilder();

			for (DocTree item : list) {
				switch (item.getKind()) {
					case TEXT:
						TextTree textTree = (TextTree) item;
						sb.append(textTree.getBody());
						break;
					case ERRONEOUS:
						sb.append(processErroneousTree(element, (ErroneousTree) item));
						break;
					default:
						// Ignore
						break;
				}
			}

			if (sb.length() != 0) {
				comment = processComment(sb.toString());
			}
		}

		return comment;
	}

	/**
	 * Traitement d'une liste d'étiquettes de commentaires d'un élément donné
	 * @param element L'élément de la documentation à traiter
	 * @param list La liste de lignes de commentaires à traiter
	 * @param kind Type d'étiquette à traiter
	 * @return les commentaires de l'étiquette sous forme d'une seule ligne
	 */
	protected String processBlockTags(Element element, List<? extends DocTree> list, DocTree.Kind kind) {
		if ((list != null) && !list.isEmpty() && (kind != null)) {
			for (DocTree item : list) {
				if (item.getKind() == kind) {
					switch (item.getKind()) {
						case AUTHOR:
							AuthorTree authorTree = (AuthorTree) item;
							return processFullBodyComment(element, authorTree.getName());
						case RETURN:
							ReturnTree returnTree = (ReturnTree) item;
							return processFullBodyComment(element, returnTree.getDescription());
						case VERSION:
							VersionTree versionTree = (VersionTree) item;
							return processFullBodyComment(element, versionTree.getBody());
						case PARAM:
						case THROWS:
							// Ignore
							break;
						default:
							reporter.print(Diagnostic.Kind.ERROR, element, "Pas de traitement pour l'élément de processBlockTags de type " + item.getKind());
							break;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Traitement d'une liste d'étiquettes de commentaires des paramètres d'un constructeur ou de la méthode d'un élément donné
	 * @param element L'élément de la documentation à traiter
	 * @param javadocTypesData Données des types des paramètres du constructeur ou de la méthode à traiter
	 * @param list La liste de lignes de commentaires à traiter
	 * @return une liste des paramètres extraits
	 */
	protected List<ParameterJavadocData> processParametersBlockTags(Element element, JavadocTypesData javadocTypesData, List<? extends DocTree> list) {
		List<ParameterJavadocData> blockTagsList = new ArrayList<>();

		if ((list != null) && !list.isEmpty()) {
			for (DocTree item : list) {
				int index = 0;

				switch (item.getKind()) {
					case PARAM:
						ParamTree paramTree = (ParamTree) item;

						if (!paramTree.isTypeParameter()) {
							blockTagsList.add(new ParameterJavadocData(paramTree.getName().toString(), javadocTypesData.getParamsTypesList().get(index), processFullBodyComment(element, paramTree.getDescription())));
						}
						break;
					case AUTHOR:
					case RETURN:
					case THROWS:
					case VERSION:
						// Ignore
						break;
					default:
						reporter.print(Diagnostic.Kind.WARNING, element, "Pas de traitement pour l'élément de processParametersBlockTags de type " + item.getKind());
						break;
				}
			}
		}

		return blockTagsList;
	}

	/**
	 * Traitement d'une liste d'étiquettes de commentaires des exceptions lancées d'un constructeur ou de la méthode d'un élément donné
	 * @param element L'élément de la documentation à traiter
	 * @param list La liste de lignes de commentaires à traiter
	 * @return une liste des exceptions lancées extraites
	 */
	protected List<ExceptionJavadocData> processThrowsBlockTags(Element element, List<? extends DocTree> list) {
		List<ExceptionJavadocData> blockTagsList = new ArrayList<>();

		if ((list != null) && !list.isEmpty()) {
			for (DocTree item : list) {
				switch (item.getKind()) {
					case THROWS:
						ThrowsTree throwsTree = (ThrowsTree) item;
						blockTagsList.add(new ExceptionJavadocData(throwsTree.getExceptionName().toString(), processFullBodyComment(element, throwsTree.getDescription())));
						break;
					case AUTHOR:
					case PARAM:
					case RETURN:
					case VERSION:
						// Ignore
						break;
					default:
						reporter.print(Diagnostic.Kind.WARNING, element, "Pas de traitement pour l'élément de processThrowsBlockTags de type " + item.getKind());
						break;
				}
			}
		}

		return blockTagsList;
	}

	/*
	 * (non-Javadoc)
	 * @see jdk.javadoc.doclet.Doclet#init(java.util.Locale, jdk.javadoc.doclet.Reporter)
	 */
	@Override
	public void init(Locale locale, Reporter reporter) {
		this.reporter = reporter;
	}

	/*
	 * (non-Javadoc)
	 * @see jdk.javadoc.doclet.Doclet#getName()
	 */
	@Override
	public String getName() {
		return ExtractClassesJavadocDoclet.class.getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * @see jdk.javadoc.doclet.Doclet#getSupportedOptions()
	 */
	@Override
	public Set<? extends Option> getSupportedOptions() {
		return Set.of();
	}

	/*
	 * (non-Javadoc)
	 * @see jdk.javadoc.doclet.Doclet#getSupportedSourceVersion()
	 */
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	/*
	 * (non-Javadoc)
	 * @see jdk.javadoc.doclet.Doclet#run(jdk.javadoc.doclet.DocletEnvironment)
	 */
	@Override
	public boolean run(DocletEnvironment environment) {
		CLASSES_JAVADOC_LIST.clear();
		CLASSES_JAVADOC_DICT.clear();

		docTrees = environment.getDocTrees();

		for (Element element : environment.getIncludedElements()) {
			if (element instanceof TypeElement) {
				ClassJavadocData classJavadocData = processClassElement((TypeElement) element);

				if (classJavadocData != null) {
					CLASSES_JAVADOC_LIST.add(classJavadocData);
					CLASSES_JAVADOC_DICT.put(classJavadocData.getName(), classJavadocData);
				}
			}
		}

		return true;
	}

	/**
	 * Extrait le champ classesJavadocList
	 * @return un List<ClassJavadocData>
	 */
	public static List<ClassJavadocData> getClassesJavadocList() {
		return CLASSES_JAVADOC_LIST;
	}

	/**
	 * Extrait le champ classesJavadocDict
	 * @return un Map<String,ClassJavadocData>
	 */
	public static Map<String, ClassJavadocData> getClassesJavadocDict() {
		return CLASSES_JAVADOC_DICT;
	}
}
