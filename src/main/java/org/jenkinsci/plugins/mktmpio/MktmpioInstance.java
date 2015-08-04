package org.jenkinsci.plugins.mktmpio;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
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
        this.username = username == null ? "" : username;
        this.password = password == null ? "" : password;
        this.type = type;
        this.url = url;
        this.prefix = type.toUpperCase().replaceAll("[^A-Z0-9]+", "");
    }

    @Nonnull
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

    @Nonnull
    private String prefixed(@Nonnull final String name) {
        return this.getPrefix() + "_" + name;
    }

    @Nonnull
    public String getId() {
        return id;
    }

    @Nonnull
    public String getHost() {
        return host;
    }

    @Nonnegative
    public int getPort() {
        return port;
    }

    @Nonnull
    public String getUsername() {
        return username;
    }

    @Nonnull
    public String getPassword() {
        return password;
    }

    @Nonnull
    public String getType() {
        return type;
    }

    @Nonnull
    private String getPrefix() {
        return prefix;
    }

    @Nonnull
    public String getUrl() {
        return url;
    }
}
