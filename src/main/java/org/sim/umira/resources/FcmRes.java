package org.sim.umira.resources;

import org.sim.umira.services.FcmService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/fcm")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FcmRes {

    @Inject
    FcmService fcmService;

    @POST
    public String send(PushRequest req) throws Exception {
        return fcmService.sendToToken(req.token, req.title, req.body);
    }
}

class PushRequest {
    public String token;
    public String title;
    public String body;
}
