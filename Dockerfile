FROM openjdk:16-jdk
ENV TZ=Europe/Warsaw
COPY build/libs/urlopia-*.jar /urlopia.jar
CMD java -jar /urlopia.jar -Xmx1g
