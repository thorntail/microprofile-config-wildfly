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

package org.wildfly.swarm.microprofile.config.example;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@Path("/hello")
public class HelloWorldEndpoint {

	@Inject
	Config config;

	@Inject
	@ConfigProperty(name = "BAR", defaultValue = "my BAR property comes from the code")
	String bar;

	@Inject
	@ConfigProperty(name = "BOOL_PROP", defaultValue = "no")
	boolean boolProp;

	// FIXME injecting directly a Duck does not work
	@Inject
	@ConfigProperty(name = "DUCK")
	Optional<Duck> duck;

	@GET
	@Produces("text/plain")
	public Response doGet() {
		Optional<String> foo = config.getOptionalValue("FOO", String.class);

		StringBuilder text = new StringBuilder();
		text.append("FOO property = " + foo + "\n");
		text.append("BAR property = " + bar + "\n");
		text.append("BOOL_PROP property = " + boolProp + "\n");
		text.append("DUCK property =" + duck.get().getName());
		return Response.ok(text).build();
	}
}