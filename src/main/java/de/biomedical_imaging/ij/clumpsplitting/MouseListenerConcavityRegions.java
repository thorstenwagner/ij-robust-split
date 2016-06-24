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
	}
	public int getImageCoordinateX(int n)
	{
		ImagePlus imageplus=WindowManager.getCurrentImage();
		return imageplus.getCanvas().offScreenX(n);
	}
	@Override
	public void mouseClicked(MouseEvent e)
	{
		Rectangle boundingBox= cr.getRectangle();
		double minX=boundingBox.getMinX();
		double minY=boundingBox.getMinY();
		double maxX=boundingBox.getMaxX();
		double maxY=boundingBox.getMaxY();
	
		if((this.getImageCoordinateX(e.getX())>minX)&&(this.getImageCoordinateX(e.getX())<maxX))
		{
			if((this.getImageCoordinateY(e.getY())>minY)&&(this.getImageCoordinateY(e.getY())<maxY))
			{
				for(ConcavityPixel point:cr.getConcavityPixelList())
				{
				Clump_Splitting.textAreaForConcavityInformation.setText(cr.getInformation(point));
				Clump_Splitting.textAreaForConcavityInformation.setBackground(Color.lightGray);
				Clump_Splitting.windowPanelConcavityRegion.add(Clump_Splitting.textAreaForConcavityInformation);
				Clump_Splitting.windowPanelConcavityRegion.setLocation(e.getXOnScreen(), e.getYOnScreen());
				Clump_Splitting.windowPanelConcavityRegion.setBackground(Color.gray);
				Clump_Splitting.windowPanelConcavityRegion.pack();
				Clump_Splitting.windowPanelConcavityRegion.setVisible(true);
				}
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

