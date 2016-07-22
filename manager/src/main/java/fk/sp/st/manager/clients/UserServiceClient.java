package fk.sp.st.manager.clients;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import java.net.URI;
import java.util.HashMap;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import fk.sp.common.extensions.hystrix.JerseyClientBase;
import lombok.Data;

/**
 * Created by ayush.kumar on 22/07/16.
 */
public class UserServiceClient implements JerseyClientBase {

  private static final String
      PATH =
      "/userservice/v0.1/customer/email";
  private Client client;
  private String emailId;


  @Inject
  public UserServiceClient(Client client) {
    this.client = client;
  }

  public UserInfo run() {

    URI uri = UriBuilder.fromUri("http://10.47.3.227:25151").path(PATH)
        .queryParam("id", emailId).
            queryParam("showResponse", "true").build();

    ClientResponse response =
        client.resource(uri).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

    //checkResponse(response);
    HashMap<String, Object> res = response.getEntity(HashMap.class);
    UserInfo userInfo = new UserInfo();
    userInfo.setDateOfBirth(res.get("date_of_birth").toString());
    userInfo.setEmail(res.get("primary_email").toString());
    userInfo.setFirstName(res.get("first_name").toString());
    userInfo.setLastName(res.get("last_name").toString());
    userInfo.setPhoneNo(res.get("primary_phone").toString());
    userInfo.setUserId(res.get("primary_account_id").toString());
    userInfo.setGender(res.get("gender").toString());

    return userInfo;
  }

  public UserServiceClient withEmailId(String emailId) {
    this.emailId = emailId;
    return this;
  }

  @Data
  public static class UserInfo {

    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String phoneNo;
  }
}
