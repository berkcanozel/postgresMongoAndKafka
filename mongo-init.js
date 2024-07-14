// mongo-init.js
db.createUser({
  user: "root",
  pwd: "password",
  roles: [{ role: "readWrite", db: "graylog" }]
});
