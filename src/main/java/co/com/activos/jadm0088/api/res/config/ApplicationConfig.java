package co.com.activos.jadm0088.api.res.config;


import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author kpaz
 */

@javax.ws.rs.ApplicationPath("api/rest/reset_password")
public class ApplicationConfig extends Application {
    @Override
    public Set<Class<?>> getClasses(){
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }
    
    private void addRestResourceClasses(Set<Class<?>> resources) {
//            resources.add(co.com.activos.jadm0074.util.CorsFilter.class);   
        resources.add(co.com.activos.jadm0088.util.CorsFilter.class);   
        resources.add(co.com.activos.jadm0088.ws.ReloadPasswordRS.class);
    }
    
}
