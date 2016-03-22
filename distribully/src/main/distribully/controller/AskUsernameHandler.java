package distribully.controller;

import javax.swing.JOptionPane;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import distribully.model.DistribullyModel;

public class AskUsernameHandler {
	private static Logger logger;
	DistribullyModel model;
	
	public AskUsernameHandler(DistribullyModel model) {
		logger = LoggerFactory.getLogger("controller.JoinclientList");
		String chosenNickname = "";
		this.model = model;

		do {
			chosenNickname = askUserForName();
			
			if (chosenNickname == null) { //user does not want to pick a username => close application
				DistribullyController.sendCloseEvent();
				return;
			}
			chosenNickname = chosenNickname.trim();
			
			if (chosenNickname.contains(" ")){
				chosenNickname = "";
				JOptionPane.showMessageDialog(null,
					    "Spaces are not allowed in the username",
					    "Invalid username",
					    JOptionPane.WARNING_MESSAGE);
			}
			if (!chosenNickname.equals("") && !nicknameAvailable(chosenNickname)) {
				chosenNickname = "";
				JOptionPane.showMessageDialog(null,
					    "Username is already is use",
					    "Invalid username",
					    JOptionPane.WARNING_MESSAGE);
			}
			
		}
		while (chosenNickname.equals(""));
		
		//check if name is unique
		logger.info("setting model nickname:" + chosenNickname);
		model.setNickname(chosenNickname);
		//this update is required to fetch the current user from the server as well:
		new ClientListUpdateHandler(model);
		DistribullyController.waitForInviteThread = new WaitForInviteThread(model);
	}

	
	public String askUserForName() {
		return JOptionPane.showInputDialog(null, "Please enter your nickname");
	}
	
	public boolean nicknameAvailable(String nickname) {
		HttpClient client = new HttpClient();
		ContentResponse response = null;
		try {
			client.start();
			String sa = model.getServerAddress();
			response = client.newRequest(sa + ":" + model.getServerPort() + "/players/" + nickname)
					.method(HttpMethod.POST)
					.param("port", model.getMyPort()+"")
					.send();
			client.stop();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
				    "Could not validate username",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			logger.error("Could not validate username");
			System.exit(69); //Service unavailable
		}
		
		if (response.getStatus() == 201) {
			JsonParser jsonParser = new JsonParser();
			logger.info(response.getContentAsString());
			JsonElement jsonElement = jsonParser.parse(response.getContentAsString());
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			String myAddress = jsonObject.get("ip").getAsString();
			model.setMyIP(myAddress);
			return true;
		} else { //Doesn't matter what the response is, the result is the same.
			return false;
		}
	}
}
