package org.jenkinsci.plugins.mktmpio;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MktmpioClientTest extends MktmpioBaseTest {
    @Test
    public void testFailsWithBadCredentials() throws Exception {
        MktmpioClient client = new MktmpioClient(mockedServer(), "fake-token");
        MktmpioInstance failed;
        prepareToRejectUnauthorized("fake-token", "redis");
        try {
            failed = client.create("redis");
            assertThat("result is null", failed == null);
        } catch (IOException ex) {
            assertThat(ex.getMessage(), containsString("Authentication required"));
        }
    }

    @Test
    public void testSucceedsWithGoodCredentials() throws Exception {
        MktmpioClient client = new MktmpioClient(mockedServer(), "totally-legit-token");
        prepareFakeInstance("totally-legit-token", "redis");
        MktmpioInstance redis = client.create("redis");
        assertThat("result is not null", redis != null);
        assertThat(redis.getType(), is("redis"));
        client.destroy(redis);
    }
}
