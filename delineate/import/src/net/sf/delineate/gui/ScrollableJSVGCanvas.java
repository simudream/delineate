/*
 * ScrollableJSVGCanvas.java - scrollable JSVGCanvas
 *
 * Copyright (C) 2003 Jan Lolling
 *               2003 Robert McKinnon
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
import org.apache.batik.swing.gvt.JGVTComponent;

import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Container;
import java.awt.Window;

public class ScrollableJSVGCanvas extends JSVGCanvas implements Scrollable {

    /**
     * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
     */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /**
     * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }

    /**
     * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }

    /**
     * Methode return true if component do not need vertical scrollbar-support
     *         and         false if vertical scrollbar are needed if component
     * bigger than viewport
     */
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    /**
     * Methode return true if component do not need horizontal scrollbar-support
     */
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    /*
    it is
    necessary to do the next
    to retrieve
    an swing
    component wich
    fits perfect
    in a scrollpane*/

    /**
     * this method will be called from constructor of JGVTComponent
     */
    protected JGVTComponent.Listener createListener() {
        return new ScrollableCanvasSVGListener();
    }

    /**
     * This method avoid the problem, that the original methode in CanvasSVGListener
     * use pack() methode to rearrange the Swing componentes
     */
    protected class ScrollableCanvasSVGListener extends JSVGCanvas.CanvasSVGListener {

        public void setMySize(Dimension d) {
            setPreferredSize(d);
            invalidate();
            Container p = getParent();
            while(p != null) {
                if(p instanceof Window) {
                    Window w = (Window)p;
                    w.validate(); // im Orignial wurde hier pack() aufgerufen
                    break;
                }
                p = p.getParent();
            }
        }

    }
}
