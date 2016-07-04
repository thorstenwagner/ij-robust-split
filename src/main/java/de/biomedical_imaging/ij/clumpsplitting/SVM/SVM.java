package de.biomedical_imaging.ij.clumpsplitting.SVM;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SVM
{

	public static void svm()
	{
		ArrayList<Double[]> featureList = new ArrayList<Double[]>();

		File f = new File("D:/Bachelorthesis/GitHubThesis/ij-robust-split/test/");
		if (f.isDirectory())
		{
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				FileReader fr = null;
				try
				{
					fr = new FileReader(files[i]);
				} catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BufferedReader br = new BufferedReader(fr);
				String s;
				try
				{
					while ((s = br.readLine()) != null)
					{
						String[] split = s.split(",");
						Double[] doubles =
						{ Double.valueOf(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]) };
						featureList.add(doubles);
					}
				} catch (NumberFormatException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		svm_parameter param = new svm_parameter();
		param.probability = 1;
		param.gamma = 0.5;
		param.nu = 0.5;
		param.C = 1;
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		param.cache_size = 20000;
		param.eps = 0.001;

		// param.nr_weight=2;

		svm_problem prob = new svm_problem();

		prob.y = new double[featureList.size()];
		prob.l = featureList.size();
		prob.x = new svm_node[featureList.size()][];

		for (int i = 0; i < featureList.size(); i++)
		{

			Double[] features = featureList.get(i);

			System.out.println(features[0] + " " + features[1]);
			prob.x[i] = new svm_node[features.length - 1];
			for (int j = 1; j < features.length; j++)
			{
				svm_node node = new svm_node();
				node.index = j;
				node.value = features[j];

				prob.x[i][j - 1] = node;
				System.out.println(i + " " + j + " " + node.value + "test");
			}
			System.out.println(features[0]);
			prob.y[i] = features[0];
			// if()
			// prob.y[i] = features[0];
		}
		svm_model model = svm.svm_train(prob, param);
		System.out.println(model.SV.length + " " + model.SV[0].length);
		for (int i = 0; i < model.SV.length; i++)
		{
			for (int j = 0; j < model.SV[i].length; j++)
			{
				System.out.print(" " + model.SV[i][j].value);
			}

			System.out.println("");
		}
		System.out.println("lallalallalalallalalal");

		for (int i = 0; i < model.sv_coef.length; i++)
		{
			for (int j = 0; j < model.sv_coef[i].length; j++)
			{
				System.out.print(" " + model.sv_coef[i][j]);
			}

			System.out.println("");
		}
		double[] weights = new double[model.SV[0].length];
		for (int i = 0; i < model.SV[0].length; i++)
		{
			for (int j = 0; j < model.SV.length; j++)
			{
				weights[i] = weights[i] + model.SV[j][i].value * model.sv_coef[0][j];
			}
		}
		System.out.println("llaalallalalallalalallalalalalallallallalalallalalallala");
		System.out.println(weights[0] + " " + weights[1]);

		double[] prob_estimates = new double[2];

		svm_node[] x = new svm_node[2];

		x[0] = new svm_node();
		x[0].index = 0;
		x[0].value = 0;
		x[1] = new svm_node();
		x[1].index = 1;
		x[1].value = 100;
		double intercept = 0;
		for (int i = 0; i < 10000; i++)
		{

			svm.svm_predict_values(model, x, prob_estimates);
			x[1].index = 1;
			x[1].value += prob_estimates[0];

			intercept = x[1].value;
		}
		double d = svm.svm_predict_values(model, x, prob_estimates);
		for (int i = 0; i < prob_estimates.length; i++)
		{
			System.out.print(prob_estimates[i] + " ");
		}
		System.out.println("");
		int[] labels = model.label;
		System.out.println(labels[0] + " " + labels[1]);
		System.out.println(intercept + "intercept");

		System.out.println(d + "prediction");

		SVMPanel panel = new SVMPanel(featureList, weights, intercept);
		JFrame frame = new JFrame("Oval Sample");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setLayout(new GridLayout(1, 1));
		frame.add(panel);

		frame.setSize(500, 500);
		frame.setVisible(true);

	}

	public static void main(String[] args)
	{
		svm();
	}

}
