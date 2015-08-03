package org.jenkinsci.plugins.mktmpio;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MktmpioInstance implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String type;
    private final String prefix;
    private final String url;

    public MktmpioInstance(final String id, final String host, final int port,
                           final String username, final String password, final String type, final String url) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.type = type;
        this.url = url;
        this.prefix = type.toUpperCase().replaceAll("[^A-Z0-9]+", "");
    }

    public Map<String, String> envVars() {
        Map<String, String> vars = new HashMap<String, String>(6);
        vars.put(prefixed("HOST"), getHost());
        vars.put(prefixed("PORT"), Integer.toString(getPort()));
        vars.put(prefixed("USERNAME"), getUsername());
        vars.put(prefixed("PASSWORD"), getPassword());
        vars.put(prefixed("ID"), getId());
        vars.put(prefixed("TYPE"), getType());
        return vars;
    }

    private String prefixed(final String name) {
        return this.getPrefix() + "_" + name;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getUrl() {
        return url;
    }
}
