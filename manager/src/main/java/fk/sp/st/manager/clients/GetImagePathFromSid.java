package fk.sp.st.manager.clients;

import com.google.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import io.dropwizard.jackson.Jackson;
import lombok.Data;

/**
 * Created by rohan.ghosh on 22/07/16.
 */
public class GetImagePathFromSid {

  private static final String
      PATH =
      "/sourceProductRecoP2P/";
  private final Client client;
  private static String URI = "http://10.33.118.143:9000/";
  private static String URI_SID = "http://sherlock-reco-client.nm.flipkart.com:25280/";
  private static final String
      PATH_SID =
      "/sherlock/stores/";

  @Inject
  public GetImagePathFromSid(Client client) {
    this.client = client;
  }

  public StoreDetailsResponse run(String sid) {
    java.net.URI uriSid = UriBuilder.fromUri(URI_SID).path(PATH_SID + sid+ "/select").build();

    client.setReadTimeout(600000000);
    ClientResponse responseSid =
        client.resource(uriSid).accept(MediaType.APPLICATION_JSON_TYPE)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .get(ClientResponse.class);

    String resSid = responseSid.getEntity(String.class);
    ObjectMapper objectMapper = Jackson.newObjectMapper();

    Map<String, Object> stringObjectMap = null;
    try {
      stringObjectMap = (Map<String, Object>)objectMapper.readValue(resSid, Object.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    StoreDetailsResponse storeDetailsResponse = new StoreDetailsResponse();

    Map<String, Object> m = (Map<String, Object>)stringObjectMap.get("RESPONSE");
    Map<String, Object> store = (Map<String, Object>)m.get("store");
    Map<String, Object> products = (Map<String, Object>)m.get("products");

    String title = (String) store.get("title");
    List<String> ids = (List<String>)products.get("ids");
    String FSN = ids.get(0);

    java.net.URI uri = UriBuilder.fromUri(URI).queryParam("source", "ALL_p2p").queryParam("pid", FSN)
        .queryParam("cross", true).path(PATH).build();

    ClientResponse response =
        client.resource(uri).accept(MediaType.APPLICATION_JSON_TYPE)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .get(ClientResponse.class);


    String s = response.getEntity(String.class);

    List<Map<String, Object>> o = null;
    try {
      o = (List<Map<String, Object>>) objectMapper.readValue(s, Object.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    String res = (String) o.get(0).get("image");
    storeDetailsResponse.setImage(res);
    storeDetailsResponse.setTitle(title);

    return storeDetailsResponse;
  }

  @Data
  public static class StoreDetailsResponse {
    String image;
    String title;
  }
}
