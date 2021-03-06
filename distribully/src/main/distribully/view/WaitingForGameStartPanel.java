package distribully.view;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import distribully.model.DistribullyModel;
import distribully.model.IObservable;
import distribully.model.IObserver;
import distribully.model.Player;

public class WaitingForGameStartPanel extends DistribullyPanel implements IObserver {

	private static final long serialVersionUID = -4781135704384241776L;
	private DistribullyModel model;
	private Dimension size;
	private static Logger logger;
	
	public WaitingForGameStartPanel(DistribullyModel model, Dimension size) {
		this.model = model;
		this.size = size;
		
		logger = LoggerFactory.getLogger("view.WaitingForGameStartPanel");
		
		this.model.addObserver(this);
		this.model.getGamePlayerList().addObserver(this);
		
		this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		this.setMinimumSize(size);
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		render();
	}
	
	public void render() {
		this.removeAll(); //Remove all components
		DistribullyPanel headerPanel = new DistribullyPanel();
		Dimension s = new Dimension(this.size.width,40);
		headerPanel.setMinimumSize(s);
		headerPanel.setPreferredSize(s);
		headerPanel.setMaximumSize(s);
		headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		DistribullyTextLabel header = new DistribullyTextLabel("Waiting For Everyone to Choose Rules...");
		headerPanel.add(header);
		this.add(headerPanel);
		
		for (Player player : model.getGamePlayerList().getPlayers()) {
			this.add(getPlayerPanel(player));
		}
	}
	
	public DistribullyPanel getPlayerPanel(Player player) {
		DistribullyPanel playerPanel = new DistribullyPanel();
		playerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		playerPanel.setMinimumSize(new Dimension(this.size.width,40));
		playerPanel.setPreferredSize(new Dimension(this.size.width,40));
		playerPanel.setMaximumSize(new Dimension(this.size.width,40));
		
		DistribullyTextLabel name = new DistribullyTextLabel(player.getName());
		name.setPreferredSize(new Dimension(200,40));
		DistribullyTextLabel choosenrules = new DistribullyTextLabel(player.isReadyToPlay() ? "ready" : "still choosing rules");
		choosenrules.setPreferredSize(new Dimension(200,40));
		
		playerPanel.add(name);
		playerPanel.add(choosenrules);
		
		return playerPanel;
	}

	@Override
	public void update(IObservable observable, Object changedObject) {
		this.render();
		logger.info("update waiting for players panel");
	}
}
