package fk.sp.st.manager.action;

import com.google.inject.Inject;

import java.util.List;

import fk.sp.st.manager.clients.AccountIdFromEmaidIdClient;
import fk.sp.st.manager.clients.RecoClient;
import fk.sp.st.manager.clients.RecoClient.RecoResponse;

/**
 * Created by rohan.ghosh on 21/07/16.
 */
public class GetRecommendedProductForEmailId {

  private final GetEventDetails getEventDetails;
  private final AccountIdFromEmaidIdClient accountIdFromEmaidIdClient;
  private final RecoClient recoClient;

  @Inject
  public GetRecommendedProductForEmailId(GetEventDetails getEventDetails,
                                         AccountIdFromEmaidIdClient accountIdFromEmaidIdClient,
                                         RecoClient recoClient) {
    this.getEventDetails = getEventDetails;
    this.accountIdFromEmaidIdClient = accountIdFromEmaidIdClient;
    this.recoClient = recoClient;
  }

  public RecoResponse invoke(String emailId) throws Exception {

    String accountId = getAccountId(emailId);
    List<String> stores = recoClient.run(accountId);



    return null;
  }

  private String getAccountId(String emailId) {
    return "ACC14003424424548275";
  }
}
