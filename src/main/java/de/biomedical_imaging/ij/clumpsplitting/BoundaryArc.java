package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Polygon;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import ij.IJ;

public class BoundaryArc 
{
	private  int[] startingPoint= new int[2];
	private int[] endPoint=new int[2];
	private Path2D path;
	
	private Polygon p;
	private String number="";
	
	public BoundaryArc(int startingPointx,int startingPointy,int endPointx,int endPointy)
	{
		startingPoint[0]=startingPointx;
		startingPoint[1]=startingPointy;
		endPoint[0]=endPointx;
		endPoint[1]=endPointy;
	}
	
	public ArrayList<Double> getGradient()
	{
		PathIterator pit=path.getPathIterator(null);
		double[] first=new double[6];
		double[] second=new double[6];
		double[] third=new double[6];
		pit.currentSegment(first);
		pit.next();
		pit.currentSegment(second);
		pit.next();
		pit.currentSegment(third);
		ArrayList<Double> angleList= new ArrayList<Double>();
		double angle;
		while(!pit.isDone())
		{
			 double ankathete= first[1]-third[1];
			 double gegenkathete=first[0]-third[0];
			 angle=Math.atan(gegenkathete/ankathete);
			angle=angle+90;
			angleList.add(angle);
			first=second;
			second=third;
			pit.currentSegment(third);
			double[] point=new double[6];
			pit.currentSegment(point);
			pit.next();
			
		}
		return angleList;
	}
	public void setEndPoint(int x,int y)
	{
		endPoint[0]=x;
		endPoint[1]=y;
	}
	public void setPath(Path2D path)
	{
		this.path=path;
	}
		
	public void setStartingPoint(int x, int y)
	{
		startingPoint[0]=x;
		startingPoint[1]=y;
	}
	public void concatNumber(int number)
	{
		this.number=this.number + ((Integer)number).toString();
		//IJ.showMessage(this.number+"number");
	}
	public String getNumber()
	{	//IJ.showMessage(this.number);
		return this.number;
	}
	public int getDigit(int position)
	{
		return number.charAt(position);
	}
	public int[] getStartingPoint()
	{
		return startingPoint;
	}
	/**
	 * berechnet die Steigung zwischen 2 Punkten
	 * @return
	 */
	public BoundaryArc computeBoundaryArcNumber()
	{
		double[] points=new double[6];
		PathIterator pit=path.getPathIterator(null);
		//pit.next();
		int beginx=(int)points[0];
        int beginy=(int)points[1];

		int oldx=0;
		int oldy=0;
		pit.currentSegment(points);
		try{
		oldx=(int)points[0];
		oldy=(int)points[1];
		}
		catch(NullPointerException e)
		{
			IJ.error("interner Fehler");
		}
		int deltaX;
		int deltaY;
		while(!pit.isDone())
		{
			
			pit.next();
			pit.currentSegment(points);
			 if((int)points[0]!=beginx||(int)points[1]!=beginy)
             {
			int x=(int)points[0];
			int y=(int)points[1];
			deltaX= x-oldx;
			deltaY=y-oldy;
			oldx=x;
			oldy=y;
			
			
		switch(deltaX)
		{
		//Koordinatensystem beachten hier ein hängendes Koordinatensystem
		
		case -1:
			switch(deltaY)
			{
			case -1:
				
				concatNumber(3);
				//IJ.showMessage("3"+" "+number);
				
				break;
			case 0:
				concatNumber(4);
				//IJ.showMessage("4"+" "+number);
				
				break;
			case 1:
				concatNumber(5);
			//IJ.showMessage("5"+" "+number);
				
				break;
			default:
				IJ.error("unvorhergesehenes Ereignis1");
				break;
			}
			break;
		case 0:
			switch(deltaY)
			{
			case -1:
				concatNumber(2);
				
				//IJ.showMessage("2"+" "+number);
				
				break;
			case 1:	
				concatNumber(6);
			
				//IJ.showMessage("6"+" "+number);
				
				break;
			default:
				IJ.error("unvorhergesehenes Ereignis2"+deltaY);
				break;
			}
			break;
		case 1:
			switch(deltaY)
			{
			case -1:
				concatNumber(1);
			//	IJ.showMessage("1"+" "+number);
				
				break;
			case 0:
				concatNumber(0);
				//IJ.showMessage("0"+" "+number);
				
				break;
			case 1:
				concatNumber(7);
			//	IJ.showMessage("7"+" "+number);
				
				break;
			default:
				IJ.error("unvorhergesehenes Ereignis3");
				break;
			}
			break;
		default:
			IJ.error("unvorhergesehenes Ereignis4");
			break;
		}
		
			
		}
		//int[] deltaX=new int[path.npoints];
		//int[] deltaY=new int[path.npoints];
		//IJ.showMessage(p.xpoints[0]+" xkoord "+p.ypoints[0]+" ykoord "+ "letzter: " + p.xpoints[p.npoints-2] +" x "+ p.ypoints[p.npoints-2]+" y ");
		//for(int i=0;i<p.npoints-1;i++)
		//{
		/*
			
				deltaX[i]= p.xpoints[i+1]-p.xpoints[i];
				deltaY[i]=p.ypoints[i+1]-p.ypoints[i];
				switch(deltaX[i])
			{
			case -1:
				switch(deltaY[i])
				{
				case -1:
					
					ba.concatNumber(3);
					//IJ.showMessage("3"+" "+number);
					
					break;
				case 0:
					ba.concatNumber(4);
					//IJ.showMessage("4"+" "+number);
					
					break;
				case 1:
					ba.concatNumber(5);
				//IJ.showMessage("5"+" "+number);
					
					break;
				default:
					IJ.error("unvorhergesehenes Ereignis1");
					break;
				}
				break;
			case 0:
				switch(deltaY[i])
				{
				case -1:
					ba.concatNumber(2);
					
					//IJ.showMessage("2"+" "+number);
					
					break;
				case 1:	
					ba.concatNumber(6);
				
					//IJ.showMessage("6"+" "+number);
					
					break;
				default:
					IJ.error("unvorhergesehenes Ereignis2"+deltaY[i]);
					break;
				}
				break;
			case 1:
				switch(deltaY[i])
				{
				case -1:
					ba.concatNumber(1);
				//	IJ.showMessage("1"+" "+number);
					
					break;
				case 0:
					ba.concatNumber(0);
					//IJ.showMessage("0"+" "+number);
					
					break;
				case 1:
					ba.concatNumber(7);
				//	IJ.showMessage("7"+" "+number);
					
					break;
				default:
					IJ.error("unvorhergesehenes Ereignis3");
					break;
				}
				break;
			default:
				IJ.error("unvorhergesehenes Ereignis4");
				break;
			}
			
				
		//}
		return ba;
*/
		}
return null;}

	
		/*processed=new boolean[imp.getHeight()][imp.getWidth()];
		int i=0;
		int j=0;
		ImageProcessor ip=imp.getProcessor();
		//wenn schwarz dann einfach weiter laufen
		
		while(i<=imp.getHeight()&&j<=imp.getWidth())
		{
			/*if(ip.getPixelValue(i, j)==1)
			{
				if(!processed[i][j])
				{
					processed[i][j]=true;
					//if(/*neuerUrsprung)
					{
					origin[0]=j;
					origin[1]=i;
					
					}
				}
				else
				{
					if(j>=imp.getWidth())
					{
						i++;
						j=0;
					}
					else
					{
					j++;
					}
				}
			
			}
			else
			{
				processed[i][j]=true;
				if(j>=imp.getWidth())
				{
					i++;
					j=0;
				}
				else
				{
				j++;
				}
			}
	
		}*/
	}

