package distribully.model;

import java.util.ArrayList;
import java.util.HashMap;

import distribully.controller.GameState;
import distribully.model.rules.DrawTwoRule;
import distribully.model.rules.Rule;
import distribully.model.rules.SkipTurnRule;

public class DistribullyModel implements IObservable {
	private ClientList onlinePlayerList;//contains the current list of online players copied from the server
	
	private ClientList gamePlayerList;//contains the players that are part of the game that this user is a part of.
	
	private String serverAddress = "http://82.73.233.237";
	private int serverPort = 4567;
	private String myIP;
	private String currentHostName;
	private int myPort = 4567;

	private ArrayList<IObserver> observers;
	private HashMap<String,String> inviteStates;

	private String nickname;
	private Stack stack;
	private HashMap<Player,Card> topOfStacks;
	private ArrayList<Card> hand;
	
	private ArrayList<Rule> allRules;
	private HashMap<Integer,Rule> choosenRules;
	
	public DistribullyModel() {
		this.stack = new Stack();
		this.onlinePlayerList = new ClientList(serverAddress,serverPort);
		this.gamePlayerList = new ClientList(serverAddress, serverPort);
		observers = new ArrayList<IObserver>();
		inviteStates = new HashMap<String,String>();
		allRules = new ArrayList<Rule>();
		choosenRules = new HashMap<Integer,Rule>();
		fillAllRules();
		hand = new ArrayList<Card>();
		topOfStacks = new HashMap<Player,Card>();
		this.hand.add(new Card(5,CardSuit.CLUBS));
		this.hand.add(new Card(12,CardSuit.HEARTS));
	}
	
	
	
	private void fillAllRules() {
		allRules.add(new DrawTwoRule(this.stack));
		allRules.add(new SkipTurnRule(this.stack));
	}



	private GameState GAME_STATE;
	
	/**
	 * finds the player object corresponding with the user.
	 * @return
	 */
	public Player getMe() {
		return this.onlinePlayerList.getPlayerByNickname(nickname);
	}
	
	public String getMyIP() {
		return myIP;
	}

	public void setMyIP(String myIP) {
		this.myIP = myIP;
	}

	public int getMyPort() {
		return myPort;
	}

	public void setMyPort(int myPort) {
		this.myPort = myPort;
	}

	public GameState getGAME_STATE() {
		return GAME_STATE;
	}

	public void setGAME_STATE(GameState gAME_STATE) {
		GAME_STATE = gAME_STATE;
		this.notifyObservers();
	}

	public ClientList getGamePlayerList() {
		return gamePlayerList;
	}

	public void setGamePlayerList(ClientList gamePlayerList) {
		this.gamePlayerList = gamePlayerList;
	}

	public ClientList getOnlinePlayerList() {
		return onlinePlayerList;
	}

	public void setOnlinePlayerList(ClientList onlinePlayerList) {
		this.onlinePlayerList = onlinePlayerList;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
		this.notifyObservers();
	}

	@Override
	public void addObserver(IObserver observer) {
		this.observers.add(observer);
	}

	@Override
	public void removeObserver(IObserver observer) {
		this.observers.remove(observer);
	}

	@Override
	public void notifyObservers() {
		//System.out.println("notifying model observers (count=" + this.observers.size() + ")");
		this.observers.forEach(observer -> observer.update(this));
	}
	
	public HashMap<String, String> getInviteStates() {
		return inviteStates;
	}

	public void setInviteStates(HashMap<String, String> inviteStates) {
		this.inviteStates = inviteStates;
	}
	
	public void putInviteState(String key, String inviteState) {
		this.inviteStates.put(key, inviteState);
		this.notifyObservers();
	}
	
	/**
	 * checks for all entries in the hashmap if the server still contains it in the game players. Drops it, if not.
	 * @param gamePlayers
	 */
	public void updateInviteStatesByListState(ClientList gamePlayers) {
		ArrayList<String> toRemove = new ArrayList<String>();
		//check which players left
		for (String name : this.inviteStates.keySet()) {
			if (this.inviteStates.get(name).equals("Accepted") && gamePlayers.getPlayerByNickname(name) == null) {
				toRemove.add(name);
			}
		}
		//remove the them from the invitation states
		toRemove.forEach(name -> this.inviteStates.remove(name));
		this.notifyObservers();
	}

	public String getCurrentHostName() {
		return currentHostName;
	}

	public void setCurrentHostName(String currentHostName) {
		this.currentHostName = currentHostName;
	}

	public void setCardRule(int cardNumber, Rule rule) {
		this.getChoosenRules().put(cardNumber, rule);
		this.notifyObservers();
	}

	public void removeCardRule(int cardNumber) {
		this.getChoosenRules().remove(cardNumber);
		this.notifyObservers();
	}

	public ArrayList<Rule> getAllRules() {
		return allRules;
	}

	public void setAllRules(ArrayList<Rule> allRules) {
		this.allRules = allRules;
	}

	public HashMap<Integer,Rule> getChoosenRules() {
		return choosenRules;
	}

	public void setChoosenRules(HashMap<Integer,Rule> choosenRules) {
		this.choosenRules = choosenRules;
	}



	public HashMap<Player,Card> getTopOfStacks() {
		return topOfStacks;
	}



	public void setTopOfStacks(HashMap<Player,Card> topOfStacks) {
		this.topOfStacks = topOfStacks;
	}



	public ArrayList<Card> getHand() {
		return hand;
	}



	public void setHand(ArrayList<Card> hand) {
		this.hand = hand;
	}
}
