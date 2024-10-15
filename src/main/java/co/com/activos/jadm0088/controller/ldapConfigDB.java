package co.com.activos.jadm0088.controller;

import co.com.activos.jadm0088.controller.consultasGeneralesAbstract;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class ldapConfigDB {

    private static Properties prop = new Properties();

    public ldapConfigDB() {

    }

    public static Properties getProp() {
        if (prop.isEmpty()) {
            Map map = consultasGeneralesAbstract.pb_rutas_credenciales_ldap();
            for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry next = (Map.Entry) iterator.next();
                prop.setProperty(next.getKey().toString(), next.getValue().toString());
            }
        }
        return prop;
    }

    public static void setProp(Properties prop) {
        ldapConfigDB.prop = prop;
    }

}
