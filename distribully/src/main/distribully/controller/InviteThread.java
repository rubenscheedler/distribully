package distribully.controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import distribully.model.DistribullyModel;
import distribully.model.Player;

public class InviteThread extends Thread{
	private String address;
	private int port;
	private Player player;
	DistribullyModel model;
	Socket s = null;
	
	public InviteThread(Player player, DistribullyModel model){
		this.player = player;
		address = player.getIp();
		port = player.getPort();
		this.model = model;
		this.start();
	}
	
	public void closeServer(){
		try{
			if(s != null){
				s.close();
			}
		}catch(Exception e){
			//Will always throw exception if the thread is waiting for a response. TODO: Ignore?.
		}
	}
	
	public void run() {
		DataOutputStream out;
		DataInputStream in;
		try {
			s = new Socket(address, port);
			in = new DataInputStream(s.getInputStream());
			out = new DataOutputStream(s.getOutputStream());
			out.writeUTF(model.getNickname());
			String data = in.readUTF();
			System.out.println("From "+ address+": " + data);
			if(data.equals("Accepted")){ //TODO: gamestate check?
				model.putInviteState(player.getName(), "Accepted");
			}else{
				model.putInviteState(player.getName(), "Rejected");
			}
		} catch (UnknownHostException e) { //Host does not exist (invalid IP/no internet connection)
			model.putInviteState(player.getName(), "Unreachable");
		} catch (ConnectException e){ //Receiver has no open socket
			model.putInviteState(player.getName(), "Rejected");
		} catch (SocketException e) {//Socked closed from the outside, don't update the view.
			//TODO: wat moet hier
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			DistribullyController.InviteThreadList.remove(this);
			if (s != null){
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
