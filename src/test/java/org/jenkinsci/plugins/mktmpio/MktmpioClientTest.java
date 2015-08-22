package org.jenkinsci.plugins.mktmpio;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;

public class MktmpioClientTest extends MktmpioBaseTest {
    @Test
    public void testFailsWithBadCredentials() throws Exception {
        MktmpioClient client = new MktmpioClient(mockedServer(), "fake-token", "mktmpio-jenkins-plugin");
        prepareToRejectUnauthorized("fake-token", "redis");
        try {
            client.create("redis");
            org.junit.Assert.fail("should client.create should have thrown");
        } catch (IOException ex) {
            assertThat(ex.getMessage(), containsString("Authentication required"));
        }
    }

    @Test
    public void testSucceedsWithGoodCredentials() throws Exception {
        MktmpioClient client = new MktmpioClient(mockedServer(), "totally-legit-token", "mktmpio-jenkins-plugin");
        prepareFakeInstance("totally-legit-token", "redis");
        MktmpioInstance redis = client.create("redis");
        assertThat(redis.getUsername(), isEmptyString());
        assertThat(redis.getType(), is("redis"));
        client.destroy(redis);
    }

    @Test
    public void testOfflineCreate() throws Exception {
        MktmpioClient client = new MktmpioClient("http://127.0.0.1:1", "does-not-matter", "mktmpio-jenkins-plugin");
        try {
            client.create("redis");
            org.junit.Assert.fail("should client.create should have thrown");
        } catch (IOException ex) {
            assertThat(ex.getMessage(), not(containsString("Authentication required")));
        }

    }

    @Test
    public void testOfflineDestroy() throws Exception {
        final MktmpioClient client = new MktmpioClient("http://127.0.0.1:1", "does-not-matter", "mktmpio-jenkins-plugin");
        final MktmpioInstance instance = new MktmpioInstance("", "", 0, "", "", "", "");
        try {
            client.destroy(instance);
            org.junit.Assert.fail("should client.create should have thrown");
        } catch (IOException ex) {
            assertThat(ex.getMessage(), not(containsString("Authentication required")));
        }
    }
}
