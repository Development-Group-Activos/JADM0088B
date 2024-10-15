package co.com.activos.jadm0088.controller;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.faces.FacesException;

import co.com.activos.jadm0088.util.OracleFactory;
import oracle.jdbc.OracleTypes;
import co.com.activos.jadm0088.interfaces.ReloadPasswordInterface;

public class ReloadPasswordDAO implements ReloadPasswordInterface {

    public ReloadPasswordDAO() {
        super();
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
//            ystem.out.println("Peticion Registrada");
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            try {
                conexion.close();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return id;
    }

    @Override
    public void sendMail(String destino, String para, String asunto, String contenido) {
        Connection conexion = OracleFactory.getConexion();
        String error = null;
        try {
            String ConsultaSql = null;
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
                System.out.println("Error:jadm0088 sendMail"+error);
            }
            conexion.close();
        } catch (Exception e) { // TODO Auto-generated
            e.printStackTrace();
            throw new FacesException(e);
        }
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

            if (cs.getString(2) == null || cs.getString(2).equals("")) {
                ResultSet rs = (ResultSet) cs.getObject(3);

                while (rs.next()) {
                    result = rs.getString("estado");
                }
            }
        } catch (Exception e) {
            System.out.println("No se puede cargar objeto de tabla agrupacionsaas, Error Causado por " + e.getMessage());
            
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
    public String updateState(String req) {
        String result = "";

        Connection conexion = OracleFactory.getConexion();

        try {
            CallableStatement cs = conexion.prepareCall("{ CALL QB_CONSOLA_BMX.PL_ACTUALIZA_CAMBIO_CLAVE(?,?)}");
            cs.setString(1, req);
            cs.registerOutParameter(2, OracleTypes.VARCHAR);
            cs.execute();

            if (cs.getString(2) != null) {
                result = cs.getString(2);
            }
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
}
