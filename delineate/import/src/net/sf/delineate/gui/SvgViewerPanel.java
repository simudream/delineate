/*
 * SvgViewerPanel.java
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
package net.sf.delineate.gui;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.SpringUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;

import net.sf.delineate.gui.ScrollableJSVGCanvas;

/**
 * Panel for viewing SVG files.
 * @author robmckinnon@users.sourceforge.net
 */
public class SvgViewerPanel {
    private String uri;
    private JFrame frame;
    private JSVGCanvas svgCanvasB;
    private JSVGCanvas svgCanvasA;

    private JLabel statusLabel = new JLabel("Ready.");
    private JPanel contentPanel = new JPanel(new SpringLayout());
    private ViewSourceAction viewSourceAction = new ViewSourceAction();

    public SvgViewerPanel(JFrame f) {
        frame = f;
    }

    public JComponent createComponents() {
        svgCanvasA = initSvgCanvas();
        svgCanvasB = initSvgCanvas();

        contentPanel.add(new JScrollPane(svgCanvasA));
        contentPanel.add(new JScrollPane(svgCanvasB));
        contentPanel.setBorder(BorderFactory.createTitledBorder("Result SVG"));

        SpringUtilities.makeCompactGrid(contentPanel, 2, 1, 1, 1, 4, 4);

        return contentPanel;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }

    public JButton getLoadButton() {
        JButton button = new JButton("Load...");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                load();
            }
        });
        return button;
    }

    public JButton getViewSourceButton() {
        JButton button = new JButton(viewSourceAction);
        button.setText("View source");
        return button;
    }

    public JButton getReloadButton() {
        JButton button = new JButton("Reload...");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                load(uri);
            }
        });
        return button;
    }

    private void load() {
        JFileChooser fileChooser = new JFileChooser(".");
        int choice = fileChooser.showOpenDialog(contentPanel);

        if(choice == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                load(file.toURL().toString());
            } catch(MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public void movePreviousSvg() {
        if(uri != null) {
            File file = getFile(uri);
            File previousFile = new File(file.getParent(), file.getName() + '~');
            file.renameTo(previousFile);
            svgCanvasB.setSVGDocument(null); // hack to prevent problem loading relative URI
            svgCanvasB.setURI(uri + '~');
        }
    }

    public void load(String uri) {
        this.uri = uri;
        System.out.println("loading " + uri);
        svgCanvasA.setSVGDocument(null); // hack to prevent problem loading relative URI
        svgCanvasA.setURI(uri);
    }

    private JSVGCanvas initSvgCanvas() {
        final JSVGCanvas svgCanvas = new ScrollableJSVGCanvas();

        svgCanvas.addSVGDocumentLoaderListener(new
            SVGDocumentLoaderAdapter() {
                public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
                    setStatus("Document loading...");
                }

                public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
                    setStatus("Document loaded.");
                }
            });

        svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            public void gvtBuildStarted(GVTTreeBuilderEvent e) {
                setStatus("Build started...");
            }

            public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
                setStatus("Build done.");
                frame.repaint();
            }
        });

        svgCanvas.addGVTTreeRendererListener(new
            GVTTreeRendererAdapter() {
                public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
                    setStatus("Rendering started...");
                }

                public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
                    String uri = svgCanvas.getURI();
                    File file = getFile(uri);
                    setStatus(file + "    " + getFileSize(file));

                }
            });

        return svgCanvas;
    }

    private File getFile(String uri) {
        String pathname = uri.substring(uri.indexOf(':')  + 1);
        File file = new File(pathname);
        return file;
    }

    private String getFileSize(File file) {
        String size;

        long bytes = file.length();

        if(bytes < 1024) {
            size = bytes + "b";
        } else {
            float kb = bytes / 1024F;

            if(kb < 1024) {
                kb = Math.round(kb * 10) / 10F;
                size = kb + "kb";
            } else {
                float mb = kb / 1024;
                mb = Math.round(mb * 10) / 10F;

                size = mb + "mb";
            }
        }
        return size;
    }

    private void setStatus(String text) {
        statusLabel.setText(text);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Batik");
        SvgViewerPanel app = new SvgViewerPanel(frame);
        frame.setContentPane(app.createComponents());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setBounds(200, 100, 400, 400);
        frame.setVisible(true);
    }

}