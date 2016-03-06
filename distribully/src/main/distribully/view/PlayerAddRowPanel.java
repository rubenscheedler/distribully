package distribully.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PlayerAddRowPanel extends JPanel {

	private static final long serialVersionUID = -7697775796287265986L;

	private JLabel addressLabel;
	private JTextField addressField;
	private JLabel nameLabel;
	private JTextField nameField;
	
	public JTextField getAddressField() {
		return addressField;
	}

	public void setAddressField(JTextField addressField) {
		this.addressField = addressField;
	}

	public JTextField getNameField() {
		return nameField;
	}

	public void setNameField(JTextField nameField) {
		this.nameField = nameField;
	}

	
	public PlayerAddRowPanel() {
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.setMinimumSize(new Dimension(800, 40));
		this.setPreferredSize(new Dimension(800, 40));
		this.setMaximumSize(new Dimension(800, 40));
		this.setBackground(new Color(80,80,80));
		
		//this.setForeground(new Color(255,255,255));
		addressLabel = new JLabel("address:");
		addressLabel.setPreferredSize(new Dimension(150,30));
		addressField = new JTextField("");
		addressField.setPreferredSize(new Dimension(200,30));//setSize();
		nameLabel = new JLabel("name:");
		nameLabel.setPreferredSize(new Dimension(150,30));
		nameField = new JTextField("");
		nameField.setPreferredSize(new Dimension(200,30));
		
		this.add(addressLabel);
		this.add(addressField);
		this.add(nameLabel);
		this.add(nameField);
	}
}