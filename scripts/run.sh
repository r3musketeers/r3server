#!/usr/bin/env sh
java -cp lib/bcpkix-jdk15on-160.jar:lib/bcprov-jdk15on-160.jar:lib/BFT-SMaRt.jar:lib/commons-codec-1.11.jar:lib/core-0.1.4.jar:lib/jpaxos.jar:lib/logback-classic-1.2.3.jar:lib/logback-core-1.2.3.jar:lib/netty-all-4.1.34.Final.jar:lib/slf4j-api-1.7.25.jar:lib/r3lib-1.0-SNAPSHOT.jar:target/r3server-1.0-SNAPSHOT.jar raas.r3server.R3Server $1
