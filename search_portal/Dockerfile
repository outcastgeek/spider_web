
# Dockerfile for running Java Applications

FROM java:8

ADD ./target/search_portal-0.0.1-SNAPSHOT-standalone.jar /srv/search_portal.jar

EXPOSE 8080

CMD ["java", "-jar", "/srv/search_portal.jar"]


