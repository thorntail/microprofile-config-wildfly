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

package org.wildfly.extension.microprofile.config.deployment;

import io.smallrye.config.SmallRyeConfigBuilder;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.modules.Module;

public class SubsystemDeploymentProcessor implements DeploymentUnitProcessor {

    public static final AttachmentKey<Config> CONFIG = AttachmentKey.create(Config.class);

    private final Iterable<ConfigSourceProvider> providers;
    private final Iterable<ConfigSource> sources;

    public SubsystemDeploymentProcessor(Iterable<ConfigSourceProvider> providers, Iterable<ConfigSource> sources) {
        this.providers = providers;
        this.sources = sources;
    }

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) {
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        Module module = deploymentUnit.getAttachment(Attachments.MODULE);

        SmallRyeConfigBuilder builder = new SmallRyeConfigBuilder();
        builder.forClassLoader(module.getClassLoader())
                .addDefaultSources()
                .addDiscoveredSources()
                .addDiscoveredConverters();

        ClassLoader loader = module.getClassLoader();
        for (ConfigSourceProvider provider : this.providers) {
            for (ConfigSource source : provider.getConfigSources(loader)) {
                builder.withSources(source);
            }
        }
        for (ConfigSource source : this.sources) {
            builder.withSources(source);
        }

        Config config = builder.build();
        deploymentUnit.putAttachment(CONFIG, config);

        ConfigProviderResolver.instance().registerConfig(config, module.getClassLoader());
    }

    @Override
    public void undeploy(DeploymentUnit context) {
        Config config = context.removeAttachment(CONFIG);

        ConfigProviderResolver.instance().releaseConfig(config);
    }
}
