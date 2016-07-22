package fk.sp.st.manager;


import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fk.sp.st.manager.action.GetEventDetails;
import fk.sp.st.manager.action.GetRecommendedProductForEmailId;
import fk.sp.st.manager.clients.PriceFromProdIdClient;
import fk.sp.st.manager.clients.UserServiceClient;
import fk.sp.st.manager.model.ListingInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/gift-master")
public class GiftMasterResource {

  private PriceFromProdIdClient priceFromProdIdClient;
  private final GetRecommendedProductForEmailId getRecommendedProductForEmailId;
  private JdbcTemplate jdbcTemplate;
  private final String
      query =
      "select fsn from fsn_details where age = ? and sex = ? and price <= ? and occasion = ?";
  private GetEventDetails getEventDetails;
  private Provider<UserServiceClient> userServiceClient;


  @Inject
  public GiftMasterResource(PriceFromProdIdClient priceFromProdIdClient,
                            GetRecommendedProductForEmailId getRecommendedProductForEmailId,
                            JdbcTemplate jdbcTemplate, GetEventDetails getEventDetails,
                            Provider<UserServiceClient> userServiceClient) {
    this.priceFromProdIdClient = priceFromProdIdClient;
    this.getRecommendedProductForEmailId = getRecommendedProductForEmailId;
    this.jdbcTemplate = jdbcTemplate;
    this.getEventDetails = getEventDetails;
    this.userServiceClient = userServiceClient;
  }


  @GET
  @Timed
  @Path("/getBestPrice")
  public ListingInfo execute(@QueryParam("product_id") String productId) {
    this.getRecommendedFSNs(25, "Male", 1000, "Birthday");
    priceFromProdIdClient.setProductId(productId);
    Object response = priceFromProdIdClient.run();
    Integer price = new Integer(Integer.MAX_VALUE);
    String listing_id = "";
    LinkedHashMap<String, Object>
        map =
        (LinkedHashMap<String, Object>) ((LinkedHashMap) ((LinkedHashMap) ((LinkedHashMap) response)
            .get("response")).get("RESPONSES")).get("Pricing Response");
    for (Map.Entry entry : map.entrySet()) {
      if (price > (Integer) ((LinkedHashMap) entry.getValue()).get("fsp")) {
        price = (Integer) ((LinkedHashMap) entry.getValue()).get("fsp");
        listing_id = entry.getKey().toString();
      }
    }

    ListingInfo listingInfo = new ListingInfo();
    listingInfo.setListingId(listing_id);
    listingInfo.setPrice(price);
    return listingInfo;
  }

  @GET
  @Timed
  @ExceptionMetered
  @Path("/getRecommendedProducts/{accID}")
  public Object getRecommended(@PathParam("accID") String accId) throws Exception {

    log.info("Recommended Products for {}", accId);
    return getRecommendedProductForEmailId.invoke(accId);
  }


  @GET
  @Path("/event/details/{user_id}")
  @Timed
  @ExceptionMetered
  public GetEventDetails.EventDetails getEventDetails(@PathParam("user_id") String userId) {

    log.info(
        "getEventDetails for {}", userId);

    return getEventDetails.getEventDetails(userId);
  }

  @GET
  @Path("/user/details")
  @Timed
  @ExceptionMetered
  public UserServiceClient.UserInfo getAccountIdFromEmail(@QueryParam("email") String email) {

    log.info(
        "get User Details for {}", email);
    return userServiceClient.get().withEmailId(email).run();


  }

  private List<String> getRecommendedFSNs(int age, String sex, int budget, String occasion) {
    List<String> rows = jdbcTemplate
        .query(String.format(query, "fsn_details"), new Object[]{age, sex, budget, occasion},
               this::readRow);
    return rows;
  }

  private String readRow(ResultSet rs, int n) throws SQLException {
    String row;
    row = rs.getString("fsn");
    return row;
  }

}
