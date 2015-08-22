package org.jenkinsci.plugins.mktmpio;

import org.junit.Test;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.jenkinsci.plugins.mktmpio.MktmpioTestUtil.roundTrip;

public class MktmpioActionTest {
    @Test
    public void testSerialization() throws Exception {
        MktmpioAction action = new MktmpioAction(new MktmpioClient("a", "b", "mktmpio-jenkins-plugin"), new MktmpioInstance[]{});
        assertThat(action, is(sameBeanAs(roundTrip(action))));
    }
}
