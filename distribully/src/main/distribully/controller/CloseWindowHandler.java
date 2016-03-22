package distribully.controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import distribully.model.DistribullyModel;
import distribully.model.TurnState;

public class CloseWindowHandler extends WindowAdapter {

	DistribullyModel model;

	public CloseWindowHandler(DistribullyModel model){
		this.model = model;
	}
	@Override
	public void windowClosing(WindowEvent e)
	{ 
		if (model.getGAME_STATE() == GameState.IN_GAME || model.getGAME_STATE() == GameState.SETTING_RULES){
			int leaveGame = JOptionPane.showConfirmDialog (null, 
					"You are in the middle of a game, are you sure you want to quit?", 
					"Confirm",JOptionPane.YES_NO_OPTION); 
			if(leaveGame == JOptionPane.YES_OPTION){
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost(model.getMe().getIp());
				Connection connection;
				try {
					connection = factory.newConnection();

					Channel channel = connection.createChannel();

					channel.exchangeDeclare(model.getNickname(), "fanout");

					JsonObject message = new JsonObject();
					message.addProperty("playerName", model.getNickname());
					Gson gson = new Gson();
					JsonParser parser = new JsonParser();
					JsonObject turnState = parser.parse(gson .toJson(new TurnState(model.getNextPlayer(), 0, model.getTurnState().getDirection(), model.getNickname() + " has left the game.", false, ""))).getAsJsonObject();
					message.add("turnState", turnState);

					channel.basicPublish(model.getNickname(), "Leave", null, message.toString().getBytes());
					System.out.println(" [x] Sent '" + message + "'");

					channel.close();
					connection.close();
				} catch (IOException | TimeoutException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}else{
				return;
			}
		}
		model.getOnlinePlayerList().deleteFromServer(model.getNickname());
		if(model.getGAME_STATE() == GameState.IN_LOBBY){
			model.getGamePlayerList().deleteFromGame(model.getNickname(),model.getCurrentHostName());
		} else if (model.getGAME_STATE() == GameState.INVITING_USERS) {
			model.getGamePlayerList().deleteGameList(model.getNickname());//=current host name
		}
		System.out.println("Closed game");
		System.exit(0);

	}
}
