package de.biomedical_imaging.ij.clumpsplitting.SplitLines;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import de.biomedical_imaging.ij.clumpsplitting.Clump;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityRegion;
import ij.process.ImageProcessor;

public class MaximumIntensitySplitLineCalculator implements AbstractSplitLineCalculator
{

	private Point2D startPoint;
	private Point2D endPoint;

	
	public MaximumIntensitySplitLineCalculator(Point2D startPoint, Point2D endPoint)
	{
		this.startPoint=startPoint;
		this.endPoint=endPoint;
	}
	@Override
	public ArrayList<AbstractSplitLine> calculatePossibleSplitLines(ArrayList<ConcavityRegion> concavityRegionList,
			Clump c, ImageProcessor ip)
	{
		
		ArrayList<AbstractSplitLine> splitLineList=new ArrayList<AbstractSplitLine>();
/*		if(startPoint.equals(endPoint))
		{
			return splitLineList;
		}*/
	int minX;
	int maxX;
	if(startPoint.getX()<endPoint.getX())
	{
		//TODO Konstante
		minX=(int)startPoint.getX();
		maxX=(int)endPoint.getX();
		
	}
	else{
		minX=(int)endPoint.getX();
		maxX=(int)startPoint.getX();
		
	}
	int minY;
	int maxY;
	if(startPoint.getY()<endPoint.getY())
	{
		//TODO Konstante
		minY=(int)startPoint.getY();
		maxY=(int)endPoint.getY();
		
	}
	else{
		minY=(int)endPoint.getY();
		maxY=(int)startPoint.getY();
		
	}
	//double[][] partialDerivative=new double[maxX-minX+2][maxY-minY+2];
	
	//horizontale Ableitung
	/*for(int i=minX+1;i<maxX;i++)
	{
		for(int j=minY+1;j<maxY;j++)
		{
			double wert=0;
			for(int m=-1;m<=1;m++)
			{
				for(int n=-1;n<=1;n++)
				{
					wert=wert+prewittHor[m+1][n+1]*ip.getPixel(i+m, j+n);
				}
			}
			wert=Math.abs(wert);
			partialDerivative[i-minX][j-minY]=wert;
		}
		
	}
	for(int i=minX+1;i<maxX-1;i++)
	{
		for(int j=minY+1;j<maxY-1;j++)
		{
			double wert=0;
			for(int m=-1;m<=1;m++)
			{
				for(int n=-1;n<=1;n++)
				{
					wert=wert+prewittVer[m+1][n+1]*ip.getPixel(i+m, j+n);
				}
			}
			wert=Math.abs(wert);
			partialDerivative[i-minX][j-minY]=partialDerivative[i-minX][j-minY]+wert;
		}
	}
	*/
	double[][] werte=new double[maxX-minX+2][maxY-minY+2];
	for(int i=0;i<maxX-minX;i++)
	{
		
		for(int j=0;j<maxY-minY;j++)
		{
			werte[i][j]=-(ip.getPixel(i+minX, j+minY))+256;
		}
	}
	/*double maxi=256;
	for(int i=0;i<partialDerivative.length;i++)
	{
		
		for(int j=0;j<partialDerivative[i].length;j++)
		{
			partialDerivative[i][j]=partialDerivative[i][j]+maxi;
		}
	}
	*/
	Point2D aktuellerPunkt= startPoint;
	
/*
	ArrayList<Point2D> besuchteNichtAbgearbeitetePunkte= new ArrayList<Point2D>();
	boolean[][] besucht=new boolean[partialDerivative.length][partialDerivative[0].length];
	double [][] distance=new double[partialDerivative.length][partialDerivative[0].length];

	Point2D[][] vorgaenger=new Point2D[partialDerivative.length][partialDerivative[0].length];
	
	while(!besucht[(int)endPoint.getX()-minX][(int)endPoint.getY()-minY])
	{
		besucht[(int)aktuellerPunkt.getX()-minX][(int)aktuellerPunkt.getY()-minY]=true;
		distance[(int)aktuellerPunkt.getX()-minX][(int)aktuellerPunkt.getY()-minY]=0;
		besuchteNichtAbgearbeitetePunkte.add(aktuellerPunkt);
		*/
		//Hier vor muss noch das berechnen des Pfades
		
		ArrayList<AccessiblePoint> unusedPoints = new ArrayList<AccessiblePoint>();
		ArrayList<AccessiblePoint> usedPoints= new ArrayList<AccessiblePoint>();
		AccessiblePoint first= new AccessiblePoint(aktuellerPunkt,0,null);
		usedPoints.add(first);
		
		//While
		while(!(aktuellerPunkt.getX()==endPoint.getX()&&aktuellerPunkt.getY()==endPoint.getY()))
		{
		for(int i=-1;i<=1;i++)
		{
			for(int j=-1;j<=1;j++)
			{
				if(aktuellerPunkt.getX()+i>=minX&&aktuellerPunkt.getX()+i<=maxX)
				{
				
					if(aktuellerPunkt.getY()+j>=minY&&aktuellerPunkt.getY()+j<=maxY)
					{
					
						boolean besucht=false;
						boolean ready=false;
						for(AccessiblePoint ap:unusedPoints)
						{
							if(ap.getPoint().getX()==aktuellerPunkt.getX()+i&&ap.getPoint().getY()==aktuellerPunkt.getY()+j)
							{
								besucht=true;
								if(ap.getWeight()>(werte[(int)aktuellerPunkt.getX()+i-minX][(int)aktuellerPunkt.getY()+j-minY]+first.getWeight()))
								{
									ap.setWeight((werte[(int)aktuellerPunkt.getX()+i-minX][(int)aktuellerPunkt.getY()+j-minY]+first.getWeight()));
								}
							}
						}
						for(AccessiblePoint ap:usedPoints)
						{
							if(ap.getPoint().getX()==aktuellerPunkt.getX()+i&&ap.getPoint().getY()==aktuellerPunkt.getY()+j)
							{
								ready=true;
							}
						}
						
						if(besucht==false&&ready==false)
						{
							Point2D temp= new Point2D.Double(aktuellerPunkt.getX()+i,aktuellerPunkt.getY()+j);
							AccessiblePoint ap=new AccessiblePoint(temp,(werte[(int)aktuellerPunkt.getX()+i-minX][(int)aktuellerPunkt.getY()+j-minY]+first.getWeight()),first);
							unusedPoints.add(ap);
						}
					}
				}
			}
		}
		Collections.sort(unusedPoints);
		if(unusedPoints.size()!=0)
		{
			aktuellerPunkt=unusedPoints.get(0).getPoint();
			first=unusedPoints.get(0);
			usedPoints.add(unusedPoints.get(0));
			unusedPoints.remove(0);
		}
	}
		ArrayList<Point2D> pointList = new ArrayList<Point2D>(); 
		while(first.getPrevious()!=null)
		{
			pointList.add(first.getPoint());
			first=first.getPrevious();
		}
		//	System.out.println(pointList.size());
	/*	if(pointList.size()<=0)
		{
			Clump.STOP++;
		}*/
		//	for(Point2D punkt:besuchteNichtAbgearbeitetePunkte)	
	/*	{
			for(int i=-1;i<=1;i++)
			{
				for(int j=-1;j<=1;j++)
				{
					if(aktuellerPunkt.getX()+i>minX&&aktuellerPunkt.getX()+i<maxX)
					{
					
						if(aktuellerPunkt.getY()+j>minY&&aktuellerPunkt.getY()+j<maxY)
						{
							if(!besucht[(int) (aktuellerPunkt.getX()+i-minX)][(int) (aktuellerPunkt.getY()+j-minY)])
							{
								if(partialDerivative[(int) (aktuellerPunkt.getX()+i-minX)][(int) (aktuellerPunkt.getY()+j-minY)]<min)
								{
									min=partialDerivative[(int) (aktuellerPunkt.getX()+i-minX)][(int) (aktuellerPunkt.getY()+j-minY)];
									vorgaenger[(int) (aktuellerPunkt.getX()+i-minX)][(int) (aktuellerPunkt.getY()+j-minY)]=aktuellerPunkt;
								}
							}
						}
					}
				}
			}
		}
	}
	/////////////////////////////////////////////////////
	System.out.println(partialDerivative.length);

	System.out.println(partialDerivative[0].length);
	ArrayList<Point2D> cutPoints=new ArrayList<Point2D>();
		Point2D aktuellerPunkt= startPoint;
		//for(int test=0;test<100;test++)
	
	while(!aktuellerPunkt.equals(endPoint))
		{
//		System.out.println((int)aktuellerPunkt.getX()+" "+minX + " "+ (int)aktuellerPunkt.getY()+" "+minY);
			double max=0;
			Point2D maxPoint=null;
			double distXV=0;
			double distYV=0;
			double distV=0;
			for(int i=-1;i<=1;i++)
			{
				for(int j=-1;j<=1;j++)
				{
					if(i!=0||j!=0)
					{
						if((int)aktuellerPunkt.getX()-minX+i+1>0&&(int)aktuellerPunkt.getX()-minX+i+1<partialDerivative.length)
						{

							if(aktuellerPunkt.getY()-minY+j+1>0&&aktuellerPunkt.getY()-minY+j+1<partialDerivative[0].length)
							{
									
					if(partialDerivative[(int)aktuellerPunkt.getX()-minX+i+1][(int)aktuellerPunkt.getY()-minY+j+1]>max||maxPoint==null)
					{
						max=partialDerivative[(int)aktuellerPunkt.getX()-minX+i+1][(int)aktuellerPunkt.getY()-minY+j+1];
						maxPoint= new Point2D.Double(aktuellerPunkt.getX()+i,aktuellerPunkt.getY()+j);
						distXV=Math.abs(aktuellerPunkt.getX()+i-endPoint.getX());
						distYV=Math.abs(aktuellerPunkt.getY()+j-endPoint.getY());
						
						distV=Math.sqrt(distXV*distXV+distYV*distYV);
					}else
					{
						if(partialDerivative[(int)aktuellerPunkt.getX()-minX+i+1][(int)aktuellerPunkt.getY()-minY+j+1]==max)
						{
							double distX=Math.abs(aktuellerPunkt.getX()+i-endPoint.getX());
							double distY=Math.abs(aktuellerPunkt.getY()+j-endPoint.getY());
							
							double dist=Math.sqrt(distX*distX+distY*distY);
							if(dist<distV)
							{
								maxPoint= new Point2D.Double(aktuellerPunkt.getX()+i,aktuellerPunkt.getY()+j);
								
							}
						}
					}
						}
					}
					}
					}
			}
			partialDerivative[(int)aktuellerPunkt.getX()-minX+1][(int)aktuellerPunkt.getY()-minY+1]=0;
		//	System.out.println("Maximaler Punkt: " +maxPoint.getX()+ " y: " + maxPoint.getY()+ " Grenzen: "+ minX+ " " + maxX+ " "+ minY+ " "+ maxY );
					cutPoints.add(aktuellerPunkt);
					aktuellerPunkt=maxPoint;
				
			
		
		
	}*/pointList.add(endPoint);
		MaximumMinimumIntensitySplitLine gdsl=new MaximumMinimumIntensitySplitLine(pointList);
		splitLineList.add(gdsl);
		return splitLineList;
	}
	

}
