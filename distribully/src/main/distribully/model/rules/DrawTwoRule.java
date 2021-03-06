package distribully.model.rules;

import distribully.model.DistribullyModel;
import distribully.model.TurnState;

public class DrawTwoRule extends Rule {

	public DrawTwoRule(DistribullyModel model) {
		super(model);
	}

	@Override
	public TurnState execute() {
		TurnState turnState = new TurnState(model.getTurnState().getNextPlayer(),model.getTurnState().getToPick(),model.getTurnState().getDirection(),model.getTurnState().getAction(),false,model.getNickname());
		
		turnState.setNextPlayer(rotateTurn(turnState.getDirection()));
		turnState.setToPick(turnState.getToPick()+2);
		turnState.setAction(model.getTurnState().getNextPlayer() + " made the next person draw 2 more. The total draw count now stands at: " + turnState.getToPick() + ".");
		return turnState;
	}

	@Override
	public String toString() {
		return "Draw two";
	}

}
