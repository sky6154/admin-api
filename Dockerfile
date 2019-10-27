FROM openjdk:14-jdk-slim
MAINTAINER kokj <sky6154@gmail.com>

VOLUME /tmp
COPY /build/libs/develobeer-admin-latest.jar develobeer-admin.jar

RUN mkdir -p /var/www

EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=live","-Dserver.port=80","-jar","/develobeer-admin.jar", "-server", "-Xmx1g"]