package org.jenkinsci.plugins.mktmpio;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Serializable;

public class MktmpioClient implements Serializable {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    private final String urlRoot;
    private final String token;

    public MktmpioClient(final String urlRoot, final String token) {
        this.urlRoot = urlRoot;
        this.token = token;
    }

    @Override
    public String toString() {
        return "MktmpioClient{" +
                "urlRoot='" + getUrlRoot() + '\'' +
                ", token='" + getToken().replaceAll(".", "*") + '\'' +
                '}';
    }

    @Nonnull
    public MktmpioInstance create(final String type)
            throws IOException {
        final String url = getUrlRoot() + "/api/v1/new/" + type;
        final HttpResponse<JsonNode> json = post(url);
        if (json.getStatus() >= 400) {
            final String msg = "Error creating " + type + " instance." + " Response code: " + json.getStatus() + " Response message: " + json.getStatusText() + " Response body: " + json.getBody().toString() + " Details: " + json.getBody().getObject().optString("error", json.getStatusText());
            throw new IOException(msg);
        }
        final JSONObject res = json.getBody().getObject();
        final String id = res.getString("id");
        final String host = res.getString("host");
        final int port = res.getInt("port");
        final String username = res.optString("username", "");
        final String password = res.optString("password", "");
        final String instanceUrl = getUrlRoot() + "/i/" + id;
        return new MktmpioInstance(id, host, port, username, password, type, instanceUrl);
    }

    @Nonnull
    private HttpResponse<JsonNode> post(final String url) throws IOException {
        try {
            return apiReq(Unirest.post(url)).asJson();
        } catch (UnirestException ex) {
            // TODO: needs test coverage
            throw new IOException("Error creating instance: " + ex.getMessage(), ex);
        }
    }

    public void destroy(final MktmpioInstance instance) throws IOException {
        try {
            apiReq(Unirest.delete(getUrlRoot() + "/api/v1/i/" + instance.getId())).asJson();
        } catch (UnirestException ex) {
            // TODO: needs test coverage
            throw new IOException("Failed to terminate instance " + instance.getId(), ex);
        }
    }

    @Nonnull
    private HttpRequestWithBody apiReq(HttpRequestWithBody original) {
        return original
                .header("accept", "application/json")
                .header("X-Auth-Token", getToken());
    }

    @SuppressWarnings("WeakerAccess")
    @Nonnull
    public String getUrlRoot() {
        return urlRoot;
    }

    @SuppressWarnings("WeakerAccess")
    @Nonnull
    public String getToken() {
        return token;
    }
}
