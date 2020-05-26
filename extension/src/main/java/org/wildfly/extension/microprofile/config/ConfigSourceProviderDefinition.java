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

import static org.jboss.as.controller.SimpleAttributeDefinitionBuilder.create;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.MODULE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.NAME;
import static org.wildfly.extension.microprofile.config._private.MicroProfileConfigLogger.ROOT_LOGGER;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.AbstractRemoveStepHandler;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.AttributeMarshaller;
import org.jboss.as.controller.ObjectTypeAttributeDefinition;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
class ConfigSourceProviderDefinition extends PersistentResourceDefinition {

    static ObjectTypeAttributeDefinition CLASS = ObjectTypeAttributeDefinition.Builder.of("class",
            create(NAME, ModelType.STRING, false)
                    .setAllowExpression(false)
                    .build(),
            create(MODULE, ModelType.STRING, false)
                    .setAllowExpression(false)
                    .build())
            .setAllowNull(false)
            .setAllowNull(true)
            .setAttributeMarshaller(AttributeMarshaller.ATTRIBUTE_OBJECT)
            .setRestartAllServices()
            .build();

    static AttributeDefinition[] ATTRIBUTES = { CLASS };

    protected ConfigSourceProviderDefinition(Registry<ConfigSourceProvider> providers) {
        super(SubsystemExtension.CONFIG_SOURCE_PROVIDER_PATH,
                SubsystemExtension.getResourceDescriptionResolver(SubsystemExtension.CONFIG_SOURCE_PROVIDER_PATH.getKey()),
                new AbstractAddStepHandler(ATTRIBUTES) {
                    @Override
                    protected boolean requiresRuntime(OperationContext context) {
                        return true;
                    }

                    @Override
                    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model) throws OperationFailedException {
                        super.performRuntime(context, operation, model);
                        String name = context.getCurrentAddressValue();
                        ModelNode classModel = CLASS.resolveModelAttribute(context, model);
                        if (classModel.isDefined()) {
                            Class<?> configSourceProviderClass = unwrapClass(classModel);
                            try {
                                providers.register(name, ConfigSourceProvider.class.cast(configSourceProviderClass.newInstance()));
                            } catch (Exception e) {
                                throw new OperationFailedException(e);
                            }
                        }
                    }
                }, new AbstractRemoveStepHandler() {
                    @Override
                    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model) {
                        String name = context.getCurrentAddressValue();
                        providers.unregister(name);
                    }
                });
    }

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Arrays.asList(ATTRIBUTES);
    }

    private static Class<?> unwrapClass(ModelNode classModel) throws OperationFailedException {
        String className = classModel.get(NAME).asString();
        String moduleName = classModel.get(MODULE).asString();
        try {
            ModuleIdentifier moduleID = ModuleIdentifier.fromString(moduleName);
            Module module = Module.getCallerModuleLoader().loadModule(moduleID);
            return module.getClassLoader().loadClass(className);
        } catch (Exception e) {
            throw ROOT_LOGGER.unableToLoadClassFromModule(className, moduleName);
        }
    }
}
