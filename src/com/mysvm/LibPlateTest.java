package com.mysvm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;

public class LibPlateTest {
	
	public static void getFeature() throws IOException {
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

	public static void main(String[] args) throws IOException {
		getFeature();
		
		String outFile = "plate-out";
		String tranFile = "plate-tran";
		String testFile = "plate-test";
		String modelFile = "plate-model";
		String[] trainArgs = { tranFile, modelFile };
		svm_train.test(trainArgs);
		String[] testArgs = { testFile, modelFile, outFile };
		svm_predict.main(testArgs);

	}

}
