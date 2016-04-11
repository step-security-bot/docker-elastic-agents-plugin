/*
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.contrib.elasticagents.docker;

import cd.go.contrib.elasticagents.Agent;
import cd.go.contrib.elasticagents.Agents;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.request.DefaultGoApiRequest;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;

import static cd.go.contrib.elasticagents.docker.DockerPlugin.*;

public class PluginRequest {
    private final GoApplicationAccessor accessor;

    public PluginRequest(GoApplicationAccessor accessor) {
        this.accessor = accessor;
    }

    public PluginSettings getConfiguration() {
        DefaultGoApiRequest request = new DefaultGoApiRequest(Constants.REQUEST_SERVER_GET_PLUGIN_SETTINGS, DockerPlugin.API_VERSION, DockerPlugin.PLUGIN_IDENTIFIER);
        GoApiResponse response = accessor.submit(request);

        if (response.responseCode() != 200) {
            LOG.error("The server sent an unexpected status code " + response.responseCode() + " with the response body " + response.responseBody());
        }
        if (StringUtils.isBlank(response.responseBody())) {
            String message = "Plugin settings is required for this plugin. Please set that up on the server.";
            LOG.error(message);
            throw new RuntimeException(message);
        }
        return PluginSettings.fromJSON(response.responseBody());
    }

    public Agents getAgents() {
        DefaultGoApiRequest request = new DefaultGoApiRequest(Constants.REQUEST_SERVER_LIST_AGENTS, DockerPlugin.API_VERSION, DockerPlugin.PLUGIN_IDENTIFIER);
        GoApiResponse response = accessor.submit(request);

        if (response.responseCode() != 200) {
            LOG.error("The server sent an unexpected status code " + response.responseCode() + " with the response body " + response.responseBody());
        }

        LOG.debug("The server said " + response.responseBody());

        return new Agents(Agent.fromJSONArray(response.responseBody()));
    }

    public void disable(Collection<Agent> toBeDisabled) {
        if (toBeDisabled.isEmpty()) {
            return;
        }

        DefaultGoApiRequest request = new DefaultGoApiRequest(Constants.REQUEST_SERVER_DISABLE_AGENT, API_VERSION, PLUGIN_IDENTIFIER);
        request.setRequestBody(Agent.toJSONArray(toBeDisabled));

        GoApiResponse response = accessor.submit(request);

        if (response.responseCode() != 200) {
            LOG.error("The server sent an unexpected status code " + response.responseCode() + " with the response body " + response.responseBody());
        }
    }

    public void delete(Collection<Agent> toBeDeleted) {
        if (toBeDeleted.isEmpty()) {
            return;
        }

        DefaultGoApiRequest request = new DefaultGoApiRequest(Constants.REQUEST_SERVER_DELETE_AGENT, API_VERSION, PLUGIN_IDENTIFIER);
        request.setRequestBody(Agent.toJSONArray(toBeDeleted));
        GoApiResponse response = accessor.submit(request);

        if (response.responseCode() != 200) {
            LOG.error("The server sent an unexpected status code " + response.responseCode() + " with the response body " + response.responseBody());
        }
    }
}
