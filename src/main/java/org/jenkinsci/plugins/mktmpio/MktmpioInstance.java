package org.jenkinsci.plugins.mktmpio;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.io.IOException;

public class MktmpioInstance {
    private final MktmpioEnvironment env;

    public MktmpioInstance(final MktmpioEnvironment env) {
        this.env = env;
    }

    public static MktmpioInstance create(final String urlRoot, final String token, final String type, final boolean shutdownWithBuild) throws IOException, InterruptedException {
        final String url = urlRoot + "/api/v1/new/" + type;
        final HttpResponse<JsonNode> json = post(url, token);
        if (json.getStatus() >= 400) {
            StringBuilder msg = new StringBuilder();
            msg.append("Error creating " + type + " instance.");
            msg.append(" Response code: " + json.getStatus());
            msg.append(" Response message: " + json.getStatusText());
            msg.append(" Response body: " + json.getBody().toString());
            msg.append(" Details: " + json.getBody().getObject().optString("error", json.getStatusText()));
            throw new IOException(msg.toString());
        }
        final JSONObject res = json.getBody().getObject();
        final String id = res.getString("id");
        final String host = res.getString("host");
        final int port = res.getInt("port");
        final String username = res.optString("username", "");
        final String password = res.optString("password", "");
        final MktmpioEnvironment env = new MktmpioEnvironment(token, id, host, port, username, password, type, shutdownWithBuild);
        return new MktmpioInstance(env);
    }

    private static HttpResponse<JsonNode> post(final String url, final String token) throws IOException {
        try {
            return Unirest.post(url)
                    .header("accept", "application/json")
                    .header("X-Auth-Token", token)
                    .asJson();
        } catch (UnirestException ex) {
            throw new IOException("Error creating instance: " + ex.getMessage(), ex);
        }
    }

    public MktmpioEnvironment getEnv() {
        return this.env;
    }

    public void destroy() throws IOException {
        try {
            Unirest.delete("https://mktmp.io/api/v1/i/" + env.id)
                    .header("accept", "application/json")
                    .header("X-Auth-Token", env.token)
                    .asJson();
        } catch (UnirestException ex) {
            throw new IOException("Failed to terminate instance " + env.id, ex);
        }
    }
}
