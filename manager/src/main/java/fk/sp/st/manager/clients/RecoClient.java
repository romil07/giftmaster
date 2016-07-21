package fk.sp.st.manager.clients;

import com.google.inject.Inject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import fk.sp.common.extensions.hystrix.JerseyClientBase;
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

  public List<String> run(String accountId) {

    URI uri = UriBuilder.fromUri(URI).path(PATH).build();

    client.setReadTimeout(0);
    String payLoad = makePayload(accountId);
    ClientResponse
        response =
        client.resource(uri).accept(MediaType.APPLICATION_JSON_TYPE)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .post(ClientResponse.class, payLoad);
    checkResponse(response);

    Map<String, Object> a = (Map<String, Object>)response.getEntity(Object.class);
    Map<String, Object> b = (Map<String, Object>) a.get("view");
    List<Map<String, Object>> z = (List<Map<String, Object>>) b.get("STORES");

    List<String> res = z.stream().map(o -> o.get("storeId").toString()).collect(Collectors.toList());
    return res;
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
