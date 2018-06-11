/**
 * 
 */
package itsp04;

/**
 * @author nelli
 *
 */

/* Simulation einer Kerberos-Session mit Zugriff auf einen Fileserver
 /* Server-Klasse
 */

import java.util.*;
import java.io.*;

public class Server extends Object {

		private final long fiveMinutesInMillis = 300000; // 5 Minuten in
															// Millisekunden

		private String myName; // Konstruktor-Parameter
		private KDC myKDC; // wird bei KDC-Registrierung gespeichert
		private long myKey; // wird bei KDC-Registrierung gespeichert

		// Konstruktor
		public Server(String name) {
			myName = name;
		}

		public String getName() {
			return myName;
		}

		public void setupService(KDC kdc) {
			// Anmeldung des Servers beim KDC
			myKDC = kdc;
			myKey = myKDC.serverRegistration(myName);
			System.out.println("Server " + myName
					+ " erfolgreich registriert bei KDC " + myKDC.getName()
					+ " mit ServerKey " + myKey);
		}
		/**
		 * showFile-Befehl mit Filepath als Parameter ausfuehren,
		 * d.h. Dateiinhalt zeilenweise auf der Konsole ausgeben
		 * 
		 * @param srvTicket
		 * @param srvAuth
		 * @param command
		 * @param parameter
		 * @return
		 * 
		 */
		public boolean requestService(Ticket srvTicket, Auth srvAuth,
				String command, String parameter) {
			srvTicket.print();
			if(srvTicket.decrypt(myKey) && (srvTicket.getServerName() == myName)){
				//Pruefen auf korrekte SessionKey, Username, Time
				if(srvAuth.decrypt(srvTicket.getSessionKey())
					&& (srvAuth.getClientName() == srvTicket.getClientName())
					&& timeValid(srvTicket.getStartTime(),srvTicket.getEndTime())
					&& timeFresh(srvAuth.getCurrentTime())){
					return this.showFile(parameter);
				}
			}
			return false;
		}

		/* *********** Services **************************** */

		private boolean showFile(String filePath) {
			/*
			 * Angegebene Datei auf der Konsole ausgeben. Rückgabe: Status der
			 * Operation
			 */
			String lineBuf = null;
			File myFile = new File(filePath);
			boolean status = false;

			if (!myFile.exists()) {
				System.out.println("Datei " + filePath + " existiert nicht!");
			} else {
				try {
					// Datei �ffnen und zeilenweise lesen
					BufferedReader inFile = new BufferedReader(
							new InputStreamReader(new FileInputStream(myFile)));
					lineBuf = inFile.readLine();
					while (lineBuf != null) {
						System.out.println(lineBuf);
						lineBuf = inFile.readLine();
					}
					inFile.close();
					status = true;
				} catch (IOException ex) {
					System.out.println("Fehler beim Lesen der Datei " + filePath
							+ ex);
				}
			}
			return status;
		}

		/* *********** Hilfsmethoden **************************** */

		private boolean timeValid(long lowerBound, long upperBound) {
			/*
			 * Wenn die aktuelle Zeit innerhalb der �bergebenen Zeitgrenzen liegt,
			 * wird true zur�ckgegeben
			 */

			long currentTime = (new Date()).getTime(); // Anzahl mSek. seit 1.1.1970
			if (currentTime >= lowerBound && currentTime <= upperBound) {
				return true;
			} else {
				System.out.println("-------- Time not valid: " + currentTime
						+ " not in (" + lowerBound + "," + upperBound + ")!");
				return false;
			}
		}

		boolean timeFresh(long testTime) {
			/*
			 * Wenn die �bergebene Zeit nicht mehr als 5 Minuten von der aktuellen
			 * Zeit abweicht, wird true zur�ckgegeben
			 */
			long currentTime = (new Date()).getTime(); // Anzahl mSek. seit 1.1.1970
			if (Math.abs(currentTime - testTime) < fiveMinutesInMillis) {
				return true;
			} else {
				System.out.println("-------- Time not fresh: " + currentTime
						+ " is current, " + testTime + " is old!");
				return false;
			}
		}	
}
