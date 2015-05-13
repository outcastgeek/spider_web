#!/bin/sh

exec consul-template \
-log-level debug \
-consul=consul:8500 \
-template "/etc/consul-templates/nginx.conf:/etc/nginx/nginx.conf:cat /etc/nginx/nginx.conf && /usr/sbin/nginx -c /etc/nginx/nginx.conf -t && /usr/sbin/nginx -c /etc/nginx/nginx.conf -g 'daemon off;'" \
-retry 30s \
-once && \
/usr/sbin/nginx -c /etc/nginx/nginx.conf -t && \
exec /usr/sbin/nginx -c /etc/nginx/nginx.conf -g "daemon off;"

