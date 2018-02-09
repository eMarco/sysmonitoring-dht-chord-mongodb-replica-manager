#!/bin/bash
cd /code
rm -rf  /usr/share/nginx/html
ln -s /code/dist /usr/share/nginx/html
yarn
ng build --env=prod

#ng serve &
nginx -g "daemon off;"
