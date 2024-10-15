package co.com.activos.jadm0088.controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import co.com.activos.jadm0088.util.OracleFactory;
import java.sql.PreparedStatement;
import oracle.jdbc.OracleTypes;
import co.com.activos.jadm0088.model.DominioSAAS;
import co.com.activos.jadm0088.interfaces.ViewInterface;

@SuppressWarnings("serial")
public class ViewDao implements ViewInterface {

    private List<DominioSAAS> dominiosToLoad;
    private String log;

    @Override
    public List<DominioSAAS> loadDominios(Long emc_id) {
        dominiosToLoad = new ArrayList<DominioSAAS>();
        Connection conexion = null;

        try {
            conexion = OracleFactory.getConexion();
            CallableStatement cs = conexion.prepareCall("{ CALL QB_CONSOLA_BMX.PL_CARGAR_DOMINIOSSAASBYEMCID(?,?,?)}");
            cs.setLong(1, emc_id);
            cs.registerOutParameter(2, OracleTypes.VARCHAR);
            cs.registerOutParameter(3, OracleTypes.CURSOR);
            cs.execute();

            if (cs.getString(2) == null || cs.getString(2).equals("")) {
                ResultSet rs = (ResultSet) cs.getObject(3);

                DominioSAAS dtmp;
                while (rs.next()) {
                    dtmp = new DominioSAAS();
                    dtmp.setAud_fecha(rs.getDate("aud_fecha"));
                    dtmp.setAud_usuario(rs.getString("aud_usuario"));
                    dtmp.setDsa_color_etiqueta(rs.getString("dsa_color_etiqueta"));
                    dtmp.setDsa_estado(rs.getString("dsa_estado"));
                    dtmp.setDsa_id(rs.getLong("dsa_id"));
                    dtmp.setDsa_nombre(rs.getString("dsa_nombre"));
                    dtmp.setDsa_orden(rs.getLong("dsa_orden"));
                    dtmp.setDsa_ramaldap(rs.getString("dsa_ramaldap"));
                    dtmp.setEsa_id(rs.getLong("esa_id"));
                    dtmp.setDsa_filtroemp(rs.getString("dsa_filtroemp"));
                    dominiosToLoad.add(dtmp);
                }
            } else {
                cs.getString(2);
            }
            cs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conexion.close();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return dominiosToLoad;
    }

    @Override
    public String loadRamaLdap(String userName) {
        String rama = "";
        Connection conexion = null;
        try {

            conexion = OracleFactory.getConexion();

            String query = "select d.dsa_ramaldap from usuarios u ,DOMINIO_SAAS d where usu_usuario =  ? AND u.rod_id = d.dsa_id";
            PreparedStatement ps = conexion
                    .prepareCall(query);
            ps.setString(1, userName.toUpperCase());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rama = rs.getString(1);
            }
            ps.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            try {
                conexion.close();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return rama;
    }

}
