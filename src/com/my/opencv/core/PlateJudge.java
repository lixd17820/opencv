package com.my.opencv.core;

import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.SVM;

/**
 * @author Created by fanwenjie
 * @author lin.yao
 * 
 */
public class PlateJudge {

	public PlateJudge() {
		loadModel();
	}

	public void loadModel() {
		loadModel(path);
	}

	public void loadModel(String s) {
		svm.clear();
		SVM.load(s);
	}

	/**
	 * 对单幅图像进行SVM判断
	 * 
	 * @param inMat
	 * @return
	 */
	public int plateJudge(final Mat inMat) {
		Mat features = this.features.getHistogramFeatures(inMat);

		// 通过直方图均衡化后的彩色图进行预测
		Mat p = features.reshape(1, 1);
		p.convertTo(p, CvType.CV_32FC1);
		float ret = svm.predict(features);

		return (int) ret;
	}

	/**
	 * 对多幅图像进行SVM判断
	 * 
	 * @param inVec
	 * @param resultVec
	 * @return
	 */
	public int plateJudge(List<Mat> inVec, List<Mat> resultVec) {

		for (int j = 0; j < inVec.size(); j++) {
			Mat inMat = inVec.get(j);

			if (1 == plateJudge(inMat)) {
				resultVec.add(inMat);
			} else { // 再取中间部分判断一次
				int w = inMat.cols();
				int h = inMat.rows();

				Mat tmpDes = inMat.clone();
				Mat tmpMat = new Mat(inMat, new Rect((int) (w * 0.05),
						(int) (h * 0.1), (int) (w * 0.9), (int) (h * 0.8)));
				Size size = inMat.size();
				Imgproc.resize(tmpMat, tmpDes, size);

				if (plateJudge(tmpDes) == 1) {
					resultVec.add(inMat);
				}
			}
		}

		return 0;
	}

	public void setModelPath(String path) {
		this.path = path;
	}

	public final String getModelPath() {
		return path;
	}

	private SVM svm = SVM.create();

	/**
	 * EasyPR的getFeatures回调函数, 用于从车牌的image生成svm的训练特征features
	 */
	private Features features = new Features();

	/**
	 * 模型存储路径
	 */
	private String path = "model/svm.xml";
}
