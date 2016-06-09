package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import ij.ImagePlus;
import ij.WindowManager;


public class MouseListenerConcavityRegions implements MouseListener
{

	private ConcavityRegion cr;
	public MouseListenerConcavityRegions( ConcavityRegion cr)
	{
		this.cr=cr;
	}
	public int getImageCoordinateY(int n)
	{
		ImagePlus imageplus=WindowManager.getCurrentImage();
		return imageplus.getCanvas().offScreenY(n);
	/*	Rectangle r=imageplus.getCanvas().getSrcRect();
		Rectangle s=imageplus.getCanvas().getBounds();
		System.out.println(s.getX()+ " "+ s.getY());
		double xZoom=r.getWidth();
		double zoomfactor=xZoom/s.getWidth();
		return zoomfactor;*/
	}
	public int getImageCoordinateX(int n)
	{
		ImagePlus imageplus=WindowManager.getCurrentImage();
		return imageplus.getCanvas().offScreenX(n);
	/*	Rectangle r=imageplus.getCanvas().getSrcRect();
		Rectangle s=imageplus.getCanvas().getBounds();
		System.out.println(s.getX()+ " "+ s.getY());
		double xZoom=r.getWidth();
		double zoomfactor=xZoom/s.getWidth();
		return zoomfactor;*/
	}
	@Override
	public void mouseClicked(MouseEvent e)
	{
		Rectangle boundingBox= cr.getRectangle();
		double minX=boundingBox.getMinX();
		double minY=boundingBox.getMinY();
		double maxX=boundingBox.getMaxX();
		double maxY=boundingBox.getMaxY();
	
			//System.out.println(e.getX()*+ " "+ e.getY()*3);
		if((this.getImageCoordinateX(e.getX())>minX)&&(this.getImageCoordinateX(e.getX())<maxX))
		{
			if((this.getImageCoordinateY(e.getY())>minY)&&(this.getImageCoordinateY(e.getY())<maxY))
			{
				Clump_Splitting.window.setText(cr.getInformation());
				Clump_Splitting.window.setBackground(Color.lightGray);
				Clump_Splitting.pane.add(Clump_Splitting.window);
				Clump_Splitting.pane.setLocation(e.getXOnScreen(), e.getYOnScreen());
		//		System.out.println("Ich tu was");
				Clump_Splitting.pane.setBackground(Color.gray);
				Clump_Splitting.pane.pack();
				Clump_Splitting.pane.setVisible(true);
			
			}
		}
			
		}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

}

