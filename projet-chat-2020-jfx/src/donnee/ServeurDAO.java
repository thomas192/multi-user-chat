/************************************************
* Auteur : Tilodry                              *
* Nom prog : NEO                                *
* Nom du fichier : AnneeDAO.java                *
* Role : DAO pour objet Annee_Modele            *
* Version : Finale                              *
* Date :  07/10/2020                            *
*************************************************/

package donnee;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import modele.Message_Modele;


public class ServeurDAO {

	
	public List<Message_Modele> lister(String serveur)
	{
		//TODO Envoyer demande liste message de "serveur"
		Random rand = new Random();
		int j = 1 + rand.nextInt(30);
		String qui[] = {"Lucas", "Yann", "Thomas"};
		String randomWords[] = {"chair","name","pottery","center","fit","major","fraction","monster","cabin","dome","squash","estate","world","champagne","drag","shareholder","breakfast","magnetic","total","trunk","pipe","tactic","democratic","curtain","area","tune","residence","literature","output","random","constituency","dominate","dull","absolute","resist","cancel","hill","sticky","spy","diamond","lick","spider","asset","exception","confuse","mind","foot","flush","tournament","knife"};

		List<Message_Modele> listeMessages = new ArrayList<>();
		for(int i = 0 ; i < j ; i ++ ) {
			Message_Modele message = new Message_Modele();
			String envoyeur = qui[rand.nextInt(3)];
			message.setUtilisateur(envoyeur);
			String contenuMessage = "";
			int wordCount = 1 + rand.nextInt(150);
			for(int k = 0 ; k < wordCount ; k++) {
				contenuMessage += randomWords[rand.nextInt(50)] + " ";
			}
			message.setMessage(contenuMessage);
			message.setDate("Jamais");
			listeMessages.add(message);
		}
		return listeMessages;
		
	}

	public List<String> requestListeServeurs(String utilisateur) {
		//TODO Envoyer demande liste serveur d'"utilisateur" a BDD
		List<String> listeServeurs = new ArrayList<String>();
		listeServeurs.add("Serveur-Public-1");
		listeServeurs.add("Serveur-Prive-1");
		listeServeurs.add("Serveur-Prive-2");
		return listeServeurs;
		
	}

	public void envoyerMessage(Message_Modele messageEnvoye, String serveur) {
		// TODO Envoyer message a BDD
	}

	public List<String> requestUtilisateursEnLigne() {
		//TODO Envoyer demande liste d'utilisateurs en ligne a BDD
		List<String> listeUtilisateursEnLigne = new ArrayList<>();
		listeUtilisateursEnLigne.add("Yann");
		listeUtilisateursEnLigne.add("Thomas");
		listeUtilisateursEnLigne.add("Lucas");
		listeUtilisateursEnLigne.add("Pierre-Alexis");
		listeUtilisateursEnLigne.add("Jonathan");
		listeUtilisateursEnLigne.add("Vincent");
		listeUtilisateursEnLigne.add("Maxence");
		listeUtilisateursEnLigne.add("Nicolas");
		listeUtilisateursEnLigne.add("Christophe");
		listeUtilisateursEnLigne.add("Charles");

		return listeUtilisateursEnLigne;
	}
}
