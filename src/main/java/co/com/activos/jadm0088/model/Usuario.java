package co.com.activos.jadm0088.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Usuario implements Serializable {

	private String login_usuario;
	private String clave_usuario;
	private String nombres;
	private String correo;
	private String displayName;
	private UsuarioSesion usuarioSesion;

	boolean autenticado = false;
	String log;

	public Usuario() {
		super();
	}

	public String getLogin_usuario() {
		return login_usuario;
	}

	public void setLogin_usuario(String login_usuario) {
		this.login_usuario = login_usuario;
	}

	public String getClave_usuario() {
		return clave_usuario;
	}

	public void setClave_usuario(String clave_usuario) {
		this.clave_usuario = clave_usuario;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public UsuarioSesion getUsuarioSesion() {
		return usuarioSesion;
	}

	public void setUsuarioSesion(UsuarioSesion usuarioSesion) {
		this.usuarioSesion = usuarioSesion;
	}

}