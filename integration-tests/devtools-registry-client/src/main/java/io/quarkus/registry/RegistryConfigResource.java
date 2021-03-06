package io.quarkus.registry;

import java.io.IOException;
import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkus.registry.config.RegistriesConfigMapperHelper;
import io.quarkus.registry.config.RegistryConfig;

@Path("/config")
public class RegistryConfigResource {

    @GET
    public Response getDefaultRegistryConfig() throws IOException {
        StringWriter sw = new StringWriter();
        RegistriesConfigMapperHelper.toJson(RegistryConfig.defaultConfig(), sw);
        return Response.ok(sw.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .build();
    }
}
