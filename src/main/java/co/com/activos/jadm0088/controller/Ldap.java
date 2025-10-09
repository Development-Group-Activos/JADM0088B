package co.com.activos.jadm0088.controller;

import java.lang.Exception;
import co.com.activos.jadm0088.controller.ldapConexion;
import co.com.activos.jadm0088.interfaces.LdapInterface;

import co.com.activos.jadm0088.model.Usuario;
import javax.naming.directory.DirContext;

public abstract class Ldap {

    public static boolean loadUser(Usuario user, String rama) {
        LdapInterface ldap = new ldapConexion();
        return ldap.loadUser(user, rama);
    }

    public static String reloadPass(String request_id) {

        try {
            LdapInterface ldap = new ldapConexion();
            return ldap.validateRequest(request_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request_id;

    }

    public static String updateState(String request_id) {

        try {
            LdapInterface ldap = new ldapConexion();
            return ldap.updateState(request_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String saveRequest(String userName) {
        try {
            LdapInterface ldap = new ldapConexion();
            return ldap.saveRequest(userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String findPropertyByAccountName2(String userName, String property) {
        
        try {
            LdapInterface ldap = new ldapConexion();
            return ldap.findPropertyByAccountName2(userName, property);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public static void sendMail(String destino, String para, String asunto, String contenido) {
        try {
            LdapInterface ldap = new ldapConexion();
            ldap.sendMail(destino, para, asunto, contenido);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static DirContext getContext() {
        ldapConexion ldap = new ldapConexion();
        return ldap.getContext(); // Llama a la implementación en ldapConexion
    }
    
    public static String getUsuarioDominio(String userName) {
    try {
        LdapInterface ldap = new ldapConexion();
        return ldap.getUsuarioDominio(userName);
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

}
