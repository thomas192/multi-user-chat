package modele;

public class Message_Modele {
	
	
	String utilisateur;
	String message;
	String date;
	
	public Message_Modele(String utilisateur, String message, String date) {
		super();
		this.utilisateur = utilisateur;
		this.message = message;
		this.date = date;
	}
	
	public Message_Modele() {
		super();
	}
	
	public String getUtilisateur() {
		return utilisateur;
	}
	public String getMessage() {
		return message;
	}
	public String getDate() {
		return date;
	}
	public void setUtilisateur(String utilisateur) {
		this.utilisateur = utilisateur;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
