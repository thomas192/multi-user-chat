package vue;

import javafx.scene.image.Image;
import javafx.stage.Stage;
//import javafx.stage.StageStyle;

public class Fenetre extends Navigateur {

	@Override
	public void start(Stage stade) throws Exception {
		stade.getIcons().add(new Image("file:50650.png"));
		stade.setTitle("Incroyable application de Chat, 100% fonctionnelle");
		stade.setScene(VueAccueil.getInstance());
		
		stade.show();
		stade.setMaximized(true);
		this.stade = stade;
	}

}
