package de.biomedical_imaging.ij.clumpsplitting;

public class ConvexHull {

	private int[] startingPoint=new int[2];
	private int[]endPoint=new int[2];
	
	public ConvexHull(int startingPointx,int startingPointy,int endPointx,int endPointy)
	{
		startingPoint[0]=startingPointx;
		startingPoint[1]=startingPointy;
		endPoint[0]=endPointx;
		endPoint[1]=endPointy;
	
	}
	public void setEndPoint(int[] endPoint)
	{
		this.endPoint=endPoint;
		
		
	}
	public int[] getStartingPoint()
	{
		return startingPoint;
	}
	public int[] getEndPoint()
	{
		return endPoint;
	}
}
