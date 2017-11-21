package com.my.opencv.core;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.*;
import static org.opencv.imgcodecs.Imgcodecs.*;
import static org.opencv.imgproc.Imgproc.*;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Size;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * @author lin.yao
 * 
 */
public class CoreFunc {

	/**
	 * 根据一幅图像与颜色模板获取对应的二值图
	 * 
	 * @param src
	 *            输入RGB图像
	 * @param r
	 *            颜色模板（蓝色、黄色）
	 * @param adaptive_minsv
	 *            S和V的最小值由adaptive_minsv这个bool值判断
	 *            <ul>
	 *            <li>如果为true，则最小值取决于H值，按比例衰减
	 *            <li>如果为false，则不再自适应，使用固定的最小值minabs_sv
	 *            </ul>
	 * @return 输出灰度图（只有0和255两个值，255代表匹配，0代表不匹配）
	 */
	public static Mat colorMatch(final Mat src, final Color r,
			final boolean adaptive_minsv) {
		final float max_sv = 255;
		final float minref_sv = 64;
		final float minabs_sv = 95;

		// blue的H范围
		final int min_blue = 100;
		final int max_blue = 140;

		// yellow的H范围
		final int min_yellow = 15;
		final int max_yellow = 40;

		// 白色的范围
		final int min_white = 0;
		final int max_white = 30;

		// 转到HSV空间进行处理，颜色搜索主要使用的是H分量进行蓝色与黄色的匹配工作
		Mat src_hsv = new Mat();

		cvtColor(src, src_hsv, Imgproc.COLOR_BGR2HSV);

		List<Mat> hsvSplit = new ArrayList<Mat>();

		split(src_hsv, hsvSplit);
		Imgproc.equalizeHist(hsvSplit.get(2), hsvSplit.get(2));
		merge(hsvSplit, src_hsv);
		// 匹配模板基色,切换以查找想要的基色
		int min_h = 0;
		int max_h = 0;
		switch (r) {
		case BLUE:
			min_h = min_blue;
			max_h = max_blue;
			break;
		case YELLOW:
			min_h = min_yellow;
			max_h = max_yellow;
			break;
		case WHITE:
			min_h = min_white;
			max_h = max_white;
			break;
		default:
			break;
		}

		float diff_h = (float) ((max_h - min_h) / 2);
		int avg_h = (int) (min_h + diff_h);

		int channels = src_hsv.channels();
		int nRows = src_hsv.rows();
		// 图像数据列需要考虑通道数的影响；
		int nCols = src_hsv.cols();
		// * channels;

		// 连续存储的数据，按一行处理
		// if (src_hsv.isContinuous()) {
		// nCols *= nRows;
		// nRows = 1;
		// }
		for (int i = 0; i < nRows; ++i) {
			Mat p = src_hsv.row(i);
			for (int j = 0; j < nCols; j++) {
				byte[] b = new byte[3];
				p.get(0, j, b);
				int H = (int) b[0] & 0xff;
				int S = (int) b[1] & 0xff;
				int V = (int) b[2] & 0xff;

				boolean colorMatched = false;

				if (H > min_h && H < max_h) {
					int Hdiff = 0;
					if (H > avg_h)
						Hdiff = H - avg_h;
					else
						Hdiff = avg_h - H;

					float Hdiff_p = Hdiff / diff_h;

					float min_sv = 0;
					if (true == adaptive_minsv)
						min_sv = minref_sv - minref_sv / 2 * (1 - Hdiff_p);
					else
						min_sv = minabs_sv;

					if ((S > min_sv && S <= max_sv)
							&& (V > min_sv && V <= max_sv))
						colorMatched = true;
				}
				b[0] = (byte) 0;
				b[1] = (byte) 0;
				b[2] = colorMatched ? (byte) 255 : (byte) 0;
				src_hsv.put(i, j, b);
			}
		}

		// 获取颜色匹配后的二值灰度图
		List<Mat> hsvSplit_done = new ArrayList<Mat>();
		split(src_hsv, hsvSplit_done);
		Mat src_grey = hsvSplit_done.get(2);
		return src_grey;
	}

	/**
	 * 判断一个车牌的颜色
	 * 
	 * @param src
	 *            车牌mat
	 * @param r
	 *            颜色模板
	 * @param adaptive_minsv
	 *            S和V的最小值由adaptive_minsv这个bool值判断
	 *            <ul>
	 *            <li>如果为true，则最小值取决于H值，按比例衰减
	 *            <li>如果为false，则不再自适应，使用固定的最小值minabs_sv
	 *            </ul>
	 * @return
	 */
	public static boolean plateColorJudge(final Mat src, final Color color,
			final boolean adaptive_minsv) {
		// 判断阈值
		final float thresh = 0.49f;

		Mat gray = colorMatch(src, color, adaptive_minsv);

		float percent = (float) countNonZero(gray)
				/ (gray.rows() * gray.cols());

		return (percent > thresh) ? true : false;
	}

	/**
	 * getPlateType 判断车牌的类型
	 * 
	 * @param src
	 * @param adaptive_minsv
	 *            S和V的最小值由adaptive_minsv这个bool值判断
	 *            <ul>
	 *            <li>如果为true，则最小值取决于H值，按比例衰减
	 *            <li>如果为false，则不再自适应，使用固定的最小值minabs_sv
	 *            </ul>
	 * @return
	 */
	public static Color getPlateType(final Mat src, final boolean adaptive_minsv) {
		if (plateColorJudge(src, Color.BLUE, adaptive_minsv)) {
			return Color.BLUE;
		} else if (plateColorJudge(src, Color.YELLOW, adaptive_minsv)) {
			return Color.YELLOW;
		} else {
			return Color.UNKNOWN;
		}
	}

	public static int countOfBigValue(Mat mat, int iValue) {
		int iCount = 0;
		Core.extractChannel(mat, mat, 0);
		List<Float> list = new ArrayList<>();
		if (mat.rows() > 1) {
			for (int i = 0; i < mat.rows(); i++) {
				double[] d = mat.get(i, 0);
				list.add((float) d[0]);
			}
		} else {
			for (int i = 0; i < mat.cols(); i++) {
				double[] d = mat.get(0, i);
				list.add((float) d[0]);
			}
		}
		for (float f : list) {
			if (f > iValue)
				iCount++;
		}
		return iCount;

	}

	/**
	 * 获取垂直或水平方向直方图
	 * 
	 * @param img
	 * @param direction
	 * @return
	 */
	public static float[] projectedHistogram(final Mat img,
			Direction direction, int threshold) {
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
		Mat re = Mat.zeros(1, sz, CvType.CV_32F);
		// extractChannel(img, img, 0);
		for (int j = 0; j < sz; j++) {
			Mat data = (direction == Direction.HORIZONTAL) ? img.row(j) : img
					.col(j);

			int count = countOfBigValue(data, threshold);
			float[] f = { count };
			re.put(0, j, f);
		}
		MinMaxLocResult mre = Core.minMaxLoc(re);
		System.out.println(mre.minVal + "/" + mre.maxVal);
		if (mre.maxVal > 0)
			re.convertTo(re, -1, 1.0f / mre.maxVal, 0);
		float[] array = new float[sz];
		re.get(0, 0, array);
		return array;
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
	public static Mat features(final Mat in, final int sizeData) {

		float[] vhist = projectedHistogram(in, Direction.VERTICAL, 0);
		float[] hhist = projectedHistogram(in, Direction.HORIZONTAL, 0);

		Mat lowData = new Mat();
		if (sizeData > 0) {
			Size s = new Size(sizeData, sizeData);
			resize(in, lowData, s);
		}

		int numCols = vhist.length + hhist.length + lowData.cols()
				* lowData.rows();
		Mat out = Mat.zeros(1, numCols, CvType.CV_32F);
		// FloatIndexer idx = out.createIndexer();
		float[] idx = new float[numCols];

		int j = 0;
		for (int i = 0; i < vhist.length; ++i, ++j) {
			idx[j] = vhist[i];
		}
		for (int i = 0; i < hhist.length; ++i, ++j) {
			idx[j] = vhist[i];
		}
		for (int r = 0; r < lowData.rows(); r++) {
			float[] f = new float[lowData.cols()];
			lowData.get(r, 0, f);
			for (int c = 0; c < f.length; c++, ++j) {
				idx[j] = f[c];
				// out.put(0, j, val);
			}
		}
		out.put(0, 0, idx);
		return out;
	}

	/**
	 * Show image
	 * 
	 * @param title
	 * @param src
	 */
	public static void showImage(final String title, final Mat src) {
		// try {
		// IplImage image = src.asIplImage();
		// if (image != null) {
		// cvShowImage(title, image);
		// cvWaitKey(0);
		// }
		// } catch (Exception ex) {
		// }
	}
}
