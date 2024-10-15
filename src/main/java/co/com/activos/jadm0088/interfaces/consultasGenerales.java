package co.com.activos.jadm0088.interfaces;

import co.com.activos.jadm0088.interfaces.consutasGeneralesInterface;
import co.com.activos.jadm0088.util.OracleFactory;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author kevrodriguez
 */
public class consultasGenerales implements consutasGeneralesInterface {

    //◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►
    //Consulta las configuraciones de la tabla ADM.RUTA
    //◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►
    @Override
    public Map pb_consulta_properties(String vcProp_agrupacion) {

        Map<String, String> PROPERTIES_PARAMETROS = null;
        Connection conexion = OracleFactory.getConexion();
        String vcError;
        String key;
        try {
            String ConsultaSql = "{ call ADP.QB_UTIL_DBJAVA.pb_consulta_properties(?,?,?)}";
            CallableStatement callableStatement;
            callableStatement = conexion.prepareCall(ConsultaSql);

            //Parametros de entrada
            callableStatement.setString("VCPROP_AGRUPACION", vcProp_agrupacion);

            //Parametros de salida
            callableStatement.registerOutParameter("VCCONSULTA", OracleTypes.CURSOR);
            callableStatement.registerOutParameter("VCERROR", OracleTypes.VARCHAR);

            callableStatement.execute();
            vcError = callableStatement.getString("VCERROR");
            if (vcError == null || vcError.equals("")) {
                ResultSet rs = (ResultSet) callableStatement.getObject("VCCONSULTA");
                PROPERTIES_PARAMETROS = new HashMap<>();
                while (rs.next()) {

                    if (rs.getString("PROP_ENCRIPTADO").equals("S")) {
                        key = Password(rs.getString("PROP_VALOR"));
                    } else {
                        key = rs.getString("PROP_VALOR");

                    }
                    PROPERTIES_PARAMETROS.put(rs.getString("PROP_KEY"), key);
                }
            }
            callableStatement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                conexion.close();
            } catch (Exception edb2) {
                edb2.printStackTrace();
            }
        }
        return PROPERTIES_PARAMETROS;
    }

    //◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►
    //Se utilizo para desencriptar las contraseñas.
    //Todas las rutas que comienzen por PASS pasaran por este proceso.
    //
    //◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►◄►
    @Override
    public String Password(String vcphw_password) {
        Connection conexion = OracleFactory.getConexion();
        String ConsultaSql;
        String password = null;
        ConsultaSql = "{ ? =call adm.QB_SINMA_CORE_UTIL.FB_DESCENCRIPTAR(?) }";
        CallableStatement callableStatement;
        try {

            callableStatement = conexion.prepareCall(ConsultaSql);
            //Parametros de entrada
            callableStatement.registerOutParameter(1, OracleTypes.VARCHAR);
            callableStatement.setString(2, vcphw_password);
            callableStatement.execute();
            password = callableStatement.getString(1);

            callableStatement.close();
            return password;
        } catch (Exception edb) {
            edb.printStackTrace();
        } finally {
            try {
                conexion.close();
            } catch (Exception edb2) {
                edb2.printStackTrace();
            }
        }
        return password;
    }

}
