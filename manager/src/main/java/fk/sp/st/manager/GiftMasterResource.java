package fk.sp.st.manager;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import fk.sp.st.manager.action.GetRecommendedProductForEmailId;
import fk.sp.st.manager.clients.PriceFromProdIdClient;
import fk.sp.st.manager.model.ListingInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Path("/gift-master")
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class GiftMasterResource {

    private PriceFromProdIdClient priceFromProdIdClient;
    private final GetRecommendedProductForEmailId getRecommendedProductForEmailId;
    private JdbcTemplate jdbcTemplate;
    private final String query = "select fsn from fsn_details where age = ? and sex = ? and price <= ? and occasion = ?";


    @Inject
    public GiftMasterResource(PriceFromProdIdClient priceFromProdIdClient, GetRecommendedProductForEmailId getRecommendedProductForEmailId,
                              JdbcTemplate jdbcTemplate) {
        this.priceFromProdIdClient = priceFromProdIdClient;
        this.getRecommendedProductForEmailId = getRecommendedProductForEmailId;
        this.jdbcTemplate = jdbcTemplate;
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
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) ((LinkedHashMap) ((LinkedHashMap) ((LinkedHashMap) response).get("response")).get("RESPONSES")).get("Pricing Response");
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
  @Path("/getRecommendedProducts")
  public Response getRecommended(String FSN) throws Exception {

    log.info("Response Test");
    getRecommendedProductForEmailId.invoke("");
    return Response.ok().build();
  }

    public String getAccountIdFromEmail(String email) {

        Map<String, String> map = new HashMap<>();
        map.put("romil.goyal@flipkart.com", "AC123456789");
        map.put("rohan.ghosh@flipkart.com", "AC127642369");
        map.put("ayush.kumar@flipkart.com", "AC124257959");
        map.put("venkatesha.p@flipkart.com", "AC135897589");
        map.put("divakar.c@flipkart.com", "AC1465983479");
        map.put("vidhisha.b@flipkart.com", "AC5894275897");
        map.put("rohit.kochar@flipkart.com", "AC2375723454");
        map.put("amitsingh.c@flipkart.com", "AC1258734875");

        return map.get(email);
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
