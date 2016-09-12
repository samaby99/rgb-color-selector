import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

public class RgbColorSelector implements Serializable {
// 	private int rgb.red = 0;
// 	private int rgb.green = 0;
// 	private int rgb.blue = 0;
	Rgb rgb; // color that holds r, g and b values
	private JTextField fieldR;
	private JTextField fieldG;
	private JTextField fieldB;
	private JPanel colorPanel;
	private JSlider sliderR;
	private JSlider sliderG;
	private JSlider sliderB;
	private JLabel errorOutput;

	
	public static void main (String[] args) {
		RgbColorSelector gui = new RgbColorSelector();
		gui.go();
	}

	private void go() {
		rgb = new Rgb();
		JFrame frame = new JFrame("RGB Color Selector");
		
		// 0. contentPanel - ueberpanel, containes other panels
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		
		// 1. buttonPanel - panel with buttons
		JPanel buttonPanel = new JPanel();
		JButton saveObject = new JButton("Save object");
		JButton loadObject = new JButton("Load object");
		JButton saveText = new JButton("Save text");
		JButton loadText = new JButton("Load text");
		saveObject.addActionListener(new SaveObjectListener());
		loadObject.addActionListener(new LoadObjectListener());
// 		saveText.addActionListener(new SaveTextListener());
// 		loadText.addActionListener(new LoadTextListener());
		buttonPanel.add(saveObject);
		buttonPanel.add(saveText);
		buttonPanel.add(loadObject);
		buttonPanel.add(loadText);
		
		// 2. colorPanel - panel showing the color
		colorPanel = new JPanel();
		colorPanel.setBackground(new Color(0, 0, 0));
		colorPanel.setPreferredSize(new Dimension(100, 50));
		
		// 3. three panels (R, G, B) with sliders and corresponding text fields
		JPanel panelR = new JPanel();
		JPanel panelG = new JPanel();
		JPanel panelB = new JPanel();
		
		sliderR = new JSlider(0, 255, 0);
		sliderR.addChangeListener(new SliderListener());
		sliderG = new JSlider(0, 255, 0);
		sliderG.addChangeListener(new SliderListener());
		sliderB = new JSlider(0, 255, 0);
		sliderB.addChangeListener(new SliderListener());
		
		fieldR = new JTextField("0", 3);
		fieldR.addActionListener(new FieldListener());
		fieldG = new JTextField("0", 3);
		fieldG.addActionListener(new FieldListener());
		fieldB = new JTextField("0", 3);
		fieldB.addActionListener(new FieldListener());
		
		JLabel labelR = new JLabel("R");
		labelR.setForeground(Color.RED);
		JLabel labelG = new JLabel("G");
		labelG.setForeground(Color.GREEN);
		JLabel labelB = new JLabel("B");
		labelB.setForeground(Color.BLUE);
		
		panelR.add(labelR);
		panelR.add(sliderR);
		panelR.add(fieldR);
		
		panelG.add(labelG);
		panelG.add(sliderG);
		panelG.add(fieldG);
		
		panelB.add(labelB);
		panelB.add(sliderB);
		panelB.add(fieldB);
		
		// 4. panel to display error messages
		errorOutput = new JLabel(" ");
		errorOutput.setFont(new Font("Serif", Font.PLAIN, 11));
		errorOutput.setForeground(Color.RED);
		
		// add panels 1, 2, 3, 4 to contentPanel 
		contentPanel.add(buttonPanel);
		contentPanel.add(colorPanel);
		contentPanel.add(panelR);
		contentPanel.add(panelG);
		contentPanel.add(panelB);
		contentPanel.add(errorOutput);
		
		frame.getContentPane().add(contentPanel);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
	}
	
	private void resetAfterError() {
		errorOutput.setText(" ");
		fieldR.setBackground(Color.WHITE);
		fieldG.setBackground(Color.WHITE);
		fieldB.setBackground(Color.WHITE);
	}
	
	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
			if(source == sliderR) {
				rgb.red = source.getValue();
				fieldR.setText(Integer.toString(rgb.red));
			} else if(e.getSource() == sliderG) {
				rgb.green = source.getValue();
				fieldG.setText(Integer.toString(rgb.green));
			} else if(e.getSource() == sliderB) {
				rgb.blue = source.getValue();
				fieldB.setText(Integer.toString(rgb.blue));
			}
			colorPanel.setBackground(new Color(rgb.red, rgb.green, rgb.blue));
			resetAfterError();
		}
	}
	
	class FieldListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JTextField source = (JTextField)e.getSource();
			String input = source.getText();
			// check whether input is an integer between 0 and 255
			if(input.matches("^\\d{1,3}$") && Integer.parseInt(input) >= 0 
					&& Integer.parseInt(input) < 255) {
				if(source == fieldR) {
					rgb.red = Integer.parseInt(input);
					sliderR.setValue(rgb.red);
				} else if(source == fieldG) {
					rgb.green = Integer.parseInt(input);
					sliderG.setValue(rgb.green);
				} else if(source == fieldB) {
					rgb.blue = Integer.parseInt(input);
					sliderB.setValue(rgb.blue);
				}
				colorPanel.setBackground(new Color(rgb.red, rgb.green, rgb.blue));
				resetAfterError();
			} else { // if entered value not an integer in 0...255 range - reset it to 0
				source.setText("0");
				if(source == fieldR) {
					sliderR.setValue(0);
				} else if(source == fieldG) {
					sliderG.setValue(0);
				} else if(source == fieldB) {
					sliderB.setValue(0);
				}
				errorOutput.setText("Input must be between 0 and 255!");
				source.setBackground(Color.RED);
			}
		}
	}
	
	class SaveObjectListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("RGB.ser"));
				os.writeObject(rgb);
				os.close();
				System.out.println("Saving was succesfull");
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	class LoadObjectListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				ObjectInputStream is = new ObjectInputStream(new FileInputStream("RGB.ser"));
				rgb = (Rgb) is.readObject();
				System.out.println("Restoring was succesfull");
				System.out.println("R: " + rgb.red);
				System.out.println("G: " + rgb.green);
				System.out.println("B: " + rgb.blue);
				
				fieldR.setText(Integer.toString(rgb.red));
				fieldG.setText(Integer.toString(rgb.green));
				fieldB.setText(Integer.toString(rgb.blue));
				
				sliderR.setValue(rgb.red);
				sliderG.setValue(rgb.green);
				sliderB.setValue(rgb.blue);
				
				colorPanel.setBackground(new Color(rgb.red, rgb.green, rgb.blue));
// 				FieldListener fl = new FieldListener();
// 				fl.actionPerformed(new ActionEvent (gui.fieldG, ActionEvent.ACTION_PERFORMED, null));
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
// 	class SaveTextListener implements ActionListener {
// 		public void actionPerformed(ActionEvent e) {
// 
// 		}
// 	}
// 	
// 	class LoadTextListener implements ActionListener {
// 		public void actionPerformed(ActionEvent e) {
// 
// 		}
// 	}
}
