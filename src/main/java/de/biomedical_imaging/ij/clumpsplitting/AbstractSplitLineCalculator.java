package de.biomedical_imaging.ij.clumpsplitting;

import java.util.ArrayList;

public interface AbstractSplitLineCalculator {
public ArrayList<AbstractSplitLine> calculatePossibleSplitLines(ArrayList<ConcavityRegion> concavityRegionList);

}
