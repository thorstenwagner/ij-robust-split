package ij_robust_split;

import static org.junit.Assert.*;

import java.util.ArrayList;

import de.biomedical_imaging.ij.clumpsplitting.Clump;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityRegion;
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
		ImagePlus ip = new ImagePlus("Testbilder/6er.png");
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
		ImagePlus ip = new ImagePlus("Testbilder/6er.png");
		ManyBlobs mb = new ManyBlobs(ip);
		mb.findConnectedComponents();
		ImageProcessor imap = ip.getProcessor();
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		ArrayList<ConcavityRegion> concavityRegionList= c.getConcavityRegionList();
		int sum=0;
		for(ConcavityRegion cr:concavityRegionList)
		{
			sum+=cr.getConcavityPixelList().size();
		}
		System.out.println(sum);
		assertEquals(14, sum);
	}
	//@org.junit.Test
	/*public void concavityPixelTest()
	{
		ImagePlus ip = new ImagePlus("Testbilder/6er.png");
		ManyBlobs mb = new ManyBlobs(ip);
		mb.findConnectedComponents();
		ImageProcessor imap = ip.getProcessor();
		ImageProcessor imageProcessorBinary = imap.duplicate();
		AutoThresholder at = new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);
		Clump c = new Clump(mb.get(0).getOuterContour(), mb.get(0).getInnerContours(), imap, imageProcessorBinary);
		ArrayList<ConcavityRegion> concavityRegionList= c.getConcavityRegionList();
		int sum=0;
		for(ConcavityRegion cr:concavityRegionList)
		{
			sum+=cr.getConcavityPixelList().size();
		}
		System.out.println(sum);
		assertEquals(14, sum);
	}*/

}
