package application;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

public class fileOverwriteWindow  {

	public static JCheckBox caseApplyToAll = new JCheckBox(Shutter.language.getProperty("caseApplyToAll"));
	public static String value;
	
	public fileOverwriteWindow(String file) {
		
		value = "cancel";
				
		JDialog frame = new JDialog();
		frame.setTitle(file + " " + Shutter.language.getProperty("alreadyExist").toLowerCase());
		frame.setLayout(null);
		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
		frame.setResizable(false);
    	frame.setModal(true);
		frame.getContentPane().setLayout(null);	
		
		System.setProperty("apple.laf.useScreenMenuBar", "false");

		JButton btnKeep = new JButton(Shutter.language.getProperty("btnKeep"));
		btnKeep.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnKeep.setBounds(7, 7, btnKeep.getPreferredSize().width, 21);
		frame.getContentPane().add(btnKeep);
		
		btnKeep.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				value = "keep";				
				frame.dispose();
				
			}
			
		});
		
		JButton btnOverwrite = new JButton(Shutter.language.getProperty("btnOverwrite"));
		btnOverwrite.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnOverwrite.setBounds(btnKeep.getX() + btnKeep.getWidth() + 4, 7, btnOverwrite.getPreferredSize().width, 21);
		frame.getContentPane().add(btnOverwrite);
		
		btnOverwrite.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				value = "overwrite";				
				frame.dispose();
				
			}
			
		});
		
		JButton btnSkip = new JButton(Shutter.language.getProperty("btnSkip"));
		btnSkip.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnSkip.setBounds(btnOverwrite.getX() + btnOverwrite.getWidth() + 4, 7, btnSkip.getPreferredSize().width, 21);
		frame.getContentPane().add(btnSkip);
		
		btnSkip.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				value = "skip";								
				frame.dispose();
				
			}
			
		});
		
		JButton btnCancel = new JButton(Shutter.language.getProperty("btnCancel"));
		btnCancel.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnCancel.setBounds(btnSkip.getX() + btnSkip.getWidth() + 4, 7, btnCancel.getPreferredSize().width, 21);
		frame.getContentPane().add(btnCancel);

		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
							
				frame.dispose();
				
			}
			
		});
		
		caseApplyToAll.setSelected(false);
		caseApplyToAll.setEnabled(true);
		caseApplyToAll.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseApplyToAll.setBounds((btnCancel.getX() + btnCancel.getWidth() + 7 - caseApplyToAll.getPreferredSize().width) / 2, btnCancel.getY() + btnCancel.getHeight() + 4, caseApplyToAll.getPreferredSize().width, 23);
		frame.getContentPane().add(caseApplyToAll);
		
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
		{
			frame.setSize(btnCancel.getX() + btnCancel.getWidth() + 7, 90);
		}
		else
			frame.setSize(btnCancel.getX() + btnCancel.getWidth() + 17, 100);
			
		frame.setLocation(Shutter.frame.getX() + (Shutter.frame.getWidth() - frame.getWidth()) / 2, Shutter.frame.getY() + (Shutter.frame.getHeight()- frame.getHeight()) / 2);
		frame.setVisible(true);
	}
}
