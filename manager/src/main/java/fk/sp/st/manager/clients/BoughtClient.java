package fk.sp.st.manager.clients;

import com.google.inject.Inject;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import fk.sp.common.extensions.hystrix.JerseyClientBase;
import io.dropwizard.jackson.Jackson;

/**
 * Created by rohan.ghosh on 21/07/16.
 */
public class BoughtClient implements JerseyClientBase {

  private static final String
      PATH =
      "/roulette/v1/useraffinity";
  private final Client client;
  private static String URI = "http://10.33.249.193:8080";

  @Inject
  public BoughtClient(Client client) {
    this.client = client;
  }

  public List<String> run(String accountId) throws IOException {

    URI uri = UriBuilder.fromUri(URI).path(PATH).build();

//    client.setReadTimeout(0);
    String payLoad = makePayload(accountId);
    ClientResponse
        response =
        client.resource(uri).accept(MediaType.APPLICATION_JSON_TYPE)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .post(ClientResponse.class, payLoad);
    checkResponse(response);

    ObjectMapper objectMapper = Jackson.newObjectMapper();
    JsonNode suv = objectMapper.readValue((String) response.getEntity(String.class), JsonNode.class);
    Iterator r = suv.path("view").path("ED").elements();
    List<String> productEventsList = Lists.newArrayList();
    r.forEachRemaining(a -> {
      JsonNode temp = null;
      try {
        temp = objectMapper.readValue(a.toString(), JsonNode.class);
      } catch (IOException e) {
        e.printStackTrace();
      }
      productEventsList.add(temp.path("eventDetails").path("productEvents").elements().next().path("productId").asText());

    });

    return productEventsList.stream().distinct().filter(a -> !a.contains(":")).collect(Collectors.toList()).subList(0, 9);
  }

  private String makePayload(String accountId) {
    return "{\"viewName\":\"historical\",\"userContext\":{\"accountId\":\"" +
        accountId + "\",\"deviceId\":\"\"}}";
  }

}
