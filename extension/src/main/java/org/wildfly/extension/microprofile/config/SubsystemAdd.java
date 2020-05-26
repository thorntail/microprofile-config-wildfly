/*
 * Copyright 2017 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.extension.microprofile.config;

import io.smallrye.config.SmallRyeConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.Phase;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceBuilder;
import org.wildfly.extension.microprofile.config._private.MicroProfileConfigLogger;
import org.wildfly.extension.microprofile.config.deployment.DependencyProcessor;
import org.wildfly.extension.microprofile.config.deployment.SubsystemDeploymentProcessor;

/**
 * Handler responsible for adding the subsystem resource to the model
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
class SubsystemAdd extends AbstractBoottimeAddStepHandler {

    static {
        // Set the static resolver reference eagerly
        ConfigProviderResolver.setInstance(new SmallRyeConfigProviderResolver());
    }

    final Iterable<ConfigSourceProvider> providers;
    final Iterable<ConfigSource> sources;

    SubsystemAdd(Iterable<ConfigSourceProvider> providers, Iterable<ConfigSource> sources) {
        this.providers = providers;
        this.sources = sources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performBoottime(OperationContext context, ModelNode operation, ModelNode model) {

        MicroProfileConfigLogger.ROOT_LOGGER.activatingSubsystem();

        ConfigMarkerService.install(context);

        context.addStep(new AbstractDeploymentChainStep() {
            @Override
            public void execute(DeploymentProcessorTarget processorTarget) {
                processorTarget.addDeploymentProcessor(SubsystemExtension.SUBSYSTEM_NAME, Phase.DEPENDENCIES, Phase.DEPENDENCIES_MICROPROFILE_CONFIG, new DependencyProcessor());
                processorTarget.addDeploymentProcessor(SubsystemExtension.SUBSYSTEM_NAME, Phase.POST_MODULE, Phase.POST_MODULE_MICROPROFILE_CONFIG, new SubsystemDeploymentProcessor(SubsystemAdd.this.providers, SubsystemAdd.this.sources));
            }
        }, OperationContext.Stage.RUNTIME);

    }

}
