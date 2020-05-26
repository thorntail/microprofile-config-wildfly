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

package org.wildfly.extension.microprofile.config._private;

import static org.jboss.logging.Logger.Level.INFO;

import org.jboss.as.controller.OperationFailedException;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
@MessageLogger(projectCode = "EMPCONF", length = 4)
public interface MicroProfileConfigLogger extends BasicLogger {
    /**
     * The root logger with a category of the package name.
     */
    MicroProfileConfigLogger ROOT_LOGGER = Logger.getMessageLogger(MicroProfileConfigLogger.class, MicroProfileConfigLogger.class.getPackage().getName());

    /**
     * Logs an informational message indicating the MicroProfile Config subsystem is being activated.
     */
    @LogMessage(level = INFO)
    @Message(id = 1, value = "Activating Eclipse MicroProfile Config Subsystem")
    void activatingSubsystem();

    @Message(id = 2, value = "Unable to load class %s from module %s")
    OperationFailedException unableToLoadClassFromModule(String className, String moduleName);

    @LogMessage(level = INFO)
    @Message(id = 3, value = "Use directory for MicroProfile Config Source: %s")
    void loadConfigSourceFromDir(String path);

    @LogMessage(level = INFO)
    @Message(id = 4, value = "Use class for MicroProfile Config Source: %s")
    void loadConfigSourceFromClass(Class clazz);
}
