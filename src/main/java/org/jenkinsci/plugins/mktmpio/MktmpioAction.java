package org.jenkinsci.plugins.mktmpio;

import hudson.model.InvisibleAction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MktmpioAction extends InvisibleAction implements Serializable {
    private static final long serialVersionUID = 1L;
    public final String token;
    public final String id;
    public final String host;
    public final int port;
    public final String username;
    public final String password;
    public final String type;
    public final boolean shutdownWithBuild;
    public final String prefix;
    public final String url;

    public MktmpioAction(final String token, final String id, final String host, final int port, final String username, final String password, final String type, final boolean shutdownWithBuild, final String url) {
        this.token = token;
        this.id = id;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.type = type;
        this.shutdownWithBuild = shutdownWithBuild;
        this.url = url;
        this.prefix = type.toUpperCase().replaceAll("[^A-Z0-9]+", "");
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
