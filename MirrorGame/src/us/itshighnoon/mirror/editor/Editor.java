package us.itshighnoon.mirror.editor;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import org.joml.Vector2f;

import us.itshighnoon.mirror.world.Level;
import us.itshighnoon.mirror.world.Wall;

public class Editor {
	private JFrame frame;
	private Viewport vp;
	private JTextPane levelData;
	private JButton loadLevel;
	private JButton saveLevel;
	
	private EditorLoader loader;
	private Level currentLevel;
	
	public Editor() {
		frame = new JFrame();
		frame.setTitle("MirrorGame Editor");
		frame.setLayout(new GridBagLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		ButtonHandler buttonListener = new ButtonHandler();
		GridBagConstraints gbc = new GridBagConstraints();
		
		loader = new EditorLoader();
		vp = new Viewport(loader);
		vp.addMouseListener(new MouseHandler());
		
		levelData = new JTextPane();
		levelData.setEditable(false);
		levelData.setText("No level loaded");
		levelData.setPreferredSize(new Dimension(vp.getPreferredSize().width, levelData.getPreferredSize().height));
		
		loadLevel = new JButton();
		loadLevel.setText("Load Level");
		loadLevel.addActionListener(buttonListener);
		loadLevel.setPreferredSize(new Dimension(vp.getPreferredSize().width / 2, loadLevel.getPreferredSize().height));
		
		saveLevel = new JButton();
		saveLevel.setText("Save Level");
		saveLevel.addActionListener(buttonListener);
		saveLevel.setPreferredSize(new Dimension(vp.getPreferredSize().width / 2, saveLevel.getPreferredSize().height));
		
		gbc.gridwidth = 2;
		frame.add(levelData, gbc);
		gbc.gridy = 1;
		frame.add(vp, gbc);
		gbc.gridwidth = 1;
		gbc.gridy = 2;
		frame.add(loadLevel, gbc);
		gbc.gridx = 1;
		frame.add(saveLevel, gbc);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	private class MouseHandler implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			e.getComponent().requestFocus();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			currentLevel.addMirror(new Wall(new Vector2f(0.0f, 0.0f), vp.getLocation(e.getX(), e.getY())));
			vp.repaint();
			setDataText();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}
	}
	
	private void setDataText() {
		// TODO text not updating
		if (currentLevel != null) {
			levelData.setText("No level loaded");
			return;
		}
		levelData.setText("Walls: " + currentLevel.getWalls().length + " | Mirrors: " + currentLevel.getMirrors().length);
	}
	
	private class ButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
			if (evt.getSource() == loadLevel) {
				try {
					JFileChooser fileChooser = new JFileChooser();
					int res = fileChooser.showOpenDialog(frame);
					if (res == JFileChooser.APPROVE_OPTION) {
						currentLevel = LevelIO.loadLevel(fileChooser.getSelectedFile(), loader);
						vp.setLevel(currentLevel);
					} else {
						throw new IllegalArgumentException("No file selected.");
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame, e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
				}
			} else if (evt.getSource() == saveLevel) {
				try {
					JFileChooser fileChooser = new JFileChooser();
					int res = fileChooser.showSaveDialog(frame);
					if (res == JFileChooser.APPROVE_OPTION) {
						LevelIO.saveLevel(fileChooser.getSelectedFile());
						JOptionPane.showMessageDialog(frame, "Level saved.");
					} else {
						throw new IllegalArgumentException("No file selected.");
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame, e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}
}
