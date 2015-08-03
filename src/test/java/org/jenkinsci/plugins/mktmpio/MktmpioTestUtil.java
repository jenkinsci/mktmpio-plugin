package org.jenkinsci.plugins.mktmpio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MktmpioTestUtil {
    public static <G> G roundTrip(G given) throws Exception {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(given);
        out.flush();
        out.close();
        final ObjectInputStream i = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        return (G) i.readObject();
    }
}
