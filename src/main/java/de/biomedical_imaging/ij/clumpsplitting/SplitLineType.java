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

/**
 * Enum defines possible SplitLineTypes. Default: STRAIGHTSPLITLINE
 * 
 * STRAIGHTSPLITLINE is a SplitLineType, which connects detected ConcavityPixels
 * by a StraightLine --> should be used, if no IntensityInformation is avaiable
 * 
 * MAXIMUMINTENSITYSPLITLINE detects shortest Path between the detected
 * ConcavityPixels, path is good, if the sum of the intensitys is high--> should
 * be used, if objects have a higher intensity at the boundary than in the
 * center
 * 
 * MINIMUMINTENSITYSPLITLINE detects shortest Path between the detected
 * ConcavityPixels, path is good, if the sum of the intensitys is low--> should
 * be used, if objects have a lower intensity at the boundary than in the center
 * 
 * GEODESICDISTANCESPLITLINE detects shortest Path between the detected
 * ConcavityPixels, path is good, if the sum of the local derivation is very
 * high--> should be used, if objects big intensity differences, if they overlap
 * 
 * * MAXIMUMINTENSITYSPLITLINEFARHAN detects shortest Path between the detected
 * ConcavityPixels, path is good, if the sum of the intensitys is high--> should
 * be used, if objects have a higher intensity at the boundary than in the
 * center, implemented like the Method from Farhan et al.
 * 
 * MINIMUMINTENSITYSPLITLINEFARHAN detects shortest Path between the detected
 * ConcavityPixels, path is good, if the sum of the intensitys is low--> should
 * be used, if objects have a lower intensity at the boundary than in the center
 * implemented like the Method from Farhan et al.
 * 
 * @author Louise
 *
 */
public enum SplitLineType
{
	STRAIGHTSPLITLINE, MAXIMUMINTENSITYSPLITLINE, MINIMUMINTENSITYSPLITLINE, GEODESICDISTANCESPLITLINE, MAXIMUMINTENSITYSPLITLINEFARHAN, MINIMUMINTENSITYSPLITLINEFARHAN
}
