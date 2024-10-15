package co.com.activos.jadm0088.service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import co.com.activos.jadm0088.model.Usuario;
import co.com.activos.jadm0088.model.DominioSAAS;
import co.com.activos.jadm0088.controller.Ldap;
import co.com.activos.jadm0088.controller.View;
import co.com.activos.jadm0088.util.OracleFactory;


import javax.naming.directory.Attributes;

/**
 *
 * @author kpaz
 */
public class ReloadPasswordService {

    // Guardar la solicitud de restablecimiento de contraseña
    public String createPassword(String userName) {
        return Ldap.saveRequest(userName);
    }

    // Validar si la solicitud de restablecimiento es válida
    public String validatePassword(String requestId) {
        return Ldap.reloadPass(requestId);
    }

    // Actualizar la contraseña del usuario
    public boolean updatePassword(String userName, String newPassword, String requestId, String domain) {
        List<DominioSAAS> dominios = View.loadDominios(Long.parseLong("1"));     //id=1 DSA_RAMALDAL = ou=Usuarios
        DominioSAAS selectedDomain = null;
        // Busca el dominio seleccionado
        for (DominioSAAS x : dominios) {
            if (domain.equals(x.getDsa_ramaldap())) {
                selectedDomain = x;
                break;
            }
        }
        // Si el dominio es nulo, devuelve error
        if (selectedDomain == null) {
            System.out.println("Error: Dominio no encontrado");
            return false;
        }      
        /////////////////////////////////////////////////////////////////////////////////////////
        //verificaciones de seguridad para que la nueva contraseña no sea igual a las ultimas 5//
        //Obtener las últimas 5 contraseñas desde la base de datos
           List<String> previousPasswords = getLastNPasswords(userName, 5);

           // Verificar si la nueva contraseña ya fue utilizada anteriormente
           String hashedNewPassword = hashPassword(newPassword);
           if (previousPasswords.contains(hashedNewPassword)) {
               System.out.println("Error: No puedes reutilizar una de las últimas 5 contraseñas.");
               return false;
           }    
        /////////////////////////////////////////////////////////////////////////////////////////
        
        // Actualiza la contraseña usando el contexto LDAP
        boolean isPasswordUpdated = updateLdapPassword(userName, newPassword, selectedDomain.getDsa_ramaldap());

        if (isPasswordUpdated) {
            // Almacenar la nueva contraseña en el historial
            saveNewPasswordToHistory(userName, hashedNewPassword);
            System.out.println("Contraseña actualizada correctamente.");
            // Actualizar el estado de la solicitud a 'C' (Cambiada)
            return Ldap.updateState(requestId).isEmpty();
        } else {
            System.out.println("Error al actualizar la contraseña.");
            return false;
        }
    }

    // Método para actualizar la contraseña en LDAP
    private boolean updateLdapPassword(String userName, String newPassword, String domain) {
        try {
            DirContext ctx = Ldap.getContext(); // Obtiene el contexto de LDAP
            if (ctx == null) {
                System.out.println("Error al obtener el contexto LDAP");
                return false;
            }

            // Obtener la contraseña actual
            String dn = "uid=" + userName + "," + domain + ",dc=activos,dc=com,dc=co";
            Attributes attrs = ctx.getAttributes(dn, new String[]{"userPassword"});
            String currentPassword = new String((byte[]) attrs.get("userPassword").get());
            
            

            // Verifica si la nueva contraseña es igual a la actual
            if (currentPassword.equals(newPassword)) {
                System.out.println("Error: La nueva contraseña no puede ser igual a la actual.");
                ctx.close();
                return false;
                
            }

            // Prepara los cambios para la nueva contraseña
            ModificationItem[] mods = new ModificationItem[1];
            Attribute mod = new BasicAttribute("userPassword", newPassword);
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod);

            // Realiza la modificación en LDAP
            ctx.modifyAttributes(dn, mods);

            // Verificación: Leer la contraseña para asegurarse de que el cambio se realizó
            attrs = ctx.getAttributes(dn, new String[]{"userPassword"});
            String updatedPassword = new String((byte[]) attrs.get("userPassword").get());

            if (updatedPassword.equals(newPassword)) {
                System.out.println("La contraseña se actualizó correctamente.");
                ctx.close();
                return true;
            } else {
                System.out.println("Error: la contraseña no se actualizó correctamente.");
                ctx.close();
                return false;
            }
        } catch (NamingException e) {
            System.out.println("Error al actualizar la contraseña en LDAP: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    /////////////////////////////////////////////////////////
    //metodos para validar las ultimas 5 contraseñas///
    
    // Método para obtener las últimas N contraseñas desde la base de datos
    private List<String> getLastNPasswords(String userName, int n) {
        List<String> passwords = new ArrayList<>();
        try (Connection connection = OracleFactory.getConexion()) {
            String query = "SELECT hashed_password FROM ADM.PASSWORD_HISTORY WHERE user_id = ? ORDER BY fecha_cambio DESC FETCH FIRST ? ROWS ONLY";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, userName);
            ps.setInt(2, n);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                passwords.add(rs.getString("hashed_password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passwords;
    }
    
     // Método para guardar la nueva contraseña en el historial
    private void saveNewPasswordToHistory(String userName, String hashedPassword) {
        try (Connection connection = OracleFactory.getConexion()) {
            String query = "INSERT INTO ADM.PASSWORD_HISTORY (user_id, hashed_password, fecha_cambio) VALUES (?, ?, SYSDATE)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, userName);
            ps.setString(2, hashedPassword);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Método para generar el hash de una contraseña usando SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar el hash de la contraseña", e);
        }
    }
    
    /////////////////////////////////////////////////////////
    
    // Método para capturar el email de usuario
    public String findPropertyByAccountName2(String userName, String property) {
        return Ldap.findPropertyByAccountName2(userName, property);
    }

    // Método para enviar notificación por correo
    public void sendPasswordResetEmail(String userEmail, String userName, String requestUrl) {//requestUrl es la url para que se envia dede el front para acceder a la ventana de ingresar nueva password
        //String template = "<html> ... </html>"; // Construcción de plantilla de email
        System.out.println("Iniciando el proceso de envío de correo...");  // Log inicial
        String template = loadEmailTemplate("templates/reset_password_template.html");

        if (template != null) {
            // Para reemplazar los marcadores de posición en la plantilla
            String emailContent = template.replace("{userName}", userName)
                    .replace("{requestUrl}", requestUrl);
            
            // Log de los datos antes de enviar el correo
            System.out.println("Preparando el correo con los siguientes datos:");
            System.out.println("Destinatario: " + userEmail);
            System.out.println("Asunto: Reestablecer Clave BMX ACTIVOS S.A.");
            System.out.println("Contenido del correo: " + emailContent);

            // Enviar correo
            Ldap.sendMail("notificacion@activos.com.co", userEmail, "Reestablecer Clave BMX ACTIVOS S.A.", emailContent);
            System.out.println("Correo enviado.");  // Log de éxito en el envío
        } else {
            System.out.println("Error: No se pudo cargar la plantilla de correo.");
        }
    }

    // Método para cargar la plantilla de correo desde el classpath
    private String loadEmailTemplate(String filePath) {
        System.out.println("Cargando la plantilla de correo desde: " + filePath);  // Log inicial
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(filePath), StandardCharsets.UTF_8))) {
            
            // Leer todo el contenido del archivo
          return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            System.out.println("Error al cargar la plantilla de correo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    
    
}
