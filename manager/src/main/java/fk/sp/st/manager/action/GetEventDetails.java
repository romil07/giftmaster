package fk.sp.st.manager.action;


import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.util.List;

import javax.inject.Inject;

import fk.sp.sa.util.QueryStore;
import lombok.Data;

public class GetEventDetails {

  private JdbcTemplate jdbcTemplate;
  private String query;

  @Inject
  public GetEventDetails(JdbcTemplate jdbcTemplate, QueryStore queryStore) {
    this.jdbcTemplate = jdbcTemplate;
    query = queryStore.get("fk.sp.sa", "get_event_details");

  }

  public EventDetails getEventDetails(String userId) {

    List<EventDetails> eventDetails = jdbcTemplate.query(query, new Object[]{userId}, (rs, n) -> {
      EventDetails details = new EventDetails();
      details.setEventDate(rs.getDate("date"));
      details.setUserId(rs.getString("user_id"));
      details.setEventName(rs.getString("event"));
      details.setEmail(rs.getString("email"));
      details.setRelationType(rs.getString("relation_type"));
      details.setUserName(rs.getString("name"));

      return details;
    });

    return eventDetails.get(0);
  }

  @Data
  public static class EventDetails {

    private String userId;
    private String eventName;
    private Date eventDate;
    private String email;
    private String relationType;
    private String userName;
  }
}
