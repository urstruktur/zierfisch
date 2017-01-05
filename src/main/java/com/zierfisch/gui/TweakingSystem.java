package com.zierfisch.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

/***
 * This system enables easy cheesy tweaking of float values
 */
public class TweakingSystem extends EntitySystem {
	
	JPanel panel;
	JFrame frame;
	List<PropertyAccessor> accessors;
	static final int INC = 100; // number of slider increments
	
	public TweakingSystem(){
		accessors = new LinkedList<PropertyAccessor>();
	}
	
	public void addSlider(PropertyAccessor pa, String name){
		accessors.add(pa);
		if(pa.getProperty() < pa.getMin() || pa.getProperty() > pa.getMax()){
			System.out.println("Invalid Range!");
		}
		float lerp = (pa.getProperty() - pa.getMin())/(pa.getMax() - pa.getMin());
		JSlider slider = new JSlider(0, INC, (int)(lerp * INC));
		
		// Add positions label in the slider
		Hashtable<Integer,JLabel> position = new Hashtable<Integer,JLabel>();
		position.put(0, new JLabel(String.format("%.01f", pa.getMin())));
		position.put(INC, new JLabel(String.format("%.01f", pa.getMax())));
		slider.setLabelTable(position);
		
		// Set major or minor ticks for the slider
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(5);
		slider.setPaintTicks(true);
		
		//slider.setPreferredSize(new Dimension(350, 60));
		
		// Set the labels to be painted on the slider
		slider.setPaintLabels(true);
		
        //Create the label.
        JLabel sliderLabelName = new JLabel(name, JLabel.LEFT);
        JLabel sliderLabelVal = new JLabel(String.format("%.03f", pa.getProperty()));
		
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				float value = (pa.getMax() - pa.getMin()) * (slider.getValue()/(float)INC) + pa.getMin();
				pa.setProperty(value);
				sliderLabelVal.setText(String.format("%.03f", pa.getProperty()));
			}
		});
		
        //sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(sliderLabelName);
		panel.add(slider);
		panel.add(sliderLabelVal);
		frame.pack();
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
		frame = new JFrame("Lil' Tweaker");
		frame.setMinimumSize(new Dimension(500,600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set the panel to add buttons
		panel = new JPanel();
		
		panel.setLayout(new GridLayout(0,3));
		
		// Set the window to be visible as the default to be false
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}
