package com.example;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static com.example.processor.TestProcessor.*;
import static org.junit.jupiter.api.Assertions.*;


@CamelSpringBootTest
@EnableAutoConfiguration
@SpringBootTest
@ActiveProfiles("test")
class KameletIntegrationTest {

    @Autowired
    ProducerTemplate producerTemplate;

    @EndpointInject("mock:test")
    MockEndpoint mockEndpoint;

    @TestConfiguration
    static class RouteTestConfig {

        @Bean
        RoutesBuilder routeTestSinkKamelet() {
            return new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:test_sink_kamelet")
                            .setProperty("originalBody", simple("${body}"))
                            .setProperty("userToSet", constant("tester"))
                            .setProperty("stanId", constant(999))
                            .log("Start invoke kamelet 1 - req ${body}")
                            .toD("kamelet:example-sink?targetObject=${exchangeProperty.pvar['targetObject']}&mapperInstance=${exchangeProperty.pvar['mapperInstance']}&methodName=${exchangeProperty.pvar['methodName']}&inputPropertiesList=${exchangeProperty.pvar['inputPropertiesList']}&exchangeArg=${exchangeProperty.pvar['exchangeArg']}")
                            .log("End invoke kamelet 1 - res ${body}")
                            .to("mock:test");
                }
            };
        }

    }


    @BeforeEach
    void beforeAllTest() {
        mockEndpoint.reset();
    }

    @Test
    void shouldAutowireProducerTemplate() {
        assertNotNull(producerTemplate);
    }

    @Test
    void shouldSetCustomName() {
        assertEquals("kamelettest", producerTemplate.getCamelContext().getName());
    }

    @Test
    void testHappy_4_14_1() throws Exception {
        mockEndpoint.setExpectedMessageCount(1);

        mockEndpoint.expects(() -> {

            Exchange exchange = mockEndpoint.getExchanges().get(0);

            String targetObject = exchange.getProperty(PARAM_TARGETOBJECT, String.class);
            String mapperInstance = exchange.getProperty(PARAM_MAPPER_INSTANCE, String.class);
            String methodName = exchange.getProperty(PARAM_METHODNAME, String.class);
            String inputPropertiesList = exchange.getProperty(PARAM_INPUTPROPERTIESLIST, String.class);
            Boolean exchangeArg = exchange.getProperty(PARAM_EXCHANGEARG, Boolean.class);

            assertEquals("%!-123456", targetObject);
            assertEquals("###:test", mapperInstance);
            assertEquals("@//", methodName);
            assertEquals("originalBody,user,id", inputPropertiesList);
            assertTrue(exchangeArg);

            assertEquals("Hello World!", exchange.getIn().getBody(String.class));
        });

        Map<String, Object> mapVars = new HashMap<>();
        mapVars.put("targetObject", "%!-123456");
        mapVars.put("mapperInstance", "###:test");
        mapVars.put("methodName", "@//");
        mapVars.put("inputPropertiesList", "originalBody,user,id");
        mapVars.put("exchangeArg", true);

        producerTemplate.sendBodyAndProperty("direct:test_sink_kamelet", "Hello World!", "pvar", mapVars);

        mockEndpoint.assertIsSatisfied();

    }

}
