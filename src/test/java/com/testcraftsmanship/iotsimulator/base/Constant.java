package com.testcraftsmanship.iotsimulator.base;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.client.auth.Credentials;

import static com.testcraftsmanship.iotsimulator.utils.AwsSsmClient.getSsmParameterValue;

public final class Constant {
    private Constant() {
    }

    private static final String MQTT_ENDPOINT_SSM_PARAMETER_NAME = "mqtt-client-endpoint";
    private static final String ACCESS_KEY_ID = DefaultAWSCredentialsProviderChain.getInstance().getCredentials().getAWSAccessKeyId();
    private static final String SECRET_KEY = DefaultAWSCredentialsProviderChain.getInstance().getCredentials().getAWSSecretKey();

    public static final String TESTING_CLIENT_ID = "testcraftsmanship-aws-client";
    public static final String TESTED_CLIENT_ID = "testcraftsmanship-iot-simulator";

    public static final Credentials AWS_CREDENTIALS = new Credentials(ACCESS_KEY_ID, SECRET_KEY);
    public static final String MQTT_ENDPOINT = getSsmParameterValue(Regions.EU_WEST_1, MQTT_ENDPOINT_SSM_PARAMETER_NAME);
}
