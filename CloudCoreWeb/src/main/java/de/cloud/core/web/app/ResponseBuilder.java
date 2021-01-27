package de.cloud.core.web.app;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.core.Response;

@Getter
@Setter
public class ResponseBuilder {
    private Response response;

    public ResponseBuilder success(Object entity) {
        this.response = Response.ok(new ResponseContainer("success", entity)).build();
        return this;
    }

    public ResponseBuilder error(String entity) {
        this.response = Response.ok(new ResponseContainer("error", entity)).build();
        return this;
    }

    public Response build() {
        return response;
    }
    @Data
    private static class ResponseContainer {
        private String status;
        private Object entity;

        public ResponseContainer(String status, Object entity) {
            this.status = status;
            this.entity = entity;
        }
    }
}
