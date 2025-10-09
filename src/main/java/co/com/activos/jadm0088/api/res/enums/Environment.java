package co.com.activos.jadm0088.api.res.enums;
/**
 * Almacena los valores del HOST de la base de datos de los diferentes ambientes
 *
 * @author Francisco Javier Rincon Alarcon
 * @version 1.0
 * @since JDK 1.8
 */
public enum Environment {

    DESA("192.168.21.147", "1521", "desa"),
    TEST("192.168.21.59", "1521", "test"),
    PRODUCTION("192.168.21.222", "1521", "activ");

    private String host;
    private String port;
    private String sid;

    Environment(String host, String port, String sid) {
        this.host = host;
        this.port = port;
        this.sid = sid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
