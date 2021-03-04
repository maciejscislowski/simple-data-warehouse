FROM maven:3-jdk-11
ADD . /appdir
WORKDIR /appdir
RUN ls -l
RUN mvn clean install

FROM openjdk:11-jdk
VOLUME /tmp
COPY --from=0 "/appdir/target/simple-*-SNAPSHOT.jar" app.jar
EXPOSE $PORT
CMD [ "sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
