package net.sf.delineate.gui;

import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.MimeTypeConstants;
import org.apache.batik.xml.XMLUtilities;
import org.w3c.dom.svg.SVGDocument;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.InputStream;
import java.io.Reader;

/**
 * To view the source of the current document.
 */
public class ViewSourceAction extends AbstractAction {

    SVGDocument svgDocument;

    public void setSvgDocument(SVGDocument svgDocument) {
        this.svgDocument = svgDocument;
    }

    public void actionPerformed(ActionEvent e) {
        if(svgDocument == null) {
            return;
        }

        final ParsedURL u = new ParsedURL(svgDocument.getURL());

        final JFrame fr = new JFrame(u.toString());
        fr.setSize(200, 200);
        final JTextArea ta = new JTextArea();
        ta.setLineWrap(true);
        ta.setFont(new Font("monospaced", Font.PLAIN, 12));

        JScrollPane scroll = new JScrollPane();
        scroll.getViewport().add(ta);
        scroll.setVerticalScrollBarPolicy
            (JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        fr.getContentPane().add(scroll, BorderLayout.CENTER);

        new Thread() {
            public void run() {
                char[] buffer = new char[4096];

                try {
                    Document doc = new PlainDocument();
                    InputStream is = u.openStream(MimeTypeConstants.MIME_TYPES_SVG);

                    Reader in = XMLUtilities.createXMLDocumentReader(is);
                    int len;
                    while((len = in.read(buffer, 0, buffer.length)) != -1) {
                        doc.insertString(doc.getLength(),
                            new String(buffer, 0, len), null);
                    }

                    ta.setDocument(doc);
                    ta.setEditable(false);
                    ta.setBackground(Color.white);
                    fr.show();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }
}
