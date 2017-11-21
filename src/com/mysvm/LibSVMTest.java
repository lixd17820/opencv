package com.mysvm;

import java.io.IOException;

public class LibSVMTest {

	public static void main(String[] args) throws IOException {

		String f = "D:/号牌识别/svm/breast-cancer";
		String tranFile = "D:/号牌识别/svm/scale_tran";
		String testFile = "D:/号牌识别/svm/scale_test";
		String[] argss = { "-p", tranFile, f + "-tran" };
		svm_scale.main(argss);
		svm_scale.main(new String[] { "-p", testFile, f + "-test" });
		String modelFile = f + "-model";
		String outFile = f + "-out";
		// String tranFile = f + "-tran";
		// String testFile = f + "-test";
		String[] trainArgs = { tranFile, modelFile };
		svm_train.test(trainArgs);
		// System.out.println(modelFile);
		String[] testArgs = { testFile, modelFile, outFile };
		svm_predict.main(testArgs);

	}

}
