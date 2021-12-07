package com.dhlee.http4.test;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

/**
 * This example demonstrates the use of {@link HttpRequestRetryHandler}.
 */
public class HttpClientRetryHandlerExample {
	
	private static CloseableHttpClient getHttpClient(int timeout, int retryCount) {
		Builder builder = RequestConfig.custom();
		builder.setConnectionRequestTimeout(timeout);
		builder.setSocketTimeout(timeout);
//		builder.setConnectTimeout(timeout * retryCount);
		RequestConfig requestConfig = builder.build();
		HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultRequestConfig(requestConfig);
		httpClientBuilder.setRetryHandler(retryHandler(retryCount));
		return httpClientBuilder.build();
	}
    public static void main(String... args) throws IOException {
    	
        CloseableHttpClient httpclient = getHttpClient(2000, 3);

        try {
            HttpGet httpget = new HttpGet("http://localhost:1234");
            
            System.out.println("Executing request " + httpget.getRequestLine());
            httpclient.execute(httpget);
            System.out.println("----------------------------------------");
            System.out.println("Request finished");
        } finally {
            httpclient.close();
        }
    }

    private static HttpRequestRetryHandler retryHandler(int retryCount){
    	System.out.println("HttpRequestRetryHandler retryCount=" + retryCount);
    	
    	HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {	
    		public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
    			System.out.println("try request: " + executionCount);
		        if (executionCount >= retryCount) {
		            // Do not retry if over max retry count
		            return false;
		        }
		        if (exception instanceof InterruptedIOException) {
		            // Timeout
		            return false;
		        }
		        if (exception instanceof UnknownHostException) {
		            // Unknown host
		            return false;
		        }
		        if (exception instanceof SSLException) {
		            // SSL handshake exception
		            return false;
		        }
		        HttpClientContext clientContext = HttpClientContext.adapt(context);
		        HttpRequest request = clientContext.getRequest();
		        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
		        if (idempotent) {
		            // Retry if the request is considered idempotent
		            return true;
		        }
		        return false;
    		}

    	};
    	
        return handler;

    }
    
    private static HttpRequestRetryHandler retryHandlerLambda(int retryCount){
    	System.out.println("HttpRequestRetryHandler retryCount=" + retryCount);
        return (exception, executionCount, context) -> {

            System.out.println("try request: " + executionCount);

            if (executionCount >= retryCount) {
                // Do not retry if over max retry count
                return false;
            }
            if (exception instanceof InterruptedIOException) {
                // Timeout
                return false;
            }
            if (exception instanceof UnknownHostException) {
                // Unknown host
                return false;
            }
            if (exception instanceof SSLException) {
                // SSL handshake exception
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) {
                // Retry if the request is considered idempotent
                return true;
            }
            return false;
        };
    }
}
