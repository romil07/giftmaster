package fk.sp.st.manager.clients;

import com.sun.jersey.api.client.Client;

import com.sun.jersey.api.client.ClientResponse;
import fk.sp.common.extensions.hystrix.JerseyClientBase;
import fk.sp.st.manager.model.PriceFromProduct;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by rohan.ghosh on 21/07/16.
 */
@Data
@NoArgsConstructor
public class PriceFromProdIdClient implements JerseyClientBase {
    private static final String
            PATH =
            "/debug/whatIsWrongWithMe";
    private Client client;
    private String productId = "PWBE6ZZHSVBRHGTF";

    @Inject
    public PriceFromProdIdClient(Client client) {
        this.client = client;
    }

    public Object run() {

        URI uri = UriBuilder.fromUri("http://10.33.50.12:8190").path(PATH)
                .queryParam("ids", productId).
                        queryParam("showResponse", "true").build();

         ClientResponse response =
                client.resource(uri).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        //checkResponse(response);

        return response.getEntity(Object.class);
    }
}
