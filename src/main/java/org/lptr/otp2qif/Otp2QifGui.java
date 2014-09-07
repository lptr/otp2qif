package org.lptr.otp2qif;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;

public class Otp2QifGui {
	private static void createAndShowGUI() {
		final JFrame frame = new JFrame("OTP to QIF Converter");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JButton convertButton = new JButton(new AbstractAction("Choose OTP file to convert") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser sourceChooser = new JFileChooser();
				sourceChooser.setDialogTitle("Choose an OTP file to convert");
				if (sourceChooser.showDialog(frame, "Choose") == JFileChooser.APPROVE_OPTION) {
					JFileChooser targetChooser = new JFileChooser();
					targetChooser.setDialogTitle("Choose where to save the QIF export");
					if (targetChooser.showDialog(frame, "Save") == JFileChooser.APPROVE_OPTION) {
						try {
							File source = sourceChooser.getSelectedFile();
							File target = targetChooser.getSelectedFile();
							if (!target.exists()
									|| JOptionPane.showConfirmDialog(frame, "Can I overwrite " + target + "?", "Overwrite file", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
								Otp2Qif.convert(source, target);
								JOptionPane.showMessageDialog(frame, "File successfully converted to: " + source);
							}
						} catch (FileNotFoundException ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(frame, "Cannot find file: " + ex, "Error", JOptionPane.ERROR_MESSAGE);
						} catch (Exception ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(frame, "Something went wrong: " + ex, "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.add(convertButton);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) throws Exception {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
