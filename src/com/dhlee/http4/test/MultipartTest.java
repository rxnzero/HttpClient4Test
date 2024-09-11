package com.dhlee.http4.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class MultipartTest {

	public MultipartTest() {
		// TODO Auto-generated constructor stub
	}
	
	public static void sendMultipart()  {
		String url = "http://localhost:8080/TestWeb/multipart";
		String json = "{\"loanKey\":\"123456\", \"bankAccountNumber\":\"123456789012\"}";
		String filePath = "d:/user.jpg.txt";
		
		
		HttpPost uploadFile = new HttpPost(url);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		// This attaches the file to the POST:
		File f = new File(filePath);
		try (
			CloseableHttpClient httpClient = HttpClients.createDefault();
		){
			builder
			.addTextBody(
				    "json-body",
				    json,
				    ContentType.APPLICATION_JSON
				)
			.addBinaryBody(
			    "image-file",
			    new FileInputStream(f),
			    ContentType.TEXT_PLAIN,
			    f.getName()
			)
			.addBinaryBody(
				    "text-file",
				    "{sample text file}".getBytes(),
				    ContentType.TEXT_PLAIN,
				    "sample.txt"
				);
			HttpEntity multipart = builder.build();
			uploadFile.setEntity(multipart);
			CloseableHttpResponse response;
		
			response = httpClient.execute(uploadFile);
			HttpEntity responseEntity = response.getEntity();
			System.out.println("ContentLength = " + responseEntity.getContentLength());
//			System.out.println("Content = \n" + getEntityData(responseEntity));
			System.out.println("Content = \n" + EntityUtils.toString(responseEntity));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private static String getEntityData(HttpEntity entity) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		entity.writeTo(bout);
		
		String contentEncoding = null;
		HeaderElement[] headerElements = entity.getContentType().getElements();
	    String charset = headerElements[0].getParameterByName("charset").getValue();
		System.out.println("charset = " + charset);
		
		Header encoding = entity.getContentEncoding();
		if (encoding != null) {
			contentEncoding = encoding.getValue();
			System.out.println("contentEncoding = " + contentEncoding);
		}
		
		return bout.toString(charset);
	}
	
	public static void main(String[] args) {
		sendMultipart();
	}

}
