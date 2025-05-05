FROM openjdk:21-jdk
COPY target/CampusBookings-0.0.1-SNAPSHOT.jar /app/CampusBookings-0.0.1-SNAPSHOT.jar
EXPOSE 8081
CMD ["java", "-jar", "/app/CampusBookings-0.0.1-SNAPSHOT.jar"]
