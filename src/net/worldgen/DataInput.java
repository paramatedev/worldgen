package net.worldgen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.worldgen.object.planet.PlanetData;
import net.worldgen.sql.Database;
import net.worldgen.util.vector.Vector4f;

public class DataInput implements WindowListener, ChangeListener, ActionListener {

	private final int sliderCount = 7;
	private final int colorCount = 5;

	private Database database;

	private JFrame frame;
	private JPanel sliderPanel;
	private JLabel[] label;
	private JSlider[] slider;
	private JPanel colorPanel;
	private JButton[] color;
	private JPanel dataPanel;
	private JButton save;
	private JButton load;
	private JButton delete;
	private JComboBox<String> box;

	private float amplitude;
	private float offset;
	private int octaves;
	private float freq;
	private float normalDetail;

	private float waterAmplitude;
	private float waterFreq;

	private Vector4f color1;
	private Vector4f color2;
	private Vector4f color3;
	private Vector4f color4;
	private Vector4f colorWater;

	public DataInput(Database database) {
		this.database = database;

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(this);
		frame.setLayout(new BorderLayout());

		sliderPanel = new JPanel();
		sliderPanel.setLayout(new GridLayout(sliderCount * 2, 1, 0, 10));
		sliderPanel.setBackground(Color.WHITE);
		frame.add(sliderPanel, BorderLayout.NORTH);

		label = new JLabel[sliderCount];
		slider = new JSlider[sliderCount];

		// amplitude
		slider[0] = new JSlider(JSlider.HORIZONTAL, 0, 1000, 300);
		slider[0].addChangeListener(this);
		amplitude = slider[0].getValue() * 0.01f;
		label[0] = new JLabel("Amplitude: " + amplitude);

		// offset
		slider[1] = new JSlider(JSlider.HORIZONTAL, 0, 50, 0);
		slider[1].addChangeListener(this);
		offset = slider[1].getValue() * 2;
		label[1] = new JLabel("Offset: " + (int) offset);

		// octaves
		slider[2] = new JSlider(JSlider.HORIZONTAL, 0, 16, 12);
		slider[2].addChangeListener(this);
		octaves = slider[2].getValue();
		label[2] = new JLabel("Octaves: " + octaves);

		// freq
		slider[3] = new JSlider(JSlider.HORIZONTAL, 1, 1000, 350);
		slider[3].addChangeListener(this);
		freq = slider[3].getValue() * 0.01f;
		label[3] = new JLabel("Frequency: " + freq);

		// normalDetail
		slider[4] = new JSlider(JSlider.HORIZONTAL, 1, 500, 40);
		slider[4].addChangeListener(this);
		normalDetail = slider[4].getValue() * 0.0001f;
		label[4] = new JLabel("Normal Detail: " + normalDetail);

		// waterAmplitude
		slider[5] = new JSlider(JSlider.HORIZONTAL, 1, 1000, 25);
		slider[5].addChangeListener(this);
		waterAmplitude = slider[5].getValue() * 0.001f;
		label[5] = new JLabel("Water Amplitude: " + waterAmplitude);

		// waterFreq
		slider[6] = new JSlider(JSlider.HORIZONTAL, 1, 10000, 1500);
		slider[6].addChangeListener(this);
		waterFreq = slider[6].getValue() * 0.1f;
		label[6] = new JLabel("Water Frequency: " + waterFreq);

		for (int i = 0; i < sliderCount; i++) {
			sliderPanel.add(label[i]);
			sliderPanel.add(slider[i]);
		}

		colorPanel = new JPanel();
		colorPanel.setLayout(new GridLayout(colorCount, 1, 0, 0));
		colorPanel.setBackground(Color.WHITE);
		frame.add(colorPanel, BorderLayout.CENTER);

		color = new JButton[colorCount];

		// layer 1
		color[0] = new JButton("Layer 1");
		color[0].setBackground(new Color(225, 184, 133));
		color[0].addActionListener(this);
		Color c = color[0].getBackground();
		color1 = new Vector4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 1);

		// layer 2
		color[1] = new JButton("Layer 2");
		color[1].setBackground(new Color(151, 210, 32));
		color[1].addActionListener(this);
		c = color[1].getBackground();
		color2 = new Vector4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 1);

		// layer 3
		color[2] = new JButton("Layer 3");
		color[2].setBackground(new Color(155, 156, 158));
		color[2].addActionListener(this);
		c = color[2].getBackground();
		color3 = new Vector4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 1);

		// layer 4
		color[3] = new JButton("Layer 4");
		color[3].setBackground(new Color(219, 220, 225));
		color[3].addActionListener(this);
		c = color[3].getBackground();
		color4 = new Vector4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 1);

		// water color
		color[4] = new JButton("Water");
		color[4].setBackground(new Color(12, 148, 180));
		color[4].addActionListener(this);
		c = color[4].getBackground();
		colorWater = new Vector4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 0.5f);

		for (int i = 0; i < colorCount; i++) {
			colorPanel.add(color[i]);
		}

		dataPanel = new JPanel();
		dataPanel.setLayout(new GridLayout(2, 2, 0, 0));
		dataPanel.setBackground(Color.WHITE);
		frame.add(dataPanel, BorderLayout.SOUTH);

		save = new JButton("Save");
		save.addActionListener(this);
		dataPanel.add(save);

		load = new JButton("Load");
		load.addActionListener(this);
		dataPanel.add(load);

		box = new JComboBox<String>();
		updateBox();
		dataPanel.add(box);

		delete = new JButton("Delete");
		delete.addActionListener(this);
		dataPanel.add(delete);

		frame.pack();
		frame.setVisible(true);
	}

	public void dispose() {
		frame.dispose();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource().equals(slider[0])) {
			amplitude = slider[0].getValue() * 0.01f;
			label[0].setText("Amplitude: " + amplitude);
		}
		if (e.getSource().equals(slider[1])) {
			offset = slider[1].getValue() * 2;
			label[1].setText("Offset: " + (int) offset);
		}
		if (e.getSource().equals(slider[2])) {
			octaves = slider[2].getValue();
			label[2].setText("Octaves: " + octaves);
		}
		if (e.getSource().equals(slider[3])) {
			freq = slider[3].getValue() * 0.01f;
			label[3].setText("Frequency: " + freq);
		}
		if (e.getSource().equals(slider[4])) {
			normalDetail = slider[4].getValue() * 0.0001f;
			label[4].setText("Normal Detail: " + normalDetail);
		}
		if (e.getSource().equals(slider[5])) {
			waterAmplitude = slider[5].getValue() * 0.001f;
			label[5].setText("Water Amplitude: " + waterAmplitude);
		}
		if (e.getSource().equals(slider[6])) {
			waterFreq = slider[6].getValue() * 0.1f;
			label[6].setText("Water Frequency: " + waterFreq);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(color[0])) {
			color[0].setBackground(JColorChooser.showDialog(null, "Choose Color", color[0].getBackground()));
			Color c = color[0].getBackground();
			color1 = new Vector4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 1);
		}
		if (e.getSource().equals(color[1])) {
			color[1].setBackground(JColorChooser.showDialog(null, "Choose Color", color[1].getBackground()));
			Color c = color[1].getBackground();
			color2 = new Vector4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 1);
		}
		if (e.getSource().equals(color[2])) {
			color[2].setBackground(JColorChooser.showDialog(null, "Choose Color", color[2].getBackground()));
			Color c = color[2].getBackground();
			color3 = new Vector4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 1);
		}
		if (e.getSource().equals(color[3])) {
			color[3].setBackground(JColorChooser.showDialog(null, "Choose Color", color[3].getBackground()));
			Color c = color[3].getBackground();
			color4 = new Vector4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 1);
		}
		if (e.getSource().equals(color[4])) {
			color[4].setBackground(JColorChooser.showDialog(null, "Choose Color", color[4].getBackground()));
			Color c = color[4].getBackground();
			colorWater = new Vector4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 0.5f);
		}

		if (e.getSource().equals(save)) {
			String name = JOptionPane.showInputDialog("Name your Planet");
			while (name.equals(""))
				name = JOptionPane.showInputDialog("Try again!");
			boolean works = false;
			while (!works) {
				try {
					works = database.addPlanet(name, applyData(new PlanetData()));
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				if (!works)
					name = JOptionPane.showInputDialog("Name already taken!");
			}
			updateBox();
		}

		if (e.getSource().equals(load)) {
			String name = (String) box.getSelectedItem();
			if (name == null)
				return;
			try {
				setData(database.getPlanet(name));
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		if (e.getSource().equals(delete)) {
			String name = (String) box.getSelectedItem();
			if (name == null)
				return;
			try {
				database.deletePlanet(name);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			updateBox();
		}
	}

	private void updateBox() {
		try {
			String[] names = database.getAllPlanetNames();
			box.removeAllItems();
			for (String name : names)
				box.addItem(name);
			if (names.length != 0)
				box.setSelectedIndex(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public PlanetData applyData(PlanetData data) {
		data.setAmplitude(amplitude);
		data.setOffset(offset);
		data.setOctaves(octaves);
		data.setFreq(freq);
		data.setNormalDetail(normalDetail);
		data.setWaterAmplitude(waterAmplitude);
		data.setWaterFreq(waterFreq);
		data.setColor1(color1);
		data.setColor2(color2);
		data.setColor3(color3);
		data.setColor4(color4);
		data.setColorWater(colorWater);
		return data;
	}

	private void setData(PlanetData data) {
		amplitude = data.getAmplitude();
		offset = data.getOffset();
		octaves = data.getOctaves();
		freq = data.getFreq();
		normalDetail = data.getNormalDetail();
		waterAmplitude = data.getWaterAmplitude();
		waterFreq = data.getWaterFreq();
		color1 = data.getColor1();
		color2 = data.getColor2();
		color3 = data.getColor3();
		color4 = data.getColor4();
		colorWater = data.getColorWater();
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		Game.stop();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}
