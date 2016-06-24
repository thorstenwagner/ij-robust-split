package de.biomedical_imaging.ij.clumpsplitting;

import java.util.ArrayList;

public class LargestDistanceConcavityPixelDetector implements AbstractConcavityPixelDetector
{

	@Override
	public ArrayList<ConcavityPixel> computeConcavityPixel(ConcavityRegion concavityRegion)
	{
		// int indexMax = 0;
		double max = 0;
		// int index = 0;
		ArrayList<ConcavityPixel> moreThanOneMax = new ArrayList<ConcavityPixel>();
		for (int i = 0; i < concavityRegion.getDistList().size(); i++)
		{
			double d = concavityRegion.getDistList().get(i);
			if (d >= max)
			{
				if (d > max)
				{
					moreThanOneMax.clear();
					if (d > Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD)
					{
						ConcavityPixel concavityPixel = new ConcavityPixel(concavityRegion.getBoundaryPointList().get(i), d, concavityRegion);

						moreThanOneMax.add(concavityPixel);
					}
					max = d;

					// indexMax = index;
				} else
				{
					if (d == max)
					{
						if (d > Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD)
						{

							ConcavityPixel concavityPixel = new ConcavityPixel(concavityRegion.getBoundaryPointList().get(i), d,concavityRegion);

							moreThanOneMax.add(concavityPixel);
						}
						max = d;
					}
				}
			}
		}

		return moreThanOneMax;
	}

}
