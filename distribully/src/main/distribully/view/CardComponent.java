package distribully.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import distribully.model.Card;

public class CardComponent extends DrawableComponent {
	private Card card;
	private int actualWidth;
	private static Logger logger;
	
	public CardComponent(int posX, int posY, int width, int height, Card card, int actualWidth) {
		super(posX,posY,width,height);
		this.card = card;
		this.actualWidth = actualWidth;
		logger = LoggerFactory.getLogger("view.CardComponent");
	}
	
	public void draw(Graphics g, boolean selected) {
		String imageName = "src/main/distribully/cards/emptycard.png";
		if (card != null) {
			imageName = card.getImage();
		}
		
		File image = new File(imageName);
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(image);
		} catch(IOException e) {
			JOptionPane.showMessageDialog(null,
				    "Something went wrong leaving the game.",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			logger.error("Could not load card images.");
		}
		
		if (selected) {
			g.setColor(Color.GREEN); //Show the player the card is selected
			g.fillRoundRect(posX-5, posY-5, width+10, height+10, 10, 10);
			g.setColor(Color.WHITE);
		}
		g.drawImage(img,posX,posY,width,height,null);
	}
	
	/**
	 * returns if a given click position matches a place within this image.
	 * @param x
	 * @param y
	 * @return
	 */
	@Override
	public boolean wasClicked(int x, int y) {
		return x >= posX && x < (posX + actualWidth) && y >= posY && y < (posY + height);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + actualWidth;
		result = prime * result + ((card == null) ? 0 : card.hashCode());
		result = prime * result + height;
		result = prime * result + posX;
		result = prime * result + posY;
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CardComponent other = (CardComponent) obj;
		if (actualWidth != other.actualWidth)
			return false;
		if (card == null) {
			if (other.card != null)
				return false;
		} else if (!card.equals(other.card))
			return false;
		if (height != other.height)
			return false;
		if (posX != other.posX)
			return false;
		if (posY != other.posY)
			return false;
		if (width != other.width)
			return false;
		return true;
	}

	public Card getCard() {
		return this.card;
	}
	
	public String toString() {
		return "[posX:" + posX + ",posY:" + posY + ",width:" + width + ",height:" + height + "]";
	}

	@Override
	public void draw(Graphics g) {
		this.draw(g,false);
	}


}
