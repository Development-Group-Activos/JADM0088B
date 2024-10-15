package co.com.activos.jadm0088.interfaces;


import co.com.activos.jadm0088.model.DominioSAAS;
import java.util.List;

public interface ViewInterface {
	public List<DominioSAAS> loadDominios(Long emc_id);
        public String loadRamaLdap(String userName) ;
}
