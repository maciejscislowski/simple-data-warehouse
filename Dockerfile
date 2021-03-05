FROM maven:3-jdk-11
ADD . /appdir
WORKDIR /appdir
RUN ls -l
RUN mvn clean install

FROM openjdk:11-jdk
VOLUME /tmp
COPY --from=0 "/appdir/target/simple-*-SNAPSHOT.jar" app.jar
EXPOSE $PORT

ENV WAIT_VERSION 2.7.2
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/$WAIT_VERSION/wait /wait
RUN chmod +x /wait

CMD [ "sh", "-c", "/wait && java $JAVA_OPTS -Dserver.port=$PORT -Dspring.profiles.active=$PROFILE -jar /app.jar" ]
#CMD [ "sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
