package co.com.activos.jadm0088.controller;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import oracle.sql.CLOB;
import java.util.Properties;

import javax.faces.FacesException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import oracle.jdbc.OracleTypes;
import java.util.Hashtable;

import co.com.activos.jadm0088.util.OracleFactory;
import co.com.activos.jadm0088.interfaces.LdapInterface;
import co.com.activos.jadm0088.model.Usuario;

public class ldapConexion implements LdapInterface {

    private static Properties credentials;

    public ldapConexion() {
        // credentials = new Properties();
        // credentials.load(getClass().getResourceAsStream("../ldapCredentials.properties"));
        ldapConfigDB properties = new ldapConfigDB();
        credentials = properties.getProp();

        //Thread currentThread = Thread.currentThread();
        //ClassLoader contextClassLoader = currentThread.getContextClassLoader();
        //InputStream propertiesStream = contextClassLoader.getResourceAsStream("/resources/ldapCredentials.properties");
//        InputStream propertiesStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/ldapCredentials.properties");
//        InputStream propertiesStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ldapCredentials.properties");
//        if (propertiesStream != null) {
//            credentials.load(propertiesStream);
//            // TODO close the stream
//        }
    }

    @Override
    public boolean loadUser(Usuario user, String rama) {
        DirContext ctx = getUserContext(user, rama);
        return ctx != null;
    }

    public DirContext getUserContext(Usuario user, String rama) {
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, credentials.getProperty("INITIAL_CONTEXT_FACTORY"));
            env.put(Context.PROVIDER_URL, credentials.getProperty("PROVIDER_URL"));
            env.put(Context.SECURITY_PRINCIPAL,
                    "uid=" + user.getLogin_usuario() + "," + rama + "," + credentials.getProperty("ARBOL_LDAP"));
            env.put(Context.SECURITY_CREDENTIALS, user.getClave_usuario());
            env.put(Context.SECURITY_AUTHENTICATION, credentials.getProperty("TIPO_AUTH_LDAP"));
            return new InitialDirContext(env);
        } catch (Exception e) {
            System.out.println("no ldap records found" + e.getMessage());

        }
        return null;
    }

    public String findPropertyByAccountName(DirContext ctx, String accountName, String property) {
        String result = "";
        try {
            String searchFilter = "(uid=" + accountName + ")";
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            /*
			 * searchControls .setReturningAttributes(new String[] {
			 * "userPassword", "uid", "givenName", "mail", "displayname" });
             */
            NamingEnumeration<SearchResult> results = ctx.search(credentials.getProperty("ARBOL_LDAP"), searchFilter,
                    searchControls);
            if (property.equals("userPassword")) {
                result = findPass(results);
            } else {
                result = findProperty(results, property);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public DirContext getContext() {
        Hashtable<String, String> environment = new Hashtable<>();

        //LDAPConnection.LDAP_V3
        environment.put(Context.INITIAL_CONTEXT_FACTORY, credentials.getProperty("INITIAL_CONTEXT_FACTORY"));
        environment.put(Context.PROVIDER_URL, credentials.getProperty("PROVIDER_URL"));
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, credentials.getProperty("SECURITY_PRINCIPAL"));
        environment.put(Context.SECURITY_CREDENTIALS, credentials.getProperty("PASS_ADMIN_LDAP"));
        //Adicionados
        environment.put(Context.REFERRAL, "follow");
        environment.put("java.naming.ldap.version", "2");
        try {
            DirContext context = new InitialDirContext(environment);
            return context;

        } catch (NamingException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return null;
    }

    @Override                                                              //indica que estás implementando el método de la interfaz LdapInterface
    public String findPropertyByAccountName2(String accountName, String property) {

        String result = "";

        DirContext ctx = getContext();
        String searchFilter = "(uid=" + accountName + ")";
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(new String[]{"userPassword", "uid", "givenName", "mail", "displayname"});

        try {
//            System.out.println(ctx);
            Object x = ctx.search("dc=activos,dc=com,dc=co", searchFilter, searchControls);
            NamingEnumeration<SearchResult> results = (NamingEnumeration<SearchResult>) x;
            if (property.equals("userPassword")) {
                result = findPass(results);
            } else {
                result = findProperty(results, property);
            }

        } catch (Exception e) {

            e.getMessage();
            e.printStackTrace();
            System.out.println(e);
        }
        return result;
    }

    public String findProperty(NamingEnumeration<SearchResult> results, String property) {
        String result = "";
        try {
            if (results.hasMoreElements()) {
                SearchResult searchResult = null;
                searchResult = (SearchResult) results.nextElement();
                Attributes attributes = searchResult.getAttributes();
                Attribute groupCn = attributes.get(property);
                String mail = "" + groupCn.get();

                result = mail;
                // make sure there is not another item available, there should
                // be only 1 match
                if (results.hasMoreElements()) {
                    System.err.println("Hay mas de un registro para esta cuenta");
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public String findPass(NamingEnumeration<SearchResult> results) {
        String result = "";
        try {
            if (results.hasMoreElements()) {
                SearchResult searchResult = null;
                searchResult = (SearchResult) results.nextElement();
                Attributes attributes = searchResult.getAttributes();
                Attribute groupCn = attributes.get("userPassword");
                result = new String((byte[]) groupCn.get());
                if (results.hasMoreElements()) {
                    System.err.println("Hay mas de un registro para esta cuenta");
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public boolean verificarCredencialesUsuario(String usuario, String claveanterior, String ramaLdap) {
        boolean estado = false;
        DirContext dc;

        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, credentials.getProperty("INITIAL_CONTEXT_FACTORY"));
            env.put(Context.PROVIDER_URL, credentials.getProperty("PROVIDER_URL"));
            // autenticaciones de este aplicativo solo sobre el arbol usuarios
            env.put(Context.SECURITY_PRINCIPAL,
                    "uid=" + usuario + "," + ramaLdap + "," + credentials.getProperty("ARBOL_LDAP"));
            env.put(Context.SECURITY_CREDENTIALS, claveanterior);
            env.put(Context.SECURITY_AUTHENTICATION, credentials.getProperty("TIPO_AUTH_LDAP"));
            try {
                dc = new InitialDirContext(env);
                estado = true;
                dc.close();
            } catch (NamingException ex) {
                estado = false;
            }

        } catch (Exception e) {
            System.out.println("BMX - RegistroLDAP - verificarCredencialesUsuario- ERRMSG: " + e);
        }
        return estado;
    }

    @Override
    public String validateRequest(String req) {
        String result = "";

        Connection conexion = OracleFactory.getConexion();

        try {
            CallableStatement cs = conexion.prepareCall("{ CALL QB_CONSOLA_BMX.PL_VALIDA_CAMBIO_CLAVE(?,?,?)}");
            cs.setString(1, req);
            cs.registerOutParameter(2, OracleTypes.VARCHAR);
            cs.registerOutParameter(3, OracleTypes.CURSOR);
            cs.execute();
            String vcError = cs.getString(2);

            if (vcError == null || vcError.equals("")) {
                ResultSet rs = (ResultSet) cs.getObject(3);

                while (rs.next()) {
                    result = rs.getString("estado");  //trae el estado del la columna con nombre "estado" el resultado desde el procedimiento almacenado
                }
                rs.close();
            }
            cs.close();
        } catch (Exception e) {
            System.out.println("No se puede cargar objeto de tabla agrupacionsaas, Error Causado por " + e);
        } finally {
            try {

                conexion.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public String updateState(String request_id) {
        String result = "";

        Connection conexion = OracleFactory.getConexion();

        try {
            CallableStatement cs = conexion.prepareCall("{ CALL QB_CONSOLA_BMX.PL_ACTUALIZA_CAMBIO_CLAVE(?,?)}");
            cs.setString(1, request_id);
            cs.registerOutParameter(2, OracleTypes.VARCHAR);
            cs.execute();

            if (cs.getString(2) != null) {
                result = cs.getString(2);
            }
            cs.close();
        } catch (SQLException e) {
            System.out.println("No se puede cargar objeto de tabla agrupacionsaas, Error Causado por " + e);
        } finally {
            try {
                conexion.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public String saveRequest(String userName) {
        SecureRandom random = new SecureRandom();
        String id = new BigInteger(130, random).toString(32);
        Connection conexion = null;

        try {
            conexion = OracleFactory.getConexion();
            CallableStatement cs = conexion.prepareCall("{ CALL QB_CONSOLA_BMX.PL_REGISTRO_RECORDAR_CLAVE(?,?,?,?)}");

            cs.setString(1, id);
            cs.setString(2, userName);
            cs.setString(3, "P");
            cs.registerOutParameter(4, OracleTypes.VARCHAR);

            cs.execute();
            if (cs.getString(4) != null) {
                return "-1";
            }
            cs.close();
        } catch (SQLException ee) {
            ee.printStackTrace();
        } finally {
            try {
                conexion.close();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
        return id;
    }

   /*
    @Override
    public void sendMail(String destino, String para, String asunto, String contenido) {
        Connection conexion = OracleFactory.getConexion();
        String error = null;
        try {
            String ConsultaSql;
            ConsultaSql = "{ call SENDMAIL(?,?,?,?,?,?,?,?,?) }";
            CallableStatement callableStatement = conexion.prepareCall(ConsultaSql);
            callableStatement.setString(1, destino);
            callableStatement.setString(2, para);
            callableStatement.setNull(3, OracleTypes.NULL);
            callableStatement.setNull(4, OracleTypes.NULL);
            callableStatement.setString(5, asunto);
            callableStatement.setString(6, contenido);
            callableStatement.setString(7, "");
            callableStatement.registerOutParameter(8, OracleTypes.VARCHAR);
            callableStatement.setString(9, "WEB");
            callableStatement.execute();
            error = (String) callableStatement.getString(8);
            if (error != null) {
                System.out.println(error);
            }
            callableStatement.close();
        } catch (SQLException e) { // TODO Auto-generated
            e.printStackTrace();
            throw new FacesException(e);
        } finally {
            try {
                conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
 */
    @Override
    public void sendMail(String vcDestino, String vcPara, String vcAsunto, String vcContenido) {
        Connection conexion = OracleFactory.getConexion();
        String error = null;
        try {
             // Crea un objeto CLOB para el contenido del correo
            CLOB clob = CLOB.createTemporary(conexion, false, CLOB.DURATION_SESSION);
            clob.setString(1, vcContenido); // Almacena el contenido en el CLOB
            
            String ConsultaSql = "{ CALL QB_CONSOLA_BMX.PL_CORREO_RESTAB_CLAVE(?, ?, ?, ?, ?) }";
            CallableStatement cs = conexion.prepareCall(ConsultaSql);
            cs.setString(1, vcDestino);   
            cs.setString(2, vcPara); 
            cs.setString(3, vcAsunto);          
            cs.setClob(4, clob); // convertir el Contenido HTML  a (CLOB)        
            cs.registerOutParameter(5, OracleTypes.VARCHAR);
            cs.execute();
            error = cs.getString(5);
            if (error != null && !error.isEmpty()) {
                System.out.println("Error al enviar el correo: " + error);
            } else {
                System.out.println("Correo enviado correctamente.");
            }
            cs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al ejecutar el procedimiento de envío de correo", e);
        } finally {
            try {
                conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
}
