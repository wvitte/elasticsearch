/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.rest.action.admin.cluster.snapshots.create;

import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.support.RestToXContentListener;

import static org.elasticsearch.client.Requests.createSnapshotRequest;
import static org.elasticsearch.rest.RestRequest.Method.POST;
import static org.elasticsearch.rest.RestRequest.Method.PUT;

/**
 * Creates a new snapshot
 */
public class RestCreateSnapshotAction extends BaseRestHandler {

    @Inject
    public RestCreateSnapshotAction(Settings settings, RestController controller, Client client) {
        super(settings, controller, client);
        controller.registerHandler(PUT, "/_snapshot/{repository}/{snapshot}", this);
        controller.registerHandler(POST, "/_snapshot/{repository}/{snapshot}", this);
        controller.registerHandler(PUT, "/_snapshot/{repository}/{snapshot}/_create", this);
        controller.registerHandler(POST, "/_snapshot/{repository}/{snapshot}/_create", this);
    }

    @Override
    public void handleRequest(final RestRequest request, final RestChannel channel, final Client client) {
        CreateSnapshotRequest createSnapshotRequest = createSnapshotRequest(request.param("repository"), request.param("snapshot"));
        createSnapshotRequest.listenerThreaded(false);
        createSnapshotRequest.source(request.content().toUtf8());
        createSnapshotRequest.masterNodeTimeout(request.paramAsTime("master_timeout", createSnapshotRequest.masterNodeTimeout()));
        createSnapshotRequest.waitForCompletion(request.paramAsBoolean("wait_for_completion", false));
        client.admin().cluster().createSnapshot(createSnapshotRequest, new RestToXContentListener<CreateSnapshotResponse>(channel));
    }
}
