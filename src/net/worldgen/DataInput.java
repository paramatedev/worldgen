package net.worldgen;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.worldgen.object.planet.PlanetData;

public class DataInput implements WindowListener, ChangeListener {

	private final int count = 5;

	private JFrame frame;
	private JPanel panel;
	private JLabel[] label;
	private JSlider[] slider;

	private float amplitude;
	private float offset;
	private int octaves;
	private float freq;
	private float normalDetail;

	public DataInput() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(this);

		panel = new JPanel();
		panel.setSize(200, 200);
		panel.setLayout(new GridLayout(count * 2, 1, 0, 10));
		panel.setBackground(Color.WHITE);
		frame.add(panel);

		label = new JLabel[count];
		slider = new JSlider[count];

		// amplitude
		slider[0] = new JSlider(JSlider.HORIZONTAL, 0, 1000, 500);
		slider[0].addChangeListener(this);
		amplitude = slider[0].getValue() * 0.01f;
		label[0] = new JLabel("Amplitude: " + amplitude);

		// offset
		slider[1] = new JSlider(JSlider.HORIZONTAL, 0, 50, 0);
		slider[1].addChangeListener(this);
		offset = slider[1].getValue() * 2;
		label[1] = new JLabel("Offset: " + (int) offset);

		// octaves
		slider[2] = new JSlider(JSlider.HORIZONTAL, 0, 16, 16);
		slider[2].addChangeListener(this);
		octaves = slider[2].getValue();
		label[2] = new JLabel("Octaves: " + octaves);

		// freq
		slider[3] = new JSlider(JSlider.HORIZONTAL, 1, 1000, 260);
		slider[3].addChangeListener(this);
		freq = slider[3].getValue() * 0.01f;
		label[3] = new JLabel("Frequency: " + freq);

		// normalDetail
		slider[4] = new JSlider(JSlider.HORIZONTAL, 1, 500, 200);
		slider[4].addChangeListener(this);
		normalDetail = slider[4].getValue() * 0.0001f;
		label[4] = new JLabel("Normal Detail: " + normalDetail);

		for (int i = 0; i < count; i++) {
			panel.add(label[i]);
			panel.add(slider[i]);
		}
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
	}

	public void applyData(PlanetData data) {
		data.setAmplitude(amplitude);
		data.setOffset(offset);
		data.setOctaves(octaves);
		data.setFreq(freq);
		data.setNormalDetail(normalDetail);
	}

	public float getAmplitude() {
		return amplitude;
	}

	public float getOffset() {
		return offset;
	}

	public int getOctaves() {
		return octaves;
	}

	public float getFreq() {
		return freq;
	}

	public float getNormalDetail() {
		return normalDetail;
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
