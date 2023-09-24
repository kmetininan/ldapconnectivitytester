/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kmetininan.ldapconnectivitytester;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.prefs.Preferences;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author kamilmetini
 */
public abstract class MySSLSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory socketFactory;
    Preferences prefs = Preferences.userRoot().node("com.kmetininan.ldaptester.ui.LDAPTester");
    public MySSLSocketFactory()  {
        try {
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
// Using null here initialises the TMF with the default trust store.
            tmf.init((KeyStore) null);

// Get hold of the default trust manager
            X509TrustManager defaultTm = null;
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    defaultTm = (X509TrustManager) tm;
                    break;
                }
            }

            FileInputStream myKeys = new FileInputStream(prefs.get("ldap.keystore.path","").replace("\\", "\\\\"));

// Do the same with your trust store this time
// Adapt how you load the keystore to your needs
            KeyStore myTrustStore = KeyStore.getInstance("jks");
            myTrustStore.load(myKeys, (prefs.get("ldap.keystore.pass","")).toCharArray());

            myKeys.close();

            tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(myTrustStore);

// Get hold of the default trust manager
            X509TrustManager myTm = null;
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    myTm = (X509TrustManager) tm;
                    break;
                }
            }

// Wrap it in your own class.
            final X509TrustManager finalDefaultTm = defaultTm;
            final X509TrustManager finalMyTm = myTm;
            X509TrustManager customTm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    // If you're planning to use client-cert auth,
                    // merge results from "defaultTm" and "myTm".
                    return finalDefaultTm.getAcceptedIssuers();
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain,
                        String authType) throws CertificateException {
                    try {
                        finalMyTm.checkServerTrusted(chain, authType);
                    } catch (CertificateException e) {
                        // This will throw another CertificateException if this fails too.
                        finalDefaultTm.checkServerTrusted(chain, authType);
                    }
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain,
                        String authType) throws CertificateException {
                    // If you're planning to use client-cert auth,
                    // do the same as checking the server.
                    finalDefaultTm.checkClientTrusted(chain, authType);
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{customTm}, null);

            socketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace(System.err);
            /* handle exception */
        } catch (KeyStoreException ex) {
            ex.printStackTrace(System.err);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        } catch (CertificateException ex) {
            ex.printStackTrace(System.err);
        } catch (KeyManagementException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
     @SuppressWarnings("unused")
    public static SocketFactory getDefault()
    {
        return new MySSLSocketFactory() {};
    }

    @Override
    public String[] getDefaultCipherSuites()
    {
        return socketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites()
    {
        return socketFactory.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket socket, String string, int i, boolean bln) throws IOException
    {
        return socketFactory.createSocket(socket, string, i, bln);
    }

    @Override
    public Socket createSocket(String string, int i) throws IOException
    {
        return socketFactory.createSocket(string, i);
    }

    @Override
    public Socket createSocket(String string, int i, InetAddress ia, int i1) throws IOException
    {
        return socketFactory.createSocket(string, i, ia, i1);
    }

    @Override
    public Socket createSocket(InetAddress ia, int i) throws IOException
    {
        return socketFactory.createSocket(ia, i);
    }

    @Override
    public Socket createSocket(InetAddress ia, int i, InetAddress ia1, int i1) throws IOException
    {
        return socketFactory.createSocket(ia, i, ia1, i1);
    }

    @Override
    public Socket createSocket() throws IOException
    {
        return socketFactory.createSocket();
    }

}
