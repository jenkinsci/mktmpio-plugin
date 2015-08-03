package org.jenkinsci.plugins.mktmpio;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Mktmpio extends SimpleBuildWrapper {
    public static final String DEFAULT_SERVER = "https://mktmp.io";
    public static final Map<String, String> TYPES = new HashMap<String, String>(5) {{
        put("mysql", "MySQL");
        put("postgres", "PostgreSQL-9.4");
        put("postgres-9.5", "PostgreSQL-9.5");
        put("redis", "Redis");
        put("mongodb", "MongoDB");
    }};
    public static final ListBoxModel TYPE_OPTIONS = new ListBoxModel(TYPES.size()) {{
        for (Map.Entry<String, String> type : TYPES.entrySet()) {
            add(type.getValue(), type.getKey());
        }
    }};
    @Extension
    public static final MktmpioDescriptor GLOBAL_CONFIG = new MktmpioDescriptor();

    // Job config
    private String dbs;

    @DataBoundConstructor
    public Mktmpio(String dbs) {
        this.dbs = dbs;
    }

    public String getDbs() {
        return dbs;
    }

    @DataBoundSetter
    public void setDbs(String dbs) {
        this.dbs = dbs;
    }

    @Override
    public MktmpioDescriptor getDescriptor() {
        return GLOBAL_CONFIG;
    }

    @Override
    public void setUp(final SimpleBuildWrapper.Context context,
                      final Run<?, ?> build,
                      final FilePath workspace,
                      final Launcher launcher,
                      final TaskListener listener,
                      final EnvVars initialEnvironment)
            throws IOException, InterruptedException {
        final String token = GLOBAL_CONFIG.getToken();
        final String baseUrl = GLOBAL_CONFIG.getServer();
        final String dbs = getDbs();
        final List<MktmpioInstance> instances = makeInstances(listener, token, baseUrl, dbs);
        final MktmpioAction action = new MktmpioAction(instances);
        for (final MktmpioInstance i : instances) {
            final Map<String, String> envVars = i.envVars();
            for (Map.Entry<String, String> entry : envVars.entrySet()) {
                listener.getLogger().printf("setting %s=%s\n", entry.getKey(), entry.getValue());
            }
            context.getEnv().putAll(envVars);
        }
        build.addAction(action);
        context.setDisposer(new MktmpioDisposer(action));
    }

    private MktmpioInstance makeInstance(TaskListener listener, String token, String baseUrl, String dbType)
            throws InterruptedException, IOException {
        listener.getLogger().printf("Attempting to create instance (server: %s, token: %s, type: %s)",
                baseUrl, token.replaceAll(".", "*"), dbType);
        final MktmpioInstance instance;
        try {
            instance = MktmpioInstance.create(baseUrl, token, dbType);
        } catch (IOException ex) {
            listener.fatalError("mktmpio: " + ex.getMessage());
            throw new InterruptedException(ex.getMessage());
        }
        listener.hyperlink(baseUrl + "/i/" + instance.id, instance.type + " instance " + instance.id);
        listener.getLogger().printf("mktmpio instance created: %s\n", instance.type);
        return instance;
    }

    private List<MktmpioInstance> makeInstances(TaskListener listener, String token, String baseUrl, final String dbs)
            throws InterruptedException, IOException {
        List<MktmpioInstance> envs = new LinkedList<MktmpioInstance>();
        for (String type : dbs.split("\\s*,\\s*")) {
            if (TYPES.containsKey(type)) {
                envs.add(makeInstance(listener, token, baseUrl, type));
            }
        }
        return envs;
    }
}
