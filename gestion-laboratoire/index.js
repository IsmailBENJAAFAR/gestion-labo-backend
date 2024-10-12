import express from "express";

const server = express();
const port = 3030;

server.get("/", (req, res) => {
  res.send("hello from gestion access part1");
});

server.listen(port, () => {
  console.log(`Example app listening on port ${port}`);
});
