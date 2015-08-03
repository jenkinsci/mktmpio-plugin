package org.jenkinsci.plugins.mktmpio;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MktmpioInstance implements Serializable {
    private static final long serialVersionUID = 1L;
    public final String token;
    public final String id;
    public final String host;
    public final int port;
    public final String username;
    public final String password;
    public final String type;
    public final String prefix;
    public final String url;

    public MktmpioInstance(final String token, final String id, final String host, final int port,
                           final String username, final String password, final String type, final String url) {
        this.token = token;
        this.id = id;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.type = type;
        this.url = url;
        this.prefix = type.toUpperCase().replaceAll("[^A-Z0-9]+", "");
    }

    public static MktmpioInstance create(final String urlRoot, final String token, final String type)
            throws IOException, InterruptedException {
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
        final String instanceUrl = urlRoot + "/i/" + id;
        return new MktmpioInstance(token, id, host, port, username, password, type, instanceUrl);
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

    public void destroy() throws IOException {
        try {
            Unirest.delete("https://mktmp.io/api/v1/i/" + id)
                    .header("accept", "application/json")
                    .header("X-Auth-Token", token)
                    .asJson();
        } catch (UnirestException ex) {
            throw new IOException("Failed to terminate instance " + id, ex);
        }
    }

    public Map<String, String> envVars() {
        Map<String, String> vars = new HashMap<String, String>(6);
        vars.put(prefixed("HOST"), host);
        vars.put(prefixed("PORT"), Integer.toString(port));
        vars.put(prefixed("USERNAME"), username);
        vars.put(prefixed("PASSWORD"), password);
        vars.put(prefixed("ID"), id);
        vars.put(prefixed("TYPE"), type);
        return vars;
    }

    private String prefixed(final String name) {
        return this.prefix + "_" + name;
    }
}
