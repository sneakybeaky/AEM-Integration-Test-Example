package com.ninedemons.aemtesting.models.title;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.apache.sling.testing.tools.http.RequestExecutor;
import org.apache.sling.testing.tools.sling.SlingClient;
import org.apache.sling.testing.tools.sling.SlingTestBase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Uses the Sling RESTful interface to test the TitleModel class.
 *
 * This test class just tests the one scenario, but illustrates the general approach
 */
public class TitleModelTest extends SlingTestBase {

    public static final String TITLE_TYPE = "extralarge";
    public static final String EXPECTED_TITLE = "Component Title";
    public static final String EXPECTED_ELEMENT = "h1";

    public static final String TEST_NODE_FOR_HAPPY_PATH = "title-component";
    public static final String TEST_APP_FOLDER_PATH = "/apps/test/title-model-test/";
    public static final String TEST_CONTENT_FOLDER_PATH = "/content/test/title-model-test/";
    public static final String TEST_PAGE = "title-model-page";
    public static final String PATH_TO_TEST_NODE = TEST_CONTENT_FOLDER_PATH + TEST_PAGE + "/"  + JcrConstants.JCR_CONTENT + "/" + TEST_NODE_FOR_HAPPY_PATH;


    /**
     * The SlingClient can be used to interact with the repository when it is
     * started. By retrieving the information for the Server URL, username and
     * password, the Sling instance will be automatically started.
     */
    private SlingClient slingClient = new SlingClient(this.getServerBaseUrl(),
            this.getServerUsername(), this.getServerPassword());


    /**
     * The actual test, will be executed once the Sling instance is started and
     * the setup is complete.
     *
     * @throws Exception
     */
    @Test
    public void whenAllPropertiesSetAgainstComponent() throws Exception {

        // Given a component instance where the title and type are set against
        // the instance node

        // When the component is rendered
        RequestExecutor result = getRequestExecutor().execute(
                getRequestBuilder().buildGetRequest(
                        PATH_TO_TEST_NODE + ".html").withCredentials(
                        "admin", "admin"));

        // Then the model should return the values defined in the instance node
        result.assertStatus(200)
                .assertContentContains("Element is \'" + EXPECTED_ELEMENT + "\'")
                .assertContentContains("Title is \'" + EXPECTED_TITLE + "\'");

    }


    /**
     * Execute before the actual test, this will be used to setup the test data
     *
     * @throws Exception
     */
    @Before
    public void setupTestContext() throws Exception {
        createTestComponent();
        createTestContent();
    }

    private void createTestComponent() throws IOException {

        createTestAppsFolder();
        uploadTestJsp();
        checkTestComponentCreated();

    }

    private void createTestContent() throws IOException {

        createTestContentFolder();
        createTestPage();
        createTestPageComponent();
        checkTestContentCreated();

    }



    private void createTestContentFolder() throws IOException {

        if (slingClient.exists(TEST_CONTENT_FOLDER_PATH)) {
            slingClient.delete(TEST_CONTENT_FOLDER_PATH);
        }

        slingClient.mkdirs(TEST_CONTENT_FOLDER_PATH);

    }

    private void checkTestContentCreated() throws IOException {
        getRequestExecutor()
                .execute(
                        getRequestBuilder().buildGetRequest(
                                PATH_TO_TEST_NODE + ".json")
                                .withCredentials("admin", "admin"))
                .assertStatus(200);
    }

    private void createTestPage() throws IOException {

        slingClient.createNode(TEST_CONTENT_FOLDER_PATH + TEST_PAGE,
                JcrConstants.JCR_PRIMARYTYPE, NameConstants.NT_PAGE,
                JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_PRIMARYTYPE, "cq:PageContent",
                JcrConstants.JCR_CONTENT + "/" + JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, "geometrixx/components/contentpage",
                JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE,"Test Page Title",
                JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_DESCRIPTION,"Test Description"
        );
    }

    private void createTestPageComponent() throws IOException {

        slingClient.createNode(PATH_TO_TEST_NODE,
                JcrConstants.JCR_PRIMARYTYPE,JcrConstants.NT_UNSTRUCTURED,
                JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, "test/title-model-test",
                "type", TITLE_TYPE,
                JcrConstants.JCR_TITLE,EXPECTED_TITLE,
                JcrConstants.JCR_DESCRIPTION,"Test Node For SimpleModel"
        );
    }




    private void checkTestComponentCreated() throws IOException {
        getRequestExecutor()
                .execute(
                        getRequestBuilder().buildGetRequest(
                                TEST_APP_FOLDER_PATH + ".3.json")
                                .withCredentials("admin", "admin"))
                .assertStatus(200).getContent();
    }

    private void uploadTestJsp() throws IOException {

        slingClient.upload(
                TEST_APP_FOLDER_PATH + "/title-model-test.jsp",
                TitleModelTest.class.getClassLoader().getResourceAsStream(
                        "jsp/title-model/titleModelTest.jsp"), -1, true);
    }

    private void createTestAppsFolder() throws IOException {

        if (slingClient.exists(TEST_APP_FOLDER_PATH)) {
            slingClient.delete(TEST_APP_FOLDER_PATH);
        }

        slingClient.mkdirs(TEST_APP_FOLDER_PATH);

    }


}
