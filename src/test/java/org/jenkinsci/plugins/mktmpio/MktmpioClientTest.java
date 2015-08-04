package org.jenkinsci.plugins.mktmpio;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;

public class MktmpioClientTest extends MktmpioBaseTest {
    @Test
    public void testFailsWithBadCredentials() throws Exception {
        MktmpioClient client = new MktmpioClient(mockedServer(), "fake-token");
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
        MktmpioClient client = new MktmpioClient(mockedServer(), "totally-legit-token");
        prepareFakeInstance("totally-legit-token", "redis");
        MktmpioInstance redis = client.create("redis");
        assertThat(redis.getUsername(), isEmptyString());
        assertThat(redis.getType(), is("redis"));
        client.destroy(redis);
    }
}
