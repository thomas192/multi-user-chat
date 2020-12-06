package vue;
import java.util.List;
import java.util.Random;

import com.sun.media.jfxmedia.logging.Logger;

import controleur.ControleurAccueil;
import donnee.ServeurDAO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import modele.Message_Modele;


public class VueAccueil extends Vue {

	protected ControleurAccueil controleur;
	protected static VueAccueil instance = null; 
	public static VueAccueil getInstance() {if(null==instance)instance = new VueAccueil();return VueAccueil.instance;}; 
	
	private VueAccueil() 
	{
		super("vuePrincipale.fxml"); 
		super.controleur = this.controleur = new ControleurAccueil();
		Logger.logMsg(Logger.INFO, "new VueAccueil()");
	}
		
	public void activerControles()
	{
		super.activerControles();
		ServeurDAO serveur = new ServeurDAO();
		Random rand = new Random();
		
		//################################### LOOK UP DES IDs UTILES ####################################//
		Button actionEnvoyerMessage = (Button) lookup("#BT-envoyer");
		TextArea contenuMessage = (TextArea) lookup("#TA-message-envoyer");
		TabPane tab = (TabPane) lookup("#gestionnaire-tab");
		BorderPane bp = (BorderPane) lookup("#BP-principale");
		//###############################################################################################//
		
		//######################### AJOUT DE LA FENETRE UTILISATEURS EN LIGNES ##########################//
		ScrollPane scrollPane_Right = new ScrollPane();
		scrollPane_Right.setMinWidth(250);
		VBox verticalBox_SPR = new VBox();
		scrollPane_Right.setContent(verticalBox_SPR);
		TextField titre = new TextField();
		titre.setText("Utilisateurs en ligne :");
		titre.setStyle("-fx-font-size: 20 ; -fx-text-fill: BLACK ; -fx-background-color: RED");
		titre.setEditable(false);
		titre.setAlignment(Pos.CENTER);
		verticalBox_SPR.getChildren().add(titre);
		bp.setRight(scrollPane_Right);
		//###############################################################################################//
		
		//############################ AFFICHAGE DES UTILISATEURS EN LIGNES #############################//
		List<String> listeUtilisateurs = serveur.requestUtilisateursEnLigne();
		for(int i = 0 ; i < listeUtilisateurs.size() ; i ++)
		{
			TextField utilisateur = new TextField(listeUtilisateurs.get(i));
			utilisateur.setEditable(false);
			utilisateur.setStyle("-fx-font-size: 20 ; -fx-text-fill: BLACK");
			utilisateur.setAlignment(Pos.CENTER);
			verticalBox_SPR.getChildren().add(utilisateur);
		}
		//###############################################################################################//

		//################################ INITIALISATION LISTE SERVEURS ################################//
		List<String> listeServeurs = serveur.requestListeServeurs("Yann");  
		for(int i = 0 ; i < listeServeurs.size() ; i ++) 
		{
			Tab tb = new Tab(listeServeurs.get(i));
			tab.getTabs().add(tb);
		}
		//###############################################################################################//
		
		//############################ ACTIONS EN CAS DE CHANGEMENT D'ONGLETS ###########################//
		tab.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> 
		{	
			System.out.println("L'onglet sélectionné est désormais : " + newTab.getText());
			newTab.setContent(null); 
			ScrollPane scrollPane = new ScrollPane();
			VBox verticalBox_SP = new VBox();
			verticalBox_SP.setBackground(new Background(new BackgroundFill(Color.DIMGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

			verticalBox_SP.prefWidthProperty().bind(scrollPane.widthProperty());

			scrollPane.setContent(verticalBox_SP);
			
			newTab.setContent(scrollPane);
			
			//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CHARGEMENT DES MESSAGES SERVEURS @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
			List<Message_Modele> listeMessage = serveur.lister(newTab.getText());
			for(int i = 0 ; i < listeMessage.size() ; i++) 
			{
				ajouterUnMessage(listeMessage.get(i),verticalBox_SP);
				scrollPane.setVvalue(1.0);
			}
			//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//

			//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ACTIONS EN CAS D'ENVOIE DE MESSAGES @@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
			actionEnvoyerMessage.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override public void handle(ActionEvent e)
					{
						if(contenuMessage.getText().trim().length() != 0)
						{
							Logger.logMsg(Logger.INFO, "Message envoyé");

							String qui[] = {"Lucas", "Yann", "Thomas"};

							Message_Modele messageEnvoye = new Message_Modele(qui[rand.nextInt(3)], contenuMessage.getText(),"Maintenant");
							
							ajouterUnMessage(messageEnvoye,verticalBox_SP);
							serveur.envoyerMessage(messageEnvoye, newTab.getText());
							
							contenuMessage.clear();
							scrollPane.setVvalue(1.0);
						}
						else Logger.logMsg(Logger.INFO, "Message non envoyé, contenu vide");
					}
			});
			//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
	    });
		//###############################################################################################//
		tab.getSelectionModel().select(1);
		tab.getSelectionModel().select(0); 
	}
	
	void ajouterUnMessage(Message_Modele message, VBox verticalBox_SP){
		
		VBox verticalBox_MSG = new VBox();
		
		Text utilisateur = new Text(message.getUtilisateur());
		utilisateur.setFill(Color.RED);
		utilisateur.setUnderline(true);
		utilisateur.setStyle("-fx-font-weight: bold ; -fx-font-size: 20");
		
		TextArea contenuMessage = new TextArea(message.getMessage());
		contenuMessage.setEditable(false);
		contenuMessage.setWrapText(true);
		contenuMessage.setStyle("-fx-control-inner-background : DIMGRAY ; -fx-text-fill: WHITE ; -fx-font-size: 15");
		contenuMessage.setPrefSize(verticalBox_SP.getWidth(), contenuMessage.getHeight());

		verticalBox_MSG.getChildren().addAll(utilisateur, contenuMessage);

		verticalBox_SP.getChildren().add(verticalBox_MSG);
	}
}
