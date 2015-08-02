package org.jenkinsci.plugins.mktmpio;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.tasks.SimpleBuildWrapper.Disposer;

import java.io.IOException;
import java.util.List;

public class MktmpioDisposer extends Disposer {
    private static final long serialVersionUID = 1L;
    private final MktmpioAction[] environments;

    public MktmpioDisposer(final List<MktmpioAction> environments) {
        this.environments = environments.toArray(new MktmpioAction[]{});
    }

    @Override
    public void tearDown(final Run<?, ?> run, final FilePath workspace,
                         final Launcher launcher, final TaskListener listener)
            throws IOException, InterruptedException {
        for (MktmpioAction env : environments) {
            if (!env.shutdownWithBuild) {
                Mktmpio.dispose(env, launcher, listener);
            }
        }
    }
}
