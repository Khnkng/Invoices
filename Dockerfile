# Pull base image.
FROM qount/java8-tomcat8:2.0
MAINTAINER mahendra <mahendra.velladandi@bighalf.io>

WORKDIR /code

COPY /target/Invoices.war /opt/tomcat/webapps/

EXPOSE 8080

VOLUME "/opt/tomcat/logs"

CMD ["/opt/tomcat/bin/catalina.sh", "run"]
