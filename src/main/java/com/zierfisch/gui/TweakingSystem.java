package com.zierfisch.gui;

import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

public class TweakingSystem extends EntitySystem {
	
	public TweakingSystem(){
		
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		// Create and set up a frame window
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("JSlider setting examples");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set the panel to add buttons
		JPanel panel = new JPanel();
		
		// Different settings on the sliders
		JSlider slider = new JSlider();
		// JSlider slider = new JSlider(JSlider.VERTICAL);
		// JSlider slider = new JSlider(-100, 100, 50);
		// JSlider slider = new JSlider(JSlider.VERTICAL, -100, 100, 50);
		
		// Set the slider with the DefaultBoundedRangeModel
		//DefaultBoundedRangeModel model = new DefaultBoundedRangeModel(20, 0, 1, 100);
		//JSlider slider = new JSlider(model);
		
		// Set major or minor ticks for the slider
		slider.setMajorTickSpacing(25);
		slider.setMinorTickSpacing(10);
		slider.setPaintTicks(true);
		
		// Set the labels to be painted on the slider
		slider.setPaintLabels(true);
		
		// Add positions label in the slider
		Hashtable position = new Hashtable();
		position.put(0, new JLabel("0"));
		position.put(25, new JLabel("25"));
		position.put(50, new JLabel("50"));
		position.put(75, new JLabel("75"));
		position.put(100, new JLabel("100"));
		
		// Set the label to be drawn
		slider.setLabelTable(position);
		
		// Add the slider to the panel
		panel.add(slider);
		
		// Set the window to be visible as the default to be false
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}
