package org.jenkinsci.plugins.mktmpio;

import hudson.model.InvisibleAction;

import java.io.Serializable;

public class MktmpioAction extends InvisibleAction implements Serializable {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 2L;
    private final MktmpioClient client;
    private final MktmpioInstance[] instances;

    public MktmpioAction(final MktmpioClient client, final MktmpioInstance[] instances) {
        this.client = client;
        this.instances = instances;
    }

    public MktmpioClient getClient() {
        return client;
    }

    public MktmpioInstance[] getInstances() {
        return instances;
    }
}
