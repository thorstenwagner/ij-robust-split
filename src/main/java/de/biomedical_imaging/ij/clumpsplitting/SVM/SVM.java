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
package de.biomedical_imaging.ij.clumpsplitting.SVM;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import ij.IJ;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import net.sf.javaml.tools.sampling.NormalBootstrapping;

/**
 * Calculates an SVM
 * 
 * @author Louise
 *
 */
public class SVM
{
	/**
	 * executes a bootstrapping
	 * 
	 * @param featureList
	 *            List of all Features
	 * @return Training data
	 */
	private static ArrayList<Double[]> bootstrap(ArrayList<Double[]> featureList)
	{
		ArrayList<Integer> positions = new ArrayList<Integer>();
		for (int i = 0; i < featureList.size(); i++)
		{
			positions.add(i);
		}
		NormalBootstrapping nb = new NormalBootstrapping();
		int size = featureList.size();
		List<Integer> sampleInt = nb.sample(positions, size, Math.round(Math.random() * 200));
		ArrayList<Double[]> sample = new ArrayList<Double[]>();
		for (int i : sampleInt)
		{
			if (!sample.contains(featureList.get(i)))
			{
				sample.add(featureList.get(i));
			}
		}
		return sample;
	}

	/**
	 * reads data from all CSV Files in a directory to train a SVM for the data
	 * 
	 * @param directoryPath
	 *            path of the Directory
	 * @return returns List of all feature traing data
	 */
	private static ArrayList<Double[]> readDataFromFile(String directoryPath)
	{
		ArrayList<Double[]> featureList = new ArrayList<Double[]>();

		File f = new File(directoryPath);
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
					e.printStackTrace();
				} catch (IOException e)
				{
					e.printStackTrace();
				}

			}

		}
		return featureList;
	}

	/**
	 * trains LibSVM for a Linear Problem for the classification of the
	 * featureList in 2D
	 * 
	 * @param featureList
	 *            Trainingsdata List
	 * @return trained model
	 */
	private static svm_model svm(ArrayList<Double[]> featureList)
	{
		svm_model model = null;
		
		svm.svm_set_print_string_function(new libsvm.svm_print_interface(){
		    @Override public void print(String s) {} // Disables svm output
		});
		
			svm_parameter param = new svm_parameter();
			
			param.probability = 1;
			param.gamma = 0.5;
			param.nu = 0.5;
			param.C = 20;
			param.svm_type = svm_parameter.C_SVC;
			param.kernel_type = svm_parameter.LINEAR;
			param.cache_size = 2000000000;
			param.eps = 0.000001;
			
			svm_problem prob = new svm_problem();

			prob.y = new double[featureList.size()];
			prob.l = featureList.size();
			prob.x = new svm_node[featureList.size()][];

			for (int i = 0; i < featureList.size(); i++)
			{

				Double[] features = featureList.get(i);

				prob.x[i] = new svm_node[features.length - 1];
				for (int j = 1; j < features.length; j++)
				{
					svm_node node = new svm_node();
					node.index = j;
					node.value = features[j];

					prob.x[i][j - 1] = node;
				}
				prob.y[i] = features[0];
			}
			model = svm.svm_train(prob, param);
		return model;
	}

	/**
	 * 
	 * @param model
	 *            trained svm_model
	 * @return double array --> position 0 contains the negative gradient of the
	 *         separating line position 1 contains the intercept of separating
	 *         line
	 */
	private static Double[] getSVMModelParameters(svm_model model)
	{
		double[] weights = new double[model.SV[0].length];
		for (int i = 0; i < model.SV[0].length; i++)
		{
			for (int j = 0; j < model.SV.length; j++)
			{
				weights[i] = weights[i] + model.SV[j][i].value * model.sv_coef[0][j];
			}
		}

		double[] prob_estimates = new double[2];

		svm_node[] x = new svm_node[2];

		x[0] = new svm_node();
		x[0].index = 1;
		x[0].value = 0;
		x[1] = new svm_node();
		x[1].index = 2;
		x[1].value = 0;
		double intercept = 0;

		do
		{

			svm.svm_predict_values(model, x, prob_estimates);

			x[1].index = 2;
			x[1].value += prob_estimates[0];
			intercept = x[1].value;

		} while (prob_estimates[0] > 0.01 || prob_estimates[0] < -0.01);
		double gradient = weights[0] / weights[1];
		System.out.println(gradient);
		System.out.println(intercept);
		Double[] parameters =
		{ gradient, intercept };
		return parameters;
	}

	/**
	 * shows the SVM in a JFrame to visualize the result of the SVM blue points
	 * are valid and red Points are unvalid SplitLines
	 * 
	 * @param featureList
	 *            List of all classified features
	 * @param gradient
	 *            negative gradient of the separating line of the SVM
	 * @param intercept
	 *            intercept of the separating line of the SVM
	 */
	private static void showSVM(ArrayList<Double[]> featureList, double gradient, double intercept)
	{
		SVMPanel panel = new SVMPanel(featureList, gradient, intercept);
		JFrame frame = new JFrame("SVM-Model");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setLayout(new GridLayout(1, 1));
		frame.add(panel);

		frame.setSize(500, 500);
		frame.setVisible(true);

	}

	private static JTable getTable(double d, double e, double f, double g)
	{
		d = d * 1000;
		d = Math.round(d);
		d = d / 1000;

		e = e * 1000;
		e = Math.round(e);
		e = e / 1000;

		f = f * 1000;
		f = Math.round(f);
		f = f / 1000;

		g = g * 1000;
		g = Math.round(g);
		g = g / 1000;
		double gesamt = d + e;

		double h = e - g;
		double i = f + h;
		double j = d - f;
		double k = j + g;
		h = h * 1000;
		h = Math.round(h);
		h = h / 1000;

		i = i * 1000;
		i = Math.round(i);
		i = i / 1000;

		j = j * 1000;
		j = Math.round(j);
		j = j / 1000;

		k = k * 1000;
		k = Math.round(k);
		k = k / 1000;
		String[][] rowData =
		{
				{ " ", "Splitline", "no Splitline", "sum" },
				{ "Class Splitline", String.valueOf(f), String.valueOf(h), String.valueOf(i) },
				{ "Class no Splitline", String.valueOf(j), String.valueOf(g), String.valueOf(k) },
				{ "sum", String.valueOf(d), String.valueOf(e), String.valueOf(gesamt) } };
		String[] columnnames =
		{ " ", "Trennungslinie", "keine Trennungslinie", "Summe" };
		JTable table = new JTable(rowData, columnnames);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		for (int l = 0; l < 4; l++)
		{
			TableColumn col = table.getColumnModel().getColumn(l);
			col.setPreferredWidth(100);
		}
		table.setEnabled(false);

		return table;

	}

	private static int countAnzPlus1(ArrayList<Double[]> training)
	{
		int n = 0;
		for (Double[] doubleArray : training)
		{
			if (doubleArray[0] == 1)
			{
				n++;
			}
		}
		return n;

	}

	private static Double[] test(svm_model model, ArrayList<Double[]> featureList)
	{
		double rightMinus1 = 0;
		double rightPlus1 = 0;
		double anzMinus1 = 0;
		double anzPlus1 = 0;
		double n = featureList.size();
		double right = 0;

		for (int i = 0; i < featureList.size(); i++)
		{

			svm_problem prob = new svm_problem();

			prob.y = new double[featureList.size()];
			prob.l = featureList.size();
			prob.x = new svm_node[featureList.size()][];

			Double[] features = featureList.get(i);

			prob.x[i] = new svm_node[features.length - 1];
			for (int j = 1; j < features.length; j++)
			{
				svm_node node = new svm_node();
				node.index = j;
				node.value = features[j];

				prob.x[i][j - 1] = node;
			}
			double predicted = svm.svm_predict(model, prob.x[i]);
			if (features[0] == -1)
			{
				anzMinus1++;
				if (predicted == features[0])
				{
					rightMinus1++;
					right++;

				}
			} else
			{
				anzPlus1++;
				if (predicted == features[0])
				{
					rightPlus1++;
					right++;

				}
			}
		}
		/*
		 * double accuracy = (right / n);
		 * 
		 * double accuracyMinus1 = (rightMinus1 / anzMinus1); double
		 * accuracyPlus1 = (rightPlus1 / anzPlus1);
		 */
		Double[] accuracies =
		{ right, anzPlus1, anzMinus1, rightPlus1, rightMinus1 };
		return accuracies;
	}

	public void trainSVM()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Choose directory with trainingdata and testdata");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		// Dialog zum Oeffnen von Dateien anzeigen
		int rueckgabeWert = chooser.showOpenDialog(null);
		String dirName = "";
		/* Abfrage, ob auf "Öffnen" geklickt wurde */
		if (rueckgabeWert == JFileChooser.APPROVE_OPTION)
		{
			// Ausgabe der ausgewaehlten Datei
			dirName = chooser.getSelectedFile().getAbsolutePath();
		}

		ArrayList<Double[]> featureList = SVM.readDataFromFile(dirName);
		double gradient = 0;
		double intercept = 0;
		double accuracy = 0;
		double positive = 0;
		double negative = 0;
		double richtigpositive = 0;
		double richtignegative = 0;
		int anz = 5;
		int durchgefuehrt = anz;
		int insgesamt = 0;
		for (int i = 0; i < anz; i++)
		{
			
			ArrayList<Double[]> training = SVM.bootstrap(featureList);
			ArrayList<Double[]> test = new ArrayList<Double[]>();
			int anzTraining = SVM.countAnzPlus1(training);
			test.addAll(featureList);
			test.removeAll(training);
			int anzTest = SVM.countAnzPlus1(test);
			System.out.println(anzTraining + " " + anzTest);
			svm_model model = svm(training);
			if (model.nSV[0] != 0)
			{
				Double[] parameters = SVM.getSVMModelParameters(model);
				gradient += parameters[0];
				intercept += parameters[1];
				Double accuracies[] = SVM.test(model, test);
				accuracy += accuracies[0];
				positive += accuracies[1];
				negative += accuracies[2];
				insgesamt = (int) (insgesamt + accuracies[1] + accuracies[2]);
				richtigpositive += accuracies[3];
				richtignegative += accuracies[4];
			} else
			{
				durchgefuehrt--;
			}

		IJ.showProgress(durchgefuehrt/anz);
		}
		showSVM(featureList, (gradient / durchgefuehrt), (intercept / durchgefuehrt));

		JTable t = SVM.getTable(positive / insgesamt, negative / insgesamt, richtigpositive / insgesamt,
				richtignegative / insgesamt);
		JFrame f = new JFrame();
		f.setSize(100, 200);
		f.setLayout(new GridLayout(2, 1));
		JLabel labelc1 = new JLabel("Optimized c1-Value");

		double gradienttemp = -gradient / durchgefuehrt;
		gradienttemp = gradienttemp * 1000;
		gradienttemp = Math.round(gradienttemp);
		gradienttemp = gradienttemp / 1000;

		double intercepttemp = intercept / durchgefuehrt;
		intercepttemp = intercepttemp * 1000;
		intercepttemp = Math.round(intercepttemp);
		intercepttemp = intercepttemp / 1000;
		JTextField textc1 = new JTextField(String.valueOf(gradienttemp));
		textc1.setDisabledTextColor(Color.black);
		JLabel labelc2 = new JLabel("Optimized c2-Value");
		JTextField textc2 = new JTextField(String.valueOf(intercepttemp));
		textc2.setDisabledTextColor(Color.black);
		textc1.setEnabled(false);
		textc2.setEnabled(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(new GridLayout(2, 2));

		panel.add(labelc1);
		panel.add(textc1);
		panel.add(labelc2);
		panel.add(textc2);
		panel.setVisible(true);
		f.add(panel);
		JPanel panel2 = new JPanel();

		panel2.add(t);
		f.add(panel2);
		f.pack();
		f.setVisible(true);

	}

}
