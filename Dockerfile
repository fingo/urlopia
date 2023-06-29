FROM openjdk:17-jdk
ENV TZ=Europe/Warsaw

COPY build/libs/urlopia-*.jar /urlopia.jar
COPY entrypoint.sh /entrypoint.sh
RUN chmod u+rwx,g+rwx,o+rx /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
