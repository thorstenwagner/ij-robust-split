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

package de.biomedical_imaging.ij.clumpsplitting.SplitLines;

import java.awt.geom.Point2D;

import ij.process.ImageProcessor;

/**
 * interface for different SplitLineTypes, implemented suclasses are explained
 * in the enum SplitLineType
 * 
 * @author Louise
 *
 */
public interface AbstractSplitLine
{

	/**
	 * Draws a Line to both imageProcessors
	 * 
	 * @param ip
	 *            imageProcessor with the original Data of an image
	 * @param binary
	 *            imageProcessor for the binarized and preprocessed image
	 */
	public void drawLine(ImageProcessor ip, ImageProcessor binary);

	public Point2D getStartPoint();

	public Point2D getEndPoint();

	public double distance();
}
