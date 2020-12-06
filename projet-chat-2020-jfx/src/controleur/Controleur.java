package controleur;

import vue.Vue;
import vue.VueAccueil;

//import vue.Navigateur;
//import vue.*;

public class Controleur {
	
	public static Vue selectionnerVuePrincipale() 
	{
		return VueAccueil.getInstance();
	}
	
}
