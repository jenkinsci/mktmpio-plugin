package org.jenkinsci.plugins.mktmpio;

import hudson.*;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Mktmpio extends SimpleBuildWrapper {
    public static final String DEFAULT_SERVER = "https://mktmp.io";
    public static final String VERSION = Jenkins.getActiveInstance().getPlugin("mktmpio").getWrapper().getVersion();
    public static final String USER_AGENT = "mktmpio-jenkins-plugin/" + VERSION;
    @SuppressWarnings("WeakerAccess")
    @Extension
    public static final MktmpioDescriptor GLOBAL_CONFIG = new MktmpioDescriptor();
    private static final Map<String, String> TYPES = new HashMap<String, String>(5) {{
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
    // Job config
    private String dbs;

    @DataBoundConstructor
    public Mktmpio(String dbs) {
        this.dbs = dbs;
    }

    @SuppressWarnings("WeakerAccess")
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
        final MktmpioClient client = new MktmpioClient(baseUrl, token, USER_AGENT);
        final MktmpioInstance[] instances = makeInstances(listener, client, dbs);
        final MktmpioAction action = new MktmpioAction(client, instances);
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

    private MktmpioInstance makeInstance(TaskListener listener, MktmpioClient client, String dbType)
            throws InterruptedException, IOException {
        listener.getLogger().printf("Attempting to create instance (client: %s, type: %s)",
                client, dbType);
        final MktmpioInstance instance;
        try {
            instance = client.create(dbType);
        } catch (IOException ex) {
            listener.fatalError("mktmpio: " + ex.getMessage());
            throw new InterruptedException(ex.getMessage());
        }
        listener.hyperlink(instance.getUrl(), instance.getType() + " instance " + instance.getId());
        listener.getLogger().printf("mktmpio instance created: %s\n", instance.getType());
        return instance;
    }

    private MktmpioInstance[] makeInstances(TaskListener listener, MktmpioClient client, final String dbs)
            throws InterruptedException, IOException {
        final ArrayList<MktmpioInstance> instances = new ArrayList<MktmpioInstance>();
        for (String type : dbs.split("\\s*,\\s*")) {
            if (TYPES.containsKey(type)) {
                instances.add(makeInstance(listener, client, type));
            }
        }
        return instances.toArray(new MktmpioInstance[instances.size()]);
    }
}
