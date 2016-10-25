FROM java:8-jdk-alpine
RUN apk --update add tzdata
ENV TZ=Europe/Warsaw
# install utilities
COPY build/libs/urlopia-0.1.0.jar /urlopia-0.1.0.jar
CMD java -jar /urlopia-0.1.0.jar