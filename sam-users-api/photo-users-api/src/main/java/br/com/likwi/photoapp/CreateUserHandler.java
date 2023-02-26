package br.com.likwi.photoapp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handler for requests to Lambda function.
 */
public class CreateUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public static final String APPLICATION_JSON = "application/json";
    public static final String USER_ID = "userId";

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", APPLICATION_JSON);
        headers.put("X-Custom-Header", APPLICATION_JSON);
        LambdaLogger logger = context.getLogger();

        logger.log("Handling CreateUser version: "+context.getFunctionVersion());

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        String inputBody = input.getBody();
        Gson gson = new Gson();

        logger.log("Original JSON: "+ inputBody);
        JsonObject userDetails = JsonParser.parseString(inputBody).getAsJsonObject();




        var responseMap = new HashMap();
        responseMap.put(USER_ID, usersDeteils.get(USER_ID));


        HashMap<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", APPLICATION_JSON);
        responseHeaders.put("Lambda-Version", context.getFunctionVersion());

        return response
                .withHeaders(responseHeaders)
                .withStatusCode(200)
                .withBody(gson.toJson(usersDeteils, Map.class));
    }
}