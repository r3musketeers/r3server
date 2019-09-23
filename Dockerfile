FROM renantarouco/r3lib:latest

WORKDIR /home/r3server
COPY config config
COPY src src
COPY *.properties ./
COPY pom.xml .

RUN mvn package

ENTRYPOINT [ "java", "-cp", "target/r3server-1.0-SNAPSHOT.jar", "raas.r3server.R3Server", "0"]
