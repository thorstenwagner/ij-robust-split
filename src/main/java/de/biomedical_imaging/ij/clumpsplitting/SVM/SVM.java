package de.biomedical_imaging.ij.clumpsplitting.SVM;


import org.apache.spark.api.java.*;
import org.apache.spark.mllib.classification.*;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
public class SVM{
	private static SparkConf conf=null;
	   
	public JavaRDD<LabeledPoint> union(String path1, String path2)
	{
		SparkContext sc = new SparkContext(conf);
	 //   String path = "test/testen6er";
	    JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sc, path1).toJavaRDD();

	    JavaRDD<LabeledPoint> data1 = MLUtils.loadLibSVMFile(sc, path2).toJavaRDD();
	    data.union(data1);
	    return data;
	}
	public SVMModel trainSVM(JavaRDD<LabeledPoint> data)
	{
		JavaRDD<LabeledPoint> training = data.sample(false, 1.0, 11L);
	    
	    training.cache();
	   
	    // Run training algorithm to build the model.

	   /* StandardScaler ss= new StandardScaler();
	    
	    StandardScalerModel ssm=ss.fit(data);
	    */
	    SVMWithSGD algorithm= new SVMWithSGD();
	    
	   algorithm.setIntercept(true);
	   // algorithm.addIntercept_$eq(true);
	   algorithm.optimizer().setNumIterations(1000);
	   algorithm.optimizer().setStepSize(0.2);
	   algorithm.optimizer().setRegParam(0);
	   //algorithm.setFeatureScaling(false);
	 /*  Updater updater= new SquaredL2Updater();
	   
	   StandardScaler scaler = new StandardScaler();
	  
//		     System.out.println("STD:"+sm.std());
	   algorithm.optimizer().setUpdater(updater);
	   */
	    SVMModel model=algorithm.run(training.rdd());
	    return model;
	}
  public static void main(String[] args) {
	System.setProperty("hadoop.home.dir", "C:\\hadoop-2.6.4");
    
    conf = new SparkConf().setAppName("Test").setMaster("local");
    JavaSparkContext scon = new JavaSparkContext(conf);

    List<LabeledPoint> test= new ArrayList<LabeledPoint>();
    double[] d={200,10};
    Vector v= new DenseVector(d);
    LabeledPoint lp= new LabeledPoint(1.0,v);
    test.add(lp);
    double[] d1={100,10};
    Vector v1= new DenseVector(d1);
    LabeledPoint lp1= new LabeledPoint(0.0,v1);
    test.add(lp1);
    double[] d2={220,20};
    Vector v2= new DenseVector(d2);
    LabeledPoint lp2= new LabeledPoint(1.0,v2);
    test.add(lp2);
    double[] d3={120,20};
    Vector v3= new DenseVector(d3);
    LabeledPoint lp3= new LabeledPoint(0.0,v3);
    test.add(lp3);
    double[] d4={240,30};
    Vector v4= new DenseVector(d4);
    LabeledPoint lp4= new LabeledPoint(1.0,v4);
    test.add(lp4);
    double[] d5={140,30};
    Vector v5= new DenseVector(d5);
    LabeledPoint lp5= new LabeledPoint(0.0,v5);
    test.add(lp5);
    JavaRDD<LabeledPoint> data= scon.parallelize(test);
	
  
    SVM svm= new SVM();
    SVMModel model=svm.trainSVM(data);
    model.clearThreshold();
    
    Vector weight= model.weights();
   double bias= model.intercept();
   
   System.out.println(weight);
   System.out.println(bias);

   
   List<LabeledPoint> list=data.collect();
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
        
		 
		//private static final long serialVersionUID = 1L;

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
