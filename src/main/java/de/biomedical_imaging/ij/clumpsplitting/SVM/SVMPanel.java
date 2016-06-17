package de.biomedical_imaging.ij.clumpsplitting.SVM;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;

public class SVMPanel extends JPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<LabeledPoint> labeledPoints= new ArrayList<LabeledPoint>();
	private Vector weight;
	private double bias;
	public SVMPanel(List<LabeledPoint> list, Vector weight, double bias)
	{
		  this.setBackground(Color.white);
		  this.setPreferredSize(new Dimension(500,500));
		   
		this.labeledPoints=list;
		this.weight=weight;
		this.bias=bias;
		  System.out.println("1:"+labeledPoints.size());
		    
	}
	public void paintComponent(Graphics g)
	   {
	      super.paintComponent(g);
	      //x-Achse
	      g.drawLine(0, this.getHeight()-20, this.getWidth(), this.getHeight()-20);
	      //y-Achse
	      g.drawLine(20, 0,20, this.getHeight());
	      
	      System.out.println(labeledPoints.size());
	      for(int i=0;i<this.getWidth();i+=50)
	      {
	    	  //Beschriftung x Achse
	    	  g.drawString(String.valueOf(i), i+20-3, this.getHeight()-5);
	    	  //Beschriftung y Achse
	    	  g.drawString(String.valueOf(i), 0, this.getHeight()-i-20+5);
	      }
	      g.setColor(Color.red);
	      for(LabeledPoint lp: labeledPoints)
	      {
	    	  double[] features=lp.features().toArray();
	    	  if(lp.label()==0)
	    	  {
	    	  g.setColor(Color.red);
	    	  }
	    	  else{
	    		  g.setColor(Color.blue);
	    	  }
	    	  g.drawRect((int)features[1]+20-3, this.getHeight()-(int)features[0]-20-3, 6,6);
	    	  
	    	 
	      }
	      g.setColor(Color.green);
    	  double[] array = this.weight.toArray();
    	 /* double m= array[0]/array[1];
    	  double b=this.bias;
    	  double x1=0;
    	  double x2=this.getWidth();
    	  double y1=-m*x1+b;
    	  double y2=-m*x2+b;
    	  
    	  System.out.println(x1+" "+ y1+ " "+ x2+ " "+ y2);*/
    	  double y =-(array[0]/array[1]);
    	  System.out.println(this.getWidth()+ " "+ y);
    	  double wert= y*this.getWidth();
    	  g.drawLine((int)20, this.getHeight()-20, this.getWidth()+20,-this.getHeight()- (int) wert-20);
	   }
}
