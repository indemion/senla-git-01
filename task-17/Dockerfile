FROM maven:3.9.12-eclipse-temurin-17-noble AS build

USER 0:0

SHELL ["/bin/sh", "-c"]
WORKDIR /app

RUN --mount=type=bind,source=/pom.xml,target=/app/pom.xml \
    --mount=type=cache,id=maven-repo,target=/root/.m2/repository,sharing=locked \
    mvn -B -T 4 dependency:resolve-plugins dependency:go-offline

RUN --mount=type=bind,source=/pom.xml,target=/app/pom.xml \
    --mount=type=bind,source=/src/,target=/app/src/ \
    --mount=type=bind,source=/checkstyle.xml,target=/app/checkstyle.xml \
    --mount=type=bind,source=/suppressions.xml,target=/app/suppressions.xml \
    --mount=type=cache,id=maven-repo,target=/root/.m2/repository,sharing=locked \
    mvn -U clean package


FROM tomcat:11-jre17-temurin-noble AS prepare

RUN addgroup --gid 1001 carservice \
    && adduser --uid 1001 --gid 1001 --shell /bin/bash --no-create-home carservice \
    \
    && chown -R carservice:carservice /usr/local/tomcat \
    && chmod -R 755 /usr/local/tomcat/conf \
    && chmod -R 775 /usr/local/tomcat/logs /usr/local/tomcat/temp /usr/local/tomcat/work /usr/local/tomcat/webapps

COPY --chown=1001:1001 --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war


FROM scratch

ENV LANGUAGE=en_US:en \
    LANG=en_US.UTF-8 \
    LC_ALL=en_US.UTF-8 \
    LD_LIBRARY_PATH=/usr/local/tomcat/native-jni-lib \
    JAVA_HOME=/opt/java/openjdk \
    TOMCAT_NATIVE_LIBDIR=/usr/local/tomcat/native-jni-lib \
    CATALINA_HOME=/usr/local/tomcat \
    PATH=/usr/local/tomcat/bin:/opt/java/openjdk/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin \
    \
    CATALINA_OPTS="-Dorg.apache.catalina.startup.EXIT_ON_INIT_FAILURE=true"

COPY --from=prepare / /

WORKDIR /usr/local/tomcat

USER carservice
EXPOSE 8080
ENTRYPOINT []
CMD ["catalina.sh", "run"]
