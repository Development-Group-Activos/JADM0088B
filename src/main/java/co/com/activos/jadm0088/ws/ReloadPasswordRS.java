package co.com.activos.jadm0088.ws;
import co.com.activos.jadm0088.api.res.dto.UpdatePasswordRequest;
import co.com.activos.jadm0088.service.ReloadPasswordService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
/**
 *
 * @author kpaz
 */

@Path("restablecer-password")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReloadPasswordRS {
    
    private final ReloadPasswordService reloadPasswordService = new ReloadPasswordService();

    // Endpoint para crear una solicitud de restablecimiento de contraseña
    @POST
    @Path("/request")
    @Produces(MediaType.APPLICATION_JSON)  
    @Consumes(MediaType.APPLICATION_JSON)   
    public Response createResetRequest(UpdatePasswordRequest request) throws Exception { // Recibe un objeto JSON en el body
        String encryptedUserName = request.getUserName(); // Se recibe encriptado

        if (reloadPasswordService.hasPendingRequest(encryptedUserName)) {
            return Response.status(Response.Status.CONFLICT).entity("Ya existe una solicitud pendiente").build();
        }

        // Pasar el username encriptado al servicio sin modificarlo
        String requestId = reloadPasswordService.createPassword(encryptedUserName);
        if (requestId.equals("-1")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Su solicitud no pudo ser procesada").build();
        }
        return Response.ok(requestId).build();  // Guarda el id de la solicitud
    }

    // Endpoint para validar una solicitud de restablecimiento de contraseña
    @GET
    @Path("/validate/{requestId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validateRequest(@PathParam("requestId") String requestId) {
        String validation = reloadPasswordService.validatePassword(requestId);

        if (validation.equals("ERROR: Solicitud inválida o expirada.")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validation).build();
        }
        
        if (validation.equals("FALSE")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validation).build();
        }
        
        return Response.ok("Solicitud exitosa para: " + validation).build();
    }

    @GET
    @Path("/find-user/{requestId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByRequestId(@PathParam("requestId") String requestId) {
        String userName = reloadPasswordService.getUserByRequestId(requestId);

        if (userName != null) {
            return Response.ok(userName).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("No se encontró un usuario para este requestId").build();
        }
    }
    
    
    // Endpoint para obtener el correo                                      //se valida explicitamente la existencia del usuario
    @GET
    @Path("/find-email")
    @Produces(MediaType.APPLICATION_JSON)  
    @Consumes(MediaType.APPLICATION_JSON)
    public Response findPropertyByAccountName2(@QueryParam("userName") String encryptedUserName, 
                                               @QueryParam("property") String property) throws Exception{ // en property enviar "mail"
        String resultado = reloadPasswordService.findPropertyByAccountName2(encryptedUserName, property);
        if (resultado == null || resultado.isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST).entity("el usuario no existe").build();
        }
        return Response.ok(resultado).build();
        
    }
      
    @POST
    @Path("/send-email")
    public Response sendPasswordResetEmail(@QueryParam("userEmail") String userEmail,
                                           @QueryParam("userName") String encryptedUserName,
                                           @QueryParam("requestUrl") String requestUrl) {
        try {
            reloadPasswordService.sendPasswordResetEmail(userEmail, encryptedUserName, requestUrl);
            return Response.ok("Correo enviado exitosamente").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al enviar email").build();
        }
    }
    
    // Endpoint para actualizar la contraseña
    @PUT
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePassword(UpdatePasswordRequest request) throws Exception {
        String resultado = reloadPasswordService.updatePassword(
            request.getUserName(), 
            request.getNewPassword(), 
            request.getRequestId(), 
            request.getDomain()
        );

        if (resultado.equals("Contraseña actualizada correctamente.")) {
            return Response.ok(resultado).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(resultado).build();
        }
    }
    
    @GET
    @Path("/dominio/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerDominioUsuario(@PathParam("userName") String userName) {
        String dominio = reloadPasswordService.obtenerDominioUsuario(userName);

        if (dominio != null) {
            return Response.ok(dominio).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("No se encontró el dominio para el usuario.").build();
        }
    }
}
