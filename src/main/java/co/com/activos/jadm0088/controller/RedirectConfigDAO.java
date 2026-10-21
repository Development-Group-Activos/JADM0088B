package co.com.activos.jadm0088.controller;

import co.com.activos.jadm0088.util.OracleFactory;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import oracle.jdbc.OracleTypes;

/**
 * Acceso a la configuracion de la URL de continuacion del restablecimiento.
 */
public class RedirectConfigDAO {

    public String obtenerUrlRedireccion(String emp) throws SQLException {
        String llamada = "{ CALL ADM.QB_CONSOLA_BMX.PL_OBTENER_URL_REDIRECCION(?,?,?,?) }";

        try (Connection conexion = OracleFactory.getConexion();
                CallableStatement cs = conexion.prepareCall(llamada)) {
            cs.setString(1, emp);
            cs.registerOutParameter(2, OracleTypes.VARCHAR);
            cs.registerOutParameter(3, OracleTypes.VARCHAR);
            cs.registerOutParameter(4, OracleTypes.VARCHAR);
            cs.execute();

            String url = cs.getString(2);
            String estadoProceso = cs.getString(3);
            String mensajeProceso = cs.getString(4);

            if (!"OK".equals(estadoProceso) || url == null || url.trim().isEmpty()) {
                throw new SQLException(mensajeProceso == null
                        ? "No fue posible obtener la URL de redireccion."
                        : mensajeProceso);
            }

            return url.trim();
        }
    }
}
