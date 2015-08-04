package org.jenkinsci.plugins.mktmpio;

import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;

public class MktmpioDescriptor extends BuildWrapperDescriptor {
    private String token = "";
    private String server = Mktmpio.DEFAULT_SERVER;

    public MktmpioDescriptor() {
        super(Mktmpio.class);
        load();
    }

    // TODO: needs test coverage
    @Override
    public boolean configure(final StaplerRequest req, final JSONObject formData) {
        token = formData.getString("token");
        server = formData.optString("server", Mktmpio.DEFAULT_SERVER);
        save();
        return true;
    }

    // TODO: needs test coverage
    @Override
    public Mktmpio newInstance(final StaplerRequest req, final JSONObject formData)
            throws hudson.model.Descriptor.FormException {
        return req.bindJSON(Mktmpio.class, formData);
    }

    @Nonnull
    public String getToken() {
        return token;
    }

    @DataBoundSetter
    public void setToken(String token) {
        if (token == null)
            this.token = "";
        else
            this.token = token;
    }

    @Nonnull
    public String getServer() {
        return server;
    }

    @DataBoundSetter
    public void setServer(String server) {
        if (server == null || server.isEmpty())
            this.server = Mktmpio.DEFAULT_SERVER;
        else
            this.server = server;
    }

    public boolean isApplicable(AbstractProject<?, ?> item) {
        return true;
    }

    @Nonnull
    public String getDisplayName() {
        return "Create temporary database server for build";
    }

    @SuppressWarnings("SameReturnValue")
    @Nonnull
    public ListBoxModel doFillDbsItems() {
        return Mktmpio.TYPE_OPTIONS;
    }
}