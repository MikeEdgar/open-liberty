package com.ibm.ws.microprofile.openapi.validation.fat;

import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.config.ServerConfiguration;
//import com.ibm.ws.openapi.fat.utils.OpenAPIConnection;

import componenttest.annotation.MinimumJavaLevel;
import componenttest.annotation.Server;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.HttpUtils;

/**
 * Tests to ensure that OpenAPI model validation works and proper events (errors, warning) are reported.
 *
 */
@RunWith(FATRunner.class)
public class OpenAPIValidationTestOne {
	
	@Server("validationServerOne")
    public static LibertyServer server;
    private static final String OPENAPI_VALIDATION_YAML = "openapi_validation";

    private static final int TIMEOUT = 10000; //in milliseconds (10 seconds)

    @BeforeClass
    public static void setUpTest() throws Exception {
        HttpUtils.trustAllCertificates();

        server.startServer("OpenAPIValidationTestOne.log", true);

        server.validateAppLoaded(OPENAPI_VALIDATION_YAML);
        
        assertNotNull("Web application is not available at /openapi_validation/",
                      server.waitForStringInLog("CWWKT0016I.*/openapi_validation/")); //wait for endpoint to become available
        assertNotNull("CWWKF0011I.* not recieved on relationServer",
                      server.waitForStringInLog("CWWKF0011I.*")); // wait for server is ready to run a smarter planet message
        assertNotNull("CWWKO1660I.* The application openapi_validation was processed",
                	  server.waitForStringInLog("CWWKO1660I.*")); //wait for application to be processed
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        if (server.isStarted()) {
            server.stopServer("CWWKO1603E");
        }
    }
    
    @Test
    public void testValidationMessagesForInfo() throws Exception {
    	//Tests that correct validation messages are provided for the validation errors in the following models:
    	//Info, Contact, License, ServerVariable(s), Server(s), PathItem, Operation, ExternalDocumentation, Parameter, RequestBody
    	
    	assertNotNull("CWWKO1650E.* The Info object must contain a valid URL. The \"not in URL format\" value specified for \"termsOfService\" is not a valid URL, Location: #/info/termsOfService", 
    			server.waitForStringInLog("CWWKO1660I.*"));
    }

}
