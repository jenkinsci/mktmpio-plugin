package org.jenkinsci.plugins.mktmpio;

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import hudson.Util;
import org.junit.After;
import org.junit.ClassRule;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MktmpioBaseTest {
    @ClassRule
    public static final WireMockRule wireMockRule = new WireMockRule(0); // No-args constructor defaults to port 8080

    static String mockedServer() {
        return "http://127.0.0.1:" + wireMockRule.port();
    }

    static void prepareToRejectUnauthorized(final String token, final String type) {
        stubFor(post(urlEqualTo("/api/v1/new/" + type))
                .withHeader("X-Auth-Token", equalTo(token))
                .withHeader("User-Agent", containing("mktmpio-jenkins-plugin"))
                .willReturn(aResponse()
                        .withStatus(403)
                        .withBody("{\"error\":\"Authentication required\"}")));
    }

    static void prepareFakeInstance(final String token, final String type) {
        final String instance = "{\"id\":\"01ab34cd56ef\",\"host\":\"12.34.56.78\",\"port\":54321,\"type\":\"" + type + "\"}";
        stubFor(post(urlEqualTo("/api/v1/new/" + type))
                .withHeader("X-Auth-Token", equalTo(token))
                .withHeader("User-Agent", containing("mktmpio-jenkins-plugin"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(201)
                        .withBody(instance)));
        stubFor(delete(urlEqualTo("/api/v1/i/01ab34cd56ef"))
                .withHeader("X-Auth-Token", equalTo(token))
                .willReturn(aResponse()
                                .withStatus(201)
                ));
    }

    @After
    public void dumpRequests() {
        for (LoggedRequest req : wireMockRule.findAll(RequestPatternBuilder.allRequests())) {
            System.out.printf("%s %s\n", req.getMethod(), req.getUrl());
            for (HttpHeader h : req.getHeaders().all()) {
                System.out.printf("  %s: %s\n", h.key(), Util.join(h.values(), "; "));
            }
        }
    }
}
