/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kmetininan.ldapconnectivitytester;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 *
 * @author kamilmetini
 */
public class Utils {
    
    public static String getUserInfo(DirContext context,String domainName,String userName) 
    {
        String userInfo = "";
        try {
            
            // locate this user's record
            SearchControls controls = new SearchControls();
            controls.setSearchScope( SearchControls.SUBTREE_SCOPE );

            NamingEnumeration renum = context.search(  domainName , "(& (cn=" + userName + ")(objectClass=Person))", controls );

            if(renum.hasMore())
            {
            SearchResult result = ( SearchResult ) renum.next();
            Logger.getLogger(Utils.class.getName()).log(Level.INFO, "result {0}", result.toString());
            Attribute memberOf = result.getAttributes().get("groupMembership");
            if(memberOf!=null)
            {// null if this user belongs to no group at all
                for(int i=0; i<memberOf.size(); i++)
                {
                    Logger.getLogger(Utils.class.getName()).log(Level.INFO, "memberOf {0}", memberOf.get(i).toString());
                    String[] temp = memberOf.get(i).toString().split(",");
                    
                    userInfo += temp[0].replace("cn=", "")+"\n";
                }
            }
            }
            
            
        } catch (Exception ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userInfo;
    }
    
    public static String getPrincipalName(String userName)
    {
        return "cn="+userName+",ou=accounts,o=zf";
    }
}
