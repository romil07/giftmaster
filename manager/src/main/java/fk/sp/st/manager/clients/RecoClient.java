package fk.sp.st.manager.clients;

import com.google.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import io.dropwizard.jackson.Jackson;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by rohan.ghosh on 22/07/16.
 */
@Slf4j
public class RecoClient {

  private static final String
      PATH =
      "/sourceProductRecoP2P/";
  private final Client client;
  private static String URI = "http://10.33.118.143:9000/";

  @Inject
  public RecoClient(Client client) {
    this.client = client;
  }

  public List<Object> run(String FSN, String type) {
    java.net.URI uri = UriBuilder.fromUri(URI).queryParam("source", "ALL_p2p").queryParam("pid", FSN)
        .queryParam("cross", true).path(PATH).build();

    ClientResponse response = null;
    try {
      response =
          client.resource(uri).accept(MediaType.APPLICATION_JSON_TYPE)
              .type(MediaType.APPLICATION_JSON_TYPE)
              .get(ClientResponse.class);
    } catch (Exception e) {
      return new ArrayList<>();
    }

    String s = response.getEntity(String.class);

    ObjectMapper objectMapper = Jackson.newObjectMapper();
    List<Map<String, Object>> o = null;
    try {
      o = (List<Map<String, Object>>) objectMapper.readValue(s, Object.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    List<Object> res = (List<Object>) o.get(1).get(type);

    if (res.size() > 10)
      return res.subList(0, 10);
    else
      return res;
  }
}
