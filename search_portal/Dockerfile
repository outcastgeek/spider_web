
# Dockerfile for running Java Applications

FROM java:8

ADD ./target/search_portal-0.0.1-SNAPSHOT-standalone.jar /srv/search_portal.jar

EXPOSE 8080

ENV logs /var/log/app_engine/custom_logs

CMD ["java", "-jar", "/srv/search_portal.jar", "-server -Xmx256M -Xms128m –XX:+UseG1GC -XX:+UseStringDeduplicationJVM"]


