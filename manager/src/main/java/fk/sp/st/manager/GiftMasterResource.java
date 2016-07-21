package fk.sp.st.manager;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import fk.sp.st.manager.clients.PriceFromProdIdClient;
import fk.sp.st.manager.model.ListingInfo;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Path("/gift-master")
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class GiftMasterResource {

    private PriceFromProdIdClient client;

    @Inject
    public GiftMasterResource(PriceFromProdIdClient client) {
        this.client = client;
    }

    @GET
    @Timed
    @Path("/getBestPrice")
    public Object execute(@QueryParam("product_id") String productId) {
        client.setProductId(productId);
        Object response = client.run();
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


}
