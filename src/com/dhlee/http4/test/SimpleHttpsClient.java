package com.dhlee.http4.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SimpleHttpsClient {

	public static void main(String[] args) {
		String urlStr = "https://192.168.10.42";
		
		StringBuffer sb = new StringBuffer();
		InputStreamReader in = null;
		BufferedReader br = null;
		HttpURLConnection conn = null;
		try {
			// 인증서 확인없이 처리하도록 
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs,
						String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs,
						String authType) {
				}
			} };
			
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
//			.setDefaultSSLSocketFactory(new EasySSLProtocolSocketFactory());
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();

			in = new InputStreamReader((InputStream) conn.getContent());
			br = new BufferedReader(in);

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}

			System.out.println(sb.toString());
			

		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		finally {
			try {
				if(br != null) br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(in != null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(conn != null) conn.disconnect();
		}
	}
}
