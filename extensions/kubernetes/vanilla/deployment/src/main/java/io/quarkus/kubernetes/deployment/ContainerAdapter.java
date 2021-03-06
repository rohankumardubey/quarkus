package io.quarkus.kubernetes.deployment;

import io.dekorate.kubernetes.config.Env;
import io.dekorate.kubernetes.config.Mount;
import io.dekorate.kubernetes.config.Port;
import io.dekorate.kubernetes.decorator.AddEnvVarDecorator;
import io.dekorate.kubernetes.decorator.AddLivenessProbeDecorator;
import io.dekorate.kubernetes.decorator.AddMountDecorator;
import io.dekorate.kubernetes.decorator.AddPortDecorator;
import io.dekorate.kubernetes.decorator.AddReadinessProbeDecorator;
import io.dekorate.kubernetes.decorator.ApplyImagePullPolicyDecorator;
import io.dekorate.utils.Images;
import io.dekorate.utils.Strings;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;

/**
 * Copied from dekorate in order to fix some issues
 */
public class ContainerAdapter {

    private static final String ANY = null;

    public static Container adapt(io.dekorate.kubernetes.config.Container container) {
        String name = container.getName();
        if (Strings.isNullOrEmpty(name)) {
            name = Images.getName(container.getImage());
        }

        ContainerBuilder builder = new ContainerBuilder()
                .withName(name)
                .withImage(container.getImage())
                // this was changed to add working dir
                .withWorkingDir(container.getWorkingDir())
                .withCommand(container.getCommand())
                .withArgs(container.getArguments());

        for (Env env : container.getEnvVars()) {
            builder.accept(new AddEnvVarDecorator(ANY, name, env));
        }
        for (Port port : container.getPorts()) {
            // this was changed to use our patched port decorator
            builder.accept(new AddPortDecorator(ANY, name, port));
        }
        for (Mount mount : container.getMounts()) {
            builder.accept(new AddMountDecorator(ANY, name, mount));
        }

        builder.accept(new ApplyImagePullPolicyDecorator(name, container.getImagePullPolicy()));
        builder.accept(new AddLivenessProbeDecorator(name, container.getLivenessProbe()));
        builder.accept(new AddReadinessProbeDecorator(name, container.getReadinessProbe()));
        return builder.build();
    }
}
