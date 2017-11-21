package com.test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lixd.jdbc.util.TextUtils;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TextOkHttp {

	OkHttpClient client = new OkHttpClient();
	private static final int GET = 0;
	private static final int POST = 1;
	public static final String DOWN_SQL_VALUE_URL = "/ydjw/services/ydjw/queryOracle";

	String get(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();

		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	public String httpTextClient(String url, int method, String... param) {
		Map<String, String> params = new HashMap<String, String>();
		if (param.length > 0 && param.length % 2 == 0) {
			for (int i = 0; i < param.length / 2; i++) {
				params.put(param[2 * i], param[2 * i + 1]);
			}
		}
		return httpTextClient(url, method, params);
	}

	public String httpTextClient(String url, int method,
			Map<String, String> params) {
		OkHttpClient client = new OkHttpClient();
		Request.Builder reqb = new Request.Builder().url(url);
		String s = "";
		if (method == POST) {
			FormBody.Builder mb = new FormBody.Builder();
			if (params != null)
				for (Map.Entry<String, String> m : params.entrySet()) {
					mb.add(m.getKey(), m.getValue());
				}
			reqb = reqb.post(mb.build());
		}
		Response response = null;
		try {
			response = client.newCall(reqb.build()).execute();
			int code = response.code();
			//System.out.println(code);
			if (response.isSuccessful()) {
				s = response.body().string();

			}
		} catch (IOException e) {
			e.printStackTrace();

		}
		return s;
	}

	public String getUrl() {
		return "http://www.ntjxj.com";
	}

	public String downloadSqlValue(String sql, String params) {
		try {
			Map<String, String> postParams = new HashMap<String, String>();
			postParams.put("sql", sql);
			if (!TextUtils.isEmpty(params))
				postParams.put("params", params);
			String s = httpTextClient(getUrl() + DOWN_SQL_VALUE_URL, POST,
					postParams);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(
				"d:/project/vehicle.txt")));
		String v = null;
		while ((v = br.readLine()) != null) {
			String[] s = v.split("\t");
			TextOkHttp example = new TextOkHttp();
			String sql = "select * from trff_app.vehicle where hpzl='" + s[0]
					+ "' and hphm='" + s[1] + "'";
			String response = example.downloadSqlValue(sql, "");
			// System.out.println(response);
			JSONArray array;
			try {
				array = new JSONArray(response);
				JSONObject obj = array.getJSONObject(0);
				System.out.println(obj.getString("hpzl") + "\t"
						+ obj.getString("hphm") + "\t" + obj.getString("syr"));

			} catch (JSONException e) {
				System.out.println(s[0] + "\t" + s[1]);

				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		br.close();
		// File f = new File("d:/test.txt");
		// BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
		// (new FileOutputStream(f))));
		// out.write(response);
		// out.close();
		// System.out.println(response);
	}

}
