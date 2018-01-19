package com.nt.backend;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.core.NestedCheckedException;

import java.net.ConnectException;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class SampleLiquibaseApplicationTests {

    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Test
    public void testDefaultSettings() throws Exception {
        /*
        We start the spring-application without the spring-boot-test-annotations at this point,
        because we want to use the OutputCapture-Rule for the spring bootstrap already
         */
        try {
            BackendApplication.main(new String[] {
                    "--server.port=0",
                    "--spring.profiles.active=test" });
        }
        catch (IllegalStateException ex) {
            if (serverNotRunning(ex)) {
                return;
            }
        }
        String output = this.outputCapture.toString();
        assertThat(output).contains("Successfully acquired change log lock")
                .contains("Table product created")
                .contains("ChangeSet classpath:/db/changelog/db.changelog-master.yaml" +
                        "::1::dieter ran successfully")
                .contains("Successfully released change log lock");
    }

    @SuppressWarnings("serial")
    private boolean serverNotRunning(IllegalStateException ex) {
        NestedCheckedException nested = new NestedCheckedException("failed", ex) {
        };
        if (nested.contains(ConnectException.class)) {
            Throwable root = nested.getRootCause();
            if (root.getMessage().contains("Connection refused")) {
                return true;
            }
        }
        return false;
    }
}