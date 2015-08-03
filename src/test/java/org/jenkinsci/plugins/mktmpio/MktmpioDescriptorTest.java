package org.jenkinsci.plugins.mktmpio;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class MktmpioDescriptorTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testToken() throws Exception {
        MktmpioDescriptor config = getDescriptor();
        config.setToken("something");
        assertThat(config.getToken(), is(equalTo("something")));
    }

    @Test
    public void testServer() throws Exception {
        MktmpioDescriptor config = getDescriptor();
        assertThat(config.getServer(), is(equalTo(Mktmpio.DEFAULT_SERVER)));
        config.setServer("something-else");
        assertThat(config.getServer(), is(equalTo("something-else")));
    }

    private MktmpioDescriptor getDescriptor() {
        return (MktmpioDescriptor) j.jenkins.getDescriptor(Mktmpio.class);
    }
}
