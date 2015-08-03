package org.jenkinsci.plugins.mktmpio;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.tasks.SimpleBuildWrapper.Disposer;

import java.io.IOException;
import java.io.PrintStream;

public class MktmpioDisposer extends Disposer {
    private static final long serialVersionUID = 1L;
    private final MktmpioAction action;

    public MktmpioDisposer(final MktmpioAction action) {
        this.action = action;
    }

    @Override
    public void tearDown(final Run<?, ?> run, final FilePath workspace,
                         final Launcher launcher, final TaskListener listener)
            throws IOException, InterruptedException {
        PrintStream log = listener.getLogger();
        MktmpioClient mktmpio = getAction().getClient();
        for (MktmpioInstance i : getAction().getInstances()) {
            mktmpio.destroy(i);
            log.printf("mktmpio instance shutdown. type: %s, host: %s, port: %d\n", i.getType(), i.getHost(), i.getPort());
        }

    }

    public MktmpioAction getAction() {
        return action;
    }
}
