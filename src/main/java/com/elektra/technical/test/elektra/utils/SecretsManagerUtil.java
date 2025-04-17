package com.elektra.technical.test.elektra.utils;

/**
 *
 * @author JORGE
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;

public class SecretsManagerUtil {

    public static Map<String, String> getDatabaseSecret(String secretName, String regionName) throws Exception {
        Region region = Region.of(regionName);
        SecretsManagerClient client = SecretsManagerClient.builder().region(region).build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        String secretJson = getSecretValueResponse.secretString();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(secretJson, Map.class); // host, username, password, dbname
    }
}
