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

package de.biomedical_imaging.ij.clumpsplitting;

/**
 * Enum to specify the used ConcavityPixelDetectorType. Default:
 * DETECTALLCONCAVITYPIXELS
 * 
 * DETECTALLCONCAVITYPIXELS is a method, which detects all local
 * ConcavityPixels, which is included between concavityRegion and Contour, it is
 * looking for the Maximas of the DistanceList of the ConcavityRegion
 * 
 * DETECTCONCAVITYPIXELSWITHLARGESTCONCAVITYDEPTH is a method which detects only
 * the global Maximum points of the DIstance list of the Concavity Region
 * 
 * @author Louise
 *
 */
public enum ConcavityPixelDetectorType
{
	DETECTALLCONCAVITYPIXELS, DETECTCONCAVITYPIXELSWITHLARGESTCONCAVITYDEPTH
}
