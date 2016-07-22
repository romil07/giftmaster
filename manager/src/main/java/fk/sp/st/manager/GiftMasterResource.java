package fk.sp.st.manager;


import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import fk.sp.st.manager.action.GetEventDetails;
import fk.sp.st.manager.action.GetRecommendedProductForEmailId;
import fk.sp.st.manager.clients.PriceFromProdIdClient;
import fk.sp.st.manager.clients.UserServiceClient;
import fk.sp.st.manager.model.ListingInfo;
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
    private final String user_occasion_vertical_query =
            "select vertical from user_vertical where user_type = ? and vertical in (select vertical from occasion_vertical where occasion = ?)";
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

    @GET
    @Timed
    @Path("/wishlist")
    public List<String> getWishList(@QueryParam("user_id") String userId) {
        Map<String, List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("afjadgh");
        list.add("afferwgh");
        list.add("afjafsdgs");
        list.add("agwerge");
        list.add("agwegeg");
        map.put("jafsa", list);

        return map.get(userId);
    }

    @GET
    @Timed
    @Path("/vertical")
    public List<String> userOccasionVertical(@QueryParam("user_type") String user_type,
                                             @QueryParam("occasion_type") String occasion){

        List<String> rows = jdbcTemplate
                .query(String.format(user_occasion_vertical_query, "fsn_details"), new Object[]{user_type, occasion}, this::readVertical);

        return rows;
    }

    private String readVertical(ResultSet rs, int n) throws SQLException {
        String vertical;
        vertical = rs.getString("vertical");
        return vertical;
    }
}
