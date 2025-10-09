package co.com.activos.jadm0088.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author KPAZ
 */
public class OracleFactory {
    
    static Connection conexion;

    private static final String DATASOURCE_NAME = "DS_Intrauser_Acti";

    public static void cerrarConexion(Connection conexion) {
        try {
            CallableStatement cs
                    = conexion.prepareCall("{ CALL QB_DEFINIR_CONTEXTO_CTO.PL_CLEAR_SESION_USUARIO }");
            cs.execute();
            conexion.close();
        } catch (SQLException sqlExeption) {
            sqlExeption.getCause().printStackTrace();
        }
    }

/*
        public static Connection getConexion() {
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            //conexion = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=off)(FAILOVER=on)(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.21.59)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.21.59)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.21.59)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=DESA)))" ,"SANTIAHERNANDEZ" ,"desa1234");
                conexion = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=off)(FAILOVER=on)(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.21.59)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.21.59)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.21.59)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=DESA)))" ,"KPAZ", "desa1234");
            //conexion = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=off)(FAILOVER=on)(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.21.147)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.21.147)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.21.147)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=TEST)))", "JUPATARROYO", "");

        } catch (SQLException sqlE) {
            sqlE.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conexion;
    }
//*/
    public static void setConexion(Connection conexion) {
        OracleFactory.conexion = conexion;
    }//*/

    /*s
     * Metodo para la conexión a servidores weblogic. test, desarrollo y produccion
     *
     * @return
     */

    public static Connection getConexion() {
        try {
            InitialContext ic = new InitialContext();
            DataSource dt = (DataSource) ic.lookup(DATASOURCE_NAME);
            return dt.getConnection();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }//*/
}