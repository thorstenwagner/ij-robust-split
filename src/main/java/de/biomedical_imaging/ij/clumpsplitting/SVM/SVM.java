package de.biomedical_imaging.ij.clumpsplitting.SVM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;
import weka.classifiers.functions.LibLINEAR;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.LibSVMLoader;

public class SVM
{

	public static void svm()
	{
		FileReader fr = null;
		try
		{
			fr = new FileReader("test/Testdaten.txt");
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		ArrayList<String> arrayListString = new ArrayList<String>();
		String s;
		try
		{
			while ((s = br.readLine()) != null)
			{
				arrayListString.add(s);

			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Problem problem = new Problem();
		problem.l = arrayListString.size();// number of training examples
		problem.n = 3; // number of features
		problem.bias = 1000;
		problem.x = new Feature[problem.l][problem.n - 1];
		problem.y = new double[problem.l];

		for (int i = 0; i < arrayListString.size(); i++)
		{
			String[] st = arrayListString.get(i).split(",");
			Feature f1 = new FeatureNode(1, Double.parseDouble(st[1]));
			Feature f2 = new FeatureNode(2, Double.parseDouble(st[2]));
			// System.out.println(st[0]);
			problem.y[i] = Double.parseDouble(st[0]);

			problem.x[i][0] = f1;
			problem.x[i][1] = f2;
		}

		SolverType solver = SolverType.L2R_L2LOSS_SVC_DUAL; // -s 0

		double C = 1.0; // cost of constraints violation
		double eps = 0.01; // stopping criteria

		Parameter parameter = new Parameter(solver, C, eps);

		Model model = Linear.train(problem, parameter);
		System.out.println(model.getDecfunBias(0) + "teeeest");
		// FastMarginModel fmm=new FastMarginModel(exampleSet, model,
		// getParameterAsBoolean(PARAMETER_USE_BIAS));
		System.out.println(model.getFeatureWeights().length);
		System.out.println(model.getFeatureWeights()[0] + " " + model.getFeatureWeights()[1]);
		try
		{
			Linear.saveModel(new File("test/model.txt"), model);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// File modelFile = new File("model");
		// model.save(modelFile);
		// load model or use it directly
		// model = Model.load(modelFile);

		// Feature[] instance = { new FeatureNode(1, 4), new FeatureNode(2, 2)
		// };
		// double prediction = Linear.predict(model, instance);
	}

	public static void main(String[] args)
	{
		// svm();
		// LibSVM libsvm = new LibSVM();
		LibLINEAR libLinear = new LibLINEAR();
		FileReader fr = null;
		try
		{
			fr = new FileReader("test/Testdaten.txt");
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		ArrayList<String> arrayListString = new ArrayList<String>();
		String s;
		try
		{
			while ((s = br.readLine()) != null)
			{
				arrayListString.add(s);

			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<Attribute> aList = new ArrayList<Attribute>();
		Attribute a = new Attribute("test");
		Attribute b = new Attribute("test1");
		FastVector<String> fvClassVal = new FastVector(2);
		fvClassVal.addElement("positive");
		fvClassVal.addElement("negative");
		Attribute classAttribute = new Attribute("theClass", fvClassVal);

		aList.add(a);
		aList.add(b);
		aList.add(classAttribute);
		Instances instances = new Instances("Instances", aList, 0);
		for (int i = 0; i < arrayListString.size(); i++)
		{
			String[] st = arrayListString.get(i).split(",");
			double[] test = new double[3];
			test[0] = Double.parseDouble(st[1]);
			test[1] = Double.parseDouble(st[2]);
			test[2] = Double.parseDouble(st[0]);
			// System.out.println(st[0]);
			Instance inst = new DenseInstance(3);
			inst.setValue(a, test[0]);
			inst.setValue(b, test[1]);
			if (test[2] == 1)
			{
				inst.setValue(classAttribute, "positive");
			} else
			{
				inst.setValue(classAttribute, "negative");
			}
			instances.add(inst);
		}
		instances.setClassIndex(instances.numAttributes() - 1);
		libLinear.setNormalize(false);
		// libLinear.setNormalize(true);
		libLinear.setBias(1);
		System.out.println(libLinear.biasTipText());
		// libLinear.setProbabilityEstimates(true);
		// libLinear.setSVMType(new SelectedTag(0, LibLINEAR.TAGS_SVMTYPE));
		try
		{
			libLinear.buildClassifier(instances);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Model testen = libLinear.getModel();
		System.out.println(testen.getLabels().length+ "Labels");
		System.out.println(testen.getBias() + "BIAS");
		System.out.println(testen.getDecfunBias(0) + "DECFUNBIAS");

		System.out.println(testen.getDecfunCoef(1, 0));
		System.out.println(testen.getFeatureWeights()[0] + " " + testen.getFeatureWeights()[1] + " "
				+ testen.getFeatureWeights()[2] + " " + testen.getFeatureWeights()[3]);

		String d = libLinear.getWeights();
		System.out.println(d);

	}
}
