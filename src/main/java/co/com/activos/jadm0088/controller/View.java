package co.com.activos.jadm0088.controller;

import java.util.List;

import co.com.activos.jadm0088.controller.ViewDao;
import co.com.activos.jadm0088.interfaces.ViewInterface;
import co.com.activos.jadm0088.model.DominioSAAS;

public abstract class View {

    public static List<DominioSAAS> loadDominios(Long emc_id) {
        ViewInterface v = new ViewDao();
        return v.loadDominios(Long.parseLong("1"));
    }

    public static String loadRamaLdap(String nameUser) {
        ViewInterface v = new ViewDao();
        return v.loadRamaLdap(nameUser);

    }
}
