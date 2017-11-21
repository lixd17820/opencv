package com.lixd.opencv;

import static org.opencv.core.Core.extractChannel;

import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.my.opencv.core.CoreFunc;
import com.my.opencv.core.Direction;
import com.my.opencv.core.Features;

public class CommTest {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		Mat mat = Imgcodecs.imread("res/train/data/plate_detect_svm/learn/HasPlate/A01_N84E28_0.jpg");
				//Mat.zeros(new Size(40, 40), CvType.CV_32F);
		//float[] f = { 200, 100, 50, 150 };
		//mat.put(1, 1, f);
		Mat re = new Features().getHistogramFeatures(mat);
		//float[] array = CoreFunc.projectedHistogram(mat, Direction.HORIZONTAL, 0);
		//System.out.println(printArray(array));
		System.out.println(re.cols());
		System.out.println(re.dump());
	}

	public static void main2(String[] args) {
		Mat mat = Mat.zeros(new Size(40, 40), CvType.CV_32FC3);
		System.out.println(mat);
		int channel = mat.channels();

		for (int i = 0; i < mat.cols(); i++) {
			if (i == 1)
				break;
			Mat col = mat.col(i);
			System.out.println(col);
			extractChannel(col, col, 0);
			System.out.println(col);
			MatOfFloat sub = new MatOfFloat(col);
			System.out.println(sub);
			System.out.println(sub.dump());
			Mat sub5 = sub.reshape(1, 1);
			System.out.println("sub5: " + sub5.dump());
			float[] array = sub.toArray();
			for (int j = 0; j < array.length; j++) {
				// if (j % 4 == 0)
				array[j] = j % 255;
			}
			MatOfFloat sub2 = new MatOfFloat(Mat.zeros(1, 40, CvType.CV_32F));
			System.out.println(sub2);
			sub2.fromArray(array);
			// System.out.println(sub2.dump());
			System.out.println(sub2);
			Mat sub3 = sub2.reshape(1, 1);
			System.out.println(sub3.dump());
			MinMaxLocResult mre = Core.minMaxLoc(sub3);
			System.out.println(mre.minVal + "/" + mre.maxVal);
			sub3.convertTo(sub3, -1, 1 / mre.maxVal, 0);
			System.out.println(sub3.dump());
		}

		// Core.minMaxLoc(src, mask)
		// System.out.println(mat.dump());
		// Imgcodecs.imwrite("D://test//test.jpg", mat);
	}

	public static String printArray(float[] fs) {
		String s = "[";
		for (float f : fs) {
			s += f + ",";
		}
		s = s.substring(0, s.length() - 1);
		s += "]";
		return s;
	}

}
