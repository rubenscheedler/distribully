package distribully.controller;


import java.awt.event.WindowEvent;


import javax.swing.JOptionPane;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import distribully.view.DistribullyWindow;

public class JoinClientListHandler {
	
	private DistribullyWindow frame;
	private static Logger logger;
	
	public JoinClientListHandler(DistribullyWindow frame) {
		logger = LoggerFactory.getLogger("controller.JoinclientList");
		this.frame = frame;
		String chosenNickname = "";

		do {
			chosenNickname = askUserForName();
			
			if (chosenNickname == null) { //user does not want to pick a username => close application
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
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
			if (!nicknameAvailable(chosenNickname)) {
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
		frame.getModel().setNickname(chosenNickname);
		//this update is required to fetch the current user from the server as well:
		new ClientListUpdateHandler(frame.getModel());
		DistribullyController.waitForInviteThread = new WaitForInviteThread(frame.getModel());
	}

	
	public String askUserForName() {
		return JOptionPane.showInputDialog(frame, "Please enter your nickname");
	}
	
	public boolean nicknameAvailable(String nickname) {
		HttpClient client = new HttpClient();
		ContentResponse response = null;
		try {
			client.start();
			String sa = frame.getModel().getServerAddress();
			response = client.newRequest(sa + ":" + frame.getModel().getServerPort() + "/players/" + nickname)
					.method(HttpMethod.POST)
					.param("port", frame.getModel().getMyPort()+"")
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
			frame.getModel().setMyIP(myAddress);
			return true;
		} else { //Doesn't matter what the response is, the result is the same.
			return false;
		}
	}
}
