package de.biomedical_imaging.ij.clumpsplitting;


import java.awt.Polygon;

import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.ImageProcessor;
/**
 * klasse vermutlich überflüssig wird später gel
 * @author Louise
 *
 */
public class ConvexHullAdministration_obsolete {
	
	public Polygon computeConvexHull(Polygon p)
	{
		PolygonRoi pr=new PolygonRoi(p,Roi.POLYGON);
		Polygon convexHull=pr.getConvexHull();
		return convexHull;
	}
	public ArrayList<ConvexHull_obsolete> computeConvexHullsForPolygon(ImagePlus imp, Polygon p)
	{
		
		ArrayList<ConvexHull_obsolete> hullList=new ArrayList<ConvexHull_obsolete>();
		Rectangle2D r=p.getBounds2D();
		ImageProcessor i=imp.getProcessor();
		i.drawRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
		/**
		 * obere rechte Koordinate des Rechtecks
		 */
		int x=(int)r.getX();
		int y=(int)r.getY();
		/**
		 * aktueller Punkt von dem aus überprüft wird
		 */
		int xCoord=x;
		int yCoord=y;
		int length=(int)Math.sqrt(r.getWidth()*r.getWidth()+r.getHeight()*r.getHeight());
		IJ.showMessage("Length: "+length+" x "+x+" y " +y+" Width "+r.getWidth()+" Height "+r.getHeight());
		double angle=0;
		do
		{
			/**
			 * Radial grid
			 */
			int newx =  xCoord + (int)(length*Math.cos(angle));
			int newy = yCoord + (int)(length*Math.sin(angle));
			Line2D.Double line=new Line2D.Double(xCoord,yCoord,newx,newy);
			Set<Point2D> intersections=null;
			
			try 
			{
				intersections = getIntersections(p, line);
				/*for(Point2D po:intersections)
				{
					IJ.showMessage("x:"+po.getX()+"y:"+po.getY());
				}*/
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
			if(!intersections.isEmpty())
			{ 
				Iterator<Point2D> it=intersections.iterator();
				/*erster Punkt
				 * 
				 */
				Point2D point= it.next();
				/**
				 * erster Punkt wird gesetzt (Startpunkt)
				 * überprüfung bei der do while überprüfen
				 * 
				 */
				
				int pointx=(int)point.getX();
				int pointy=(int)point.getY();
				xCoord=pointx;
				yCoord=pointy;
				x=pointx;
				y=pointy;
				int minx=(int)Math.abs(xCoord-(int)point.getX());
				int miny=(int)Math.abs(yCoord-(int)point.getY());
				int diff=(int)(r.getWidth()*r.getHeight());
				if(minx+miny!=0)
				{
					 diff=minx+miny;
				}
				
				//int diff=minx+miny;
				
				//Problem: diff wird bei erstem Durchlauf 0!!!
				while(it.hasNext())
				{
					point=it.next();
					minx=(int)Math.abs(xCoord-point.getX());
					miny=(int)Math.abs(yCoord-point.getY());
					
					if(diff>minx+miny&&minx+miny!=0)
					{
						diff=minx+miny;
						pointx=(int)point.getX();
						pointy=(int)point.getY();
						
					}
				}
				
				ConvexHull_obsolete convexHull=new ConvexHull_obsolete(/*xCoord,yCoord,pointx,pointy*/);
				xCoord=pointx;
				yCoord=pointy;
				angle=0;
				hullList.add(convexHull);
				}
			
				angle+=0.1;
			
				
			
			
			IJ.showMessage(angle+"a");
			/**
			 * abfrage falsch!!!
			 */
		}while(angle<360&&xCoord!=x&&yCoord!=y);
			
		return hullList;
		
	}
	


    public static Set<Point2D> getIntersections( Polygon poly, Line2D.Double line) throws Exception {

    	PathIterator polyIt = poly.getPathIterator(null); //Getting an iterator along the polygon path
        double[] coords = new double[6]; //Double array with length 6 needed by iterator
        double[] firstCoords = new double[2]; //First point (needed for closing polygon path)
        double[] previousPointCoords = new double[2]; //Previously visited point
        Set<Point2D> intersections = new HashSet<Point2D>();
        polyIt.currentSegment(firstCoords); //Getting the first coordinate pair
        previousPointCoords[0] = firstCoords[0]; //Priming the previous coordinate pair
        previousPointCoords[1] = firstCoords[1];
        polyIt.next();
        while(!polyIt.isDone()) {
             int type = polyIt.currentSegment(coords);
            switch(type) {
                case PathIterator.SEG_LINETO : {
                    final Line2D.Double currentLine = new Line2D.Double(previousPointCoords[0], previousPointCoords[1], coords[0], coords[1]);
                    if(currentLine.intersectsLine(line))
                        intersections.add(getIntersection(currentLine, line));
                    previousPointCoords[0] = coords[0];
                    previousPointCoords[1] = coords[1];
                    break;
                }
                case PathIterator.SEG_CLOSE : {
                    final Line2D.Double currentLine = new Line2D.Double(coords[0], coords[1], firstCoords[0], firstCoords[1]);
                    if(currentLine.intersectsLine(line))
                        intersections.add(getIntersection(currentLine, line));
                    break;
                }
                default : {
                    throw new Exception("Unsupported PathIterator segment type.");
                }
            }
            polyIt.next();
        }
        return intersections;

    }

    public static Point2D getIntersection(Line2D.Double line1, Line2D.Double line2) {

        double x1,y1, x2,y2, x3,y3, x4,y4;
        x1 = line1.x1; y1 = line1.y1; x2 = line1.x2; y2 = line1.y2;
        x3 = line2.x1; y3 = line2.y1; x4 = line2.x2; y4 = line2.y2;
         double x = (
                (x2 - x1)*(x3*y4 - x4*y3) - (x4 - x3)*(x1*y2 - x2*y1)
                ) /
                (
                (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4)
                );
         double y = (
                (y3 - y4)*(x1*y2 - x2*y1) - (y1 - y2)*(x3*y4 - x4*y3)
                ) /
                (
                (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4)
                );

        return new Point2D.Double(x, y);

    }
	
	
//	static boolean found=false;
	/*
	public SortedSet<RationalNumber> getAllPointsOfACoordinateSystemInAngleSort(int width,int height)
	{
		SortedSet<RationalNumber> soSe=new TreeSet<RationalNumber>();
		
		for(int i=width-1;i>=0;i--)
		{
			for(int j=height-1;j>=0;j--)
			{
				soSe.add(new RationalNumber(i,j,height));
			}
		}
		return soSe;
	}
	
public ArrayList<ConvexHull> computeConvexHullsForPolygon(ImagePlus imp,Polygon p)
{

	Rectangle r=p.getBounds();
	int x=(int)r.getX();
	int y=(int)r.getY();
	int pointX=x;
	int pointY=y;
	int width=(int)r.getWidth();
	int height=(int)r.getHeight();
	do{
		
	SortedSet<RationalNumber> soSe=this.getAllPointsOfACoordinateSystemInAngleSort(width, height);
	Iterator<RationalNumber> it=soSe.iterator();
	while(it.hasNext())
	{
		RationalNumber o=it.next();
		Line2D line=new Line2D.Double(pointX,pointY,o.getX()+pointX,o.getY()+pointY);
		if(/*wenn sich die Linien Schneiden)

	/*	{
			//Schnittpunkt= x,y 
			
		}
		
	}
	}while(x!=0&&y!=0);
	
	Set<Point> set=new TreeSet<Point>();
	for(int i=0; i<p.npoints;i++)
	{
		Point temp=new Point(p.xpoints[i],p.ypoints[i]);
		set.add(temp);
	}
	do
	{
	Line2D.Double l=new Line2D.Double(pointX,pointY,width,pointY);
	
	
	

	
	}while(x!=pointX&& y!=pointY);
	/*
	 * //ich brauche 2 koordinaten für die convexHull
	/*IJ.showMessage("computeConvexHullsForPolygon");
	int startCoord[]=new int[2];
	/**
	 * startCoord=Punkt von dem wir anfangen d.h. Punkt um den der Winkel dreht
	 
	startCoord[0]=0;
	startCoord[1]=0;
	/**
	 * Dreh Winkel Alpha
	 
	double alpha=0;
	/**
	 * länge der Hypothenuse
	 * 
	 *
	 
	int hypLen=1;
	int coord[]=new int[2];
	/**
	 * coord zu überprüfende Koordinate x,y
	 
	coord[1]=0;
	coord[0]=0;
	int firstPoint[]=new int[2];
	
	ConvexHull convexHull=null;
	ArrayList<ConvexHull> hullList=new ArrayList<ConvexHull>();
	while(alpha<360 /*&& !found)
	{//IJ.showMessage("alpha: "+alpha+" hypLen:"+ hypLen );
		//found=false;
		hypLen=1;
	
		/**
		 
		 * überprüfung ob Index out of Bounds 
		 
		/**
		 * *TODO* evtl. vorziehen der Variablen definition????
		 
		while((startCoord[0]+Math.cos(alpha)*hypLen)<imp.getWidth()&&(startCoord[1]+Math.sin(alpha))<imp.getHeight()/*&&!found&&(startCoord[1]+Math.sin(alpha))>=0 &&(startCoord[0]+Math.cos(alpha)*hypLen)>=0)
		{//IJ.showMessage(startCoord[0]+" "+startCoord[1]);
			/**
			 * ins minus laufen
			 
			/**
			 * x= Ankathete
			 * y=Gegenkathete
			 
			//IJ.showMessage("cos("+alpha+")="+Math.cos(alpha));
		//	IJ.showMessage("Startcoord" +startCoord[0]+ " "+ startCoord[1]);
			coord[0]=startCoord[0]+(int)(Math.cos(alpha)*hypLen);
			
			//*TODO*int rundung richtig???
			
			coord[1]=startCoord[1]+(int)(Math.sin(alpha)*hypLen);
		IJ.showMessage(coord[0]+"," +coord[1]);
			if(p.contains(coord[0], coord[1])&&startCoord[0]!=coord[0]&&startCoord[1]!=coord[1])
				
				//*TODO* geht das so mit contains... könnte auch in einem Blob sein??
				// Geht vermutlich nicht evtl arrays xpoints /ypoints in set und dann in
				
			{
				
				//*TODO* Überprüfung Anfangspunkt wieder erreicht???????
				if(convexHull==null)
				{
				/**
				 * punkt des Polygons wurde gefunden ist startpunkt-> Algorithmus erneut vom Startpunkt ausführen
				 
					startCoord[0]=coord[0];
					startCoord[1]=coord[1];
					
					convexHull=new ConvexHull(startCoord);
					hullList.add(convexHull);
					alpha=0;
					hypLen=1;
					firstPoint[0]=coord[0];
					firstPoint[1]=coord[1];
				}
				else
				{
						if(convexHull.getStartingPoint()!=coord&&firstPoint[0]!=coord[0]&&firstPoint[1]!=coord[1])
						{
					
							startCoord[0]=coord[0];
							startCoord[1]=coord[1];

							convexHull.setEndPoint(startCoord);
							convexHull=new ConvexHull(startCoord);
							hullList.add(convexHull);
							alpha=0;
							hypLen=1;
						}
						else
						{
							if(firstPoint[1]!=coord[1]&&firstPoint[0]!=coord[0])
							{
								convexHull.setEndPoint(coord);
								return hullList;
							}
						}
				}
			}
			else
			{
				hypLen++;
			}
		}
		alpha+=0.1;//*TODO*Schrittweite? Diese ist mist müsste evtl. von größe des Bildes abhängen
	}
	return null;
	//*TODO*/
//}
}
