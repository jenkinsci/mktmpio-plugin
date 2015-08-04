package org.jenkinsci.plugins.mktmpio;

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
    public void testDefaults() throws Exception {
        MktmpioDescriptor config = getDescriptor();
        config.setToken("");
        config.setServer("");
        assertThat(config.getToken(), isEmptyString());
        assertThat(config.getServer(), is(Mktmpio.DEFAULT_SERVER));
        config.setToken("something");
        assertThat(config.getToken(), is("something"));
        config.setServer("something-else");
        assertThat(config.getServer(), is("something-else"));
    }

    private MktmpioDescriptor getDescriptor() {
        return (MktmpioDescriptor) j.jenkins.getDescriptor(Mktmpio.class);
    }
}
