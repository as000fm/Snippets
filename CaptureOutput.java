package outils.abstractions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Classe qui capture les sorties System.out et System.err pour une section d'exécution donnée
 * @author Claude Toupin - 19 sept. 2024
 */
public abstract class CaptureOutput {
	/** Capture de System.out **/
	private String capturedOut;

	/** Capture de System.err **/
	private String capturedErr;

	/**
	 * Constructeur de base
	 */
	public CaptureOutput() {
		this.capturedOut = null;
		this.capturedErr = null;
	}

	/**
	 * Section de l'exécution à être capturée
	 */
	protected abstract void captureSection();

	public void doCaptureSection() throws IOException {
		try ( //
				ByteArrayOutputStream outStream = new ByteArrayOutputStream(); //
				ByteArrayOutputStream errStream = new ByteArrayOutputStream(); //
				PrintStream psOut = new PrintStream(outStream); //
				PrintStream psErr = new PrintStream(errStream) //
		) {
			PrintStream oldOut = System.out;
			PrintStream oldErr = System.err;

			System.setOut(psOut);
			System.setErr(psErr);

			captureSection();

			System.setOut(oldOut);
			System.setErr(oldErr);

			capturedOut = outStream.toString();
			capturedErr = errStream.toString();
		}
	}

	/**
	 * Extrait le champ capturedOut
	 * @return un String
	 */
	public String getCapturedOut() {
		return capturedOut;
	}

	/**
	 * Modifie le champ capturedOut
	 * @param capturedOut La valeur du champ capturedOut
	 */
	public void setCapturedOut(String capturedOut) {
		this.capturedOut = capturedOut;
	}

	/**
	 * Extrait le champ capturedErr
	 * @return un String
	 */
	public String getCapturedErr() {
		return capturedErr;
	}

	/**
	 * Modifie le champ capturedErr
	 * @param capturedErr La valeur du champ capturedErr
	 */
	public void setCapturedErr(String capturedErr) {
		this.capturedErr = capturedErr;
	}
}
