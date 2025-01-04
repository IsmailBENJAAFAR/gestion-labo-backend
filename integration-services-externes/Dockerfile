FROM node:20.5.0-alpine

WORKDIR /app

COPY ./dist .
COPY ./env.sh ./env.sh

RUN `./env.sh` && rm ./env.sh

ENTRYPOINT [ "node", "src/main" ]