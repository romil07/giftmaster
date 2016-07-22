package fk.sp.st.manager.action;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import fk.sp.st.manager.clients.BoughtClient;
import fk.sp.st.manager.clients.RecoClient;

/**
 * Created by rohan.ghosh on 21/07/16.
 */
public class GetRecommendedProductForEmailId {

  private final BoughtClient boughtClient;
  private final RecoClient recoClient;

  @Inject
  public GetRecommendedProductForEmailId(BoughtClient boughtClient,
                                         RecoClient recoClient) {
    this.boughtClient = boughtClient;
    this.recoClient = recoClient;
  }

  public Object invoke(String accountId) throws Exception {
    List<String> boughtProductId = boughtClient.run(accountId);

    Object res = boughtProductId.stream().map(o -> recoClient.run(o)).reduce(new ArrayList<>(), (a, b) -> {
      a.addAll(b);
      return a;
    });

    return res;
  }
}
