package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Polygon;

public class InnerContour
{
	private Polygon contour;
	private Polygon convexHull;
	public InnerContour(Polygon contour, Polygon convexHull)
	{
		this.contour=contour;
		this.convexHull=convexHull;
	}
	public Polygon getContour()
	{
		return contour;
	}
	public Polygon getConvexHull()
	{
		return convexHull;
	}

}
