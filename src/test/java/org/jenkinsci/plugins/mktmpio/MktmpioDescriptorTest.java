package org.jenkinsci.plugins.mktmpio;

import hudson.model.FreeStyleProject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;

public class MktmpioDescriptorTest {
    @Rule
    public final JenkinsRule j = new JenkinsRule().withNewHome();

    @Test
    public void testServerDefault() throws Exception {
        final MktmpioDescriptor config = getDescriptor();
        config.setServer("");
        assertThat(config.getServer(), is(Mktmpio.DEFAULT_SERVER));
        config.setServer("something-else");
        assertThat(config.getServer(), is("something-else"));
        config.setServer(null);
        assertThat(config.getServer(), is(Mktmpio.DEFAULT_SERVER));
    }

    @Test
    public void testTokenDefault() throws Exception {
        final MktmpioDescriptor config = getDescriptor();
        config.setToken("");
        assertThat(config.getToken(), isEmptyString());
        config.setToken("something");
        assertThat(config.getToken(), is("something"));
        config.setToken(null);
        assertThat(config.getToken(), isEmptyString());
    }

    @Test
    public void testGlobalConfigRoundTrip() throws Exception {
        final MktmpioDescriptor config = getDescriptor();
        final String server = config.getServer();
        final String token = config.getToken();
        j.configRoundtrip();
        assertThat(config.getServer(), is(server));
        assertThat(config.getToken(), is(token));
    }

    @Test
    public void testJobConfigRoundTrip() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        Mktmpio before = new Mktmpio("redis");
        p.getBuildWrappersList().add(before);
        j.configRoundtrip(p);
        Mktmpio after = p.getBuildWrappersList().get(Mktmpio.class);
        j.assertEqualBeans(before, after, "dbs");
    }

    private MktmpioDescriptor getDescriptor() {
        return (MktmpioDescriptor) j.jenkins.getDescriptor(Mktmpio.class);
    }
}
