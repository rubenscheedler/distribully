package distribully.view;

import javax.swing.JMenuItem;

import distribully.controller.SetUpGameHandler;
import distribully.model.DistribullyModel;

public class SetUpGameButton extends JMenuItem{

	private static final long serialVersionUID = -3732106117892923335L;
	
	public SetUpGameButton(DistribullyModel model) {
		this.setText("Set up game");
		
		this.addActionListener(new SetUpGameHandler(model));
	}
}

