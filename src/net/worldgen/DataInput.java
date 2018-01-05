package net.worldgen;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DataInput implements WindowListener, ChangeListener {

	private JFrame frame;
	private JPanel panel;
	private JSlider slider1;
	private JSlider slider2;
	private JSlider slider3;
	private JSlider slider4;

	private float amplitude;
	private float offset;
	private int octaves;
	private float freq;

	public DataInput() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(this);

		panel = new JPanel();
		panel.setSize(200, 200);
		panel.setLayout(new GridLayout(2, 1, 0, 10));
		panel.setBackground(Color.WHITE);
		frame.add(panel);

		// amplitude
		slider1 = new JSlider(JSlider.HORIZONTAL, 0, 1000, 500);
		slider1.addChangeListener(this);
		panel.add(slider1);
		amplitude = slider1.getValue() * 0.01f;

		// offset
		slider2 = new JSlider(JSlider.HORIZONTAL, 0, 50, 0);
		slider2.addChangeListener(this);
		panel.add(slider2);
		offset = slider2.getValue() * 2;

		// octaves
		slider3 = new JSlider(JSlider.HORIZONTAL, 0, 16, 16);
		slider3.addChangeListener(this);
		panel.add(slider3);
		octaves = slider3.getValue();

		// freq
		slider4 = new JSlider(JSlider.HORIZONTAL, 1, 100, 26);
		slider4.addChangeListener(this);
		panel.add(slider4);
		freq = slider4.getValue() * 0.1f;

		frame.pack();
		frame.setVisible(true);
	}

	public void dispose() {
		frame.dispose();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource().equals(slider1))
			amplitude = slider1.getValue() * 0.01f;
		if (e.getSource().equals(slider2))
			offset = slider2.getValue() * 2;
		if (e.getSource().equals(slider3))
			octaves = slider3.getValue();
		if (e.getSource().equals(slider4))
			freq = slider4.getValue() * 0.1f;
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
