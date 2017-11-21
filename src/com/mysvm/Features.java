package com.mysvm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Features {

	public enum Direction {
		UNKNOWN, VERTICAL, HORIZONTAL
	}

	/**
	 * 获取垂直或水平方向直方图
	 * 
	 * @param img
	 * @param direction
	 * @return
	 */
	public static float[] projectedHistogram(final Mat img, Direction direction) {
		int sz = 0;
		switch (direction) {
		case HORIZONTAL:
			sz = img.rows();
			break;

		case VERTICAL:
			sz = img.cols();
			break;

		default:
			break;
		}

		// 统计这一行或一列中，非零元素的个数，并保存到nonZeroMat中
		float[] nonZeroMat = new float[sz];
		Core.extractChannel(img, img, 0);
		for (int j = 0; j < sz; j++) {
			Mat data = (direction == Direction.HORIZONTAL) ? img.row(j) : img
					.col(j);
			int count = Core.countNonZero(data);
			nonZeroMat[j] = count;
		}

		// Normalize histogram
		float max = 0;
		for (int j = 0; j < nonZeroMat.length; ++j) {
			max = Math.max(max, nonZeroMat[j]);
		}

		if (max > 0) {
			for (int j = 0; j < nonZeroMat.length; ++j) {
				nonZeroMat[j] /= max;
			}
		}

		return nonZeroMat;
	}

	/**
	 * Assign values to feature
	 * <p>
	 * 样本特征为水平、垂直直方图和低分辨率图像所组成的矢量
	 * 
	 * @param in
	 * @param sizeData
	 *            低分辨率图像size = sizeData*sizeData, 可以为0
	 * @return
	 */
	public static MatOfFloat features(final Mat in, final int sizeData) {

		float[] vhist = projectedHistogram(in, Direction.VERTICAL);
		float[] hhist = projectedHistogram(in, Direction.HORIZONTAL);

		Mat lowData = new Mat();
		if (sizeData > 0) {
			Imgproc.resize(in, lowData, new Size(sizeData, sizeData));
		}
		int numCols = vhist.length + hhist.length + lowData.cols()
				* lowData.rows();
		MatOfFloat out = new MatOfFloat(Mat.zeros(1, numCols, CvType.CV_32F));
		// float[] array = out.toArray();

		int j = 0;
		for (int i = 0; i < vhist.length; ++i, ++j) {
			out.put(0, j, vhist[i]);
		}
		for (int i = 0; i < hhist.length; ++i, ++j) {
			out.put(0, j, hhist[i]);
		}
		for (int x = 0; x < lowData.cols(); x++) {
			for (int y = 0; y < lowData.rows(); y++, ++j) {
				byte[] data = new byte[1];
				lowData.get(y, x, data);
				// lowData.ptr(x, y).get() & 0xFF;
				out.put(0, j, data[0]);
			}
		}

		return out;
	}

	public MatOfFloat getHistogramFeatures(Mat image) {
		Mat grayImage = new Mat();
		Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

		Mat img_threshold = new Mat();
		Imgproc.threshold(grayImage, img_threshold, 0, 255, Imgproc.THRESH_OTSU
				+ Imgproc.THRESH_BINARY);

		return features(img_threshold, 0);
	}

	public void addTranPlate(List<String> files, BufferedWriter bwTran,
			BufferedWriter bwTest, float label) {
		int count = files.size();
		int bouny = (int) ((double)count * 0.7);
		int c = 0;
		for (String fn : files) {
			System.out.println(fn);
			Mat in = Imgcodecs.imread(fn);
			MatOfFloat out = getHistogramFeatures(in);
			float[] array = out.toArray();
			String model = label + "  ";
			for (int i = 0; i < array.length; i++) {
				model += (i + 1) + ":" + array[i] + " ";
			}
			model = model.substring(0, model.length() - 1) + "\n";
			try {
				if (c > bouny) {
					bwTest.write(model);
				} else {
					bwTran.write(model);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			c++;
		}
	}

	public List<File> addTranPlate(String dir, BufferedWriter bw, float label) {
		List<File> list = new ArrayList<File>();
		File hasDir = new File(dir);
		File[] fs = hasDir.listFiles();
		int count = fs.length;
		int bouny = (int) (count * 0.3);
		for (File file : fs) {
			count++;
			if (count > bouny) {
				list.add(file);
			}
			String fn = dir + file.getName();
			Mat in = Imgcodecs.imread(fn);
			MatOfFloat out = getHistogramFeatures(in);
			float[] array = out.toArray();
			String model = label + "  ";
			for (int i = 0; i < array.length; i++) {
				model += (i + 1) + ":" + array[i] + " ";
			}
			try {
				bw.write(model.substring(0, model.length() - 1) + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public static void main(String[] args) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String sHasDir = "res/learn/HasPlate/";
		String sNoDir = "res/learn/NoPlate/";
		BufferedWriter bwTran = FileStream.fileWriterStream("plate-tran", true);
		BufferedWriter bwTest = FileStream.fileWriterStream("plate-test", true);
		Features fe = new Features();
		File hasDir = new File(sHasDir);
		File noDir = new File(sNoDir);
		List<String> hasList = new ArrayList<String>();
		for (File f : hasDir.listFiles())
			hasList.add(sHasDir + f.getName());
		List<String> noList = new ArrayList<String>();
		for (File f : noDir.listFiles())
			noList.add(sNoDir + f.getName());
		fe.addTranPlate(hasList, bwTran, bwTest, 1.0f);
		fe.addTranPlate(noList, bwTran, bwTest, 2.0f);
		bwTran.close();
		bwTest.close();
	}

}
