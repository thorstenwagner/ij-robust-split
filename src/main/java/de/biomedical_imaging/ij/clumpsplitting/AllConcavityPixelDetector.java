package de.biomedical_imaging.ij.clumpsplitting;

import java.util.ArrayList;

import ij.plugin.filter.MaximumFinder;

public class AllConcavityPixelDetector implements AbstractConcavityPixelDetector
{
	@Override
	public ArrayList<ConcavityPixel> computeConcavityPixel(ConcavityRegion concavityRegion)
	{
		double[] p=new double[concavityRegion.getDistList().size()];
		for(int i=0;i<concavityRegion.getDistList().size();i++)
		{
			p[i]=concavityRegion.getDistList().get(i);
		}
		boolean excludeOnEdges= true;
		
		int[] maxima= MaximumFinder.findMaxima(p, Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD, excludeOnEdges);
		
		ArrayList<ConcavityPixel> concavityPixelList= new ArrayList<ConcavityPixel>();
		for(int i=0;i<maxima.length;i++)
		{
			if(concavityRegion.getDistList().get(maxima[i])>Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD)
			{
			ConcavityPixel concavityPixel= new ConcavityPixel(concavityRegion.getBoundaryPointList().get(maxima[i]),(concavityRegion.getDistList().get(maxima[i])), concavityRegion);
			concavityPixelList.add(concavityPixel);
			}
		}
		return concavityPixelList;
	}

}
