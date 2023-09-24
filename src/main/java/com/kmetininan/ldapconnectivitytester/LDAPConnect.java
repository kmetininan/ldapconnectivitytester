/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kmetininan.ldapconnectivitytester;

import java.util.Hashtable;

import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author kamilmetini
 */
public class LDAPConnect {

    public static String connect(String domainName,String url, String userName, String credentials,boolean isSecure,JButton btn) {

        Hashtable env = new Hashtable();
        String principal = Utils.getPrincipalName(userName);
        env.put(DirContext.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(DirContext.SECURITY_PRINCIPAL, principal);
        env.put(DirContext.PROVIDER_URL, url);
        env.put(DirContext.SECURITY_CREDENTIALS, credentials);
        env.put(DirContext.SECURITY_AUTHENTICATION, "simple");
        env.put(DirContext.REFERRAL, "follow");  
        
        if(isSecure)
        {
        env.put("java.naming.ldap.factory.socket", "com.kmetininan.ldapconnectivitytester.MySSLSocketFactory");
        }
        
        DirContext dirContext = null;
        try {
           dirContext = new InitialDirContext(env);
            
            JOptionPane.showMessageDialog(btn, "Connected.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return Utils.getUserInfo(dirContext,domainName,userName);
                  
        } catch (Exception e) {
           
            JOptionPane.showMessageDialog(btn, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
   
 return "";
    }
}
