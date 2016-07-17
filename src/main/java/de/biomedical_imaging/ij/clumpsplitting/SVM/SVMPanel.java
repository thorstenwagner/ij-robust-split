/*
The MIT License (MIT)

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

package de.biomedical_imaging.ij.clumpsplitting.SVM;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * visualizes an SVM
 * 
 * @author Louise
 *
 */
public class SVMPanel extends JPanel
{

	private static final long serialVersionUID = 1L;
	ArrayList<Double[]> featureList = null;
	private double weight;
	private double bias;

	/**
	 * 
	 * @param featureList
	 *            List of all Features classified by the SVM
	 * @param weight
	 *            negative gradient of the separating line of the SVM
	 * @param bias
	 *            intercept of the separating line of the SVM
	 */
	public SVMPanel(ArrayList<Double[]> featureList, double weight, double bias)
	{
		this.featureList = featureList;
		this.setBackground(Color.white);
		this.setPreferredSize(new Dimension(500, 500));

		this.weight = weight;
		this.bias = bias;

	}

	/**
	 * Paints Coordinate system and Points and separating Line of the SVM
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		// x-Axis
		g.drawLine(0, this.getHeight() - 20, this.getWidth(), this.getHeight() - 20);
		// y-Axis
		g.drawLine(20, 0, 20, this.getHeight());

		for (int i = 0; i < this.getWidth(); i += 50)
		{
			// labels the x Axis
			g.drawString(String.valueOf(i), i + 20 - 3, this.getHeight() - 5);
			// labels the y Axis
			g.drawString(String.valueOf(i), 0, this.getHeight() - i - 20 + 5);
		}
		g.setColor(Color.red);
		for (Double[] data : featureList)
		{
			double[] features =
			{ data[1], data[2] };
			if (data[0] == -1)
			{
				g.setColor(Color.red);
			} else
			{
				g.setColor(Color.blue);
			}
			// draws all Points
			g.drawRect((int) features[0] + 20 - 3, this.getHeight() - (int) features[1] - 20 - 3, 6, 6);

		}
		//draws separating line of the SVMF
		g.setColor(Color.green);
		double y = -weight;
		double wert = y * this.getWidth();
		g.drawLine((int) 20, (int) (this.getHeight() - 20 - Math.round(bias)), this.getWidth() + 20,
				(int) ((this.getHeight() - 20 - (Math.round(wert) + Math.round(bias)))));
	}
}
