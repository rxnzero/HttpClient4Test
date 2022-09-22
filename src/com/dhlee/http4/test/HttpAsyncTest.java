package com.dhlee.http4.test;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.protocol.HttpContext;

public class HttpAsyncTest {

  public static void main(final String[] args) throws Exception {

    final CloseableHttpAsyncClient httpclient = HttpAsyncClients
        .createDefault();
    httpclient.start();
    
    String targetUrl = 
    		"http://localhost:8080/example/test.jsp";
//    "http://www.google.com/";
    try {
      final Future<Boolean> future = httpclient.execute(
          HttpAsyncMethods.createGet(targetUrl),
          new MyResponseConsumer(), null);
      final Boolean result = future.get();
      if (result != null && result.booleanValue()) {
        System.out.println("Request successfully executed");
      } else {
        System.out.println("Request failed");
      }
      System.out.println("Shutting down");
    } finally {
      httpclient.close();
    }
    System.out.println("Done");
  }

  static class MyResponseConsumer extends AsyncCharConsumer<Boolean> {

    @Override
    protected void onResponseReceived(final HttpResponse response) {
    	System.out.println(">> onResponseReceived - " + response);
    }

    @Override
    protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl)
        throws IOException {
    	System.out.println(">> onCharReceived - " + buf.toString() +" : " + ioctrl.toString());
      while (buf.hasRemaining()) {
        System.out.print(buf.get());
      }
    }

    @Override
    protected void releaseResources() {
    	System.out.println(">> releaseResources");
    }

    @Override
    protected Boolean buildResult(final HttpContext context) {
    	System.out.println(">> buildResult - " + context.toString());
      return Boolean.TRUE;
    }
  }
}