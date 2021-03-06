package distribully.view;


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import distribully.controller.GameState;
import distribully.model.DistribullyModel;
import distribully.model.IObservable;
import distribully.model.IObserver;
import distribully.model.Player;

public class PlayerOverviewPanel extends DistribullyPanel implements IObserver {

	private static final long serialVersionUID = -2882716648466999779L;
	private DistribullyModel model;
	private Dimension size;
	
	public PlayerOverviewPanel(DistribullyModel model, Dimension size) {
		this.model = model;
		this.size = size;
		model.getOnlinePlayerList().addObserver(this);
		model.getGamePlayerList().addObserver(this);
		model.addObserver(this);
		this.render();
	}
	
	protected void render() {
		this.removeAll(); //Remove all elements, then re-add them
		
		//Determine which list of players to render: game members or all online
		ArrayList<Player> players;
		if (model.getGAME_STATE() == GameState.IN_LOBBY) {
			players = model.getGamePlayerList().getPlayers();
		} else {
			players = model.getOnlinePlayerList().getPlayers();
		}
		size.height = Math.max(players.size()*40 + 80,size.height);//Make sure panel is high enough

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setMinimumSize(size);
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		
		this.add(getHeaderPanel());
		if (players.size() == 1) {
			DistribullyPanel p = new DistribullyPanel();
			p.setMinimumSize(new Dimension(this.size.width,30));
			p.setPreferredSize(new Dimension(this.size.width,30));
			p.setMaximumSize(new Dimension(this.size.width,30));
			p.setLayout(new FlowLayout(FlowLayout.LEFT));
			DistribullyTextLabel l = new DistribullyTextLabel("No available players");
			p.add(l);
			this.add(p);
		} else {
			
			
			for (Player p : players) {
				//Do not render self in the general overview
				if (p.getName().equals(model.getNickname()) && model.getGAME_STATE() != GameState.IN_LOBBY) {
					continue;
				}
				this.renderPlayer(p);
			}
		}
		this.revalidate();
		this.repaint();
	}

	protected DistribullyPanel getHeaderPanel() {
		DistribullyPanel headerPanel = new DistribullyPanel();
		headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		headerPanel.setMinimumSize(new Dimension(this.size.width, 40));
		headerPanel.setPreferredSize(new Dimension(this.size.width, 40));
		headerPanel.setMaximumSize(new Dimension(this.size.width, 40));
		if (model.getGAME_STATE() == GameState.INVITING_USERS) {
			//Add a button to actually start the game
			headerPanel.add(new StartGameButton(model));
			headerPanel.add(new StopSettingUpGameButton(model));
		} else if (model.getGAME_STATE() == GameState.IN_LOBBY) {
			//Add a button to leave the lobby, that is: remove yourself from the game player list
			headerPanel.add(new LeaveLobbyButton(model));
		} else if (model.getGAME_STATE() == GameState.NOT_PLAYING) {
			headerPanel.setHeaderFont();
			DistribullyTextLabel headerLabel = new DistribullyTextLabel("Players that are online");
			headerLabel.setHeaderFont();
			headerPanel.add(headerLabel);
		}
		
		return headerPanel;
	}
	
	protected void renderPlayer(Player player) {
		DistribullyPanel playerPanel = new DistribullyPanel();
		playerPanel.setMinimumSize(new Dimension(this.size.width, 40));
		playerPanel.setPreferredSize(new Dimension(this.size.width, 40));
		playerPanel.setMaximumSize(new Dimension(this.size.width, 40));
		
		playerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		DistribullyTextLabel nameLabel = new DistribullyTextLabel(player.getName());
		nameLabel.setPreferredSize(new Dimension(400,40));
		playerPanel.add(nameLabel);
		
		if (model.getGAME_STATE() == GameState.INVITING_USERS) {
			playerPanel.add(getInvitationPanel(player));
		} else if (model.getGAME_STATE() == GameState.IN_LOBBY) {
			playerPanel.add(getLobbyPanel());
		}
		
		this.add(playerPanel);
	}

	/*
	 * Returns a panel containing either a working invite button, or an invitation state
	 */
	private DistribullyPanel getInvitationPanel(Player player) {
		DistribullyPanel playerPanel = new DistribullyPanel();
		if (player.isAvailable()) {
			InviteButton inviteButton = null;
			//Check if player was already invited
			if (model.getInviteStates().containsKey(player.getName())) {
				playerPanel.add(new JLabel(model.getInviteStates().get(player.getName())));
			} else {
				inviteButton = new InviteButton(model, player.getName());
				playerPanel.add(inviteButton);
			}
		} else {
			DistribullyTextLabel unavailableLabel;
			
			if (model.getInviteStates().containsKey(player.getName())) {
				unavailableLabel = new DistribullyTextLabel(model.getInviteStates().get(player.getName()));
			} else {
				unavailableLabel = new DistribullyTextLabel("unavailable");
			}
			playerPanel.add(unavailableLabel);
		}
		return playerPanel;
	}
	
	private DistribullyPanel getLobbyPanel() {
		DistribullyPanel playerPanel = new DistribullyPanel();
		return playerPanel;
	}
	
	@Override
	public void update(IObservable observable, Object changedObject) {
		this.render();
	}
}
