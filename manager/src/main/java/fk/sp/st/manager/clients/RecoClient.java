package fk.sp.st.manager.clients;

import com.google.inject.Inject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import fk.sp.common.extensions.hystrix.JerseyClientBase;
import io.dropwizard.jackson.Jackson;
import lombok.Data;

/**
 * Created by rohan.ghosh on 21/07/16.
 */
public class RecoClient implements JerseyClientBase {

  private static final String
      PATH =
      "/roulette/v1/useraffinity";
  private final Client client;
  private static String URI = "http://10.33.249.193:8080";

  @Inject
  public RecoClient(Client client) {
    this.client = client;
  }

  public List<String> run(String accountId) throws IOException {

    URI uri = UriBuilder.fromUri(URI).path(PATH).build();

    client.setReadTimeout(0);
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
    Stream<String> targetStream = StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(r, Spliterator.ORDERED),
        false);

    List<String> abc = targetStream.limit(10).map(o -> {
      JsonNode temp = null;

      try {
        temp = objectMapper.readValue(o, JsonNode.class);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return temp.path("eventDetails").path("productEvents").asText();
    }).collect(Collectors.toList());

    List<String> prod = abc.stream().map(o -> {
      JsonNode t = null;
      try {
        t = objectMapper.readValue(o, JsonNode.class);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return t.path("productId").elements().next().path("productId").asText();

    }).collect(Collectors.toList());

    return prod;
  }

  private String makePayload(String accountId) {
    return "{\"viewName\":\"historical\",\"userContext\":{\"accountId\":\"" +
        accountId + "\",\"deviceId\":\"\"}}";
  }

  @Data
  public static class RecoResponse {

    List<C> STORES;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class C {
      String storeId;
    }
  }
}
