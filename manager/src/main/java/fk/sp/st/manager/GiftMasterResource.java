package fk.sp.st.manager;

import com.google.inject.persist.Transactional;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path ("/gift-master")
@Transactional
public class GiftMasterResource {


  public GiftMasterResource() {

  }

  @GET
  @Timed
  @ExceptionMetered
  public Response execute() {

    log.info(
        "Response Test");

    return Response.ok().build();
  }


}
