/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Greg Messner <greg@messners.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.gitlab4j.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import java.util.Arrays;
import java.util.List;

import org.gitlab4j.api.Constants.ApplicationScope;
import org.gitlab4j.api.models.Application;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * In order for these tests to run you must set the following properties in test-gitlab4j.properties
 * <p>
 * TEST_HOST_URL
 * TEST_PRIVATE_TOKEN
 * <p>
 * If any of the above are NULL, all tests in this class will be skipped.
 */
@Category(IntegrationTest.class)
public class TestApplications extends AbstractIntegrationTest {

    private static final String TEST_APPLICATION_NAME = "Test Application for GitLab4J-API";
    private static final String TEST_APPLICATION_REDIRECT = "http://example.com/application";
    private static final List<ApplicationScope> TEST_APPLICATION_SCOPES =
            Arrays.asList(ApplicationScope.SUDO, ApplicationScope.EMAIL);

    private static GitLabApi gitLabApi;

    public TestApplications() {
        super();
    }

    @BeforeClass
    public static void setup() {
        // Must setup the connection to the GitLab test server
        gitLabApi = baseTestSetup();

        if (gitLabApi != null) {
            try {
                List<Application> apps = gitLabApi.getApplicationsApi().getApplications();
                for (Application app : apps) {
                    if (TEST_APPLICATION_NAME.equals(app.getApplicationName())) {
                        gitLabApi.getApplicationsApi().deleteApplication(app.getId());
                    }
                }
            }  catch (Exception ignore) {}
        }
    }

    @AfterClass
    public static void teardown() throws GitLabApiException {
    }

    @Before
    public void beforeMethod() {
        assumeNotNull(gitLabApi);
    }

    @Test
    public void testGetApplications() throws GitLabApiException {
        List<Application> apps = gitLabApi.getApplicationsApi().getApplications();
        assertNotNull(apps);
    }

    @Test
    public void testAddAndDeleteApplication() throws GitLabApiException {

        List<Application> apps = gitLabApi.getApplicationsApi().getApplications();
        int appCount = apps.size();

        Application app = gitLabApi.getApplicationsApi().createApplication(TEST_APPLICATION_NAME, TEST_APPLICATION_REDIRECT, TEST_APPLICATION_SCOPES);
        assertNotNull(app);

        apps = gitLabApi.getApplicationsApi().getApplications();
        assertTrue(apps.size() == appCount + 1);

        Application found = apps.stream().filter(a -> TEST_APPLICATION_NAME.equals(a.getApplicationName())).findAny().orElse(null);
        assertNotNull(found);

        gitLabApi.getApplicationsApi().deleteApplication(app.getId());
        apps = gitLabApi.getApplicationsApi().getApplications();
        assertTrue(apps.size() == appCount);
        found = apps.stream().filter(a -> TEST_APPLICATION_NAME.equals(a.getApplicationName())).findAny().orElse(null);
        assertNull(found);
    }
}