/*
 * DelineateApplication.java - GUI for converting raster images to SVG using AutoTrace
 *
 * Copyright (C) 2003 Robert McKinnon
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sf.delineate;

import net.sf.delineate.gui.SettingsPanel;
import net.sf.delineate.gui.SvgViewerPanel;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SpringUtilities;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;

/**
 * GUI for converting raster images to SVG using AutoTrace
 * @author robmckinnon@users.sourceforge.net
 */
public class DelineateApplication {

    public DelineateApplication(String parameterFile) throws Exception {
        final SettingsPanel settingsPanel = new SettingsPanel(parameterFile);

        JFrame frame = new JFrame("Delineate - raster to SVG converter");
        final SvgViewerPanel svgViewerPanel = new SvgViewerPanel(frame);

        JButton button = initDelineateButton(settingsPanel, svgViewerPanel);

        JPanel buttonPanel = new JPanel();
//        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        buttonPanel.add(button);
//        buttonPanel.add(svgViewerPanel.getViewSourceButton());
//        buttonPanel.add(svgViewerPanel.getLoadButton());

        JPanel controlPanel = new JPanel(new SpringLayout());
        controlPanel.add(settingsPanel.getPanel());
        controlPanel.add(buttonPanel);
        SpringUtilities.makeCompactGrid(controlPanel, 2, 1, 2, 2, 2, 2);
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.add(controlPanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(wrapperPanel, BorderLayout.EAST);
        panel.add(svgViewerPanel.createComponents());
        panel.add(svgViewerPanel.getStatusLabel(), BorderLayout.SOUTH);

        frame.setContentPane(panel);
        frame.setBounds(130, 30, 800, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JButton initDelineateButton(final SettingsPanel settingsPanel, final SvgViewerPanel svgViewerPanel) {
        JButton button = new JButton("Run");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                svgViewerPanel.movePreviousSvg();
                String command = settingsPanel.getCommand();

                try {
                    Process process = Runtime.getRuntime().exec(command);
                    process.waitFor();
                    String outputFile = settingsPanel.getOutputFile();
                    svgViewerPanel.load("file:" + outputFile);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return button;
    }

    public static void main(String args[]) throws Exception {
        new DelineateApplication(args[0]);
    }

}
