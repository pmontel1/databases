package ui.swing.panels;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import models.Person;
import models.Skill;
import models.queries.SkillQueries;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class PersonSkillPanel extends JPanel {
	private Connection connection;
	private Person person;
	private SkillQueries skillQueries;
	private List<Skill> list;
	
	private ButtonController buttonController;
	
	JTextArea taSkills;
	JButton editButton;
	private JScrollPane scrollPane;

	public PersonSkillPanel(Connection connection) {
		this.connection = connection;
		this.skillQueries = new SkillQueries(connection);
		this.list = new ArrayList<Skill>();
		
		this.buttonController = new ButtonController();
		
		initializeGUIComponents();
	}
	
	public void initializeGUIComponents() {
		
		//Setup Data Panel
		editButton = new JButton("Edit");
		editButton.addActionListener(buttonController);
		JPanel buttonPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		buttonPanel.add(editButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
			
		// Setup the Main Panel.
		setBorder(BorderFactory.createTitledBorder( 
			BorderFactory.createEtchedBorder(), "Skills"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// Setup TextField
		scrollPane = new JScrollPane();
		taSkills = new JTextArea();
		taSkills.setLineWrap(true);
		taSkills.setWrapStyleWord(true);
		taSkills.setEditable(false);
		scrollPane.setViewportView(taSkills);
		
		add(scrollPane);
		add(buttonPanel);
	}

	public void displayPerson(Person person) {
		this.person = person;
		setSkillTextField();
	}

	private void setSkillTextField() { 
		String personCode = person.getPersonCode();
		try {
			this.list = skillQueries.getSkillsOfPerson(personCode);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder();
		for (Skill skill: list) {
			sb.append(skill.getSkillName() + ", ");
		}
		taSkills.setText(sb.toString());
	}
		
	private class ButtonController implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
				editButton();
		}
		
		private void editButton() {
			JDialog dialog = new JDialog();
			dialog.getContentPane().add(new EditPersonSkillsPanel(connection, person));
			dialog.setBounds(100, 100, 550, 400);
			dialog.setTitle("Change Skills");
			dialog.setVisible(true);
			dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			dialog.addWindowListener(new EditWindowListener());
			dialog.setModal(true);
		}
	}
	
	public class PersonListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			displayPerson((Person)evt.getNewValue());
		}
	}
	
	public class EditWindowListener extends WindowAdapter {
		@Override
		public void windowClosed(WindowEvent e) {
			setSkillTextField();
		}
	}
}
