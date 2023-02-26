package br.com.likwi.photoapp;

import br.com.likwi.photoapp.service.CognitoUserService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class CreateUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public static final String APPLICATION_JSON = "application/json";
    public static final String USER_ID = "userId";

    private final String MY_COGNITO_USER_POOL_ID = System.getenv("MY_COGNITO_USER_POOL_ID");
    private final String MY_COGNITO_CLIENT_APP_SECRET = System.getenv("MY_COGNITO_CLIENT_APP_SECRET");
//    private final String MY_COGNITO_USER_POOL_ID = DecrtptKey.decryptKey("MY_COGNITO_USER_POOL_ID");
//    private final String MY_COGNITO_CLIENT_APP_SECRET = DecrtptKey.decryptKey("MY_COGNITO_CLIENT_APP_SECRET");

    private CognitoUserService cognitoUserService;

    public CreateUserHandler() {
        this.cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Gson gson = new Gson();
        LambdaLogger logger = context.getLogger();
        Gson responseBody = new Gson();
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", APPLICATION_JSON);
        responseHeaders.put("X-Custom-Header", APPLICATION_JSON);
        responseHeaders.put("Lambda-Version", context.getFunctionVersion());

        logger.log("Handling CreateUser version: " + context.getFunctionVersion());

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(responseHeaders)
                .withStatusCode(500);
        String inputBody = input.getBody();

        logger.log("Original JSON: " + inputBody);
        JsonObject userDetails = JsonParser.parseString(inputBody).getAsJsonObject();

        try{

            JsonObject createUserResult = this.cognitoUserService.createUser(userDetails,
                    this.MY_COGNITO_USER_POOL_ID,
                    this.MY_COGNITO_CLIENT_APP_SECRET);

            response.withStatusCode(200);
            response.withBody(responseBody.toJson(createUserResult, JsonObject.class));

        }catch (AwsServiceException e){
            logger.log(e.awsErrorDetails().errorMessage());
            response.withBody(responseBody.toJson(e, AwsServiceException.class));
        }
        catch (Exception e){
            logger.log(e.getLocalizedMessage());
            response.withBody(responseBody.toJson(e, Exception.class));
        }

        return response;
    }
}