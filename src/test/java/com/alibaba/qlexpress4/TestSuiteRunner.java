package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QFunction;
import com.alibaba.qlexpress4.runtime.QRuntime;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Author: DQinYuan
 */
public class TestSuiteRunner {

    private static final String ASSERT_FUNCTION_NAME = "assert";
    private static final String TEST_PATH_ATT = "TEST_PATH";
    private static final String ERROR_CODE_DIR_NAME = "errcode";

    private Express4Runner testRunner;

    @Before
    public void before() {
        this.testRunner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        testRunner.addFunction(ASSERT_FUNCTION_NAME, new AssertFunction());
    }

    @Test
    public void suiteTest() throws URISyntaxException, IOException {
        Path testSuiteRoot = getTestSuiteRoot();
        handleDirectory(testSuiteRoot, "");
    }

    private void handleDirectory(Path dir, String pathPrefix) throws IOException {
        Files.list(dir).forEach(path -> {
            try {
                String newPrefix = pathPrefix + "/" + path.getFileName();
                if (Files.isDirectory(path)) {
                    handleDirectory(path, newPrefix);
                } else if (ERROR_CODE_DIR_NAME.equals(dir.getFileName().toString())) {
                    handleErrCode(path, newPrefix, path.getFileName().toString()
                            .replace(".ql", ""));
                } else {
                    handleFile(path, newPrefix);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleErrCode(Path qlFile, String path, String expectErrCode) throws IOException {
        Map<String, Object> attachments = new HashMap<>();
        attachments.put(TEST_PATH_ATT, path);

        String qlScript = new String(Files.readAllBytes(qlFile));
        try {
            testRunner.execute(qlScript, Collections.emptyMap(), QLOptions.builder()
                    .attachments(attachments)
                    .build());
        } catch (QLException qlException) {
            assertEquals(path + " error code assert fail", expectErrCode, qlException.getErrorCode());
        } catch (Exception e) {
            throw new RuntimeException(path + " unknown error", e);
        }
    }

    private void handleFile(Path qlFile, String path) throws IOException {
        Map<String, Object> attachments = new HashMap<>();
        attachments.put(TEST_PATH_ATT, path);

        String qlScript = new String(Files.readAllBytes(qlFile));
        try {
            testRunner.execute(qlScript, Collections.emptyMap(), QLOptions.builder()
                    .attachments(attachments)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(path + " unknown error", e);
        }
    }

    @Test
    public void assertTest() {
        Map<String, Object> attachment = new HashMap<>();
        attachment.put(TEST_PATH_ATT, "a/b.ql");

        QLOptions attachOptions = QLOptions.builder()
                .attachments(attachment)
                .build();
        testRunner.execute("assert(true)", Collections.emptyMap(), attachOptions);
        assertErrCodeAndReason(testRunner, "assert(false)", attachOptions,
                "CALL_FUNCTION_BIZ_EXCEPTION",
                "a/b.ql: assert fail");
        assertErrCodeAndReason(testRunner, "assert(false, 'my test')", attachOptions,
                "CALL_FUNCTION_BIZ_EXCEPTION", "a/b.ql: my test");
        // variable can be the same name with function
        testRunner.execute("assert = 4;assert(assert == 4)",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
    }

    private Path getTestSuiteRoot() throws URISyntaxException {
        return Paths.get(getClass().getClassLoader()
                .getResource("testsuite").toURI());
    }

    private void assertErrCodeAndReason(Express4Runner express4Runner, String script,
                                        QLOptions qlOptions,
                                        String errCode, String reason) {
        try {
            express4Runner.execute(script, Collections.emptyMap(),
                    qlOptions);
        } catch (QLException e) {
            assertEquals(errCode, e.getErrorCode());
            assertEquals(reason, e.getReason());
        }
    }

    private static class AssertFunction implements QFunction {
        @Override
        public Object call(QRuntime qRuntime, Parameters parameters) throws Exception {
            int pSize = parameters.size();
            switch (pSize) {
                case 1:
                    Boolean b = (Boolean) parameters.getValue(0);
                    if (b == null || !b) {
                        throw new UserDefineException(wrap(qRuntime.attachment(),
                                "assert fail"));
                    }
                    return null;
                case 2:
                    Boolean b0 = (Boolean) parameters.getValue(0);
                    if (b0 == null || !b0) {
                        throw new UserDefineException(wrap(qRuntime.attachment(),
                                (String) parameters.getValue(1)));
                    }
                    return null;
                default:
                    throw new UserDefineException("invalid parameter size");
            }
        }

        private String wrap(Map<String, Object> attachments, String originErrInfo) {
            return attachments.get(TEST_PATH_ATT) + ": " + originErrInfo;
        }
    }
}
