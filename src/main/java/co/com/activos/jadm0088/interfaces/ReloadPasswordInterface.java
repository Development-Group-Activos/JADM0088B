package co.com.activos.jadm0088.interfaces;

public interface ReloadPasswordInterface {
    
    public String saveRequest(String userName);
    public String validateRequest(String req);
    public String updateState(String req);
    public void sendMail(String destino, String para, String asunto,String contenido);
}
