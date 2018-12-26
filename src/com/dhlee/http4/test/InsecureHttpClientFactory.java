package com.dhlee.http4.test;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

public class InsecureHttpClientFactory {
	 protected Logger log = Logger.getLogger(this.getClass());
	 DefaultHttpClient hc = new DefaultHttpClient();
		 
	 public DefaultHttpClient build() {
//	    	configureProxy();
			configureCookieStore();
			configureSSLHandling();
			return hc;
		}
	       
	        private void configureProxy() {
	                HttpHost proxy = new HttpHost("proxy.example.org", 3182);
	                hc.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	        }

		private void configureCookieStore() {
			CookieStore cStore = new BasicCookieStore();
			hc.setCookieStore(cStore);
		}

		private void configureSSLHandling() {
			Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
			SSLSocketFactory sf = buildSSLSocketFactory();
			Scheme https = new Scheme("https", 443, sf);
			SchemeRegistry sr = hc.getConnectionManager().getSchemeRegistry();
			sr.register(http);
			sr.register(https);
		}

		private SSLSocketFactory buildSSLSocketFactory() {
			TrustStrategy ts = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					return true; // heck yea!
				}
			};

			SSLSocketFactory sf = null;

			try {
				/* build socket factory with hostname verification turned off. */
				sf = new SSLSocketFactory(ts, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			} catch (NoSuchAlgorithmException e) {
				log.error("Failed to initialize SSL handling.", e);
			} catch (KeyManagementException e) {
				log.error("Failed to initialize SSL handling.", e);
			} catch (KeyStoreException e) {
				log.error("Failed to initialize SSL handling.", e);
			} catch (UnrecoverableKeyException e) {
				log.error("Failed to initialize SSL handling.", e);
			}

			return sf;
		}


}
