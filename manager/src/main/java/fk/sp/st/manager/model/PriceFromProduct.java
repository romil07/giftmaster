package fk.sp.st.manager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.json.JsonSnakeCase;

import java.util.List;
import java.util.Map;

/**
 * Created by romil.goyal on 21/07/16.
 */
@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceFromProduct {

    private Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, String>>>>>>>> listingInfo;

    private static class ListingInfo {

        @JsonProperty
        private String mrp;
        @JsonProperty
        private String fsp;
        @JsonProperty
        private String listingId;
    }

}
