import java.io.Serializable;

public class Rgb implements Serializable {
	private int red = 0;
	private int green = 0;
	private int blue = 0;
	
	public void setRed(int r) {
		red = r;
	}
	
	public void setGreen(int g) {
		green = g;
	}
	
	public void setBlue(int b) {
		blue = b;
	}

	public int getRed() {
		return red;
	}
	
	public int getGreen() {
		return green;
	}
	
	public int getBlue() {
		return blue;
	}
}