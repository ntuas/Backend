FROM openjdk:8u151-jre-slim
WORKDIR /app
ENTRYPOINT ["/app/run-backend.sh"]
ENV JAVA_OPTS="-Xmx2g"
COPY logback.xml /app/logback.xml
COPY *.jar /app/backend.jar
COPY *.sh /app/run-backend.sh
RUN chmod +x /app/run-backend.sh

EXPOSE 8888

ARG VERSION
LABEL nt.uas.group="nt.uas.backend" nt.uas.name="backend" nt.uas.version="${VERSION}"