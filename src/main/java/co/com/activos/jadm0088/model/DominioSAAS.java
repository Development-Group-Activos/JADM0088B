package co.com.activos.jadm0088.model;

import java.io.Serializable;
import java.util.Date;

public class DominioSAAS implements Serializable {

    @SuppressWarnings("compatibility:-8118650086652042924")
    private static final long serialVersionUID = 1L;
    
    private Long dsa_id;//                         not null number(15)
    private Long esa_id;//                                  number(15)
    private String dsa_nombre;//                              varchar2(50)
    private String dsa_ramaldap;//                            varchar2(50)                                                                                                                                                                                  
    private Long dsa_orden;//                               number(15)                                                                                                                                                                                    
    private String dsa_color_etiqueta;//                      varchar2(50)                                                                                                                                                                                  
    private String dsa_estado;//                              varchar2(2)                                                                                                                                                                                   
    private Date aud_fecha;//                               date                                                                                                                                                                                          
    private String aud_usuario;//                             varchar2(50) 
    private String dsa_filtroemp; 
    
    public DominioSAAS() {
        super();
    }
    
    public DominioSAAS(Long esa_id){
        setDsa_id(new Long(0));
        setEsa_id(esa_id);
    }

    public void setDsa_id(Long dsa_id) {
        this.dsa_id = dsa_id;
    }

    public Long getDsa_id() {
        return dsa_id;
    }

    public void setEsa_id(Long esa_id) {
        this.esa_id = esa_id;
    }

    public Long getEsa_id() {
        return esa_id;
    }

    public void setDsa_nombre(String dsa_nombre) {
        this.dsa_nombre = dsa_nombre;
    }

    public String getDsa_nombre() {
        return dsa_nombre;
    }

    public void setDsa_ramaldap(String dsa_ramaldap) {
        this.dsa_ramaldap = dsa_ramaldap;
    }

    public String getDsa_ramaldap() {
        return dsa_ramaldap;
    }

    public void setDsa_orden(Long dsa_orden) {
        this.dsa_orden = dsa_orden;
    }

    public Long getDsa_orden() {
        return dsa_orden;
    }

    public void setDsa_color_etiqueta(String dsa_color_etiqueta) {
        this.dsa_color_etiqueta = dsa_color_etiqueta;
    }

    public String getDsa_color_etiqueta() {
        return dsa_color_etiqueta;
    }

    public void setDsa_estado(String dsa_estado) {
        this.dsa_estado = dsa_estado;
    }

    public String getDsa_estado() {
        return dsa_estado;
    }

    public void setAud_fecha(Date aud_fecha) {
        this.aud_fecha = aud_fecha;
    }

    public Date getAud_fecha() {
        return aud_fecha;
    }

    public void setAud_usuario(String aud_usuario) {
        this.aud_usuario = aud_usuario;
    }

    public String getAud_usuario() {
        return aud_usuario;
    }

    public String getDsa_filtroemp() {
        return dsa_filtroemp;
    }

    public void setDsa_filtroemp(String dsa_filtroemp) {
        this.dsa_filtroemp = dsa_filtroemp;
    }
}
