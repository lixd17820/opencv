package com.my.opencv.core;

import java.util.ArrayList;
import java.util.List;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.core.Core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Mat;

/**
 * 
 * @author Created by fanwenjie
 * @author lin.yao
 * 
 */
public class Features {

	
	public Mat getHisteqFeatures(final Mat image) {
		return histeq(image);
	}

	
	public Mat getHistogramFeatures(Mat image) {
		Mat grayImage = new Mat();
		Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);

		Mat img_threshold = new Mat();
		Imgproc.threshold(grayImage, img_threshold, 0, 255, Imgproc.THRESH_OTSU
				+ Imgproc.THRESH_BINARY);

		return CoreFunc.features(img_threshold, 0);
	}

	
	public Mat getSIFTFeatures(final Mat image) {
		// TODO: 待完善
		return null;
	}

	
	public Mat getHOGFeatures(final Mat image) {
		// TODO: 待完善
		return null;
	}

	private Mat histeq(Mat in) {
		Mat out = new Mat(in.size(), in.type());
		if (in.channels() == 3) {
			Mat hsv = new Mat();
			List<Mat> hsvSplit = new ArrayList<Mat>();
			cvtColor(in, hsv, COLOR_BGR2HSV);
			split(hsv, hsvSplit);
			equalizeHist(hsvSplit.get(2), hsvSplit.get(2));
			merge(hsvSplit, hsv);
			cvtColor(hsv, out, COLOR_HSV2BGR);
			hsv = null;
			hsvSplit = null;
			System.gc();
		} else if (in.channels() == 1) {
			equalizeHist(in, out);
		}
		return out;
	}
	
	public Mat getHistogram(Mat in) {
		  //const int VERTICAL = 0;
		  //const int HORIZONTAL = 1;

		  // Histogram features
			//Mat vhist = CoreFunc.projectedHistogram(in, Direction.VERTICAL,0);
		  //Mat vhist = CoreFunc.ProjectedHistogram(in, VERTICAL);
		 // Mat hhist = CoreFunc.ProjectedHistogram(in, HORIZONTAL);

		  // Last 10 is the number of moments components
		  //int numCols = vhist.cols() + hhist.cols();

		 // Mat out = Mat.zeros(1, numCols, CV_32F);

//		  int j = 0;
//		  for (int i = 0; i < vhist.cols; i++) {
//		    out.at<float>(j) = vhist.at<float>(i);
//		    j++;
//		  }
//		  for (int i = 0; i < hhist.cols; i++) {
//		    out.at<float>(j) = hhist.at<float>(i);
//		    j++;
//		  }

		  return null;
		}
	
	public void getLBPplusHistFeatures(Mat image, Mat features) {
//		  Mat grayImage;
//		  cvtColor(image, grayImage,Imgproc.COLOR_RGB2GRAY);
//
//		  Mat lbpimage;
//		  //lbpimage = libfacerec::olbp(grayImage);
//		  Mat lbp_hist; 
//		  //= libfacerec::spatial_histogram(lbpimage, 64, 8, 4);
//		  //features = lbp_hist.reshape(1, 1);
//
//		  Mat greyImage;
//		  cvtColor(image, greyImage, Imgproc.COLOR_RGB2GRAY);
//
//		  //grayImage = histeq(grayImage);
//		  Mat img_threshold;
//		  threshold(greyImage, img_threshold, 0, 255,
//				  Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);
//		  Mat histomFeatures = getHistogram(img_threshold);

		  //hconcat(lbp_hist.reshape(1, 1), histomFeatures.reshape(1, 1), features);
		  //std::cout << features << std::endl;
		  //features = histomFeatures;
		}
}
