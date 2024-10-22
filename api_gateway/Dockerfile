FROM debian:bullseye-20241016

RUN apt-get update -y && apt-get upgrade -y

RUN apt-get install -y nginx openssl curl

RUN mkdir -p /etc/nginx/ssl /var/run/nginx

RUN openssl req -x509 \
            -sha256 -days 356 \
            -nodes \
            -newkey rsa:2048 \
            -subj "/CN=samidrissil.usms.ac.ma/" \
            -keyout /etc/nginx/ssl/gestion_labo.key -out /etc/nginx/ssl/gestion_labo.crt

COPY conf/nginx.conf /etc/nginx/nginx.conf

COPY ./dist /var/www/gestion_labo 

CMD [ "nginx", "-g", "daemon off;" ]
