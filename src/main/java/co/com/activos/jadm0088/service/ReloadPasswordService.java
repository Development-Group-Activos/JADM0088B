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
import co.com.activos.jadm0088.model.DominioSAAS;
import co.com.activos.jadm0088.controller.Ldap;
import co.com.activos.jadm0088.controller.View;
import co.com.activos.jadm0088.util.OracleFactory;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import javax.naming.directory.Attributes;

/**
 *
 * @author kpaz
 */
public class ReloadPasswordService {
    
    private static final String SECRET_KEY = "JADM0088SECRETKY"; // de 16 caracteres para AES-128 esto lo mando desde el front

    // Método para desencriptar
    public static String decryptAES(String encryptedText) throws Exception {
        byte[] keyBytes = SECRET_KEY.getBytes("UTF-8");
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText); // Decodificar desde Base64
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        
        return new String(decryptedBytes, "UTF-8"); 
    }

    // Guardar la solicitud de restablecimiento de contraseña
    public String createPassword(String encryptedUserName) throws Exception {
        String userName = decryptAES(encryptedUserName);
        return Ldap.saveRequest(userName);
    }
    
    // Buscar dominio
    public String obtenerDominioUsuario(String userName) {
    return Ldap.getUsuarioDominio(userName);
    }
    
     // validar si ya se crear solicitudes 
    public boolean hasPendingRequest(String encryptedUserName) throws Exception {
        
        boolean hasPending = false;
        try (Connection connection = OracleFactory.getConexion()) {
            String userName = decryptAES(encryptedUserName);
            String query = "SELECT COUNT(*) FROM ADM.RECORDAR_CLAVE_SOLICITUDES WHERE RCS_USER_ID = ? AND RCS_ESTADO = 'M'";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                hasPending = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hasPending;
    }

    // Validar si la solicitud de restablecimiento es válida
    public String validatePassword(String requestId) {
        if (!isRequestValid(requestId)) {
            return "ERROR: Solicitud inválida o expirada.";
        }   
        return Ldap.reloadPass(requestId);
    }
    

    private boolean isRequestValid(String requestId) {
        boolean isValid = false;
        try (Connection connection = OracleFactory.getConexion()) {
            String query = "SELECT COUNT(*) FROM ADM.RECORDAR_CLAVE_SOLICITUDES WHERE RCS_ID = ? AND RCS_ESTADO = 'P'";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, requestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                isValid = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isValid;
    }
    
    //  método para buscar usuario por requestId
    public String getUserByRequestId(String requestId) {
        String userName = null;
        try (Connection connection = OracleFactory.getConexion()) {
            String query = "SELECT RCS_USER_ID FROM ADM.RECORDAR_CLAVE_SOLICITUDES WHERE RCS_ID = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, requestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                userName = rs.getString("RCS_USER_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userName;
    }
 

    
    
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        String specialCharacters = "!@#$%^&*(),.?\":{}|<>";

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (specialCharacters.indexOf(c) != -1) {
                hasSpecialChar = true;
            }
        }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
    

        public String updatePassword(String encryptedUserName, String encryptedNewPassword, String requestId, String domain) throws Exception {
  
        // Desencriptar valores que llegan desde el frontend
        String userName = decryptAES(encryptedUserName);
        String newPassword = decryptAES(encryptedNewPassword);

        // Validaciones: Longitud, mayúsculas, minúsculas, números, caracteres especiales
        if (!isValidPassword(newPassword)) {
            return " La nueva contraseña no cumple con los requisitos de seguridad.";
        }
    
        
 
        List<DominioSAAS> dominios = View.loadDominios(Long.parseLong("1"));
        DominioSAAS selectedDomain = null;

        for (DominioSAAS x : dominios) {
            if (domain.equals(x.getDsa_ramaldap())) {
                selectedDomain = x;
                break;
            }
        }

        if (selectedDomain == null) {
            return "Error: Dominio no encontrado.";
        }

        
        // Obtener las últimas 5 contraseñas desde la base de datos
        List<String> previousPasswords = getLastNPasswords(userName, 5);
        String hashedNewPassword = hashPassword(newPassword);

        // Verificar si la nueva contraseña ya fue utilizada
        if (previousPasswords.contains(hashedNewPassword)) {
            return "No puedes reutilizar una de las últimas 5 contraseñas anteriores.";
        }

        // Llamar al método modificado que ahora retorna un mensaje detallado
        String updateResult = updateLdapPassword(userName, newPassword, selectedDomain.getDsa_ramaldap());

        if (updateResult.equals("Contraseña actualizada correctamente.")) {
            saveNewPasswordToHistory(userName, hashedNewPassword);
            Ldap.updateState(requestId);
            return updateResult;
        } else {
            return updateResult;
        }
    }

        // Método para actualizar la contraseña en LDAP
       private String updateLdapPassword(String userName, String newPassword , String domain) {
            try {
                DirContext ctx = Ldap.getContext();
                if (ctx == null) {
                    return "Error: No se pudo obtener el contexto LDAP.";
                }
                
                String dn = "uid=" + userName + "," + domain + ",dc=activos,dc=com,dc=co";
                Attributes attrs = ctx.getAttributes(dn, new String[]{"userPassword"});
                String currentPassword = new String((byte[]) attrs.get("userPassword").get());

                // Guardar la contraseña actual en la base de datos antes de la comparación
                String hashedCurrentPassword = hashPassword(currentPassword);
                saveNewPasswordToHistory(userName, hashedCurrentPassword);

                // Validación: la nueva contraseña no puede ser igual a la actual
                if (currentPassword.equals(newPassword)) {
                    ctx.close();
                    return "La nueva contraseña no puede ser igual a las ultimas 5 contraseñas.";
                }

                // Modificación de la contraseña en LDAP
                ModificationItem[] mods = new ModificationItem[1];
                Attribute mod = new BasicAttribute("userPassword", newPassword);
                mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod);
                ctx.modifyAttributes(dn, mods);

                // Verificación de cambio de contraseña
                attrs = ctx.getAttributes(dn, new String[]{"userPassword"});
                String updatedPassword = new String((byte[]) attrs.get("userPassword").get());

                ctx.close();

                if (updatedPassword.equals(newPassword)) {
                    return "Contraseña actualizada correctamente.";
                } else {
                    return "No se pudo actualizar la contraseña.";
                }

            } catch (NamingException e) {
                return "Por favor verifica que el usuario y el dominio sean correctos";
            }
        }
      

        // Método para obtener las últimas N contraseñas desde la base de datos
        private List<String> getLastNPasswords(String userName, int n) {
            List<String> passwords = new ArrayList<>();
            try (Connection connection = OracleFactory.getConexion()) {
                String query = "SELECT hashed_password FROM ADM.PASSWORD_HISTORY WHERE user_id = ? GROUP BY hashed_password ORDER BY MAX(HIST_ID) DESC FETCH FIRST ? ROWS ONLY";
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
    
    // Método para capturar el email de usuario
    public String findPropertyByAccountName2(String encryptedUserName, String property) throws Exception {
        String userName = decryptAES(encryptedUserName);
        
        return Ldap.findPropertyByAccountName2(userName, property);
    }
    // Método para enviar el email de usuario
     public void sendPasswordResetEmail(String userEmail, String encryptedUserName, String requestUrl) throws Exception {
        System.out.println("Iniciando el proceso de envío de correo...");
        String userName = decryptAES(encryptedUserName);
        
        String template = loadEmailTemplate("templates/reset_password_template.html");
        
        if (template != null) {

            String emailContent = template.replace("{userName}", userName)
                                          .replace("{requestUrl}", requestUrl);
           

            System.out.println("Preparando el correo con los siguientes datos:");
            System.out.println("Destinatario: " + userEmail);
            System.out.println("Asunto: Reestablecer Clave BMX");
            System.out.println("Contenido del correo: " + emailContent);

            Ldap.sendMail("notificacion@activos.com.co", userEmail, "Reestablecer Clave BMX", emailContent);
            System.out.println("Correo enviado.");
        } else {
            System.out.println("Error: No se pudo cargar la plantilla de correo.");
        }
    }
     //Se carga el template
    private String loadEmailTemplate(String filePath) {
        System.out.println("Cargando la plantilla de correo desde: " + filePath);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(filePath), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            System.out.println("Error al cargar la plantilla de correo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
