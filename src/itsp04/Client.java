package itsp04;

/* Simulation einer Kerberos-Session mit Zugriff auf einen Fileserver
 /* Client-Klasse
 */

public class Client extends Object {

		private KDC myKDC; // Konstruktor-Parameter

		private String currentUser; // Speicherung bei Login nötig
		private Ticket tgsTicket = null; // Speicherung bei Login nötig
		private long tgsSessionKey; // K(C,TGS) // Speicherung bei Login nötig

		private String tgsServer = "mytgs";
		
		private Server myFileServer;
		
		// Konstruktor
		public Client(KDC kdc) {
			myKDC = kdc;
		}
		/**
		 * TGS-Ticket fuer den uebergebenen User vom KDC (AS) holen
		 * (TGS-Servername: myTGS) und zusammen mit dem TGS-Sessionkey und 
		 * dem UserNamen speichern.
		 * 
		 * @param userName
		 * @param password
		 * @return Status (Login ok/fehlgeschlagen)
		 * 
		 */
		public boolean login(String userName, char[] password) {
			/* ToDo */
			//Speichern des userNames
			currentUser = userName;
			long nonce 	=  generateNonce();
			
			TicketResponse tgsTicketResponse = myKDC.requestTGSTicket(userName, tgsServer, nonce);
			
			if(tgsTicketResponse != null){
				//Sitzungsschluessel mit password verschluesselt
				long passwordLong = generateSimpleKeyFromPassword(password);
				
				if(tgsTicketResponse.decrypt(passwordLong) && (nonce == tgsTicketResponse.getNonce())){
					//Speicherung des tgsTickets
					tgsTicket = tgsTicketResponse.getResponseTicket();
					//Speicherung des tgsSessionkeys
					tgsSessionKey = tgsTicketResponse.getSessionKey();
					return true;
				}
			}
			
			return false;
		}
		/**
		 * Serverticket vom KDC (TGS) holen und schowFile-Service beim uebergebenen
		 * Fileserver anfordern.
		 * 
		 * @param fileServer
		 * @param filePath
		 * @return Status (Befehlsausfuehrung ok / fehlgeschlagen)
		 * 
		 */
		public boolean showFile(Server fileServer, String filePath) {
			myFileServer = fileServer;
			long serverSessionKey;
			Ticket serverTicket = null;
			
			/* ToDo */
			long currentTime = System.currentTimeMillis();
			Auth tgsAuth = new Auth(currentUser, currentTime);
			// Authentifikation mit dem Key verschlüsseln
			tgsAuth.encrypt(tgsSessionKey);
			// nonce
			long nonce 	=  generateNonce();
			
			TicketResponse serverTicketResponse = myKDC.requestServerTicket(tgsTicket, tgsAuth, tgsServer, nonce);
			
			if(serverTicketResponse.decrypt(tgsSessionKey) && (nonce == serverTicketResponse.getNonce())){
				serverTicket = serverTicketResponse.getResponseTicket();
				serverSessionKey = serverTicketResponse.getSessionKey();
			}
			
			if(serverTicket != null){
				return myFileServer.requestService(serverTicket, tgsAuth, "showFile", filePath);
			}
			
			return false;
		}

		/* *********** Hilfsmethoden **************************** */

		private long generateSimpleKeyFromPassword(char[] passwd) {
			// Liefert einen eindeutig aus dem Passwort abgeleiteten Schlüssel
			// zurück, hier simuliert als long-Wert
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
