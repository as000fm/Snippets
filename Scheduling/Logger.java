package outils.loggers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import outils.loggers.data.RunnableLoggerEntryData;

/**
 * Classe qui implémente une journalisation sur un fil d'exécution (thread)
 * @author Claude Toupin - 16 août 2024
 */
public class RunnableLogger implements Runnable {
	/** Sémaphore **/
	private final ReentrantLock reentrantLock;

	/** Liste des entrées à journaliser **/
	private final List<RunnableLoggerEntryData> entriesList;

	/** Instance de journalisation **/
	private final Logger logger;

	/** Planificateur de la journalisation **/
	private final ScheduledExecutorService scheduler;

	/** Indicateur de textes courts pour un message de niveau info **/
	private boolean shortLogInfo;

	/**
	 * Constructeur de base
	 */
	public RunnableLogger() {
		this(true);
	}

	/**
	 * Constructeur de base
	 * @param shortLogInfo Indicateur de textes courts pour un message de niveau info
	 */
	public RunnableLogger(boolean shortLogInfo) {
		this.shortLogInfo = shortLogInfo;
		this.reentrantLock = new ReentrantLock();
		this.entriesList = new ArrayList<>();
		this.logger = Logger.getLogger(RunnableLogger.class.getName());
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		this.scheduler.scheduleWithFixedDelay(this, 250, 500, TimeUnit.MILLISECONDS);
	}

	/**
	 * Fin de l'exécution planifiée
	 */
	public void shutdown() {
		scheduler.shutdownNow();

		try {
			scheduler.awaitTermination(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// Ignore...
		}
	}

	/**
	 * Ajout d'une entrée au journal
	 * @param name Nom de la journalisation
	 * @param level Niveau du message
	 * @param message Message à journaliser
	 */
	public void log(String name, Level level, String message) {
		reentrantLock.lock();

		try {
			entriesList.add(new RunnableLoggerEntryData(name, level, message));
		} finally {
			reentrantLock.unlock();
		}
	}

	/**
	 * Ajout d'une entrée au journal
	 * @param name Nom de la journalisation
	 * @param level Niveau du message
	 * @param message Message à journaliser
	 * @param thrown Erreur associée au message
	 */
	public void log(String name, Level level, String message, Throwable thrown) {
		reentrantLock.lock();

		try {
			entriesList.add(new RunnableLoggerEntryData(name, level, message, thrown));
		} finally {
			reentrantLock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		reentrantLock.lock();

		try {
			for (RunnableLoggerEntryData entry : entriesList) {
				String message;
				
				if (shortLogInfo && Level.INFO.equals(entry.getLevel())) {
					message = entry.getName() + ": " + entry.getMessage();
				} else {
					message = "for " + entry.getName() + ": " + entry.getMessage();
				}
				
				if (entry.getThrown() == null) {
					logger.log(entry.getLevel(), message);
				} else {
					logger.log(entry.getLevel(), message, entry.getThrown());
				}
			}

			entriesList.clear();
		} finally {
			reentrantLock.unlock();
		}
	}

	/**
	 * Extrait le champ shortLogInfo
	 * @return un boolean
	 */
	public boolean isShortLogInfo() {
		return shortLogInfo;
	}

	/**
	 * Modifie le champ shortLogInfo
	 * @param shortLogInfo La valeur du champ shortLogInfo
	 */
	public void setShortLogInfo(boolean shortLogInfo) {
		this.shortLogInfo = shortLogInfo;
	}

}

// -------------------

package outils.loggers.data;

import java.util.Objects;
import java.util.logging.Level;

import outils.loggers.RunnableLogger;

/**
 * Classe des données pour la journalisation sur un fil d'exécution (thread)
 * @author Claude Toupin - 16 août 2024
 */
public class RunnableLoggerData {
	/** Instance de la journalisation sur un fil d'exécution (thread) **/
	private final RunnableLogger runnableLogger;

	/** Classe source de la journalisation **/
	private final Class<?> forClass;

	/** Nom de la classe **/
	private final String name;

	/** Nom simple de la classe **/
	private final String simpleName;

	/**
	 * Constructeur de base
	 * @param runnableLogger Instance de la journalisation sur un fil d'exécution (thread)
	 * @param forClass Classe source de la journalisation
	 */
	public RunnableLoggerData(RunnableLogger runnableLogger, Class<?> forClass) {
		this.runnableLogger = runnableLogger;
		this.forClass = forClass;
		this.name = forClass.getName();
		this.simpleName = forClass.getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RunnableLoggerData [forClass=" + forClass + "]";
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
		RunnableLoggerData other = (RunnableLoggerData) obj;
		return Objects.equals(forClass, other.forClass);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(forClass);
	}

	/**
	 * Extrait le nom de la classe source à utiliser
	 * @param level Niveau du message
	 * @return le nom de la classe source à utiliser
	 */
	protected String getLogName(Level level) {
		if (runnableLogger.isShortLogInfo() && Level.INFO.equals(level)) {
			return simpleName;
		}

		return name;
	}

	/**
	 * Ajout d'une entrée au journal
	 * @param level Niveau du message
	 * @param message Message à journaliser
	 */
	public void log(Level level, String message) {
		runnableLogger.log(getLogName(level), level, message);
	}

	/**
	 * Ajout d'une entrée au journal
	 * @param level Niveau du message
	 * @param message Message à journaliser
	 * @param thrown Erreur associée au message
	 */
	public void log(Level level, String message, Throwable thrown) {
		runnableLogger.log(getLogName(level), level, message, thrown);
	}

	/**
	 * Extrait le champ runnableLogger
	 * @return un RunnableLogger
	 */
	public RunnableLogger getRunnableLogger() {
		return runnableLogger;
	}

	/**
	 * Extrait le champ forClass
	 * @return un Class<?>
	 */
	public Class<?> getForClass() {
		return forClass;
	}
}

// -------------

package outils.loggers.data;

import java.util.Objects;
import java.util.logging.Level;

/**
 * Classe des données d'une entrée à journaliser
 * @author Claude Toupin - 16 août 2024
 */
public class RunnableLoggerEntryData {
	/** Nom de la journalisation **/
	private final String name;

	/** Niveau du message **/
	private final Level level;

	/** Message à journaliser **/
	private final String message;

	/** Erreur associée au message **/
	private final Throwable thrown;

	/**
	 * Constructeur de base
	 * @param name Nom de la journalisation
	 * @param level Niveau du message
	 * @param message Message à journaliser
	 * @param thrown Erreur associée au message
	 */
	public RunnableLoggerEntryData(String name, Level level, String message) {
		this(name, level, message, null);
	}

	/**
	 * Constructeur de base
	 * @param name Nom de la journalisation
	 * @param level Niveau du message
	 * @param message Message à journaliser
	 * @param thrown Erreur associée au message
	 */
	public RunnableLoggerEntryData(String name, Level level, String message, Throwable thrown) {
		this.name = name;
		this.level = level;
		this.message = message;
		this.thrown = thrown;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RunnableLoggerEntryData [name=" + name + ", level=" + level + ", message=" + message + ", thrown=" + thrown + "]";
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
		RunnableLoggerEntryData other = (RunnableLoggerEntryData) obj;
		return Objects.equals(level, other.level) && Objects.equals(message, other.message) && Objects.equals(name, other.name);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(level, message, name);
	}

	/**
	 * Extrait le champ name
	 * @return un String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Extrait le champ level
	 * @return un Level
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * Extrait le champ message
	 * @return un String
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Extrait le champ thrown
	 * @return un Throwable
	 */
	public Throwable getThrown() {
		return thrown;
	}
}

// --------------------

package server.listeners;

import java.util.logging.Level;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import outils.commun.OutilsCommun;
import outils.loggers.RunnableLogger;

/**
 * Journalisation de CentrAPI sur un fil d'exécution (thread)
 * @author Claude Toupin - 29 mars 2025
 */
@WebListener
public class CentrAPILogger implements ServletContextListener {
	/** Instance de la journalisation sur un fil d'exécution (thread) **/
	private static final RunnableLogger INSTANCE = new RunnableLogger();

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		INSTANCE.log(CentrAPILogger.class.getSimpleName(), Level.INFO, "Runnable logging started on " + OutilsCommun.getHostName());
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		INSTANCE.shutdown();
	}

	/**
	 * Extrait le champ instance
	 * @return un RunnableLogger
	 */
	public static RunnableLogger getInstance() {
		return INSTANCE;
	}

}

// ------------------

/**
 * Servlet implementation for OpenAI-style chat completion API
 * @author Claude Toupin - 8 févr. 2025
 */
@WebServlet("/v1/chat/completions")
public class ChatCompletionsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/** Logger for catalina.out **/
	private static final RunnableLoggerData LOGGER = new RunnableLoggerData(CentrAPILogger.getInstance(), ChatCompletionsServlet.class);

