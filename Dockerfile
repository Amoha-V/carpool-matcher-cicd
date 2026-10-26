# Runtime image. The JAR is built and tested by Maven first (locally or in the pipeline):
#   mvn -B clean package   ->   target/app.jar
FROM eclipse-temurin:17-jre-alpine

# Run as a fixed non-root user (numeric id so Kubernetes can verify runAsNonRoot)
RUN addgroup -S -g 10001 app && adduser -S -u 10001 -G app app

WORKDIR /app
COPY target/app.jar app.jar

# Git commit of the build, shown in the app footer
ARG BUILD_SHA=local
ENV BUILD_SHA=${BUILD_SHA} \
    JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

EXPOSE 8080
USER 10001:10001

# Used by plain Docker; Kubernetes uses its own probes
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
