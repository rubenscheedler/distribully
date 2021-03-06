package distribully.model;

public class TurnState {
	private String nextPlayer;
	private int toPick; //How many should the next person to draw draw
	private int direction; //Rotation direction
	private String action; //Action that was taken
	private boolean chooseSuit; //Should the player choose a suit
	private String lastStack; //Who's stack was last played on
	
	public TurnState(String nextPlayer, int toPick, int direction, String action, boolean chooseSuit, String lastStack){
		this.nextPlayer = nextPlayer;
		this.toPick = toPick;
		this.direction = direction;
		this.action = action;
		this.setChooseSuit(chooseSuit);
		this.setLastStack(lastStack);
	}

	public String getNextPlayer() {
		return nextPlayer;
	}

	public void setNextPlayer(String nextPlayer) {
		this.nextPlayer = nextPlayer;
	}

	public int getToPick() {
		return toPick;
	}

	public void setToPick(int toPick) {
		this.toPick = toPick;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isChooseSuit() {
		return chooseSuit;
	}

	public void setChooseSuit(boolean chooseSuit) {
		this.chooseSuit = chooseSuit;
	}

	public String getLastStack() {
		return lastStack;
	}

	public void setLastStack(String lastStack) {
		this.lastStack = lastStack;
	}
	
}
