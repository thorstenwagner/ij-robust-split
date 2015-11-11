package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Polygon;
import java.util.ArrayList;

public class Boundary {
private Polygon p;
public Boundary(Polygon p)
{
	this.p=p;
}
public ArrayList<Double> getDistance()
{
	ArrayList<Double> distanceList=new ArrayList<Double>();
	
	return distanceList;
}

private ArrayList<Double> getGradients()
{
	ArrayList<Double> angleList=new ArrayList<Double>();
	double angle;
	for (int i=1;i<p.npoints-1;i++)
	{
		 double ankathete= p.ypoints[i-1]-p.ypoints[i+1];
		 double gegenkathete=p.xpoints[i-1]-p.xpoints[i+1];
		 angle=Math.atan(gegenkathete/ankathete);
		 angle=angle+90;
		 angleList.add(angle);
		 
		 
	}
	return angleList;
}
}
