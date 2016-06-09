/*
The MIT License (MIT)

Copyright (c) 2016 Louise Bloch (louise.bloch001@stud.fh-dortmund.de), Thorsten Wagner (wagner@b
iomedical-imaging.de)

Permission is hereby granted, free of charge, to any person obtaining a
copy
of this software and associated documentation files (the "Software"),
to deal
in the Software without restriction, including without limitation the
rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
IN THE
SOFTWARE.
*/

package de.biomedical_imaging.ij.clumpsplitting.SplitLines;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.vecmath.Vector2d;

import de.biomedical_imaging.ij.clumpsplitting.Clump;
import de.biomedical_imaging.ij.clumpsplitting.Clump_Splitting;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityRegion;
import ij.process.ImageProcessor;



/**
 * calculates Straight SplitLines
 * 
 * @author Louise
 *
 */

public class StraightSplitLineCalculator implements AbstractSplitLineCalculator
{

	
	/*
	public static ArrayList<StraightSplitLineBetweenTwoConcavityRegions> splitLinesTwoConcavityRegions = new ArrayList<StraightSplitLineBetweenTwoConcavityRegions>();
	public static ArrayList<StraightSplitLineBetweenConcavityRegionAndPoint> splitLinesConcavityRegionPoint = new ArrayList<StraightSplitLineBetweenConcavityRegionAndPoint>();
*/
/*	private JWindow toolTip=new JWindow();
    private JTextArea label=new JTextArea();
    */
    /*
    private void manageSplitLineInformation()
    {
    	/*
    	for(StraightSplitLineBetweenTwoConcavityRegions sslbcr:splitLinesTwoConcavityRegions)
        {
        		String st=drawInformationAboutSplitLineTwoConcavityRegions(sslbcr);
        	
        }
        for(StraightSplitLineBetweenConcavityRegionAndPoint sslbcrap:splitLinesConcavityRegionPoint)
        {

        		
        		String st=drawInformationAboutSplitLineConcavityRegionPoint(sslbcrap);
        	
          }

    	*/
    /*	label.setBackground(Color.lightGray);
        WindowManager.getCurrentImage().getWindow().getCanvas().addMouseMotionListener(new MouseMotionListener()
        		{

					@Override
					public void mouseDragged(MouseEvent e)
					{
						// TODO Auto-generated method stub
						
					}

					@Override
					public void mouseMoved(MouseEvent e)
					{
						 int xL=e.getX();
			            	int yL=e.getY();
			            	
						Line polygonRoi = new Line(xL,yL,xL,yL);
						polygonRoi.setStrokeWidth(5);
					    polygonRoi.setStrokeColor(Color.BLUE);
						
					    // Roi.setColor(Color.red);
					    Clump.overlaySplitLines.add(polygonRoi);

					    Overlay o =new Overlay();
					    for(Roi overlay:Clump.overlaySplitLines)
		            	{
		            		o.addElement(overlay);
		            	}

						Clump_Splitting.imp.setOverlay(o);
						System.out.println(splitLinesTwoConcavityRegions.size()+ " "+  splitLinesConcavityRegionPoint.size());
						int x = e.getXOnScreen()+10;
			            int y = e.getYOnScreen()+10;

			         //   int xL=e.getX();
		            //	int yL=e.getY();
		            	
			            for(StraightSplitLineBetweenTwoConcavityRegions sslbcr:splitLinesTwoConcavityRegions)
			            {
			            	Point2D.Double point=new Point2D.Double(xL, yL);
			            	System.out.println(sslbcr.contains(point));
			            	if(sslbcr.contains(point))
			            	{
			            		System.out.println("ahhhhh");
			            		String st=drawInformationAboutSplitLineTwoConcavityRegions(sslbcr);
			            		label.setText(st);
			            	}
			            }
			            for(StraightSplitLineBetweenConcavityRegionAndPoint sslbcrap:splitLinesConcavityRegionPoint)
			            {
			  
			            	Point2D.Double point=new Point2D.Double(xL, yL);
			            	System.out.println(sslbcrap.contains(point));
			            	
			            	if(sslbcrap.contains(point))
			            	{
			            		System.out.println("teeest");
			            		
			            		String st=drawInformationAboutSplitLineConcavityRegionPoint(sslbcrap);
			            		label.setText(st);
			            	}
			            }
			            
			            toolTip.setLocation(x,y);
			            toolTip.setVisible(true);
			          System.out.println("moving");
			            toolTip.repaint();
						// TODO Auto-generated method stub
						
					}
        	
        		});
		WindowManager.getCurrentImage().getWindow().getCanvas().addMouseListener(new MouseListener()
				{

			
					@Override
					public void mouseClicked(MouseEvent e)
					{
					}

					@Override
					public void mousePressed(MouseEvent e)
					{
						
					}

					@Override
					public void mouseReleased(MouseEvent e)
					{
						// TODO Auto-generated method stub
						
					}

					@Override
					public void mouseEntered(MouseEvent e)
					{

						toolTip.add(label);
						
						toolTip.pack();
				//		Component c = (Component)e.getSource();
					//	 Point p= e.getLocationOnScreen();
				           
			            int x = e.getXOnScreen()+10;
			            int y = e.getYOnScreen()+10;
			            
			            
			            toolTip.setLocation(x,y);
			            toolTip.setVisible(true);
			    //        System.out.println("test");	

						//i++;
					//	System.out.println("entered"+ i);
					}

					@Override
					public void mouseExited(MouseEvent e)
					{
					//	toolTip.setVisible(false);
						}
			
				});
				}
*/

   /* 
	private String drawInformationAboutSplitLineTwoConcavityRegions(StraightSplitLineBetweenTwoConcavityRegions sl)
	{
		/*int hoehe=(int)cI.getMaxDist();
		int x=(int) (cI.getMaxDistCoord().getX())-hoehe;

		int y=(int) (cI.getMaxDistCoord().getY())-hoehe;
		int width=2*hoehe;
		int height=2*hoehe;
		
		int gegenkatheteStart=Math.abs((int)cI.getEndY()-(int)cI.getMaxDistCoord().getY());
		int ankatheteStart=Math.abs((int)cI.getEndX()-(int)cI.getMaxDistCoord().getX());
		double angleStart=Math.atan(gegenkatheteStart/ankatheteStart);
		angleStart=(360/(2*Math.PI))*angleStart;
		int gegenkatheteEnd=Math.abs((int)cI.getStartY()-(int)cI.getMaxDistCoord().getY());
		int ankatheteEnd=Math.abs((int)cI.getStartX()-(int)cI.getMaxDistCoord().getX());
		double angleEnd=Math.atan(gegenkatheteEnd/ankatheteEnd);
		angleEnd=(360/(2*Math.PI))*angleEnd;
		System.out.println(angleStart+ " "+ angleEnd);
		Shape angle= new Arc2D.Double(x,y,width,height,angleEnd,angleStart, Arc2D.PIE);
		Roi region= new ShapeRoi(angle);
		region.setStrokeWidth(1);
		region.setStrokeColor(Color.orange);
		double cangle= (360/(2*Math.PI))*this.concavityAngle;
		cangle=Math.round(cangle);
		Roi text= new TextRoi(cI.getMaxDistCoord().getX(),cI.getMaxDistCoord().getY(),cangle+"");
		text.setStrokeColor(Color.orange);
		Clump.overlayAngleCtrl.add(region);
		Clump.overlayAngleCtrl.add(text);*/
		
		//Clump.overlaySplitLines.clear();
	/*	Line polygonRoi = new Line((int) sl.getCI().getMaxDistCoord().getX(), (int) sl.getCI().getMaxDistCoord().getY(), (int) sl.getCJ().getMaxDistCoord().getX(),
				(int) sl.getCJ().getMaxDistCoord().getY());
		polygonRoi.setStrokeWidth(5);
	    polygonRoi.setStrokeColor(Color.BLUE);
		
	    // Roi.setColor(Color.red);
	    Clump.overlayAngleCtrl.add(polygonRoi);

	    double ccAlignment= ((360)/(2*Math.PI))*sl.getConcavityConcavityAlignment();
		double clAlignment= ((360)/(2*Math.PI))*sl.getConcavityLineAlignment();
		
		String st= "Saliency: "+Math.round(sl.getSaliency()) +"\n"+" Concavity-Concavity-Alignment: "+ Math.round(ccAlignment)+"\n"+" Concavity-Line-Alignment: "+ Math.round(clAlignment);
		return null;*/
	/*	return "";
		}*/

		public ArrayList<AbstractSplitLine> calculatePossibleSplitLines(ArrayList<ConcavityRegion> concavityRegionList,
			Clump c, ImageProcessor ip)
	{
		StraightSplitLine bestSplitLine=this.calculatePossibleStraightSplitLines(concavityRegionList, c);
		ArrayList<AbstractSplitLine> possibleSplitLines=new ArrayList<AbstractSplitLine>();
		
		if(Clump_Splitting.SPLITLINETYPE==0)
		{
		possibleSplitLines.add(bestSplitLine);
		}
		else
		{
			if(Clump_Splitting.SPLITLINETYPE==1)
			{
				if(bestSplitLine!=null)
				{
				AbstractSplitLineCalculator mmislc = new MaximumIntensitySplitLineCalculator(bestSplitLine.getStartPoint(),bestSplitLine.getEndPoint());
				possibleSplitLines = mmislc.calculatePossibleSplitLines(concavityRegionList, c,ip);
				}
			}else{
			if(Clump_Splitting.SPLITLINETYPE==3)
			{
			//	System.out.println(bestSplitLine);
				if(bestSplitLine!=null)
				{
				GeodesicDistanceSplitLineCalculator gdslc= new GeodesicDistanceSplitLineCalculator(bestSplitLine.getStartPoint(),bestSplitLine.getEndPoint());
				possibleSplitLines=gdslc.calculatePossibleSplitLines(concavityRegionList, c, ip);
				}
				}
			else{
				if(Clump_Splitting.SPLITLINETYPE==2)
				{
					if(bestSplitLine!=null)
					{
					AbstractSplitLineCalculator mmislc = new MinimumIntensitySplitLineCalculator(bestSplitLine.getStartPoint(),bestSplitLine.getEndPoint());
					possibleSplitLines = mmislc.calculatePossibleSplitLines(concavityRegionList, c,ip);
					}
				
				}
			}
		}}
		return possibleSplitLines;
	}
	private StraightSplitLine calculatePossibleStraightSplitLines(ArrayList<ConcavityRegion> concavityRegionList,
			Clump c)
	{
	
		ArrayList<StraightSplitLine> possibleSplitLines = this
				.computeStraigthSplitLineBetweenTwoConcavityRegions(concavityRegionList);
		/*TODO*/
		StraightSplitLine bestSplitLine=null;
		
			if(possibleSplitLines.size() >=1)
			{
				double maxChi=0;
				for(StraightSplitLine ssl:possibleSplitLines)
				{
					if(ssl instanceof StraightSplitLineBetweenTwoConcavityRegions)
					{
						StraightSplitLineBetweenTwoConcavityRegions sslbrcr= (StraightSplitLineBetweenTwoConcavityRegions)ssl;
						double chi=sslbrcr.getChi();
						if(chi>maxChi)
						{
							maxChi=chi;
							bestSplitLine=sslbrcr;
						}
					}
				}
			/*	if(maxChi>0.5)
				{
			//		return bestSplitLine;
				}
				else{
					possibleSplitLines.clear();
				}*/
			}
			if (possibleSplitLines.size() == 0)
			{
				ArrayList<StraightSplitLine> splitLineList=this.computeStraightSplitLineBetweenConcavityRegionAndPoint(concavityRegionList, c);
				if(splitLineList.size()>0)
				{
				bestSplitLine =splitLineList.get(0);
			//	bestSplitLine.drawInformation();
			//	return bestSplitLine;
				}
				
				
			}
		//	manageSplitLineInformation();
			return bestSplitLine;
		}
	
	

	/**
	 * computes all possible Straight SplitLines between two concavityRegions by
	 * caluculating the needed parameters and evaluating them
	 * 
	 * @param concavityRegionList
	 *            List with all concavityRegions of a Clump
	 * @return List including all possible SplitLines for the Clump if the List
	 *         is empty it would be tried if there are any possible SplitLines
	 *         between ConcavityRegion and BoundaryPoint
	 */

	private ArrayList<StraightSplitLine> computeStraigthSplitLineBetweenTwoConcavityRegions(
			ArrayList<ConcavityRegion> concavityRegionList)
	{
		
	
		ArrayList<StraightSplitLine> possibleSplitLines = new ArrayList<StraightSplitLine>();
		for (int i = 0; i < concavityRegionList.size() -1; i++)
		{
			ConcavityRegion cOne = concavityRegionList.get(i);
			for (int j = i + 1; j < concavityRegionList.size(); j++)
			{

				ConcavityRegion cTwo = concavityRegionList.get(j);

				if (!cOne.equals(cTwo))
				{
					double maxDistSum=cOne.getMaxDist()+cTwo.getMaxDist();
					maxDistSum=maxDistSum*1000;
					maxDistSum=Math.round(maxDistSum);
					maxDistSum=maxDistSum/1000;
					double distance= cOne.getMaxDistCoord().distance(cTwo.getMaxDistCoord());
					distance= distance*1000;
					distance= Math.round(distance);
					distance= distance/1000;
					String str= cOne.getMaxDistCoord().getX()+", "+ cOne.getMaxDistCoord().getY()+", "+cTwo.getMaxDistCoord().getX()+", "+cTwo.getMaxDistCoord().getY()+", "+maxDistSum+ ", "+ distance;
					try
					{
				//		System.out.println(str);
						Clump_Splitting.bw.write("\n");
						Clump_Splitting.bw.write(str);
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					double chi=this.computeChi(cOne,cTwo);
					if(chi>0.5)
					{
						if(chi>0.8)
						{
							double saliency = this.computeSaliency(cOne, cTwo);
							//IJ.log(saliency+"");
							double concavityConcavityAlignment = this.computeConcavityConcavityAlignment(cOne, cTwo);
							double concavityLineAlignment = this.computeConcavityLineAlignment(cOne, cTwo);
							
							possibleSplitLines.clear();
							StraightSplitLineBetweenTwoConcavityRegions splitLineAll = new StraightSplitLineBetweenTwoConcavityRegions(
									cOne, cTwo, saliency, concavityConcavityAlignment, concavityLineAlignment,chi);
							try
							{
								Clump_Splitting.bw.write(", Trennungslinie");
							} catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							possibleSplitLines.add(splitLineAll);
							return possibleSplitLines;
						}
					double saliency = this.computeSaliency(cOne, cTwo);
					//IJ.log(saliency+"");
					if (saliency > Clump_Splitting.SALIENCY_THRESHOLD)
					{
						//IJ.log("Nicht aussortiert");
						double concavityConcavityAlignment = this.computeConcavityConcavityAlignment(cOne, cTwo);
						
						if (concavityConcavityAlignment < Clump_Splitting.CONCAVITYCONCAVITY_THRESHOLD)
						{
							double concavityLineAlignment = this.computeConcavityLineAlignment(cOne, cTwo);
							
							if (concavityLineAlignment < Clump_Splitting.CONCAVITYLINE_THRESHOLD)
							{
								try
								{
									Clump_Splitting.bw.write(", Trennungslinie");
								} catch (IOException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								StraightSplitLineBetweenTwoConcavityRegions splitLineAll = new StraightSplitLineBetweenTwoConcavityRegions(
										cOne, cTwo, saliency, concavityConcavityAlignment, concavityLineAlignment,chi);
								
								possibleSplitLines.add(splitLineAll);
							}
						}
					}

				}
					}
			}
		}
		
		return possibleSplitLines;

	}

	private double computeChi(ConcavityRegion cI, ConcavityRegion cJ)
	{
		double distance;
		Point2D pOne = cI.getMaxDistCoord();
		Point2D pTwo = cJ.getMaxDistCoord();
		double distX;
		double distY;
		distX = Math.abs(pOne.getX() - pTwo.getX());
		distY = Math.abs(pOne.getY() - pTwo.getY());
		distance = Math.sqrt((distX * distX) + (distY * distY));
		double chi=((Clump_Splitting.C1*cI.getMaxDist()+Clump_Splitting.C1*cJ.getMaxDist()+Clump_Splitting.C2)/(distance+Clump_Splitting.C1*cI.getMaxDist()+Clump_Splitting.C1*cJ.getMaxDist()+Clump_Splitting.C2));
	//	IJ.log(chi+" ");
		return chi;
	}
	/**
	 * Computes a SplitLine Between a concavityRegion and a point on the
	 * Boundary therefor it needs the ConcavityRegion with the largest and the
	 * second largest ConcavityDepth of the Clump, the Concavity Region with the
	 * largest ConcavityDepth is set as the ConcavityRegion to Split the Clump
	 * and the ConcavityRegion with the second largest ConcavityDepth is used as
	 * reference value
	 * 
	 * @param concavityRegionList
	 *            List with all concavityRegions of the Clump
	 * @param c
	 *            Clump to get ConcavityRegions with largest and second largest
	 *            ConcavityDepth
	 * @param possibleSplitLines
	 *            ArrayList with all SplitLines has to be empty to get into this
	 *            method
	 * @return List of SplitLines has only one Element if this method is used
	 */
	private ArrayList<StraightSplitLine> computeStraightSplitLineBetweenConcavityRegionAndPoint(
			ArrayList<ConcavityRegion> concavityRegionList, Clump c)
	{
		ArrayList<StraightSplitLine> liste=new ArrayList<StraightSplitLine>();
		if (concavityRegionList.size() > 0)
		{

			ConcavityRegion concavityRegion = c.getRegionOfMaxConcavityDepth();
			double concavityAngle = this.computeConcavityAngle(concavityRegion);
			double concavityRatio = this.computeConcavityRatio(c, concavityRegion);
			
			StraightSplitLineBetweenConcavityRegionAndPoint ssl = this
					.computeSplitLineBetweenConcavityPointAndPoint(concavityRegion, c, concavityAngle,
							concavityRatio);
			if (concavityAngle < Clump_Splitting.CONCAVITYANGLE_THRESHOLD)
			{
				if (concavityRatio > Clump_Splitting.CONCAVITYRATIO_THRESHOLD)
				{
					liste.add(ssl);
				}
			}
		}
		return liste;
	}

	/**
	 * computes the Saliency of the ConcavityRegion used for
	 * SplitLinesBetweenTwoConcavityRegions. SALIENCY evaluates, if the
	 * concavityRegion has a large Concaveness and the possible Line is short
	 * enough.
	 * 
	 * @param cI
	 *            first ConcavityRegion. The possible SplitLine is computed for
	 *            the ConcavityPoint of this ConcavityRegion
	 * @param cJ
	 *            second ConcavityRegion. The possible SplitLine is computed for
	 *            the ConcavityPoint of this ConcavityRegion
	 * @return value of saliency
	 */
	private double computeSaliency(ConcavityRegion cI, ConcavityRegion cJ)
	{
		double minCDi = 0;
		if (cI.getMaxDist() > cJ.getMaxDist())
		{
			minCDi = cJ.getMaxDist();
		} else
		{
			minCDi = cI.getMaxDist();
		}
		double distance;
		double saliency = 0;
		Point2D pOne = cI.getMaxDistCoord();
		Point2D pTwo = cJ.getMaxDistCoord();
		double distX;
		double distY;
		distX = Math.abs(pOne.getX() - pTwo.getX());
		distY = Math.abs(pOne.getY() - pTwo.getY());
		distance = Math.sqrt((distX * distX) + (distY * distY));
		if ((minCDi + distance) > 0)
		{
			saliency = minCDi / (minCDi + distance);
		}
		return saliency;

	}

	/**
	 * computes the Vector vi of a ConcavityRegion. The Vector2d vi is a Vector
	 * with the direction from the middelpoint of the convexhull to the
	 * ConcavityPoint
	 * 
	 * @param cI
	 *            the concavityRegion of Interest
	 * @return the Vector between the middelpoint of the convexhull and the
	 *         concavityPoint of the ConcavityRegion
	 */

	private Vector2d computeVi(ConcavityRegion cI)
	{
		Point2D midPointI = cI.getMidPointOfConvexHull();
		Point2D maxPointI = cI.getMaxDistCoord();
		double xPointDistOne = maxPointI.getX() - midPointI.getX();
		double yPointDistOne = maxPointI.getY() - midPointI.getY();
		Vector2d vi = new Vector2d(xPointDistOne, yPointDistOne);
		return vi;
	}

	/**
	 * computes the concavityConcavityAlignment. used for
	 * SplitLinesBetweenTwoConcavityRegions. CONCAVITYCONCAVITYALIGNMENT
	 * evaluates the angle between the concavityRegionorientations.
	 * 
	 * @param cI
	 *            first ConcavityRegion. The possible SplitLine is computed for
	 *            the ConcavityPoint of this ConcavityRegion
	 * @param cJ
	 *            second ConcavityRegion. The possible SplitLine is computed for
	 *            the ConcavityPoint of this ConcavityRegion
	 * @return value of concavityConcavityAlignment
	 */
	private double computeConcavityConcavityAlignment(ConcavityRegion cI, ConcavityRegion cJ)
	{
		double concavityConcavityAlignment = 0;
		Vector2d vI = this.computeVi(cI);
		Vector2d vJ = this.computeVi(cJ);
		vI.normalize();
		vJ.normalize();

		concavityConcavityAlignment = Math.PI - Math.acos(vI.dot(vJ));
		return concavityConcavityAlignment;

	}

	/**
	 * computes the ConcavityLineAlignment. used for
	 * SplitLinesBetweenTwoConcavityRegions. CONCAVITYLINEALIGNMENT evaluates
	 * the largest angle between the concavityRegion Orientation and the
	 * Possible Splitline.
	 * 
	 * @param cI
	 *            first ConcavityRegion. The possible SplitLine is computed for
	 *            the ConcavityPoint of this ConcavityRegion
	 * @param cJ
	 *            second ConcavityRegion. The possible SplitLine is computed for
	 *            the ConcavityPoint of this ConcavityRegion
	 * @return value of concavityLineAlignment
	 */

	private double computeConcavityLineAlignment(ConcavityRegion cI, ConcavityRegion cJ)
	{
		double concavityLineAlignment = 0;
		Point2D maxPointOne = cI.getMaxDistCoord();
		Point2D maxPointTwo = cJ.getMaxDistCoord();
		double xDist = maxPointTwo.getX() - maxPointOne.getX();
		double yDist = maxPointTwo.getY() - maxPointOne.getY();

		Vector2d uij = new Vector2d(xDist, yDist);
		Vector2d vOne = this.computeVi(cI);
		Vector2d vTwo = this.computeVi(cJ);
		uij.normalize();
		vOne.normalize();
		vTwo.normalize();
		Vector2d muij = new Vector2d(-uij.getX(), -uij.getY());

		double phiOne = Math.acos(vOne.dot(uij));
		double phiTwo = Math.acos(vTwo.dot(muij));
		if (phiOne > phiTwo)
		{
			concavityLineAlignment = phiOne;
		} else
		{
			concavityLineAlignment = phiTwo;
		}
		return concavityLineAlignment;
	}

	/**
	 * computes the ConcavityAngle. used for
	 * SplitLinesBetweenConcavityRegionAndPoint. angle between startPoint of the
	 * concavityRegion, endPoint of the concavityRegion and the Point with the
	 * largest concavityDepth of the concavityRegion.
	 * 
	 * @param cI
	 *            The possible SplitLine is computed for the ConcavityPoint of
	 *            this ConcavityRegion. The concavityRegion cI should be the
	 *            ConcavityRegion with the largest concavityDepth
	 * @return value of concavityAngle
	 */
	private double computeConcavityAngle(ConcavityRegion cI)
	{
		Point2D.Double a = new Point2D.Double(cI.getStartX(), cI.getStartY());
		Point2D.Double b = new Point2D.Double(cI.getEndX(), cI.getEndY());
		Point2D c = cI.getMaxDistCoord();

		double clength = Math
				.sqrt((b.getX() - a.getX()) * (b.getX() - a.getX()) + (b.getY() - a.getY()) * (b.getY() - a.getY()));
		double alength = Math
				.sqrt((b.getX() - c.getX()) * (b.getX() - c.getX()) + (b.getY() - c.getY()) * (b.getY() - c.getY()));
		double blength = Math
				.sqrt((c.getX() - a.getX()) * (c.getX() - a.getX()) + (c.getY() - a.getY()) * (c.getY() - a.getY()));

		double gamma = Math
				.acos(((clength * clength) - (alength * alength) - (blength * blength)) / (-2 * Math.abs(alength) * Math.abs(blength)));
		return gamma;
	}

	/**
	 * computes the concavityRatio. used for
	 * SplitLinesBetweenConcavityRegionAndPoint. ratio between the actual
	 * largest concavityDepth and the second largest concavityDepth.
	 * 
	 * @param c
	 *            the actual Clump is needed to get the ConcavityRegions with
	 *            the largest and the second largest ConcavityDepth
	 * @param cI
	 *            The possible SplitLine is computed for the ConcavityPoint of
	 *            this ConcavityRegion. The concavityRegion cI should be the
	 *            ConcavityRegion with the largest concavityDepth
	 * @return value of concavityRatio
	 */
	private double computeConcavityRatio(Clump c, ConcavityRegion cI)
	{
		double concavityRatio;
		concavityRatio = ((cI.getMaxDist()) / (c.getSecondMaxConcavityRegionDepth()));
		return concavityRatio;
	}

	/**
	 * computes the SplitLine between a concavityRegion and a Point
	 * 
	 * @param cI
	 *            The possible SplitLine is computed for the ConcavityPoint of
	 *            this ConcavityRegion. The concavityRegion cI should be the
	 *            ConcavityRegion with the largest concavityDepth
	 * @param c
	 *            the actual Clump is needed to get the ConcavityRegions with
	 *            the largest and the second largest ConcavityDepth
	 * @param concavityAngle
	 *            the concavityAngle computed for the concavityRegion
	 * @param concavityRatio
	 *            the concavityRatio computed for the concavityRegion
	 * @return Straight SplitLine for the concavityRegion
	 */
	private StraightSplitLineBetweenConcavityRegionAndPoint computeSplitLineBetweenConcavityPointAndPoint(
			ConcavityRegion cI, Clump c, double concavityAngle, double concavityRatio)
	{
		Point2D midPoint = cI.getMidPointOfConvexHull();
		Point2D concavityPoint = cI.getMaxDistCoord();
		Line2D.Double line = new Line2D.Double(midPoint, concavityPoint);
		Polygon p = c.getBoundary();
		HashSet<Point2D.Double> test=new HashSet<Point2D.Double>();
		for(int i=0;i<p.npoints;i++)
		{
			test.add(new Point2D.Double(p.xpoints[i], p.ypoints[i]));
		}
		test.removeAll(cI.getBoundaryPointList());
		
		double minDist = line.ptLineDist(p.xpoints[0], p.ypoints[0]);
	//	int indexMinDist = 0;
		double dist = 0;
		java.util.Iterator<Point2D.Double> it=test.iterator();
		Point2D.Double minDistPoint=null;
		while (it.hasNext())
		{
			Point2D.Double point=it.next();
			if (point.getX() != concavityPoint.getX() && point.getY() != concavityPoint.getY())
			{
				dist = line.ptLineDist(point);
				if (dist < minDist)
				{
					
					minDist = dist;
					minDistPoint=point;
				//	indexMinDist = i;
				}
			}
		}
		StraightSplitLineBetweenConcavityRegionAndPoint s = new StraightSplitLineBetweenConcavityRegionAndPoint(cI,
				concavityAngle, concavityRatio, minDistPoint);
		return s;
	}
}
