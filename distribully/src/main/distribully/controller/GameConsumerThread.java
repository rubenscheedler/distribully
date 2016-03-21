package distribully.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import distribully.model.Card;
import distribully.model.CardSuit;
import distribully.model.DistribullyModel;
import distribully.model.Player;
import distribully.model.TurnState;

public class GameConsumerThread extends Thread{

	DistribullyModel model;
	String queueName;
	boolean playing;
	public GameConsumerThread(DistribullyModel model){
		this.model = model;
		new ClientListUpdateHandler(model); //Ensure the playerList is up to date
		queueName = model.getNickname();
		String hostName = model.getCurrentHostName();
		Player host = model.getOnlinePlayerList().getPlayerByNickname(hostName);
		playing = true;
		try {
			initPlayerExchange(host);
		} catch (ShutdownSignalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConsumerCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.start();

	}

	public void run(){
		while(playing){
			//TODO : WAT DAN
		}
	}

	public void setPlaying(boolean playing){
		this.playing = playing;
	}

	private void initPlayerExchange(Player player){
		ConnectionFactory factory = new ConnectionFactory();  
		String playerName = player.getName();
		factory.setHost(player.getIp());
		
		try {
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(queueName, false, false, false, null);
			channel.exchangeDeclare(playerName, "fanout");
			channel.queueBind(queueName, playerName, "");
			channel.queuePurge(queueName);
			Consumer consumer = new MessageConsumer(channel);
			channel.basicConsume(queueName, true, consumer);
			System.out.println("host connected: " + playerName);
		} catch (TimeoutException e) {
			//Player has lost internet availability or rabbitMQ is not running -> remove from playerList
			model.getGamePlayerList().getPlayers().remove(player);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class MessageConsumer extends DefaultConsumer{
		JsonParser parser;
		Gson gson;
		public MessageConsumer(Channel channel) {
			super(channel);
			parser = new JsonParser();
			gson = new Gson();
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope,
				AMQP.BasicProperties properties, byte[] body) throws IOException {
			switch(envelope.getRoutingKey()){
			case "Start":
				System.out.println("Game is starting!");
				if(DistribullyController.lobbyThread != null){
					DistribullyController.lobbyThread.setInLobby(false);
				}
				new ClientListUpdateHandler(model);
				model.getGamePlayerList().getPlayers().forEach(player -> initPlayerExchange(player));
				model.setGAME_STATE(GameState.SETTING_RULES);
				break;
			case "Leave":
				JsonElement jeLeave = parser.parse(new String(body));
				String playerNameLeave = jeLeave.getAsJsonObject().get("playerName").getAsString();
				model.getGamePlayerList().getPlayers().removeIf(player -> player.getName().equals(playerNameLeave));
				System.out.println(playerNameLeave + " left");
				
				//TODO: view?
				if(model.getGamePlayerList().getPlayers().size() <= 1){
					if(model.getGamePlayerList().getPlayers().stream().anyMatch(p -> p.getName() == model.getNickname())){
						//YOU WON //TODO: Dit iets automatischer maken, niet alleen bij leave, mss bij elke refresh/actie?
					}
					//Redirect main screen, reboot al die threads enzo
				}
				break;
			case "Rules":
				JsonElement jeRule = parser.parse(new String(body));
				String playerNameRule = jeRule.getAsJsonObject().get("playerName").getAsString();
				System.out.println("Rules from  "+ playerNameRule + " received");
				Player player = model.getGamePlayerList().getPlayerByNickname(playerNameRule);
				player.setReadyToPlay(true);
				if(model.getGamePlayerList().getPlayers().stream().allMatch(p->p.isReadyToPlay())){
					
					model.setAndBroadCastTopOfStack();
					model.generateNewHand();
					//only one player may create the first turn object. We define this as the alphabetically first player
					ArrayList<Player> toSort = new ArrayList<Player>();
					toSort.addAll(model.getGamePlayerList().getPlayers());
					Collections.sort(toSort, (p1, p2) -> p1.getName().compareTo(p2.getName()));
					if (model.getNickname().equals(toSort.get(0).getName())) { //Someone has to do it
						model.generateAndSendFirstTurnObject();
					}
					model.setGAME_STATE(GameState.IN_GAME);
				}
				break;
			case "PlayCard":
				JsonObject jeCard = parser.parse(new String(body)).getAsJsonObject();
				int cardId = Integer.parseInt(jeCard.get("cardId").getAsString());
				int cardSuit = Integer.parseInt(jeCard.get("cardSuit").getAsString());
				String playerName = jeCard.get("playerName").getAsString();
				System.out.println("Card "+ cardId + " from suite " + cardSuit +" played on stack of "+ playerName); //TODO cardSuite parser
				//TODO: View
				break;
			case "NextTurn":
				JsonObject jeTurn = parser.parse(new String(body)).getAsJsonObject();
				String action = jeTurn.get("action").getAsString();
				JsonObject turnState = jeTurn.get("turnState").getAsJsonObject();
				TurnState newState = gson.fromJson(turnState, TurnState.class);
				model.setTurnState(newState);
				System.out.println("Next player is "+ newState.getNextPlayer() +" by action " + action);
				//TODO: action handlen
				//TODO: View
				break;
			case "ChooseSuit":
				JsonObject jeSuit = parser.parse(new String(body)).getAsJsonObject();
				int suit = Integer.parseInt(jeSuit.get("cardSuit").getAsString());
				String playerNext = jeSuit.get("playerNextName").getAsString();
				String playerCurrent = jeSuit.get("playerName").getAsString();
				System.out.println("Next player is "+ playerNext +", new suite on "+ playerCurrent +" is " + suit); //suite parse
				//TODO: View
				break;
			case "InitStack":
				JsonObject jeStack = parser.parse(new String(body)).getAsJsonObject();
				int stackCardId = Integer.parseInt(jeStack.get("cardId").getAsString());
				int stackSuit = Integer.parseInt(jeStack.get("cardSuit").getAsString());
				String playerStackName = jeStack.get("playerName").getAsString();
				System.out.println(playerStackName +" has top of stack " + stackSuit + " " + stackCardId);
				//TODO: view
				model.putTopOfStack(model.getGamePlayerList().getPlayerByNickname(playerStackName), new Card(stackCardId, CardSuit.values()[stackSuit]));
				break;
			case "Win":
				JsonObject jeWin = parser.parse(new String(body)).getAsJsonObject();
				String playerWinner = jeWin.get("playerWinner").getAsString();
				System.out.println(playerWinner + " has won.");
				//TODO: popup
				//TODO: Reset Hashmap of responses
				//TODO: Back to main (threads, gamestate)

				break;
			}
		}
	}
}
