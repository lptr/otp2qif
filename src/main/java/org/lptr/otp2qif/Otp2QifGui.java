package org.lptr.otp2qif;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Otp2QifGui {
	private static void createAndShowGUI() {
		final JFrame frame = new JFrame("OTP to QIF Converter");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JButton convertButton = new JButton(new AbstractAction("Choose OTP file to convert") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser sourceChooser = new JFileChooser();
				sourceChooser.setMultiSelectionEnabled(true);
				sourceChooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(File f) {
						return f.getName().endsWith(".xls");
					}

					@Override
					public String getDescription() {
						return "*.xls (Excel files)";
					}
				});
				sourceChooser.setDialogTitle("Choose an OTP files to convert");
				if (sourceChooser.showDialog(frame, "Choose") == JFileChooser.APPROVE_OPTION) {
					List<File> sources = Arrays.asList(sourceChooser.getSelectedFiles());
					List<File> converted = Lists.newArrayList();
					for (File source : sources) {
						try {
							File target = new File(source.getParentFile(), source.getName() + ".qif");
							if (target.exists()
									&& JOptionPane.showConfirmDialog(frame, "Can I overwrite " + target + "?", "Overwrite file", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
								continue;
							}
							Otp2Qif.convert(source, target);
							converted.add(target);
						} catch (Exception ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(frame, String.format("Something went wrong with '%s': %s", source, ex), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					if (converted.isEmpty()) {
						JOptionPane.showMessageDialog(frame, "Did not convert anything");
					} else {
						JOptionPane.showMessageDialog(frame, "Files successfully converted:\n\n - " + Joiner.on("\n - ").join(converted));
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
