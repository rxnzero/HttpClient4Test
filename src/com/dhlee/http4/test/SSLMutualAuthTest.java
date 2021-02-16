package com.dhlee.http4.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.PrivateKeyDetails;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.apache.http.ssl.SSLContexts;
import org.json.simple.JSONObject;

/*
Secure Sockets Layer (SSL) is a standard security technology for establishing an encrypted link 
between a server and a client.
 It is widely applied during transactions involving sensitive or personal information 
 such as credit card numbers, login credentials, and Social Security numbers. 
 The encryption can be applied in one direction or both ? one-way or two-way. In one-way SSL,
 the client confirms the identity of the server while the identity of the client remains anonymous. 
 In two-way SSL, AKA mutual SSL, the client confirms the identity of the server and the server confirms the identity of the client.
Two-way SSL begins with a ¡°hello¡± from the client to the server. 
The server replies with a ¡°hello¡± paired with its public certificate.
The client verifies the received certificate using certificates stored in the client¡¯s TrustStores.
If the server certificate validation is successful, the client will present certificate stores in their KeyStores.
The server validates the received certificate using the server¡¯s TrustStores. 
The server decrypts session keys using the server¡¯s private key to establish a secure connection.
Java employs Java Keystore (JKS), a password-protected database for certificates and keys. 
Each entry must be identified by a unique alias. Keystore provides credentials.
Java also uses Truststore which is located in $JAVA_HOME/lb/security/cacerts. 
It stores trusted Certificate Authority (CA) entries and self-signed certificates from trusted third parties. Truststore verifies server identities.
Java also provides keytool, a command-line tool to maintain the Keystore and the Truststore.
You can run TrustStore using the following code. 
Replace $CERT_ALIAS and $CERT_PASSWORD with your alias and password, respectively.

1) If we do not have the server certificate, we use openssl to retrieve it
echo -n | openssl s_client -connect SERVERDOMAIN:PORT -servername SERVERDOMAIN
-key myclient.key -cert myclient.cert.pem
| sed -ne ¡®/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p¡¯
| tee ¡°server.crt¡±

2) Create the Truststore from the server certificate
keytool -import -alias $CERT_ALIAS -file server.crt -keystore truststore.jks -deststorepass $CERT_PASSWORD


Next, generate the IdentityStore. Replace $CERT_ALIAS and $CERT_PASSWORD with your alias and password, respectively.
1) Concatenate all certificates into one PEM file
cat intermediate.cert.pem myclient.cert.pem myclient.key > full-chain.keycert.pem

2) Generate the PKCS12 keystore with the alias of the server url
openssl pkcs12 -export -in full-chain.keycert.pem
-out full-chain.keycert.p12
-password env:$CERT_PASSWORD 
-name $CERT_ALIAS 
-noiter -nomaciter  

3) Convert .p12 to .jks
keytool -importkeystore -srckeystore full-chain.keycert.p12
-srcstoretype pkcs12 -srcalias $CERT_ALIAS -srcstorepass $CERT_PASSWORD 
-destkeystore identity.jks -deststoretype jks
-deststorepass $CERT_PASSWORD -destalias $CERT_ALIAS
 */

public class SSLMutualAuthTest {

	public SSLMutualAuthTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main (String[] args) {
		System.out.println("MagicDude4Eva 2-way / mutual SSL-authentication test");
		org.apache.log4j.BasicConfigurator.configure();
//		Logger.getRootLogger().setLevel(Level.INFO);
		try {
			String CERT_ALIAS = "myalias";
			String CERT_PASSWORD = "mypassword";
			
			KeyStore identityKeyStore = KeyStore.getInstance("jks");
			FileInputStream identityKeyStoreFile = new FileInputStream(new File("identity.jks"));
			identityKeyStore.load(identityKeyStoreFile, CERT_PASSWORD.toCharArray());

			KeyStore trustKeyStore = KeyStore.getInstance("jks");
			FileInputStream trustKeyStoreFile = new FileInputStream(new File("truststore.jks"));
			trustKeyStore.load(trustKeyStoreFile, CERT_PASSWORD.toCharArray());
			
			SSLContext sslContext = SSLContexts.custom()
					// load identity keystore
					.loadKeyMaterial(identityKeyStore, CERT_PASSWORD.toCharArray(), new PrivateKeyStrategy() {
						@Override
						public String chooseAlias(Map<String, PrivateKeyDetails> aliases, Socket socket) {
							return CERT_ALIAS;
						}
					})
					// load trust keystore
					.loadTrustMaterial(trustKeyStore, null)
					.build();
			SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
					new String[]{"TLSv1.2", "TLSv1.1"},
					null,
					SSLConnectionSocketFactory.getDefaultHostnameVerifier());
			CloseableHttpClient client = HttpClients.custom()
					.setSSLSocketFactory(sslConnectionSocketFactory)
					.build();
			// Call a SSL-endpoint
			JSONObject json = new JSONObject();
			json.put("param1", "value1");
			json.put("param2", "value2");
			callEndPoint (client, "https://secure.server.com/endpoint", json);
		} catch (Exception ex) {
			System.out.println("Boom, we failed: " + ex);
			ex.printStackTrace();
		}
	}

	private static void callEndPoint (CloseableHttpClient httpClient, String aEndPointURL, JSONObject aPostParams) {
		try {
			System.out.println("Calling URL: " + aEndPointURL);
			HttpPost post = new HttpPost(aEndPointURL);
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");
			StringEntity entity = new StringEntity(aPostParams.toString());
			post.setEntity(entity);
			System.out.println("**POST** request Url: " + post.getURI());
			System.out.println("Parameters : " + aPostParams);
			HttpResponse response = httpClient.execute(post);
			int responseCode = response.getStatusLine().getStatusCode();
			System.out.println("Response Code: " + responseCode);
			System.out.println("Content:-n");
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception ex) {
			System.out.println("Boom, we failed: " + ex);
			ex.printStackTrace();
		}
	}
}