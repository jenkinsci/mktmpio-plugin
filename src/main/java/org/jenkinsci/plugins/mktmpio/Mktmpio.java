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

    static void dispose(final MktmpioAction env, final Launcher launcher, final TaskListener listener) throws IOException, InterruptedException {
        final String instanceID = env.id;
        MktmpioInstance instance = new MktmpioInstance(env);
        instance.destroy();
        listener.getLogger().printf("mktmpio instance shutdown. type: %s, host: %s, port: %d\n", env.type, env.host, env.port);
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
        final List<MktmpioAction> environments = createInstances(listener, token, baseUrl, dbs);
        for (final MktmpioAction env : environments) {
            final Map<String, String> envVars = env.envVars();
            for (Map.Entry<String, String> entry : envVars.entrySet()) {
                listener.getLogger().printf("setting %s=%s\n", entry.getKey(), entry.getValue());
            }
            build.addAction(env);
            context.getEnv().putAll(envVars);
        }
        context.setDisposer(new MktmpioDisposer(environments));
    }

    private MktmpioAction getMktmpioEnvironment(TaskListener listener, String token, String baseUrl, String dbType) throws InterruptedException, IOException {
        listener.getLogger().printf("Attempting to create instance (server: %s, token: %s, type: %s)",
                baseUrl, token.replaceAll(".", "*"), dbType);
        final MktmpioInstance instance;
        try {
            instance = MktmpioInstance.create(baseUrl, token, dbType);
        } catch (IOException ex) {
            listener.fatalError("mktmpio: " + ex.getMessage());
            throw new InterruptedException(ex.getMessage());
        }
        final MktmpioAction env = instance.getEnv();
        listener.hyperlink(baseUrl + "/i/" + env.id, env.type + " instance " + env.id);
        listener.getLogger().printf("mktmpio instance created: %s\n", env.type);
        return env;
    }

    private List<MktmpioAction> createInstances(TaskListener listener, String token, String baseUrl, final String dbs) throws InterruptedException, IOException {
        List<MktmpioAction> envs = new LinkedList<MktmpioAction>();
        for (String type : dbs.split("\\s*,\\s*")) {
            if (TYPES.containsKey(type)) {
                envs.add(getMktmpioEnvironment(listener, token, baseUrl, type));
            }
        }
        return envs;
    }
}
