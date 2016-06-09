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
	@Override
	public void mouseClicked(MouseEvent e)
	{
		Rectangle boundingBox= cr.getRectangle();
		double minX=boundingBox.getMinX();
		double minY=boundingBox.getMinY();
		double maxX=boundingBox.getMaxX();
		double maxY=boundingBox.getMaxY();
	
			//System.out.println(e.getX()*+ " "+ e.getY()*3);
		if((e.getX()*Clump_Splitting.zoomfactor>minX)&&(e.getX()*Clump_Splitting.zoomfactor<maxX))
		{
			if((e.getY()*Clump_Splitting.zoomfactor>minY)&&(e.getY()*Clump_Splitting.zoomfactor<maxY))
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

