package com.lixd.opencv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.my.opencv.core.PlateLocate;

public class FirstOpencv {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Mat m = Imgcodecs.imread("d:\\091748591556892.jpg",
		// Imgcodecs.CV_LOAD_IMAGE_COLOR);
		// System.out.println(m.type() + "/" + m.channels());
		// int[] b = new int[] { 0, 0, 0 };
		// m.get(0, 0, b);
		// for (int c : b) {
		// System.out.println(c);
		// }
		// m.put(0, 0, b);
		Mat m = new Mat();
		System.out.println(m);
		Mat temp = Mat.zeros(1, 16, CvType.CV_32FC1);
		System.out.println(temp);
		System.out.println(temp.size()+"/" + temp.channels());
		float[] b = new float[8];
		for (int i = 0; i < b.length; i++) {
			b[i] = 1f;
		}
		temp.put(0, 0, b);
		System.out.println(temp.dump());
		temp.copyTo(m);
		System.out.println(m);

	}
}
