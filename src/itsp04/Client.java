package itsp04;

/* Simulation einer Kerberos-Session mit Zugriff auf einen Fileserver
 /* Client-Klasse
 */

public class Client extends Object {

		private KDC myKDC; // Konstruktor-Parameter

		private String currentUser; // Speicherung bei Login n�tig
		private Ticket tgsTicket = null; // Speicherung bei Login n�tig
		private long tgsSessionKey; // K(C,TGS) // Speicherung bei Login n�tig

		// Konstruktor
		public Client(KDC kdc) {
			myKDC = kdc;
		}

		public boolean login(String userName, char[] password) {
			return false;
			/* ToDo */
		}

		public boolean showFile(Server fileServer, String filePath) {
			return false;
			/* ToDo */
		}

		/* *********** Hilfsmethoden **************************** */

		private long generateSimpleKeyFromPassword(char[] passwd) {
			// Liefert einen eindeutig aus dem Passwort abgeleiteten Schl�ssel
			// zur�ck, hier simuliert als long-Wert
			long pwKey = 0;
			if (passwd != null) {
				for (int i = 0; i < passwd.length; i++) {
					pwKey = pwKey + passwd[i];
				}
			}
			return pwKey;
		}

		private long generateNonce() {
			// Liefert einen neuen Zufallswert
			long rand = (long) (100000000 * Math.random());
			return rand;
		}
}
