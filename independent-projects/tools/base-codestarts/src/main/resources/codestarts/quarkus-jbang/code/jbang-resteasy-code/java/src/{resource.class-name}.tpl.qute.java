//usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA {java.version}
//JAVAC_OPTIONS -parameters
//DEPS {quarkus.bom.group-id}:{quarkus.bom.artifact-id}:{quarkus.bom.version}@pom
{#for dep in dependencies}
//DEPS {dep}
{/for}

import io.quarkus.runtime.Quarkus;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("{resource.path}")
@ApplicationScoped
public class {resource.class-name} {

    @GET
    public String sayHello() {
        return "{resource.response}";
    }

    public static void main(String[] args) {
        Quarkus.run(args);
    }
}