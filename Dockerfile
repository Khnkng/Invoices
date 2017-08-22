# Pull base image.
FROM inspectit/tomcat:latest
MAINTAINER mahendra <mahendra.velladandi@qount.io>

WORKDIR /code

COPY /target/Invoices.war /usr/local/tomcat/tomcat/webapps/

ENV JAVA_OPTS="-Dinspectit.repository=cmr.998d23e0.svc.dockerapp.io:9070 -Dinspectit.agent.name=invoices-services-dev-agent"

EXPOSE 8080

VOLUME "/usr/local/tomcat/logs"

CMD ["catalina.sh", "run"]
