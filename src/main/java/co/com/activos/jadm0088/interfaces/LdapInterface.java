package co.com.activos.jadm0088.interfaces;

import co.com.activos.jadm0088.model.Usuario;

public interface LdapInterface {
	public  boolean loadUser(Usuario user, String rama);
	public String validateRequest(String request_id);
	public String updateState(String request_id);
	public String saveRequest(String userName);
        public String findPropertyByAccountName2(String accountName, String property);
	public void sendMail(String destino, String para, String asunto, String contenido);
        public String getUsuarioDominio(String userName);
}
