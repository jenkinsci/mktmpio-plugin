package org.jenkinsci.plugins.mktmpio;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.tasks.SimpleBuildWrapper.Disposer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MktmpioDisposer extends Disposer {
    private static final long serialVersionUID = 1L;
    private final MktmpioEnvironment[] environments;

    public MktmpioDisposer(final List<MktmpioEnvironment> environments) {
        this.environments = environments.toArray(new MktmpioEnvironment[]{});
    }

    @Override
    public void tearDown(final Run<?, ?> run, final FilePath workspace,
                         final Launcher launcher, final TaskListener listener)
            throws IOException, InterruptedException {
        for (MktmpioEnvironment env : environments) {
            if (!env.shutdownWithBuild) {
                Mktmpio.dispose(env, launcher, listener);
            }
        }
    }
}
