package br.com.likwi.photoapp.service;

import com.google.gson.JsonObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class CognitoUserService {

    private CognitoIdentityProviderClient cognitoIdentityProviderClient;

    public CognitoUserService(String region) {
        this.cognitoIdentityProviderClient = CognitoIdentityProviderClient
                .builder()
                .region(Region.of(region))
                .build();
    }

    public CognitoUserService(CognitoIdentityProviderClient cognitoIdentityProviderClient) {

        this.cognitoIdentityProviderClient = cognitoIdentityProviderClient;
    }

    public JsonObject createUser(JsonObject user, String appClientId, String appClientSecret) {
        JsonObject createUserResult = new JsonObject();
        String email = user.get("email").getAsString();

        List<AttributeType> attributeTypeList = createAttributesType(user, email);

        String calculateSecretHash = calculateSecretHash(appClientId, appClientSecret, email);

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(email)
                .password(user.get("password").getAsString())
                .userAttributes(attributeTypeList)
                .clientId(appClientId)
                .secretHash(calculateSecretHash)
                .build();


        SignUpResponse signUpResponse = this.cognitoIdentityProviderClient.signUp(signUpRequest);
        createUserResult.addProperty("isSuccessful",signUpResponse.sdkHttpResponse().isSuccessful());
        createUserResult.addProperty("statusCode",signUpResponse.sdkHttpResponse().statusCode());
        createUserResult.addProperty("cognitoUserId",signUpResponse.userSub());
        createUserResult.addProperty("isConfirmed",signUpResponse.userConfirmed());

        return createUserResult;
    }

    private static List<AttributeType> createAttributesType(JsonObject user, String email) {
        AttributeType userId = AttributeType.builder()
                .name("custom:userId")
                .value(UUID.randomUUID().toString())
                .build();

        AttributeType nameAttributeType = AttributeType.builder()
                .name("nameAttributeType")
                .value(user.get("firstName").getAsString().concat(" ").concat(user.get("lastName").getAsString()))
                .build();
        AttributeType emailAttributeType = AttributeType.builder()
                .name("email")
                .value(email)
                .build();

        List<AttributeType> attributeTypeList = new ArrayList<>();
        attributeTypeList.add(userId);
        attributeTypeList.add(nameAttributeType);
        attributeTypeList.add(emailAttributeType);
        return attributeTypeList;
    }

    public static String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating ");
        }
    }
}
