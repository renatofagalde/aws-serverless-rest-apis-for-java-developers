package br.com.likwi.photoapp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handler for requests to Lambda function.
 */
public class PostHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        String inputBody = input.getBody();
        Gson gson = new Gson();
        Map<String, String> usersDeteils = gson.fromJson(inputBody, Map.class);
        usersDeteils.put("userId", UUID.randomUUID().toString());

        //TODO process user details

        HashMap responseMap = new HashMap();
        responseMap.put("firstName", usersDeteils.get("firtName"));
        responseMap.put("lastName", usersDeteils.get("lastName"));
        responseMap.put("userId", usersDeteils.get("userId"));

        HashMap<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");

        return response
                .withHeaders(responseHeaders)
                .withStatusCode(200)
                .withBody(gson.toJson(usersDeteils, Map.class));
    }
}


}
