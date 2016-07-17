/*

Copyright (c) 2016 Louise Bloch (louise.bloch001@stud.fh-dortmund.de), Thorsten Wagner (wagner@b
iomedical-imaging.de)

Permission is hereby granted, free of charge, to any person obtaining a
copy
of this software and associated documentation files (the "Software"),
to deal
in the Software without restriction, including without limitation the
rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
IN THE
SOFTWARE.
*/

package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.TextRoi;

/**
 * Mouse Listener to Control mouseactions for all ConcavityRegions of a Clump.
 * If Mouse is Clicked in the BoundingBox of the Concavity Region, information
 * about the ConcavityRegion is displayed
 * 
 * @author Louise
 *
 */

public class MouseListenerConcavityRegions implements MouseListener
{

	private ConcavityRegion cr;

	public MouseListenerConcavityRegions(ConcavityRegion cr)
	{
		this.cr = cr;
	}

	public int getImageCoordinateY(int n)
	{
		ImagePlus imageplus = WindowManager.getCurrentImage();
		return imageplus.getCanvas().offScreenY(n);
	}

	public int getImageCoordinateX(int n)
	{
		ImagePlus imageplus = WindowManager.getCurrentImage();
		return imageplus.getCanvas().offScreenX(n);
	}

	/**
	 * shows information about a ConcavityRegion if mouse is Clicked in the
	 * Bounding Box of the ConcavityRegion
	 */
	@Override
	public void mouseClicked(MouseEvent e)
	{
		Rectangle boundingBox = cr.getRectangle();
		double minX = boundingBox.getMinX();
		double minY = boundingBox.getMinY();
		double maxX = boundingBox.getMaxX();
		double maxY = boundingBox.getMaxY();

		if ((this.getImageCoordinateX(e.getX()) > minX) && (this.getImageCoordinateX(e.getX()) < maxX))
		{
			if ((this.getImageCoordinateY(e.getY()) > minY) && (this.getImageCoordinateY(e.getY()) < maxY))
			{
				for (ConcavityPixel point : cr.getConcavityPixelList())
				{

					Clump_Splitting.overlayTextConvexHull.clear();
					TextRoi text = new TextRoi(minX, minY, cr.getInformation(point));
					TextRoi.setFont("Default", 10, Font.PLAIN);
					text.setStrokeWidth(5);
					text.setStrokeColor(Color.red);
					Clump_Splitting.overlayTextConvexHull.add(text);

					Clump_Splitting.showOverlay();
				}
			}
		}

	}

	@Override
	public void mousePressed(MouseEvent e)
	{

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

}
