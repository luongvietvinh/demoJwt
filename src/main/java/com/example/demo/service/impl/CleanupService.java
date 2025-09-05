package com.example.demo.service.impl;


import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class CleanupService implements RequestHandler<Object, String> {

  @Override
  public String handleRequest(Object input, Context context) {
      String url = "http://localhost:8081/batch/cleanup"; // Spring Boot API cleanup

      try (CloseableHttpClient client = HttpClients.createDefault()) {
          HttpDelete request = new HttpDelete(url);
          var response = client.execute(request);

          String responseBody = EntityUtils.toString(response.getEntity());
          context.getLogger().log("Cleanup API response: " + responseBody);

          return "Status: " + response.getStatusLine().getStatusCode() + " - " + responseBody;
      } catch (Exception e) {
          context.getLogger().log("Error calling cleanup API: " + e.getMessage());
          return "Error: " + e.getMessage();
      }
  }

}
