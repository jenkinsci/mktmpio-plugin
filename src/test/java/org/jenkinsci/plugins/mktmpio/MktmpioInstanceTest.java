package org.jenkinsci.plugins.mktmpio;

import org.junit.Test;

import java.util.Map;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.jenkinsci.plugins.mktmpio.MktmpioTestUtil.roundTrip;

public class MktmpioInstanceTest {
    @Test
    public void preservesProperties() throws Exception {
        MktmpioInstance simple = new MktmpioInstance("id", "host", 1, "username", "password", "type", "url");
        assertThat(simple.getId(), is("id"));
        assertThat(simple.getHost(), is("host"));
        assertThat(simple.getPort(), is(1));
        assertThat(simple.getUsername(), is("username"));
        assertThat(simple.getPassword(), is("password"));
        assertThat(simple.getType(), is("type"));
        assertThat(simple.getUrl(), is("url"));
    }

    @Test
    public void producesNamespacedEnvironmentVariables() throws Exception {
        MktmpioInstance simple = new MktmpioInstance("id", "host", 1, "username", "password", "type", "url");
        Map<String, String> env = simple.envVars();
        assertThat(env.size(), is(6));
        assertThat(env, hasEntry("TYPE_ID", "id"));
        assertThat(env, hasEntry("TYPE_HOST", "host"));
        assertThat(env, hasEntry("TYPE_PORT", "1"));
        assertThat(env, hasEntry("TYPE_USERNAME", "username"));
        assertThat(env, hasEntry("TYPE_PASSWORD", "password"));
        assertThat(env, hasEntry("TYPE_TYPE", "type"));
        assertThat(env, not(hasEntry("TYPE_URL", "url")));
    }

    @Test
    public void serializesAndDeserializes() throws Exception {
        MktmpioInstance given = new MktmpioInstance("id", "host", 1, "username", "password", "type", "url");
        assertThat(given, is(sameBeanAs(roundTrip(given))));
    }
}