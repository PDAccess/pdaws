FROM maven:3-jdk-8 AS builder

ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN --mount=type=cache,target=/root/.m2 mvn -f $HOME/pom.xml clean install -Dmaven.test.skip -Pprod

FROM openjdk:8u312-jre-slim AS main
ENV USER=pdaccess
ENV GROUPNAME=$USER
ENV UID=10001
ENV GID=10001

RUN addgroup \
    --gid "$GID" \
    "$GROUPNAME" \
&&  adduser \
    --disabled-password \
    --gecos "" \
    --home "$(pwd)" \
    --ingroup "$GROUPNAME" \
    --no-create-home \
    --uid "$UID" \
    $USER

VOLUME /tmp
COPY --from=builder /usr/app/target/*.jar app.jar

USER pdaccess:pdaccess

ENTRYPOINT ["java", "-XX:+UseSerialGC", "-Xss256k", "-XX:ReservedCodeCacheSize=64M", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=90.0","-XshowSettings:vm","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
