package distribully.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import distribully.model.DistribullyModel;

public class StopSettingUpGameHandler implements ActionListener {

	private DistribullyModel model;
	
	public StopSettingUpGameHandler(DistribullyModel model) {
		this.model = model;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//Delete game list from server
		model.getGamePlayerList().deleteGameList(model.getCurrentHostName());
		//Kill open invites
		DistribullyController.InviteThreadList.forEach(thread -> thread.closeServer());
		//Stop update thread of game list
		DistribullyController.updateGameHostThread.setIsSettingUpGame(false);

		new BackToMainPageHandler(model);
	}

}
