FROM alpine/java:21-jdk

WORKDIR /app

COPY target/devops-inventory-lab-0.0.1-SNAPSHOT.jar /app/

EXPOSE 8080

CMD ["java", "-jar", "devops-inventory-lab-0.0.1-SNAPSHOT.jar"]