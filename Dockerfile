FROM nimmis/java-centos:openjdk-8-jdk
ENV TZ=Europe/Warsaw
COPY build/libs/urlopia-0.1.0.jar /urlopia-0.1.0.jar
CMD java -jar /urlopia-0.1.0.jar
