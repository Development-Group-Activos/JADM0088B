package co.com.activos.jadm0088.model;

import java.io.Serializable;
import java.util.Date;

public class UsuarioSesion implements Serializable {

    @SuppressWarnings("compatibility:5138464271018010941")
    private static final long serialVersionUID = 1L;
    
    Long   uss_id      ;//      not null number(15)     
    String uss_id_session;//             varchar2(1000) 
    String uss_id_session_as;//          varchar2(1000) 
    String uss_ip  ;//                   varchar2(60)   
    String usu_usuario;//                varchar2(50)   
    String tdc_td_epl  ;//               varchar2(2)    
    Long   epl_nd;//                     number(15)     
    String tdc_td  ;//                   varchar2(2)    
    Long   emp_nd  ;//                   number(15)     
    String tdc_td_fil;//                 varchar2(2)    
    Long   emp_nd_fil  ;//               number(15)     
    Date   uss_fecha_ingreso;//          date   
    String log="";     // log de errores
    
    public UsuarioSesion() {
        super();
    }

    public Long getUss_id() {
        return uss_id;
    }

    public void setUss_id(Long uss_id) {
        this.uss_id = uss_id;
    }

    public String getUss_id_session() {
        return uss_id_session;
    }

    public void setUss_id_session(String uss_id_session) {
        this.uss_id_session = uss_id_session;
    }

    public String getUss_id_session_as() {
        return uss_id_session_as;
    }

    public void setUss_id_session_as(String uss_id_session_as) {
        this.uss_id_session_as = uss_id_session_as;
    }

    public String getUss_ip() {
        return uss_ip;
    }

    public void setUss_ip(String uss_ip) {
        this.uss_ip = uss_ip;
    }

    public String getUsu_usuario() {
        return usu_usuario;
    }

    public void setUsu_usuario(String usu_usuario) {
        this.usu_usuario = usu_usuario;
    }

    public String getTdc_td_epl() {
        return tdc_td_epl;
    }

    public void setTdc_td_epl(String tdc_td_epl) {
        this.tdc_td_epl = tdc_td_epl;
    }

    public Long getEpl_nd() {
        return epl_nd;
    }

    public void setEpl_nd(Long epl_nd) {
        this.epl_nd = epl_nd;
    }

    public String getTdc_td() {
        return tdc_td;
    }

    public void setTdc_td(String tdc_td) {
        this.tdc_td = tdc_td;
    }

    public Long getEmp_nd() {
        return emp_nd;
    }

    public void setEmp_nd(Long emp_nd) {
        this.emp_nd = emp_nd;
    }

    public String getTdc_td_fil() {
        return tdc_td_fil;
    }

    public void setTdc_td_fil(String tdc_td_fil) {
        this.tdc_td_fil = tdc_td_fil;
    }

    public Long getEmp_nd_fil() {
        return emp_nd_fil;
    }

    public void setEmp_nd_fil(Long emp_nd_fil) {
        this.emp_nd_fil = emp_nd_fil;
    }

    public Date getUss_fecha_ingreso() {
        return uss_fecha_ingreso;
    }

    public void setUss_fecha_ingreso(Date uss_fecha_ingreso) {
        this.uss_fecha_ingreso = uss_fecha_ingreso;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
