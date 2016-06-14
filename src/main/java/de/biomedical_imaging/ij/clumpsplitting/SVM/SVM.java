package de.biomedical_imaging.ij.clumpsplitting.SVM;

import scala.Tuple2;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.*;
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JFrame;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
public class SVM{
  public static void main(String[] args) {
	System.setProperty("hadoop.home.dir", "C:\\hadoop-2.6.4");
    SparkConf conf = new SparkConf().setAppName("SVM Classifier Example").setMaster("local");
    SparkContext sc = new SparkContext(conf);
    String path = "test/testenN'40A1t";
    JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sc, path).toJavaRDD();

    // Split initial RDD into two... [60% training data, 40% testing data].
    JavaRDD<LabeledPoint> training = data.sample(false, 1.0, 11L);
    training.cache();
    JavaRDD<LabeledPoint> test = data.subtract(training);

   
    // Run training algorithm to build the model.
//    int numIterations = 100;
   SVMWithSGD algorithm= new SVMWithSGD();
    algorithm.setIntercept(true);
    
    final SVMModel model=algorithm.run(training.rdd());
    
 //   final SVMModel model = SVMWithSGD.train(training.rdd(), numIterations).setIntercept(true);

    
    Vector weight= model.weights();
   double bias= model.intercept();
   
   System.out.println(weight);
   System.out.println(bias);
   try
{
	Thread.sleep(1000);
} catch (InterruptedException e)
{
	// TODO Auto-generated catch block
	e.printStackTrace();
}
   List<LabeledPoint> list=training.collect();
   System.out.println("2:" + list.size());
   SVMPanel panel= new SVMPanel(list,weight,bias);
   JFrame frame = new JFrame("Oval Sample");
   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

   frame.setLayout(new GridLayout(1, 1));
   frame.add(panel);
  

   frame.setSize(500, 500);
   frame.setVisible(true);
    // Clear the default threshold.
    model.clearThreshold();

    // Compute raw scores on the test set.
  /*  JavaRDD<Tuple2<Object, Object>> scoreAndLabels = test.map(
      new Function<LabeledPoint, Tuple2<Object, Object>>() {
         
		 
	/*	private static final long serialVersionUID = 1L;

		public Tuple2<Object, Object> call(LabeledPoint p) {
          Double score = model.predict(p.features());
          return new Tuple2<Object, Object>(score, p.label());
        }
      }
    );

    // Get evaluation metrics.
    BinaryClassificationMetrics metrics =
      new BinaryClassificationMetrics(JavaRDD.toRDD(scoreAndLabels));
    double auROC = metrics.areaUnderROC();

    System.out.println("Area under ROC = " + auROC);

    // Save and load model
/*   model.save(sc, "myModelPath");
    SVMModel sameModel = SVMModel.load(sc, "myModelPath");*/
  }
}
