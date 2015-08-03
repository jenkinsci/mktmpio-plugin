package org.jenkinsci.plugins.mktmpio;

import hudson.model.InvisibleAction;

import java.io.Serializable;
import java.util.List;

public class MktmpioAction extends InvisibleAction implements Serializable {
    private static final long serialVersionUID = 1L;
    public final MktmpioInstance[] instances;

    public MktmpioAction(final List<MktmpioInstance> instances) {
        this.instances = instances.toArray(new MktmpioInstance[]{});
    }
}
