/*
 * Copyright 2022 ST-Lab
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */

package org.apache.calcite.avatica.remote;

import com.desoss.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.calcite.avatica.metrics.MetricsSystem;
import org.apache.calcite.avatica.metrics.Timer;
import org.apache.calcite.avatica.metrics.Timer.Context;
import org.apache.calcite.avatica.remote.Service.Request;
import org.apache.calcite.avatica.remote.Service.Response;

import java.io.IOException;
import java.io.StringWriter;

/** json请求的处理器,解码json请求发给service，并将响应编码成json
 * Implementation of {@link org.apache.calcite.avatica.remote.Handler}
 * that decodes JSON requests, sends them to a {@link Service},
 * and encodes the responses into JSON.
 *
 * @see org.apache.calcite.avatica.remote.JsonService
 */
public class JsonHandler extends AbstractHandler<String> {

    protected static final ObjectMapper MAPPER = JsonService.MAPPER;

    // start-db add
    static {
        MAPPER.registerModule(new JtsModule());
    }
    // start-db end

    final MetricsSystem metrics;
    final Timer serializationTimer;

    public JsonHandler(Service service, MetricsSystem metrics) {
        super(service);
        this.metrics = metrics;
        this.serializationTimer = this.metrics.getTimer(
            MetricsHelper.concat(JsonHandler.class, HANDLER_SERIALIZATION_METRICS_NAME)
        );
    }

    public HandlerResponse<String> apply(String jsonRequest) {
        return super.apply(jsonRequest);
    }

    @Override
    Request decode(String request) throws IOException {
        try (final Context ctx = serializationTimer.start()) {
            return MAPPER.readValue(request, Service.Request.class);
        }
    }

    /**
     * Serializes the provided object as JSON.
     *
     * @param response The object to serialize.
     * @return A JSON string.
     */
    @Override
    String encode(Response response) throws IOException {
        try (final Context ctx = serializationTimer.start()) {
            final StringWriter w = new StringWriter();
            MAPPER.writeValue(w, response);
            return w.toString();
        }
    }
}

// End JsonHandler.java
