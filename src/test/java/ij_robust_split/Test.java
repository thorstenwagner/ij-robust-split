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

package ij_robust_split;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;

import de.biomedical_imaging.ij.clumpsplitting.AbstractConcavityPixelDetector;
import de.biomedical_imaging.ij.clumpsplitting.Clump;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityPixel;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityRegion;
import de.biomedical_imaging.ij.clumpsplitting.LargestDistanceConcavityPixelDetector;
import de.biomedical_imaging.ij.clumpsplitting.SplitLines.AbstractSplitLine;
import de.biomedical_imaging.ij.clumpsplitting.SplitLines.AbstractSplitLineCalculator;
import de.biomedical_imaging.ij.clumpsplitting.SplitLines.GeodesicDistanceSplitLineCalculator;
import de.biomedical_imaging.ij.clumpsplitting.SplitLines.MaximumIntensitySplitLineCalculator;
import de.biomedical_imaging.ij.clumpsplitting.SplitLines.MinimumIntensitySplitLineCalculator;
import de.biomedical_imaging.ij.clumpsplitting.SplitLines.StraightSplitLineBetweenConcavityRegionAndPoint;
import de.biomedical_imaging.ij.clumpsplitting.SplitLines.StraightSplitLineBetweenTwoConcavityRegions;
import de.biomedical_imaging.ij.clumpsplitting.SplitLines.StraightSplitLineCalculator;
import ij.ImagePlus;
import ij.blob.ManyBlobs;
import ij.process.AutoThresholder;
import ij.process.ImageProcessor;
import ij.process.AutoThresholder.Method;

public class Test
{

	/**
	 * Test to control if the count of concavityRegions is detected right
	 */
	@org.junit.Test
	public void concavityRegionTest()
	{
		URL url = this.getClass().getClassLoader().getResource("6er.png");
		ImagePlus ip = new ImagePlus(url.getPath());

		// detect components
		ManyBlobs mb = new ManyBlobs(ip);
		mb.findConnectedComponents();

		ImageProcessor imap = ip.getProcessor();

		// get binary Image
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);

		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		assertEquals(13, c.getConcavityRegionList().size());
	}

	/**
	 * Test to control if the number of bestSplitLines for a Clump is correct
	 */
	@org.junit.Test
	public void bestSplitLineTest()
	{
		URL url = this.getClass().getClassLoader().getResource("2Objekte.jpg");
		ImagePlus ip = new ImagePlus(url.getPath());

		ImageProcessor imap = ip.getProcessor();

		// get binary Image
		ImageProcessor imageProcessorBinary = imap.duplicate();

		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);

		imageProcessorBinary.threshold(threshold);

		ManyBlobs mb = new ManyBlobs(new ImagePlus("", imageProcessorBinary));
		mb.findConnectedComponents();

		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		assertEquals(1, c.getSplitLines().size());
	}

	/**
	 * Test to control if SplitLinePositions are correct
	 */
	@org.junit.Test
	public void bestSplitLinePointsTest()
	{
		URL url = this.getClass().getClassLoader().getResource("2Objekte.jpg");
		ImagePlus ip = new ImagePlus(url.getPath());

		ImageProcessor imap = ip.getProcessor();

		// get binary Image
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);

		ManyBlobs mb = new ManyBlobs(new ImagePlus("", imageProcessorBinary));
		mb.findConnectedComponents();

		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		// get detected SplitLines
		AbstractSplitLine asl = c.getSplitLines().get(0);

		// extracts Startpoint and endpoint of the SplitLine
		Point2D[] points =
		{ asl.getStartPoint(), asl.getEndPoint() };
		Point2D startPoint = new Point2D.Double(139, 114);
		Point2D endPoint = new Point2D.Double(135, 134);
		Point2D[] comparePoints =
		{ startPoint, endPoint };

		assertArrayEquals(points, comparePoints);
	}

	/**
	 * Test to control if SplitLine width is correct
	 */
	@org.junit.Test
	public void bestSplitLineWidthTest()
	{
		URL url = this.getClass().getClassLoader().getResource("2Objekte.jpg");
		ImagePlus ip = new ImagePlus(url.getPath());

		ImageProcessor imap = ip.getProcessor();

		// get binary Image
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);

		ManyBlobs mb = new ManyBlobs(new ImagePlus("", imageProcessorBinary));
		mb.findConnectedComponents();

		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		// get detected SplitLines
		AbstractSplitLine asl = c.getSplitLines().get(0);

		assertEquals(20, asl.distance(), 1);
	}

	/**
	 * Test to control if GeodesicDistanceSplitLine width is correct
	 */
	@org.junit.Test
	public void bestSplitLineWidthTest2()
	{
		URL url = this.getClass().getClassLoader().getResource("2Objekte.jpg");
		ImagePlus ip = new ImagePlus(url.getPath());

		ImageProcessor imap = ip.getProcessor();

		// get binary Image
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);

		ManyBlobs mb = new ManyBlobs(new ImagePlus("", imageProcessorBinary));
		mb.findConnectedComponents();

		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		// get detected SplitLines
		AbstractSplitLine asl = c.getSplitLines().get(0);

		AbstractSplitLineCalculator aslc = new GeodesicDistanceSplitLineCalculator(asl.getStartPoint(),
				asl.getEndPoint());

		ArrayList<AbstractSplitLine> splitLines = aslc.calculatePossibleSplitLines(c.getConcavityRegionList(), c, imap,
				imageProcessorBinary);

		assertEquals(27, splitLines.get(0).distance(), 3);
	}

	/**
	 * Test to control if MaximumIntensitySplitLine width is correct
	 */
	@org.junit.Test
	public void bestSplitLineWidthTest4()
	{
		URL url = this.getClass().getClassLoader().getResource("2Objekte.jpg");
		ImagePlus ip = new ImagePlus(url.getPath());

		ImageProcessor imap = ip.getProcessor();

		// get binary Image
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);

		ManyBlobs mb = new ManyBlobs(new ImagePlus("", imageProcessorBinary));
		mb.findConnectedComponents();

		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		// get detected SplitLines
		AbstractSplitLine asl = c.getSplitLines().get(0);

		AbstractSplitLineCalculator aslc = new MaximumIntensitySplitLineCalculator(asl.getStartPoint(),
				asl.getEndPoint());

		ArrayList<AbstractSplitLine> splitLines = aslc.calculatePossibleSplitLines(c.getConcavityRegionList(), c, imap,
				imageProcessorBinary);

		assertEquals(26, splitLines.get(0).distance(), 3);
	}

	/**
	 * Test to control if MaximumIntensitySplitLine width is correct
	 */
	@org.junit.Test
	public void bestSplitLineWidthTest3()
	{
		URL url = this.getClass().getClassLoader().getResource("2Objekte.jpg");
		ImagePlus ip = new ImagePlus(url.getPath());

		ImageProcessor imap = ip.getProcessor();

		// get binary Image
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);

		ManyBlobs mb = new ManyBlobs(new ImagePlus("", imageProcessorBinary));
		mb.findConnectedComponents();

		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		// get detected SplitLines
		AbstractSplitLine asl = c.getSplitLines().get(0);

		AbstractSplitLineCalculator aslc = new MinimumIntensitySplitLineCalculator(asl.getStartPoint(),
				asl.getEndPoint());

		ArrayList<AbstractSplitLine> splitLines = aslc.calculatePossibleSplitLines(c.getConcavityRegionList(), c, imap, imageProcessorBinary);

		assertEquals(30, splitLines.get(0).distance(), 3);
	}

	/**
	 * test to control if number of ConcavityPixels of a Clump is correct
	 */
	@org.junit.Test
	public void concavityPixelTest()
	{
		URL url = this.getClass().getClassLoader().getResource("6er.png");
		ImagePlus ip = new ImagePlus(url.getPath());

		ManyBlobs mb = new ManyBlobs(ip);
		mb.findConnectedComponents();

		ImageProcessor imap = ip.getProcessor();
		// get Binary image
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		// gets all ConcavityRegions of a Clump
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		// sums all ConcavityPixels of a Clump
		int sum = 0;
		for (ConcavityRegion cr : concavityRegionList)
		{
			sum += cr.getConcavityPixelList().size();
		}
		System.out.println(sum);
		assertEquals(14, sum);
	}

	/**
	 * Test to control if the right concavity Depth is detected
	 */
	@org.junit.Test
	public void concavityDepthTest()
	{
		URL url = this.getClass().getClassLoader().getResource("6er.png");
		ImagePlus ip = new ImagePlus(url.getPath());

		ManyBlobs mb = new ManyBlobs(ip);
		mb.findConnectedComponents();
		ImageProcessor imap = ip.getProcessor();
		// gets binary image of the Picture
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);

		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		// gets all ConcavityRegions of a Clump
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		// computes largest ConcavityDepth of a Clump
		double max = 0;
		for (ConcavityRegion cr : concavityRegionList)
		{
			ArrayList<ConcavityPixel> concavityPixelList = cr.getConcavityPixelList();
			for (ConcavityPixel cp : concavityPixelList)
			{
				if (cp.distance() > max)
				{
					max = cp.distance();
				}
			}

		}
		assertEquals(60, max, 2);
	}

	/**
	 * Test to control if the right concavity Depth is detected by
	 * LargestDistanceConcavityDetection
	 */
	@org.junit.Test
	public void concavityDepthTest2()
	{
		URL url = this.getClass().getClassLoader().getResource("6er.png");
		ImagePlus ip = new ImagePlus(url.getPath());

		ManyBlobs mb = new ManyBlobs(ip);
		mb.findConnectedComponents();
		ImageProcessor imap = ip.getProcessor();
		// gets binary image
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);

		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		// detect all concavityRegions of a Clump
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		// detect all ConcavityPixels with Largest ConcavityDepth
		AbstractConcavityPixelDetector ldcpd = new LargestDistanceConcavityPixelDetector();

		// detects largest ConcavityRegion
		double max = 0;
		for (ConcavityRegion cr : concavityRegionList)
		{
			ArrayList<ConcavityPixel> concavityPixelList = ldcpd.computeConcavityPixel(cr);

			for (ConcavityPixel cp : concavityPixelList)
			{
				if (cp.distance() > max)
				{
					max = cp.distance();
				}
			}

		}
		assertEquals(60, max, 2);
	}

	/**
	 * tests if correct saliency is detected
	 */
	@org.junit.Test
	public void saliencyTest()
	{
		URL url = this.getClass().getClassLoader().getResource("2Objekte.jpg");
		ImagePlus ip = new ImagePlus(url.getPath());

		ImageProcessor imap = ip.getProcessor();
		// gets binary image
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);

		ManyBlobs mb = new ManyBlobs(new ImagePlus("", imageProcessorBinary));
		mb.findConnectedComponents();

		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		// computes all concavityRegions of a Clump
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		// Calculates StraightSplitLines
		AbstractSplitLineCalculator sslc = new StraightSplitLineCalculator();
		// gets all possible SplitLines
		ArrayList<AbstractSplitLine> possibleSplitLines = sslc.calculatePossibleSplitLines(concavityRegionList, c,
				imap, imageProcessorBinary);

		StraightSplitLineBetweenTwoConcavityRegions sslbtcr = (StraightSplitLineBetweenTwoConcavityRegions) possibleSplitLines
				.get(0);

		assertEquals(1, sslbtcr.getSaliency(), 0.01);
	}

	/**
	 * Tests if the correct concavityConcavityAlignment is detected
	 */
	@org.junit.Test
	public void concavityConcavityAlignmentTest()
	{
		URL url = this.getClass().getClassLoader().getResource("2Objekte.jpg");
		ImagePlus ip = new ImagePlus(url.getPath());

		ImageProcessor imap = ip.getProcessor();
		// binary Image is detected
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		ManyBlobs mb = new ManyBlobs(new ImagePlus("", imageProcessorBinary));
		mb.findConnectedComponents();

		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		AbstractSplitLineCalculator sslc = new StraightSplitLineCalculator();
		// all possible SplitLines were detected
		ArrayList<AbstractSplitLine> possibleSplitLines = sslc.calculatePossibleSplitLines(concavityRegionList, c,
				imap, imageProcessorBinary);
		StraightSplitLineBetweenTwoConcavityRegions sslbtcr = (StraightSplitLineBetweenTwoConcavityRegions) possibleSplitLines
				.get(0);
		double cca = (360 / (2 * Math.PI)) * sslbtcr.getConcavityConcavityAlignment();
		assertEquals(8, cca, 1);
	}

	/**
	 * Tests if the correct concavityLineAlignment is detected
	 */

	@org.junit.Test
	public void concavityLineAlignmentTest()
	{
		URL url = this.getClass().getClassLoader().getResource("2Objekte.jpg");
		ImagePlus ip = new ImagePlus(url.getPath());

		ImageProcessor imap = ip.getProcessor();
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		ManyBlobs mb = new ManyBlobs(new ImagePlus("", imageProcessorBinary));
		mb.findConnectedComponents();

		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		AbstractSplitLineCalculator sslc = new StraightSplitLineCalculator();
		ArrayList<AbstractSplitLine> possibleSplitLines = sslc.calculatePossibleSplitLines(concavityRegionList, c,
				imap, imageProcessorBinary);
		StraightSplitLineBetweenTwoConcavityRegions sslbtcr = (StraightSplitLineBetweenTwoConcavityRegions) possibleSplitLines
				.get(0);
		double ccl = (360 / (2 * Math.PI)) * sslbtcr.getConcavityLineAlignment();
		assertEquals(6, ccl, 1);
	}

	/**
	 * Tests if the correct concavityAngle is detected
	 */

	@org.junit.Test
	public void concavityAngleTest()
	{
		URL url = this.getClass().getClassLoader().getResource("4EineRegion.png");
		ImagePlus ip = new ImagePlus(url.getPath());

		ImageProcessor imap = ip.getProcessor();
		ImageProcessor imageProcessorBinary = imap.duplicate();
		imageProcessorBinary.blurGaussian(2.0);
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		ManyBlobs mb = new ManyBlobs(new ImagePlus("", imageProcessorBinary));
		mb.setBackground(0);

		mb.findConnectedComponents();

		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		AbstractSplitLineCalculator sslc = new StraightSplitLineCalculator();
		ArrayList<AbstractSplitLine> possibleSplitLines = sslc.calculatePossibleSplitLines(concavityRegionList, c,
				imap, imageProcessorBinary);
		StraightSplitLineBetweenConcavityRegionAndPoint sslbtcr = (StraightSplitLineBetweenConcavityRegionAndPoint) possibleSplitLines
				.get(0);

		double ca = (360 / (2 * Math.PI)) * sslbtcr.getConcavityAngle();
		assertEquals(33, ca, 1);
	}

	/**
	 * Tests if the correct concavityRatio is detected
	 */

	@org.junit.Test
	public void concavityRatioTest()
	{
		URL url = this.getClass().getClassLoader().getResource("4EineRegion.png");
		ImagePlus ip = new ImagePlus(url.getPath());

		ImageProcessor imap = ip.getProcessor();
		ImageProcessor imageProcessorBinary = imap.duplicate();
		imageProcessorBinary.blurGaussian(2.0);
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		ManyBlobs mb = new ManyBlobs(new ImagePlus("", imageProcessorBinary));
		mb.setBackground(0);

		mb.findConnectedComponents();

		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		AbstractSplitLineCalculator sslc = new StraightSplitLineCalculator();
		ArrayList<AbstractSplitLine> possibleSplitLines = sslc.calculatePossibleSplitLines(concavityRegionList, c,
				imap, imageProcessorBinary);
		StraightSplitLineBetweenConcavityRegionAndPoint sslbtcr = (StraightSplitLineBetweenConcavityRegionAndPoint) possibleSplitLines
				.get(0);
		double cr = sslbtcr.getConcavityRatio();
		assertEquals(7, cr, 0.5);
	}

}
