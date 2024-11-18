package co.com.activos.jadm0088.ws;
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
    public Response createResetRequest(@QueryParam("username") String username) {
        if (reloadPasswordService.hasPendingRequest(username)) {
            return Response.status(Response.Status.CONFLICT).entity("Ya existe una solicitud pendiente").build();
        }

        String requestId = reloadPasswordService.createPassword(username);
        if (requestId.equals("-1")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Su solicitud no pudo ser procesada").build();
        }
        return Response.ok(requestId).build();  // Guarda el id de la solicitud
    }

    // Endpoint para validar una solicitud de restablecimiento de contraseña
    @GET
    @Path("/validate/{requestId}")          //usa el id de la solicitud anterior para validadar
    @Produces(MediaType.APPLICATION_JSON)  
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validateRequest(@PathParam("requestId") String requestId) {
        String validation = reloadPasswordService.validatePassword(requestId);
        if (validation.equals("FALSE")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Su solicitud es inválida o ha expirado").build();
        }
        return Response.ok("solicitud exitosa para: " + validation).build();
    }

    // Endpoint para obtener el correo                                      //se valida explicitamente la existencia del usuario
    @GET
    @Path("/find-email")
    @Produces(MediaType.APPLICATION_JSON)  
    @Consumes(MediaType.APPLICATION_JSON)
    public Response findPropertyByAccountName2(@QueryParam("userName") String userName, 
                                               @QueryParam("property") String property){ // en property enviar "mail"
        String resultado = reloadPasswordService.findPropertyByAccountName2(userName, property);
        if (resultado == null || resultado.isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST).entity("el usuario no existe").build();
        }
        return Response.ok(resultado).build();
        
    }
      
    @POST
    @Path("/send-email")
    public Response sendPasswordResetEmail(@QueryParam("userEmail") String userEmail,
                                           @QueryParam("userName") String userName,
                                           @QueryParam("requestUrl") String requestUrl) {
        try {
            reloadPasswordService.sendPasswordResetEmail(userEmail, userName, requestUrl);
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
    public Response updatePassword(@QueryParam("userName") String username,
                                   @QueryParam("newPassword") String newPassword,
                                   @QueryParam("requestId") String requestId,
                                   @QueryParam("domain") String domain) {
        boolean success = reloadPasswordService.updatePassword(username, newPassword, requestId, domain);
        if (success) {
            return Response.ok("Contraseña actualizada").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error al actualizar la contraseña").build();
        }
    }
    
}
