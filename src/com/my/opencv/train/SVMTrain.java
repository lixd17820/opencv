package com.my.opencv.train;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;

import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.core.Core.*;
import static org.opencv.imgcodecs.Imgcodecs.*;

import com.my.opencv.core.Features;
import com.my.opencv.util.Convert;
import com.my.opencv.util.Util;

import java.util.*;

/*
 * Created by fanwenjie
 * @version 1.1
 */
public class SVMTrain {

	private Features callback = new Features();
	private static final String hasPlate = "HasPlate";
	private static final String noPlate = "NoPlate";

	public SVMTrain(Features callback) {
		this.callback = callback;
	}

	public SVMTrain() {
	}

	private void learn2Plate(float bound, final String name) {
		final String filePath = "res/train/data/plate_detect_svm/learn/" + name;
		Vector<String> files = new Vector<String>();
		// //获取该路径下的所有文件
		Util.getFiles(filePath, files);
		int size = files.size();
		if (0 == size) {
			System.out.println("File not found in " + filePath);
			return;
		}
		Collections.shuffle(files, new Random(new Date().getTime()));
		// //随机选取70%作为训练数据，30%作为测试数据
		int boundry = (int) (bound * size);

		Util.recreateDir("res/train/data/plate_detect_svm/train/" + name);
		Util.recreateDir("res/train/data/plate_detect_svm/test/" + name);

		System.out.println("Save " + name + " train!");
		for (int i = 0; i < boundry; i++) {
			System.out.println(files.get(i));
			Mat img = imread(files.get(i));
			String str = "res/train/data/plate_detect_svm/train/" + name + "/"
					+ name + "_" + Integer.valueOf(i).toString() + ".jpg";
			imwrite(str, img);
		}

		System.out.println("Save " + name + " test!");
		for (int i = boundry; i < size; i++) {
			System.out.println(files.get(i));
			Mat img = imread(files.get(i));
			String str = "res/train/data/plate_detect_svm/test/" + name + "/"
					+ name + "_" + Integer.valueOf(i).toString() + ".jpg";
			imwrite(str, img);
		}
	}

	private void getPlateTrain(Mat trainingImages,
			Vector<Integer> trainingLabels, final String name) {
		int label = 1;
		final String filePath = "res/train/data/plate_detect_svm/train/" + name;
		Vector<String> files = new Vector<String>();

		// //获取该路径下的所有文件
		Util.getFiles(filePath, files);

		int size = files.size();
		if (0 == size) {
			System.out.println("File not found in " + filePath);
			return;
		}
		System.out.println("get " + name + " train!");
		for (int i = 0; i < size; i++) {
			// System.out.println(files[i].c_str()).toString());
			Mat img = imread(files.get(i));

			// 调用回调函数决定特征
			Mat features = this.callback.getHisteqFeatures(img);
			features = features.reshape(1, 1);
			trainingImages.push_back(features);
			trainingLabels.add(label);
		}
	}

	private void getPlateTest(List<Mat> testingImages,
			Vector<Integer> testingLabels, final String name) {
		int label = 1;
		final String filePath = "res/train/data/plate_detect_svm/test/" + name;
		Vector<String> files = new Vector<String>();
		Util.getFiles(filePath, files);

		int size = files.size();
		if (0 == size) {
			System.out.println("File not found in " + filePath);
			return;
		}
		System.out.println("get " + name + " test!");
		for (int i = 0; i < size; i++) {
			Mat img = imread(files.get(i));
			testingImages.add(img);
			testingLabels.add(label);
		}
	}

	public void learn2HasPlate() {
		learn2HasPlate(0.7f);
	}

	public void learn2HasPlate(float bound) {
		learn2Plate(bound, hasPlate);
	}

	public void learn2NoPlate() {
		learn2NoPlate(0.7f);
	}

	public void learn2NoPlate(float bound) {
		learn2Plate(bound, noPlate);
	}

	public void getNoPlateTrain(Mat trainingImages,
			Vector<Integer> trainingLabels) {
		getPlateTrain(trainingImages, trainingLabels, noPlate);
	}

	public void getHasPlateTrain(Mat trainingImages,
			Vector<Integer> trainingLabels) {
		getPlateTrain(trainingImages, trainingLabels, hasPlate);
	}

	public void getHasPlateTest(List<Mat> testingImages,
			Vector<Integer> testingLabels) {
		getPlateTest(testingImages, testingLabels, hasPlate);
	}

	public void getNoPlateTest(List<Mat> testingImages,
			Vector<Integer> testingLabels) {
		getPlateTest(testingImages, testingLabels, noPlate);
	}

	// ! 测试SVM的准确率，回归率以及FScore
	public void getAccuracy(Mat testingclasses_preditc, Mat testingclasses_real) {
		int channels = testingclasses_preditc.channels();
		System.out.println("channels: " + Integer.valueOf(channels).toString());
		int nRows = testingclasses_preditc.rows();
		System.out.println("nRows: " + Integer.valueOf(nRows).toString());
		int nCols = testingclasses_preditc.cols() * channels;
		System.out.println("nCols: " + Integer.valueOf(nCols).toString());
		int channels_real = testingclasses_real.channels();
		System.out.println("channels_real: "
				+ Integer.valueOf(channels_real).toString());
		int nRows_real = testingclasses_real.rows();
		System.out.println("nRows_real: "
				+ Integer.valueOf(nRows_real).toString());
		int nCols_real = testingclasses_real.cols() * channels;
		System.out.println("nCols_real: "
				+ Integer.valueOf(nCols_real).toString());

		double count_all = 0;
		double ptrue_rtrue = 0;
		double ptrue_rfalse = 0;
		double pfalse_rtrue = 0;
		double pfalse_rfalse = 0;

		for (int i = 0; i < nRows; i++) {
			Mat temp = testingclasses_preditc.rowRange(i, i + 1);
			byte[] b = new byte[4];
			temp.get(0, 0, b);
			final float predict = Convert.toFloat(b);
			temp = testingclasses_real.rowRange(i, i + 1);
			temp.get(0, 0, b);
			final float real = Convert.toFloat(b);

			count_all++;

			// System.out.println("predict:" << predict).toString());
			// System.out.println("real:" << real).toString());

			if (predict == 1.0 && real == 1.0)
				ptrue_rtrue++;
			if (predict == 1.0 && real == 0)
				ptrue_rfalse++;
			if (predict == 0 && real == 1.0)
				pfalse_rtrue++;
			if (predict == 0 && real == 0)
				pfalse_rfalse++;
		}

		System.out
				.println("count_all: " + Double.valueOf(count_all).toString());
		System.out.println("ptrue_rtrue: "
				+ Double.valueOf(ptrue_rtrue).toString());
		System.out.println("ptrue_rfalse: "
				+ Double.valueOf(ptrue_rfalse).toString());
		System.out.println("pfalse_rtrue: "
				+ Double.valueOf(pfalse_rtrue).toString());
		System.out.println("pfalse_rfalse: "
				+ Double.valueOf(pfalse_rfalse).toString());

		double precise = 0;
		if (ptrue_rtrue + ptrue_rfalse != 0) {
			precise = ptrue_rtrue / (ptrue_rtrue + ptrue_rfalse);
			System.out
					.println("precise: " + Double.valueOf(precise).toString());
		} else {
			System.out.println("precise: NA");
		}

		double recall = 0;
		if (ptrue_rtrue + pfalse_rtrue != 0) {
			recall = ptrue_rtrue / (ptrue_rtrue + pfalse_rtrue);
			System.out.println("recall: " + Double.valueOf(recall).toString());
		} else {
			System.out.println("recall: NA");
		}

		if (precise + recall != 0) {
			double F = (precise * recall) / (precise + recall);
			System.out.println("F: " + Double.valueOf(F).toString());
		} else {
			System.out.println("F: NA");
		}
	}

	public int svmTrain(boolean dividePrepared, boolean trainPrepared) {

		Mat classes = new Mat();
		Mat trainingData = new Mat();

		Mat trainingImages = new Mat();
		Vector<Integer> trainingLabels = new Vector<Integer>();

		if (!dividePrepared) {
			// 分割learn里的数据到train和test里
			System.out.println("Divide learn to train and test");
			learn2HasPlate();
			learn2NoPlate();
		}

		// 将训练数据加载入内存
		if (!trainPrepared) {
			System.out.println("Begin to get train data to memory");
			getHasPlateTrain(trainingImages, trainingLabels);
			getNoPlateTrain(trainingImages, trainingLabels);

			trainingImages.copyTo(trainingData);
			trainingData.convertTo(trainingData, CvType.CV_32FC1);
			Mat temp = Mat.zeros(1, trainingLabels.size(), CvType.CV_32FC1);
			System.out.println(temp);
			float[] labels = new float[trainingLabels.size()];
			for (int i = 0; i < trainingLabels.size(); ++i)
				labels[i] = trainingLabels.get(i).intValue();
			temp.put(0, 0, labels);
			temp.copyTo(classes);
			System.out.println(temp);
			System.out.println(classes);
		}
		System.out.println(classes.empty());
		// Test SVM
		List<Mat> testingImages = new ArrayList<Mat>();
		Vector<Integer> testingLabels_real = new Vector<Integer>();

		// 将测试数据加载入内存
		System.out.println("Begin to get test data to memory");
		getHasPlateTest(testingImages, testingLabels_real);
		getNoPlateTest(testingImages, testingLabels_real);
		SVM svm = SVM.create();
		if (!trainPrepared && !classes.empty() && !trainingData.empty()) {
			svm.setType(SVM.C_SVC);
			svm.setKernel(SVM.RBF);
			svm.setDegree(0.1);
			// 1.4 bug fix: old 1.4 ver gamma is 1
			svm.setGamma(0.1);
			svm.setCoef0(0.1);
			svm.setC(1);
			svm.setNu(0.1);
			svm.setP(0.1);
			svm.setTermCriteria(new TermCriteria(TermCriteria.MAX_ITER, 20000,
					0.0001));

			// Train SVM
			System.out.println("Begin to generate svm");

			try {
				//SVM.getDefaultGridPtr(SVM.C);
				// CvSVM svm(trainingData, classes, Mat(), Mat(), SVM_params);
				svm.trainAuto(trainingData, Ml.ROW_SAMPLE, classes, 10,
						SVM.getDefaultGridPtr(SVM.C),
						SVM.getDefaultGridPtr(SVM.GAMMA),
						SVM.getDefaultGridPtr(SVM.P),
						SVM.getDefaultGridPtr(SVM.NU),
						SVM.getDefaultGridPtr(SVM.COEF),
						SVM.getDefaultGridPtr(SVM.DEGREE), true);
			} catch (Exception err) {
				System.out.println(err.getMessage());
			}
			svm.save("res/rain/svm.xml");
			System.out.println("Svm generate done!");
			// CvFileStorage fsTo = CvFileStorage.open(,
			// CvMemStorage.create(), CV_STORAGE_WRITE);

		} else {
			try {
				String path = "res/train/svm.xml";
				svm.load(path);
			} catch (Exception err) {
				System.out.println(err.getMessage());
				return 0; // next predict requires svm
			}
		}

		System.out.println("Begin to predict");

		double count_all = 0;
		double ptrue_rtrue = 0;
		double ptrue_rfalse = 0;
		double pfalse_rtrue = 0;
		double pfalse_rfalse = 0;

		int size = (int) testingImages.size();
		for (int i = 0; i < size; i++) {
			// System.out.println(files[i].c_str());
			Mat p = testingImages.get(i);

			// 调用回调函数决定特征
			Mat features = callback.getHistogramFeatures(p);
			features = features.reshape(1, 1);
			features.convertTo(features, CvType.CV_32FC1);

			int predict = (int) svm.predict(features);
			int real = testingLabels_real.get(i);

			if (predict == 1 && real == 1)
				ptrue_rtrue++;
			if (predict == 1 && real == 0)
				ptrue_rfalse++;
			if (predict == 0 && real == 1)
				pfalse_rtrue++;
			if (predict == 0 && real == 0)
				pfalse_rfalse++;
		}

		count_all = size;

		System.out.println("Get the Accuracy!");

		System.out
				.println("count_all: " + Double.valueOf(count_all).toString());
		System.out.println("ptrue_rtrue: "
				+ Double.valueOf(ptrue_rtrue).toString());
		System.out.println("ptrue_rfalse: "
				+ Double.valueOf(ptrue_rfalse).toString());
		System.out.println("pfalse_rtrue: "
				+ Double.valueOf(pfalse_rtrue).toString());
		System.out.println("pfalse_rfalse: "
				+ Double.valueOf(pfalse_rfalse).toString());

		double precise = 0;
		if (ptrue_rtrue + ptrue_rfalse != 0) {
			precise = ptrue_rtrue / (ptrue_rtrue + ptrue_rfalse);
			System.out
					.println("precise: " + Double.valueOf(precise).toString());
		} else
			System.out.println("precise: NA");

		double recall = 0;
		if (ptrue_rtrue + pfalse_rtrue != 0) {
			recall = ptrue_rtrue / (ptrue_rtrue + pfalse_rtrue);
			System.out.println("recall: " + Double.valueOf(recall).toString());
		} else
			System.out.println("recall: NA");

		double Fsocre = 0;
		if (precise + recall != 0) {
			Fsocre = 2 * (precise * recall) / (precise + recall);
			System.out.println("Fsocre: " + Double.valueOf(Fsocre).toString());
		} else
			System.out.println("Fsocre: NA");
		return 0;
	}
}
