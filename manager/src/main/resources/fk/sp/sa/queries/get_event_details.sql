SELECT
  e.user_id,
  e.date,
  e.event,
  u.email,
  u.relation_type,
  e.name
FROM user as u JOIN event as e ON u.friend_id = e.user_id
WHERE datediff(e.date, CURDATE()) <= 7 AND u.user_id = ?;