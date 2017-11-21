package com.mysvm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileStream {
	public static BufferedWriter fileWriterStream(String fileName,
			boolean append) {
		BufferedWriter fp_save = null;
		try {
			fp_save = new BufferedWriter(new FileWriter(fileName, append));
		} catch (IOException e) {
			System.err.println("can't open file " + fileName);
			System.exit(1);
		}
		return fp_save;
	}

	public static void writerData(BufferedWriter bw, String data)
			throws IOException {
		bw.write(data);
	}
}
