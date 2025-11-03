package com.example.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class TestProcessor implements Processor {

    public static final String PARAM_TARGETOBJECT = "ExampleSink_targetObject";
    public static final String PARAM_MAPPER_INSTANCE = "ExampleSink_mapperInstance";
    public static final String PARAM_METHODNAME = "ExampleSink_methodName";
    public static final String PARAM_INPUTPROPERTIESLIST = "ExampleSink_inputPropertiesList";
    public static final String PARAM_EXCHANGEARG = "ExampleSink_exchangeArg";

    @Autowired
    private CamelContext camelContext;

    @Override
    public void process(Exchange exchange) throws Exception {

        String targetObject = exchange.getProperty(PARAM_TARGETOBJECT, String.class);
        log.info("sink kamelet parameter => targetObject ={}", targetObject);

        String mapperInstance = exchange.getProperty(PARAM_MAPPER_INSTANCE, String.class);
        log.info("sink kamelet parameter => mapperInstance ={}", mapperInstance);

        String methodName = exchange.getProperty(PARAM_METHODNAME, String.class);
        log.info("sink kamelet parameter => methodName ={}", methodName);

        String inputPropertiesList = exchange.getProperty(PARAM_INPUTPROPERTIESLIST, String.class);
        log.info("sink kamelet parameter => inputPropertiesList ={}", inputPropertiesList);

        Boolean exchangeArg = exchange.getProperty(PARAM_EXCHANGEARG, Boolean.class);
        log.info("sink kamelet parameter => exchangeArg ={}", exchangeArg);

    }
}
