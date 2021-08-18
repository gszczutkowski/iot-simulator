package com.testcraftsmanship.iotsimulator.utils;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersRequest;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;

import java.util.List;

public final class AwsSsmClient {
    private AwsSsmClient() {
    }

    /**
     * Method returns value of the parameter from AWS System Manager Shared Resources Parameter Store
     *
     * @param parameterName name of the parameter in Parameter Store
     * @return value of the parameter in Parameter Store
     */
    public static String getSsmParameterValue(Regions region, String parameterName) {
        List<Parameter> parameters = AWSSimpleSystemsManagementClientBuilder.standard()
                .withRegion(region)
                .build()
                .getParameters(new GetParametersRequest()
                        .withWithDecryption(true)
                        .withNames(parameterName))
                .getParameters();
        return parameters.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find parameter with name: " + parameterName))
                .getValue();
    }
}