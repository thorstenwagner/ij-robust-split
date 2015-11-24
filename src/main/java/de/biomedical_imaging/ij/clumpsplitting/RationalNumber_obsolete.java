package de.biomedical_imaging.ij.clumpsplitting;

/**
 * klasse ist überflüssig
 * @author Louise
 *
 */
public class RationalNumber_obsolete implements Comparable<RationalNumber_obsolete>{
private int x;
private int y;
private final double value;

public RationalNumber_obsolete(int x, int y, int largestValue)
{
	this.x=x;
	this.y=y;
	double tempX=x;
	double tempY=y;
	if(y!=0)
	{
	this.value=tempY/tempX;
	}
	else{
		this.value=largestValue+1;
	}
}
public boolean equals(RationalNumber_obsolete r)
{
	if(r.getValue()==this.getValue())
	{
		return true;
	}
	return false;
}
public int getX()
{
	return x;
}
public int getY()
{
	return y;
}

public double getValue()
{
	return this.value;
}
@Override
public int compareTo(RationalNumber_obsolete r) {
	if(this.getValue()==r.getValue())
	{
		return 0;
	}
	else
	{
		if(this.getValue()<r.getValue())
		{
			return -1;
		}
		else{
			return 1;
		}
	}
		
}

}
