package com.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.my.opencv.core.PlateJudge;
import com.my.opencv.train.SVMTrain;

public class PlateTest {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		// PlateJudge jd = new PlateJudge();
		// File dir = new File("test/");
		// File[] fs = dir.listFiles();
		// for (File file : fs) {
		// System.out.println(file);
		// Mat mr = Imgcodecs.imread(file.getAbsolutePath());
		// //List<Mat> resultVec = new ArrayList<Mat>();
		// int re = jd.plateJudge(mr);
		// System.out.println(re);
		// }
		SVMTrain svm = new SVMTrain();
		svm.svmTrain(false, false);
	}

}
