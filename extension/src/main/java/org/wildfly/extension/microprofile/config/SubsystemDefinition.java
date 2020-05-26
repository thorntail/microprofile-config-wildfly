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

import java.util.Collection;
import java.util.Collections;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.ReloadRequiredRemoveStepHandler;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.as.controller.registry.ManagementResourceRegistration;

import static org.jboss.as.weld.Capabilities.WELD_CAPABILITY_NAME;

/**
 * @author <a href="mailto:tcerar@redhat.com">Tomaz Cerar</a>
 */
public class SubsystemDefinition extends PersistentResourceDefinition {
    private static final String CONFIG_CAPABILITY_NAME = "org.wildlfy.microprofile.config";

    static final RuntimeCapability<Void> CONFIG_CAPABILITY =
            RuntimeCapability.Builder.of(CONFIG_CAPABILITY_NAME)
                    .addRequirements(WELD_CAPABILITY_NAME)
                    .build();

    protected SubsystemDefinition(Iterable<ConfigSourceProvider> providers, Iterable<ConfigSource> sources) {
        super(new SimpleResourceDefinition.Parameters(SubsystemExtension.SUBSYSTEM_PATH, SubsystemExtension.getResourceDescriptionResolver(SubsystemExtension.SUBSYSTEM_NAME))
                .setAddHandler(new SubsystemAdd(providers, sources))
                .setRemoveHandler(ReloadRequiredRemoveStepHandler.INSTANCE)
                .setCapabilities(CONFIG_CAPABILITY)
        );
    }

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Collections.emptySet();
    }

    @Override
    public void registerOperations(ManagementResourceRegistration resourceRegistration) {
        super.registerOperations(resourceRegistration);
        //you can register aditional operations here
    }

    @Override
    public void registerAttributes(ManagementResourceRegistration resourceRegistration) {
        //you can register attributes here
    }
}
