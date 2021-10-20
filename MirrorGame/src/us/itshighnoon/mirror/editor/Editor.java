package us.itshighnoon.mirror.editor;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import us.itshighnoon.mirror.Main;
import us.itshighnoon.mirror.editor.state.EditorState;
import us.itshighnoon.mirror.editor.state.EnemyState;
import us.itshighnoon.mirror.editor.state.FloorState;
import us.itshighnoon.mirror.editor.state.MirrorState;
import us.itshighnoon.mirror.editor.state.SpawnState;
import us.itshighnoon.mirror.editor.state.WallState;
import us.itshighnoon.mirror.lwjgl.object.Texture;
import us.itshighnoon.mirror.lwjgl.object.TexturedModel;
import us.itshighnoon.mirror.world.Level;

public class Editor {
	private JFrame frame;
	private Viewport vp;
	private JLabel levelData;
	private JButton loadLevel;
	private JButton saveLevel;
	private JButton newLevel;
	private JComboBox<String> tools;
	private JLabel icon;
	private JTextField parameter;
	
	private EditorLoader loader;
	private Level currentLevel;
	private EditorState state;
	
	private static final WallState WALL_STATE = new WallState();
	private static final MirrorState MIRROR_STATE = new MirrorState();
	private static final FloorState FLOOR_STATE = new FloorState();
	private static final EnemyState ENEMY_STATE = new EnemyState();
	private static final SpawnState SPAWN_STATE = new SpawnState();
	
	public Editor() {
		frame = new JFrame();
		frame.setTitle("MirrorGame Editor");
		frame.setLayout(new GridBagLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocation(100, 100);
		ButtonHandler buttonListener = new ButtonHandler();
		GridBagConstraints gbc = new GridBagConstraints();
		
		loader = new EditorLoader();
		vp = new Viewport(loader);
		vp.addMouseListener(new MouseHandler());
		vp.addMouseMotionListener(new MouseDragHandler());
		
		Main.triangle = new TexturedModel(loader.loadQuad(), loader.loadTexture("res/texture/triangle.png"));
		Main.square = new TexturedModel(loader.loadQuad(), loader.loadTexture("res/texture/square.png"));
		Main.pentagon = new TexturedModel(loader.loadQuad(), loader.loadTexture("res/texture/pentagon.png"));
		Main.hexagon = new TexturedModel(loader.loadQuad(), loader.loadTexture("res/texture/hexagon.png"));
		Main.octagon = new TexturedModel(loader.loadQuad(), loader.loadTexture("res/texture/octagon.png"));
		
		levelData = new JLabel();
		levelData.setText("No level loaded.");
		levelData.setPreferredSize(new Dimension(vp.getPreferredSize().width, levelData.getPreferredSize().height));
		
		loadLevel = new JButton();
		loadLevel.setText("Load Level");
		loadLevel.addActionListener(buttonListener);
		loadLevel.setPreferredSize(new Dimension(vp.getPreferredSize().width / 3, loadLevel.getPreferredSize().height));
		
		saveLevel = new JButton();
		saveLevel.setText("Save Level");
		saveLevel.addActionListener(buttonListener);
		saveLevel.setPreferredSize(new Dimension(vp.getPreferredSize().width / 3, saveLevel.getPreferredSize().height));
		
		newLevel = new JButton();
		newLevel.setText("New Level");
		newLevel.addActionListener(buttonListener);
		newLevel.setPreferredSize(new Dimension(vp.getPreferredSize().width - loadLevel.getPreferredSize().width - saveLevel.getPreferredSize().width, newLevel.getPreferredSize().height));
		
		tools = new JComboBox<String>();
		tools.addItem("Spawn");
		tools.addItem("Floor");
		tools.addItem("Wall");
		tools.addItem("Mirror");
		tools.addItem("Enemy");
		tools.addActionListener(new DropdownHandler());
		tools.setPreferredSize(new Dimension(loadLevel.getPreferredSize().width + loadLevel.getPreferredSize().height, tools.getPreferredSize().height));
		
		icon = new JLabel();
		icon.setPreferredSize(new Dimension(loadLevel.getPreferredSize().height, loadLevel.getPreferredSize().height));
		
		parameter = new JTextField();
		parameter.addActionListener(new TextFieldHandler());
		parameter.setPreferredSize(loadLevel.getPreferredSize());
		
		gbc.gridwidth = 3;
		frame.add(levelData, gbc);
		gbc.gridy = 1;
		frame.add(vp, gbc);
		gbc.gridwidth = 1;
		gbc.gridy = 2;
		frame.add(loadLevel, gbc);
		gbc.gridx = 1;
		frame.add(saveLevel, gbc);
		gbc.gridx = 2;
		frame.add(newLevel, gbc);
		gbc.gridwidth = 2;
		gbc.gridx = 3;
		gbc.gridy = 0;
		frame.add(tools, gbc);
		gbc.gridwidth = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.NORTH;
		frame.add(icon, gbc);
		gbc.gridx = 4;
		frame.add(parameter, gbc);
		
		frame.pack();
		frame.setVisible(true);
		
		state = SPAWN_STATE;
		currentLevel = new Level();
		vp.setLevel(currentLevel);
		setDataText();
	}
	
	private void setDataText() {
		if (currentLevel == null) {
			levelData.setText("No level loaded.");
			return;
		}
		levelData.setText("Walls: " + currentLevel.getWalls().size() + " | Mirrors: " + currentLevel.getMirrors().size());
	}
	
	private class MouseHandler implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				state.pressLeft(currentLevel, vp.getLocation(e.getX(), e.getY()));
				vp.repaint();
				setDataText();
				break;
			case MouseEvent.BUTTON3:
				state.pressRight(currentLevel, vp.getLocation(e.getX(), e.getY()));
				vp.repaint();
				setDataText();
				break;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				state.releaseLeft(currentLevel, vp.getLocation(e.getX(), e.getY()));
				vp.repaint();
				setDataText();
				break;
			case MouseEvent.BUTTON3:
				state.releaseRight(currentLevel, vp.getLocation(e.getX(), e.getY()));
				vp.repaint();
				setDataText();
				break;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			e.getComponent().requestFocus();
		}

		@Override
		public void mouseExited(MouseEvent e) {}
	}
	
	private class MouseDragHandler implements MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {
			state.drag(currentLevel, vp.getLocation(e.getX(), e.getY()));
			vp.repaint();
			setDataText();
		}

		@Override
		public void mouseMoved(MouseEvent e) {}
	}
	
	private class ButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
			if (evt.getSource() == loadLevel) {
				try {
					JFileChooser fileChooser = new JFileChooser();
					int res = fileChooser.showOpenDialog(frame);
					if (res == JFileChooser.APPROVE_OPTION) {
						currentLevel = new Level(fileChooser.getSelectedFile().getAbsolutePath(), loader);
						vp.setLevel(currentLevel);
						setDataText();
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
						currentLevel.save(fileChooser.getSelectedFile().getAbsolutePath(), loader.flip());
						JOptionPane.showMessageDialog(frame, "Level saved.");
					} else {
						throw new IllegalArgumentException("No file selected.");
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame, e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
				}
			} else if (evt.getSource() == newLevel) {
				currentLevel = new Level();
				vp.setLevel(currentLevel);
				setDataText();
			}
		}
	}
	
	private class DropdownHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch ((String)tools.getSelectedItem()) {
			case "Spawn":
				state = SPAWN_STATE;
				break;
			case "Floor":
				state = FLOOR_STATE;
				break;
			case "Wall":
				state = WALL_STATE;
				break;
			case "Mirror":
				state = MIRROR_STATE;
				break;
			case "Enemy":
				state = ENEMY_STATE;
			}
		}
	}
	
	private class TextFieldHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
			if (state == FLOOR_STATE) {
				try {
					Texture tex = loader.loadTexture(parameter.getText());
					FLOOR_STATE.setFloorTexture(new TexturedModel(loader.loadQuad(), tex));
					icon.setIcon(new ImageIcon(loader.getImage(tex.getTextureId()).getScaledInstance(icon.getPreferredSize().width, icon.getPreferredSize().height, Image.SCALE_DEFAULT)));
				} catch (Exception e) {
					icon.setIcon(null);
				}
			} else if (state == ENEMY_STATE) {
				Texture tex = null;
				switch (parameter.getText()) {
				case "triangle":
					tex = loader.loadTexture("res/texture/triangle.png");
					break;
				case "square":
					tex = loader.loadTexture("res/texture/square.png");
					break;
				case "pentagon":
					tex = loader.loadTexture("res/texture/pentagon.png");
					break;
				case "hexagon":
					tex = loader.loadTexture("res/texture/hexagon.png");
					break;
				case "octagon":
					tex = loader.loadTexture("res/texture/octagon.png");
					break;
				}
				if (tex != null) {
					icon.setIcon(new ImageIcon(loader.getImage(tex.getTextureId()).getScaledInstance(icon.getPreferredSize().width, icon.getPreferredSize().height, Image.SCALE_DEFAULT)));
				} else {
					icon.setIcon(null);
				}
				ENEMY_STATE.setEnemyType(new TexturedModel(loader.loadQuad(), tex), parameter.getText());
			}
		}
	}
}
