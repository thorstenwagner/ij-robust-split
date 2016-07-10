package ij_robust_split;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.ArrayList;

import de.biomedical_imaging.ij.clumpsplitting.AbstractConcavityPixelDetector;
import de.biomedical_imaging.ij.clumpsplitting.Clump;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityPixel;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityRegion;
import de.biomedical_imaging.ij.clumpsplitting.LargestDistanceConcavityPixelDetector;
import de.biomedical_imaging.ij.clumpsplitting.SplitLines.AbstractSplitLine;
import de.biomedical_imaging.ij.clumpsplitting.SplitLines.AbstractSplitLineCalculator;
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

	@org.junit.Test
	public void concavityRegionTest()
	{
		URL url = this.getClass().getClassLoader().getResource("6er.png");
		ImagePlus ip = new ImagePlus(url.getPath());

	//	ImagePlus ip = new ImagePlus("Testbilder/6er.png");
		ManyBlobs mb = new ManyBlobs(ip);
		mb.findConnectedComponents();
		ImageProcessor imap = ip.getProcessor();
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		assertEquals(13, c.getConcavityRegionList().size());
	}

	@org.junit.Test
	public void concavityPixelTest()
	{
		URL url = this.getClass().getClassLoader().getResource("6er.png");
		ImagePlus ip = new ImagePlus(url.getPath());

		//ImagePlus ip = new ImagePlus("Testbilder/6er.png");
		ManyBlobs mb = new ManyBlobs(ip);
		mb.findConnectedComponents();
		ImageProcessor imap = ip.getProcessor();
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		int sum = 0;
		for (ConcavityRegion cr : concavityRegionList)
		{
			sum += cr.getConcavityPixelList().size();
		}
		System.out.println(sum);
		assertEquals(14, sum);
	}

	@org.junit.Test
	public void concavityDepthTest()
	{
		URL url = this.getClass().getClassLoader().getResource("6er.png");
		ImagePlus ip = new ImagePlus(url.getPath());

		//ImagePlus ip = new ImagePlus("Testbilder/6er.png");
		ManyBlobs mb = new ManyBlobs(ip);
		mb.findConnectedComponents();
		ImageProcessor imap = ip.getProcessor();
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
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
		System.out.println(max);
		assertEquals(100, max, 2);
	}

	@org.junit.Test
	public void concavityDepthTest2()
	{
		URL url = this.getClass().getClassLoader().getResource("6er.png");
		ImagePlus ip = new ImagePlus(url.getPath());

		//ImagePlus ip = new ImagePlus("Testbilder/6er.png");
		ManyBlobs mb = new ManyBlobs(ip);
		mb.findConnectedComponents();
		ImageProcessor imap = ip.getProcessor();
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		AbstractConcavityPixelDetector ldcpd = new LargestDistanceConcavityPixelDetector();

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
		System.out.println(max);
		assertEquals(100, max, 2);
	}

	@org.junit.Test
	public void saliencyTest()
	{
		URL url = this.getClass().getClassLoader().getResource("2Objekte.png");
		ImagePlus ip = new ImagePlus(url.getPath());

		//ImagePlus ip = new ImagePlus("Testbilder/2Objekte.png");
		ManyBlobs mb = new ManyBlobs(ip);
		mb.findConnectedComponents();
		ImageProcessor imap = ip.getProcessor();
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		AbstractSplitLineCalculator sslc = new StraightSplitLineCalculator();
		ArrayList<AbstractSplitLine> possibleSplitLines = sslc.calculatePossibleSplitLines(concavityRegionList, c,
				imap);
		StraightSplitLineBetweenTwoConcavityRegions sslbtcr = (StraightSplitLineBetweenTwoConcavityRegions) possibleSplitLines
				.get(0);
		assertEquals(1, sslbtcr.getSaliency(), 0.01);
	}

	@org.junit.Test
	public void concavityConcavityAlignmentTest()
	{
		URL url = this.getClass().getClassLoader().getResource("2Objekte.png");
		ImagePlus ip = new ImagePlus(url.getPath());

		//ImagePlus ip = new ImagePlus("Testbilder/2Objekte.png");
		ManyBlobs mb = new ManyBlobs(ip);
		mb.findConnectedComponents();
		ImageProcessor imap = ip.getProcessor();
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		AbstractSplitLineCalculator sslc = new StraightSplitLineCalculator();
		ArrayList<AbstractSplitLine> possibleSplitLines = sslc.calculatePossibleSplitLines(concavityRegionList, c,
				imap);
		StraightSplitLineBetweenTwoConcavityRegions sslbtcr = (StraightSplitLineBetweenTwoConcavityRegions) possibleSplitLines
				.get(0);
		double cca=(360/(2*Math.PI))*sslbtcr.getConcavityConcavityAlignment();
		assertEquals(7, cca, 1);
	}
	@org.junit.Test
	public void concavityLineAlignmentTest()
	{
		URL url = this.getClass().getClassLoader().getResource("2Objekte.png");
		ImagePlus ip = new ImagePlus(url.getPath());

		//ImagePlus ip = new ImagePlus("Testbilder/2Objekte.png");
		ManyBlobs mb = new ManyBlobs(ip);
		mb.findConnectedComponents();
		ImageProcessor imap = ip.getProcessor();
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		AbstractSplitLineCalculator sslc = new StraightSplitLineCalculator();
		ArrayList<AbstractSplitLine> possibleSplitLines = sslc.calculatePossibleSplitLines(concavityRegionList, c,
				imap);
		StraightSplitLineBetweenTwoConcavityRegions sslbtcr = (StraightSplitLineBetweenTwoConcavityRegions) possibleSplitLines
				.get(0);
		double ccl=(360/(2*Math.PI))*sslbtcr.getConcavityLineAlignment();
		assertEquals(10, ccl, 1);
	}
	@org.junit.Test
	public void concavityAngleTest()
	{
		URL url = this.getClass().getClassLoader().getResource("4EineRegion.png");
		ImagePlus ip = new ImagePlus(url.getPath());

		//ImagePlus ip = new ImagePlus("Testbilder/4EineRegion.png");
		ManyBlobs mb = new ManyBlobs(ip);
		mb.setBackground(0);
		mb.findConnectedComponents();
		ImageProcessor imap = ip.getProcessor();
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		AbstractSplitLineCalculator sslc = new StraightSplitLineCalculator();
		ArrayList<AbstractSplitLine> possibleSplitLines = sslc.calculatePossibleSplitLines(concavityRegionList, c,
				imap);
		StraightSplitLineBetweenConcavityRegionAndPoint sslbtcr = (StraightSplitLineBetweenConcavityRegionAndPoint) possibleSplitLines
				.get(0);
		double ca=(360/(2*Math.PI))*sslbtcr.getConcavityAngle();
		assertEquals(56, ca, 1);
	}
	@org.junit.Test
	public void concavityRatioTest()
	{
		URL url = this.getClass().getClassLoader().getResource("4EineRegion.png");
		ImagePlus ip = new ImagePlus(url.getPath());

		//ImagePlus ip = new ImagePlus("Testbilder/4EineRegion.png");
		ManyBlobs mb = new ManyBlobs(ip);
		mb.setBackground(0);
		mb.findConnectedComponents();
		ImageProcessor imap = ip.getProcessor();
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		ArrayList<ConcavityRegion> concavityRegionList = c.getConcavityRegionList();
		AbstractSplitLineCalculator sslc = new StraightSplitLineCalculator();
		ArrayList<AbstractSplitLine> possibleSplitLines = sslc.calculatePossibleSplitLines(concavityRegionList, c,
				imap);
		StraightSplitLineBetweenConcavityRegionAndPoint sslbtcr = (StraightSplitLineBetweenConcavityRegionAndPoint) possibleSplitLines
				.get(0);
		double cr=sslbtcr.getConcavityRatio();
		assertEquals(27, cr, 0.5);
	}
	// @org.junit.Test
	/*
	 * public void concavityPixelTest() { ImagePlus ip = new
	 * ImagePlus("Testbilder/6er.png"); ManyBlobs mb = new ManyBlobs(ip);
	 * mb.findConnectedComponents(); ImageProcessor imap = ip.getProcessor();
	 * ImageProcessor imageProcessorBinary = imap.duplicate(); AutoThresholder
	 * at = new AutoThresholder(); int[] histogram =
	 * imageProcessorBinary.getHistogram(); int threshold =
	 * at.getThreshold(Method.Default, histogram);
	 * imageProcessorBinary.threshold(threshold); Clump c = new
	 * Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap,
	 * imageProcessorBinary); ArrayList<ConcavityRegion> concavityRegionList=
	 * c.getConcavityRegionList(); int sum=0; for(ConcavityRegion
	 * cr:concavityRegionList) { sum+=cr.getConcavityPixelList().size(); }
	 * System.out.println(sum); assertEquals(14, sum); }
	 */

}
