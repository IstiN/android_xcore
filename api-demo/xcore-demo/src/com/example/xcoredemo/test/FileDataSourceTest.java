package com.example.xcoredemo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.FileDataSource;

public class FileDataSourceTest implements IDemoTest {

	@Override
	public String test() {
		try {
			InputStream inputStream = new FileDataSource()
					.getSource(new DataSourceRequest("/mnt/sdcard/TestFile"));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream), 8192);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line).append(System.getProperty("line.separator"));
			}
			String value = sb.toString();
			inputStream.close();
			return value;
		} catch (IOException e) {
			return null;
		}
	}

}
