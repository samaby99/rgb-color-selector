import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
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
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;

public class RgbColorSelector implements Serializable {

	Rgb rgb; // color that holds r, g and b values
	private JTextField fieldR;
	private JTextField fieldG;
	private JTextField fieldB;
	private JPanel colorPanel;
	private JSlider sliderR;
	private JSlider sliderG;
	private JSlider sliderB;
	private JLabel errorOutput;
	private JFrame frame;

	public static void main (String[] args) {
		RgbColorSelector gui = new RgbColorSelector();
		gui.go();
	}

	private void go() {
		rgb = new Rgb();
		frame = new JFrame("RGB Color Selector");
		
		// 0. contentPanel - main panel, containes other panels
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		// 1. menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu menuSave = new JMenu("Save");
		JMenuItem saveObjectMenuItem = new JMenuItem("Save as object (.ser)");
		saveObjectMenuItem.addActionListener(new SaveObjectListener());
		JMenuItem saveTextMenuItem = new JMenuItem("Save as text (.csv)");
		saveTextMenuItem.addActionListener(new SaveTextListener());
		menuSave.add(saveObjectMenuItem);
		menuSave.add(saveTextMenuItem);
		
		JMenu menuOpen = new JMenu("Open");
		JMenuItem openObjectMenuItem = new JMenuItem("Open .ser file");
		openObjectMenuItem.addActionListener(new OpenObjectListener());
		JMenuItem openTextMenuItem = new JMenuItem("Open .csv file");
		openTextMenuItem.addActionListener(new OpenTextListener());
		menuOpen.add(openObjectMenuItem);
		menuOpen.add(openTextMenuItem);
		
		menuBar.add(menuSave);
		menuBar.add(menuOpen);
		
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
		
		contentPanel.add(colorPanel);
		contentPanel.add(panelR);
		contentPanel.add(panelG);
		contentPanel.add(panelB);
		contentPanel.add(errorOutput);
		
		frame.getContentPane().add(contentPanel);
		frame.setJMenuBar(menuBar);
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
		public void stateChanged(ChangeEvent ev) {
			JSlider source = (JSlider)ev.getSource();
			int value = source.getValue();
			if(source == sliderR) {
				rgb.setRed(value);
				fieldR.setText(Integer.toString(value));
			} else if(ev.getSource() == sliderG) {
				rgb.setGreen(value);
				fieldG.setText(Integer.toString(value));
			} else if(ev.getSource() == sliderB) {
				rgb.setBlue(value);
				fieldB.setText(Integer.toString(value));
			}
			colorPanel.setBackground(
					new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue()));
			resetAfterError();
		}
	}
	
	class FieldListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			JTextField source = (JTextField)ev.getSource();
			String textVal = source.getText();
			// check whether textVal is an integer between 0 and 255
			if(textVal.matches("^\\d{1,3}$") && Integer.parseInt(textVal) >= 0 
					&& Integer.parseInt(textVal) < 255) {
				int intVal = Integer.parseInt(textVal);
				if(source == fieldR) {
					rgb.setRed(intVal);
					sliderR.setValue(intVal);
				} else if(source == fieldG) {
					rgb.setGreen(intVal);
					sliderG.setValue(intVal);
				} else if(source == fieldB) {
					rgb.setBlue(intVal);
					sliderB.setValue(intVal);
				}
				colorPanel.setBackground(
						new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue()));
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
	
	public class SaveObjectListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
					oos.writeObject(rgb);
					oos.close();
				} catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	class OpenObjectListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
					rgb = (Rgb) ois.readObject();
					setNewValues();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	class SaveTextListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(file));
					writer.write("R,G,B\n");
					writer.write(rgb.getRed() + "," + rgb.getGreen() + "," + rgb.getBlue());
					writer.close();
				} catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	class OpenTextListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String line = null;
					int lineCounter = 0;
					while((line = reader.readLine()) != null) { 
						if(lineCounter==1) { //skip first line "R,G,B"
							String[] tokens = line.split(",");
							rgb.setRed(Integer.parseInt(tokens[0]));
							rgb.setGreen(Integer.parseInt(tokens[1]));
							rgb.setBlue(Integer.parseInt(tokens[2]));
							setNewValues();
						}
						lineCounter++;
					}
					reader.close();
				} catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private void setNewValues() {
		fieldR.setText(Integer.toString(rgb.getRed()));
		fieldG.setText(Integer.toString(rgb.getGreen()));
		fieldB.setText(Integer.toString(rgb.getBlue()));
		sliderR.setValue(rgb.getRed());
		sliderG.setValue(rgb.getGreen());
		sliderB.setValue(rgb.getBlue());
		colorPanel.setBackground(new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue()));
	}
}
