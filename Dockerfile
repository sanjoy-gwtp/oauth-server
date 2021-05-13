FROM openjdk:8u181

ARG ARTIFACT

COPY ${ARTIFACT} /opt/app.jar

EXPOSE 8080 5005

ENV JAVA_OPTS=""

WORKDIR /opt

CMD java ${XX_JAVA_OPTS} ${JAVA_OPTS} -Dspring.profiles.active=$CONFIG_PROFILES -Dspring.cloud.config.uri=$CONFIG_SERVER_URI -jar app.jar --spring.profiles.active=${CONFIG_PROFILES} --spring.cloud.config.uri=${CONFIG_SERVER_URI}
