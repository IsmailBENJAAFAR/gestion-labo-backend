import express, { Express, Request, Response } from "express";
import prisma from "../utils/prisma/prisma";
import getByteArray from "../utils/images/fs_image";

const server: Express = express();
const port = 3900;
const rootURL = "/laboratoire";

server.use(express.json());

server.get(`${rootURL}/`, async (req: Request, res: Response) => {
  const labo = await prisma.laboratoire.findMany();
  res.send(labo);
});

server.post(`${rootURL}/create`, async (req: Request, res: Response) => {
  const data = {
    nom: req.body.nom,
    logo: getByteArray(req.body.logo).toJSON().data,
    nrc: req.body.nrc,
    active: req.body.active,
  };
  await prisma.laboratoire
    .create({
      data: data,
    })
    .then(() => {
      res.status(201);
      res.send("OK");
    })
    .catch((err) => {
      console.log(err);
      res.status(400);
      res.send("Bad request");
    });
});

server.get(`${rootURL}/:id`, async (req: Request, res: Response) => {
  const labo = await prisma.laboratoire
    .findUnique({
      where: {
        id: Number.parseInt(req.params.id),
      },
    })
    .then(() => {
      res.status(200);
      res.send(labo);
    })
    .catch((err) => {
      console.log(err);
      res.status(404);
      res.send("Not found");
    });
});

server.delete(`${rootURL}/:id`, async (req: Request, res: Response) => {
  const labo = await prisma.laboratoire
    .delete({
      where: {
        id: Number.parseInt(req.params.id),
      },
    })
    .then(() => {
      res.status(204);
      res.send(labo);
    })
    .catch((err) => {
      console.log(err);
      res.status(400);
      res.send("Bad request");
    });
});

server.post(`${rootURL}/:id`, async (req: Request, res: Response) => {
  const labo = await prisma.laboratoire
    .update({
      where: {
        id: Number.parseInt(req.params.id),
      },
      data: {
        nom: req.body.nom,
        nrc: req.body.nrc,
        active: req.body.active,
      },
    })
    .then(() => {
      res.status(200);
      res.send(labo);
    })
    .catch((err) => {
      console.log(err);
      res.status(400);
      res.send("Bad request");
    });
});

server.listen(port, () => {
  console.log(`[server]: Server is running at http://localhost:${port}`);
});
