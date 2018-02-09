#!/bin/bash
cd /code
rm -rf  /usr/share/nginx/html
ln -s /code/src /usr/share/nginx/html

npm install
npm run-script build

#ng serve &
nginx -g "daemon off;"
