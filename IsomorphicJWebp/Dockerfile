
# Dockerfile for running Java Applications

FROM java:8

ADD ./target/com.outcastgeek.isomorphicjwebp-1.0-SNAPSHOT.jar /srv/IsomorphicJWebp.jar

ENV logs /var/log/app_engine/custom_logs

ENV JAVA_OPTS "-server -Xmx256M -Xms128m –XX:+UseG1GC -XX:+UseStringDeduplicationJVM"

CMD ["java", "-jar", "/srv/IsomorphicJWebp.jar"]


