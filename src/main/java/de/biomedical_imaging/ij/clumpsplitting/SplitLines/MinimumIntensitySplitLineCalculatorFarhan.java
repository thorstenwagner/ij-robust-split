package de.biomedical_imaging.ij.clumpsplitting.SplitLines;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

import de.biomedical_imaging.ij.clumpsplitting.Clump;
import de.biomedical_imaging.ij.clumpsplitting.Clump_Splitting;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityRegion;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityRegionAdministration;
import ij.process.AutoThresholder;
import ij.process.ImageProcessor;
import ij.process.AutoThresholder.Method;

public class MinimumIntensitySplitLineCalculatorFarhan implements AbstractSplitLineCalculator
{

	static final int[][] NULLTONINETY={{1,1,1},{0,0,1},{0,0,0}};
	static final int[][] NINETYTOHUNDREDEIGHTY={{1,1,0},{1,0,0},{1,0,0}};
	static final int[][] HUNDREDEIGHTYTOTWOHUNDREDSEVENTY={{0,0,0},{1,0,0},{1,1,1}};
	static final int[][] TWOHUNDREDSEVENTTOTHREEHUNDREDSIXTY={{0,0,1},{0,0,1},{0,1,1}};
	
	
	@Override
	public ArrayList<AbstractSplitLine> calculatePossibleSplitLines(ArrayList<ConcavityRegion> concavityRegionList,
			Clump c, ImageProcessor ip)
	{
		ArrayList<AbstractSplitLine> splitLines=new ArrayList<AbstractSplitLine>();
		Collections.sort(concavityRegionList);
		
			ArrayList<Point2D> points=new ArrayList<Point2D>();
			if(concavityRegionList.size()>0)
			{
			ConcavityRegion cr=concavityRegionList.get(concavityRegionList.size()-1);
			Point2D aktuellerPunkt= cr.getMaxDistCoord();
			System.out.println("Startpunkt: "+aktuellerPunkt.getX()+" "+aktuellerPunkt.getY()+" "+ ip.getPixel((int)aktuellerPunkt.getX(), (int)aktuellerPunkt.getY()));
			points.add(aktuellerPunkt);
			double orientation=cr.getOrientation();
			int[][] filter;
			System.out.println(orientation);
			if(orientation>0&&orientation<=(Math.PI/2))
			{
				filter=MinimumIntensitySplitLineCalculatorFarhan.NULLTONINETY;
				System.out.println("0-90");
			}
			else{
				if(orientation>(Math.PI/2)&&orientation<=(Math.PI))
				{
					filter=MinimumIntensitySplitLineCalculatorFarhan
							.NINETYTOHUNDREDEIGHTY;
					System.out.println("90-180");
					
				}
				else{
					if(orientation>(Math.PI)&&orientation<=((Math.PI)*1.5))
					{
					filter=MinimumIntensitySplitLineCalculatorFarhan.HUNDREDEIGHTYTOTWOHUNDREDSEVENTY;
					System.out.println("180-270");
					
					}
					else{
						filter=MinimumIntensitySplitLineCalculatorFarhan.TWOHUNDREDSEVENTTOTHREEHUNDREDSIXTY;
						System.out.println("270-360");
						
					}
				}
			}
	/*		ip.blurGaussian(2.0);
			ip.autoThreshold();
			ip.erode();
			
			ip.dilate();*/	
			ImageProcessor binary= ip.duplicate();
			AutoThresholder at= new AutoThresholder();
			int[] histogram = binary.getHistogram();
			int threshold=at.getThreshold(Method.RenyiEntropy, histogram);
			
			binary.blurGaussian(1.0);
			binary.threshold(threshold);
		//	binary.autoThreshold();
			if(Clump_Splitting.BACKGROUNDCOLOR==1)
			{
			binary.erode();
			
			binary.dilate();
			} else{
				binary.invert();
				binary.erode();
				
				binary.dilate();
				binary.invert();
				
			}
		
			
//			System.out.println(binary.getPixel((int)aktuellerPunkt.getX(), (int)aktuellerPunkt.getY())+ " "+Clump_Splitting.BACKGROUNDCOLOR);
			int value;
			if(Clump_Splitting.BACKGROUNDCOLOR==1)
			{
				value=255;
			}
			else{
				value =0;
			}
			boolean equals=(binary.getPixel((int)aktuellerPunkt.getX(), (int)aktuellerPunkt.getY())==value);
		//	System.out.println(equals);
			while(!equals&&aktuellerPunkt.getX()>0&&aktuellerPunkt.getY()>0&&aktuellerPunkt.getX()<ip.getWidth()&aktuellerPunkt.getY()<ip.getHeight())
			{
				int min=256;
				Point2D temp=null;
				for(int m=-1;m<=1;m++)
				{
					for(int n=-1;n<=1;n++)
					{
						if(filter[m+1][n+1]==1)
						{
				//			System.out.println("max"+max+"Pixelwert: "+ ip.getPixel((int)aktuellerPunkt.getX()+m, (int)aktuellerPunkt.getY()+n));
							if((orientation>0&&orientation<=(Math.PI/2)&&m==-1 &&n==1)||(orientation>(Math.PI/2)&&orientation<=(Math.PI)&&m==-1 &&n==-1)||(orientation>(Math.PI)&&orientation<=((Math.PI)*1.5)&&m==1 &&n==-1)||(orientation>((Math.PI)*1.5)&&orientation<=((Math.PI)*2)&&m==1 &&n==1))
							{
								System.out.println(ip.getPixel((int)aktuellerPunkt.getX()+m, (int)aktuellerPunkt.getY()+n));
								if(ip.getPixel((int)aktuellerPunkt.getX()+n, (int)aktuellerPunkt.getY()+m)<=min)
								{
								min= ip.getPixel((int)aktuellerPunkt.getX()+n, (int)aktuellerPunkt.getY()+m);
								temp=new Point2D.Double(aktuellerPunkt.getX()+n, aktuellerPunkt.getY()+m);
								}
							}else{
								if(ip.getPixel((int)aktuellerPunkt.getX()+n, (int)aktuellerPunkt.getY()+m)<min)
								{
								min= ip.getPixel((int)aktuellerPunkt.getX()+n, (int)aktuellerPunkt.getY()+m);
								temp=new Point2D.Double(aktuellerPunkt.getX()+n, aktuellerPunkt.getY()+m);
								}
							}
						}
					}
				}
				//ip.autoThreshold();
		/*		if(t<100)
				{
				System.out.println(temp.getX()+" "+ temp.getY()+" "+ip.getPixel((int)temp.getX(), (int)temp.getY())+ " "+ binary.getPixel((int)temp.getX(), (int)temp.getY()));
				t++;
				}*/
				points.add(temp);
				aktuellerPunkt=temp;
		//		ip.drawDot((int)aktuellerPunkt.getX(), (int)aktuellerPunkt.getY());
		//		System.out.println(aktuellerPunkt.getX()+ " "+ aktuellerPunkt.getY());
				equals=(binary.getPixel((int)aktuellerPunkt.getX(), (int)aktuellerPunkt.getY())==value);
			}
			}
			if(points.size()>3)
			{
				if(points.get(points.size()-1).getX()==0||points.get(points.size()-1).getY()==0||points.get(points.size()-1).getX()>=ip.getWidth()||points.get(points.size()-1).getY()>=ip.getHeight()||ConcavityRegionAdministration.allConcavityRegionPoints.contains(points.get(points.size()-1)))
				{
					MaximumMinimumIntensitySplitLine mmis=new MaximumMinimumIntensitySplitLine(points);
					System.out.println(mmis.getStartPoint().getX()+" "+ mmis.getStartPoint().getY()+" "+mmis.getEndPoint().getX()+" "+ mmis.getEndPoint().getY());
			
					splitLines.add(mmis);
				}
			}
		return splitLines;
		}
	

}
