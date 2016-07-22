package fk.sp.st.manager;


import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import fk.sp.st.manager.action.GetEventDetails;
import fk.sp.st.manager.action.GetRecommendedProductForEmailId;
import fk.sp.st.manager.clients.GetImagePathFromSid;
import fk.sp.st.manager.clients.PriceFromProdIdClient;
import fk.sp.st.manager.clients.RecoClient;
import fk.sp.st.manager.clients.UserServiceClient;
import fk.sp.st.manager.model.ListingInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
  private final RecoClient recoClient;
  private final GetImagePathFromSid getImagePathFromSid;
  private final String user_occasion_vertical_query =
      "select vertical from user_vertical where user_type = ? and vertical in (select vertical from occasion_vertical where occasion = ?)";



  @Inject
  public GiftMasterResource(PriceFromProdIdClient priceFromProdIdClient,
                            GetRecommendedProductForEmailId getRecommendedProductForEmailId,
                            JdbcTemplate jdbcTemplate, GetEventDetails getEventDetails,
                            Provider<UserServiceClient> userServiceClient,
                            RecoClient recoClient, GetImagePathFromSid getImagePathFromSid) {
    this.priceFromProdIdClient = priceFromProdIdClient;
    this.getRecommendedProductForEmailId = getRecommendedProductForEmailId;
    this.jdbcTemplate = jdbcTemplate;
    this.getEventDetails = getEventDetails;
    this.userServiceClient = userServiceClient;
    this.recoClient = recoClient;
    this.getImagePathFromSid = getImagePathFromSid;
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
  @Timed
  @ExceptionMetered
  @Path("/getSimilarProducts/{FSN}")
  public Object getSimilarProducts(@PathParam("FSN") String fsn) throws Exception {

    log.info("Similar Products for {}", fsn);
    return recoClient.run(fsn,"same");
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

  @GET
  @Path("/vertical_image")
  @Timed
  @ExceptionMetered
  public GetImagePathFromSid.StoreDetailsResponse getImage(@QueryParam("sid") String storeId) {
//    String s = "http://10.33.118.143:9000/sourceProductRecoP2P/?&source=ALL_p2p&pid=SKIEAU5F9KZEVMDF&cross=true";
    String y = "http://sherlock-reco-client.nm.flipkart.com:25280/sherlock/stores/6bo/ffn/adt/select";
    return getImagePathFromSid.run(storeId);
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

    @GET
    @Timed
    @Path("/wishlist")
    public List<FSNDetails> getWishList(@QueryParam("user_id") String userId) {
        Map<String, List<FSNDetails>> map = new HashMap<>();
        List<FSNDetails> list = new ArrayList<>();
        list.add(new FSNDetails("SHTEHHP68F6XZF5E", "http://img6a.flixcart.com/image/watch/p/d/5/1016-b-skmei-400x400-imaegfzcpycgzqnp.jpeg"));
        list.add(new FSNDetails("WATE66XQVBDHCUTE", "http://img6a.flixcart.com/image/shirt/j/n/d/hlsh008852-dark-denim-highlander-s-400x400-imaejy6gbjmvesfz.jpeg"));
        map.put("ACC13592967042198203", list);

        List<FSNDetails> list2 = new ArrayList<>();
        list2.add(new FSNDetails("itme9jff5gzdzvrv", "http://img6a.flixcart.com/image/fabric/e/x/j/jamavimal-202-jellyapparel-400x400-imaeduw4hky8ykc4.jpeg"));
        list2.add(new FSNDetails("itme79dzfbyvfayw", "http://img5a.flixcart.com/image/watch/u/c/9/ag-005-agile-400x400-imae78shhyhe7zgx.jpeg"));
        map.put("AC1BO1DVX5LI20XYCD28FP1JPH74EMSC", list2);

        List<FSNDetails> list3 = new ArrayList<>();
        list3.add(new FSNDetails("itme9jff5gzdzvrv", "http://img6a.flixcart.com/image/fabric/e/x/j/jamavimal-202-jellyapparel-400x400-imaeduw4hky8ykc4.jpeg"));
        list3.add(new FSNDetails("WATE66XQVBDHCUTE", "http://img5a.flixcart.com/image/watch/u/c/9/ag-005-agile-400x400-imae78shhyhe7zgx.jpeg"));
        map.put("ACVYSILDYXZ857J4CMM1LZZL7JHZFDC8", list3);

        return map.get(userId);
    }

    @GET
    @Timed
    @Path("/vertical")
    public List<String> userOccasionVertical(@QueryParam("user_type") String user_type,
                                             @QueryParam("occasion_type") String occasion) {

        List<String> rows = jdbcTemplate
                .query(String.format(user_occasion_vertical_query, "fsn_details"), new Object[]{user_type, occasion}, this::readVertical);

        return rows;
    }

    private String readVertical(ResultSet rs, int n) throws SQLException {
        String vertical;
        vertical = rs.getString("vertical");
        return vertical;
    }

    @Data
    @AllArgsConstructor
    private static class FSNDetails {
        private String fsn;
        private String image_url;
    }
}
