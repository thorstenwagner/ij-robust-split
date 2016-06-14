package de.biomedical_imaging.ij.clumpsplitting.SplitLines;

public class SplitLineAssignmentSVM
{

	private int startX;
	private int startY;
	private int endX;
	private int endY;
	
	private int classificationValue;
	private double distance;
	private double sumConcavityDepth;
	
	public int getClassificationValue()
	{
		return classificationValue;
	}

	public void setClassificationValue(int classificationValue)
	{
		this.classificationValue = classificationValue;
	}

	public int getStartX()
	{
		return startX;
	}

	public int getStartY()
	{
		return startY;
	}

	public int getEndX()
	{
		return endX;
	}

	public int getEndY()
	{
		return endY;
	}

	public double getDistance()
	{
		return distance;
	}

	public double getSumConcavityDepth()
	{
		return sumConcavityDepth;
	}

	
	public SplitLineAssignmentSVM(int startX, int startY, int endX, int endY,int classificationValue, double distance, double sumConcavityDepth)
	{
		this.startX=startX;
		this.startY=startY;
		this.endX=endX;
		this.endY=endY;
		this.classificationValue=classificationValue;
		this.distance=distance;
		this.sumConcavityDepth=sumConcavityDepth;
	}
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof SplitLineAssignmentSVM)
		{
			SplitLineAssignmentSVM slaSVM =(SplitLineAssignmentSVM)o;
			if(slaSVM.getStartX()==this.getStartX()&&slaSVM.getEndX()==this.getEndX()&&slaSVM.getStartY()==this.getStartY()&&slaSVM.getEndY()==this.getEndY())
			{
				return true;
			}
			else{
				if(slaSVM.getStartX()==this.getEndX()&&slaSVM.getEndX()==this.getStartX()&&slaSVM.getEndY()==this.getEndY()&&slaSVM.getEndY()==this.getStartY())
				{
					return true;
				}
				else{
					return false;
				}
			}
		}
		else{
			return false;
		}
	}
}
		
