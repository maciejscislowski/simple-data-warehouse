FROM maven:3-jdk-11 as target
WORKDIR /build
COPY pom.xml .
# Caching maven deps
RUN mvn dependency:go-offline
COPY src/ /build/src/
RUN mvn package -DskipTests

FROM openjdk:11-jdk
EXPOSE $PORT
COPY --from=target /build/target/*.jar /app/app.jar
ENV WAIT_VERSION 2.7.2
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/$WAIT_VERSION/wait /wait
RUN chmod +x /wait
CMD [ "sh", "-c", "/wait && java $JAVA_OPTS -Dserver.port=$PORT -Dspring.profiles.active=$PROFILE -jar /app/app.jar" ]
