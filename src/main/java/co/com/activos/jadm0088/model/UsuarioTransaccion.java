package co.com.activos.jadm0088.model;

import java.io.Serializable;
import java.util.Date;

public class UsuarioTransaccion implements Serializable{

    @SuppressWarnings("compatibility:-7859508854398473131")
    private static final long serialVersionUID = 1L;
    
    Long   id_usuario   ;// not null number(12)     
    Long   trn_secuencia;// not null number(12)     
    String mtc_prog    ;//  not null varchar2(8)    
    String trn_log     ;//  not null varchar2(4000) 
    String trn_ip     ;//   not null varchar2(100)  
    String trn_tabla  ;//   not null varchar2(100)  
    String trn_parametro;// not null varchar2(500)  
    Date   aud_fecha   ;//  not null date           
    String usu_usuario ;//           varchar2(30) 
    
    public UsuarioTransaccion() {
        super();
    }

    public Long getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Long id_usuario) {
        this.id_usuario = id_usuario;
    }

    public Long getTrn_secuencia() {
        return trn_secuencia;
    }

    public void setTrn_secuencia(Long trn_secuencia) {
        this.trn_secuencia = trn_secuencia;
    }

    public String getMtc_prog() {
        return mtc_prog;
    }

    public void setMtc_prog(String mtc_prog) {
        this.mtc_prog = mtc_prog;
    }

    public String getTrn_log() {
        return trn_log;
    }

    public void setTrn_log(String trn_log) {
        this.trn_log = trn_log;
    }

    public String getTrn_ip() {
        return trn_ip;
    }

    public void setTrn_ip(String trn_ip) {
        this.trn_ip = trn_ip;
    }

    public String getTrn_tabla() {
        return trn_tabla;
    }

    public void setTrn_tabla(String trn_tabla) {
        this.trn_tabla = trn_tabla;
    }

    public String getTrn_parametro() {
        return trn_parametro;
    }

    public void setTrn_parametro(String trn_parametro) {
        this.trn_parametro = trn_parametro;
    }

    public Date getAud_fecha() {
        return aud_fecha;
    }

    public void setAud_fecha(Date aud_fecha) {
        this.aud_fecha = aud_fecha;
    }

    public String getUsu_usuario() {
        return usu_usuario;
    }

    public void setUsu_usuario(String usu_usuario) {
        this.usu_usuario = usu_usuario;
    }
}
