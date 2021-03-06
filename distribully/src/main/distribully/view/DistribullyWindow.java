package distribully.view;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.swing.ScrollPaneConstants.*;

import distribully.controller.ClientListUpdateHandler;
import distribully.controller.CloseWindowHandler;
import distribully.controller.GameState;
import distribully.model.DistribullyModel;
import distribully.model.IObservable;
import distribully.model.IObserver;

public class DistribullyWindow extends JFrame implements IObserver {

	private static final long serialVersionUID = -6180030798589552918L;
	private static Logger logger;
	
	//Model
	private DistribullyModel model;
	
	//View components
	private DistribullyPanel mainPanel;
	private PlayerOverviewPanel playerOverviewPanel;
	private SelectRulesPanel selectRulesPanel;
	private WaitingForGameStartPanel waitingForGameStartPanel;
	private HandPanel gamePanel;
	private JScrollPane scrollPane;
	
	
	public DistribullyWindow(DistribullyModel model) {
		this.model = model;
		logger = LoggerFactory.getLogger("view.DistribullyWindow");
		//Get the size of the monitor
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.height -= 100;
		//window properties
		this.setSize(screenSize.width, screenSize.height);
		this.setVisible(true);
		this.setTitle("Distribully");
		model.addObserver(this);
		//Init of menu
		this.setJMenuBar(new DistribullyMenu(this.model));
		new ClientListUpdateHandler(this.model);
		
		Dimension contentSize = this.getContentPane().getSize();
		contentSize.width -= 25;
		contentSize.height = screenSize.height - 100;

		playerOverviewPanel = new PlayerOverviewPanel(model,contentSize);
		selectRulesPanel = new SelectRulesPanel(this,contentSize);
		gamePanel = new HandPanel(model,contentSize);
		waitingForGameStartPanel = new WaitingForGameStartPanel(model,contentSize);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.addWindowListener(new CloseWindowHandler(model));
		
		this.determinePanelToShow();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));
		
		addScrollPane(mainPanel);
		
		this.revalidate();
		this.repaint();
	}
	
	public DistribullyModel getModel() {
		return this.model;
	}

	@Override
	public void update(IObservable observable, Object gameState) {
		if (gameState instanceof GameState) {
			this.remove(scrollPane);
			determinePanelToShow();	
			
			logger.info("gameState update window|gameState"+gameState.toString()+"|comCount:"+this.getComponent(0).toString());
			addScrollPane(mainPanel);
			this.revalidate();
			this.repaint();
		} 
	}
	
	public void determinePanelToShow() {
		switch (model.getGAME_STATE()) {
		case SETTING_RULES:
			mainPanel = selectRulesPanel;
			break;
		case WAITING_FOR_GAMESTART:
			mainPanel = waitingForGameStartPanel;
			break;
		case IN_GAME:
			mainPanel = gamePanel;
			break;
		case NOT_PLAYING:
		case INVITING_USERS:
		case IN_LOBBY:
		default:
			mainPanel = playerOverviewPanel;
			break;
		}
	}
	
	public void addScrollPane(DistribullyPanel panel) {
		scrollPane = new JScrollPane(panel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		
		scrollPane.getViewport().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				mainPanel.revalidate();
				mainPanel.repaint();
			}
		});
		panel.revalidate();
		panel.repaint();
		scrollPane.revalidate();
		scrollPane.repaint();
		this.add(scrollPane);
		

	}
}
