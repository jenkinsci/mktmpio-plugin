package org.jenkinsci.plugins.mktmpio;

import hudson.CopyOnWrite;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

public class MktmpioDescriptor extends BuildWrapperDescriptor {

    @CopyOnWrite
    private String token, server = Mktmpio.DEFAULT_SERVER;

    public MktmpioDescriptor() {
        super(Mktmpio.class);
        load();
    }

    @Override
    public boolean configure(final StaplerRequest req, final JSONObject formData) {
        token = formData.getString("token");
        server = formData.optString("server", Mktmpio.DEFAULT_SERVER);
        save();
        return true;
    }

    @Override
    public Mktmpio newInstance(final StaplerRequest req, final JSONObject formData)
            throws hudson.model.Descriptor.FormException {
        return req.bindJSON(Mktmpio.class, formData);
    }

    public String getToken() {
        return token;
    }

    @DataBoundSetter
    public void setToken(String token) {
        this.token = token;
    }

    public String getServer() {
        return server;
    }

    @DataBoundSetter
    public void setServer(String server) {
        this.server = server;
    }

    public boolean isApplicable(AbstractProject<?, ?> item) {
        return true;
    }

    public String getDisplayName() {
        return "Create temporary database server for build";
    }

    public ListBoxModel doFillDbsItems() {
        return Mktmpio.TYPE_OPTIONS;
    }
}