FROM debian:bullseye

RUN apt-get update -y && apt-get upgrade -y

RUN apt-get install -y nginx openssl vim

RUN apt-get install -y curl

RUN mkdir -p /etc/nginx/ssl /var/run/nginx

RUN openssl req -x509 \
            -sha256 -days 356 \
            -nodes \
            -newkey rsa:2048 \
            -subj "/CN=samidrissil.usms.ac.ma/" \
            -keyout /etc/nginx/ssl/inception.key -out /etc/nginx/ssl/inception.crt

COPY conf/nginx.conf /etc/nginx/nginx.conf

CMD [ "nginx", "-g", "daemon off;" ]
