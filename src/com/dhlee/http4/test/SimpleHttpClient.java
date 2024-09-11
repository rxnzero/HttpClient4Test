package com.dhlee.http4.test;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class SimpleHttpClient {
	public void givenRedirectsAreDisabled_whenConsumingUrlWhichRedirects_thenNotRedirected() 
			  throws ClientProtocolException, IOException {
			    DefaultHttpClient instance = new DefaultHttpClient();

			    HttpParams params = new BasicHttpParams();
			    params.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
			    // HttpClientParams.setRedirecting(params, false); // alternative

			    HttpGet httpGet = new HttpGet("http:/testabc.com");
			    httpGet.setParams(params);
			    
			    CloseableHttpResponse response = instance.execute(httpGet);

			    int status = response.getStatusLine().getStatusCode();
			    System.out.println("http status code = "+ status);
			}
	
	public static void main(String[] args) throws HttpException, IOException {

		System.out.println(System.getProperty("java.vendor"));
		 System.out.println(System.getProperty("java.vendor.url"));
		 System.out.println(System.getProperty("java.version"));
		 
		 int timeout = 5;
		 RequestConfig config = RequestConfig.custom()
		   .setConnectTimeout(timeout * 1000)
		   .setConnectionRequestTimeout(timeout * 1000)
		   .setSocketTimeout(timeout * 1000).build();
		 CloseableHttpClient httpclient = 
				  HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		 
//		 InsecureHttpClientFactory factory = new InsecureHttpClientFactory();
//		 CloseableHttpClient httpclient = factory.build().set; //HttpClients.createDefault();
		 
		 HttpGet httpget = new HttpGet("https://www.google.com/");
		 
		 System.out.println("Executing request " + httpget.getRequestLine());

       // Create a custom response handler
       ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

           @Override
           public String handleResponse(
                   final HttpResponse response) throws ClientProtocolException, IOException {
               int status = response.getStatusLine().getStatusCode();
               if (status >= 200 && status < 300) {
                   HttpEntity entity = response.getEntity();
                   return entity != null ? EntityUtils.toString(entity) : null;
               } else {
                   throw new ClientProtocolException("Unexpected response status: " + status);
               }
           }

       };
       
		try {
			String responseBody = httpclient.execute(httpget, responseHandler);
          System.out.println("----------------------------------------");
          System.out.println(responseBody);
		} finally {
			httpclient.close();
		}
		
		
		

				
	}
}
