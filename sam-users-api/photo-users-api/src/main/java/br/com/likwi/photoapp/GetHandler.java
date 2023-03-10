package br.com.likwi.photoapp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class GetHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public static final String APPLICATION_JSON = "application/json";
    public static final String USER_ID = "userId";

    private static final String MY_COGNITO_USER_POOL_ID = System.getenv("MY_COGNITO_USER_POOL_ID");
    private static final String MY_COGNITO_CLIENT_APP_SECRET = System.getenv("MY_COGNITO_CLIENT_APP_SECRET");

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Gson gson = new Gson();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", APPLICATION_JSON);
        headers.put("X-Custom-Header", APPLICATION_JSON);
        LambdaLogger logger = context.getLogger();

        logger.log("Handling GetUser version: "+context.getFunctionVersion());
        logger.log("query parameters: "+gson.toJson(input.getQueryStringParameters(), Map.class));
        logger.log("MY_COGNITO_USER_POOL_ID "+ MY_COGNITO_USER_POOL_ID);
        logger.log("MY_COGNITO_CLIENT_APP_SECRET "+ MY_COGNITO_CLIENT_APP_SECRET);


        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);


        var responseMap = new HashMap();
        responseMap.put("MY_COGNITO_USER_POOL_ID", MY_COGNITO_USER_POOL_ID);
        responseMap.put("MY_COGNITO_CLIENT_APP_SECRET", MY_COGNITO_CLIENT_APP_SECRET);



        HashMap<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", APPLICATION_JSON);
        responseHeaders.put("Lambda-Version", context.getFunctionVersion());

        return response
                .withHeaders(responseHeaders)
                .withStatusCode(200)
                .withBody(gson.toJson(input.getQueryStringParameters(), Map.class));
    }
}