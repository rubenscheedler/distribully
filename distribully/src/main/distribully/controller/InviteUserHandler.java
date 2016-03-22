package distribully.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import distribully.model.DistribullyModel;
import distribully.model.Player;

public class InviteUserHandler implements ActionListener {

	private DistribullyModel model;
	private String name;
	private static Logger logger;
	
	public InviteUserHandler(String username, DistribullyModel model) {
		this.model = model;
		name = username;
		logger = Logger.getLogger("controller.InviteUserHandler");
		logger.setParent(Logger.getLogger("controller.DistribullyController"));
		
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Player player = model.getOnlinePlayerList().getPlayers().stream().filter(x -> x.getName().equals(name)).findFirst().get();
		if(player == null){
			//TODO: afvangen bitch
		}else{
			logger.fine("creating invite thread...");
			InviteThread thread = new InviteThread(player, model);
			DistribullyController.InviteThreadList.add(thread);
			model.putInviteState(name, "waiting for response...");
		}
		
	}

}
