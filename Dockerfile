FROM openjdk:17

# copy the packaged jar file into our docker image
COPY /target/Fraud-1.0.1.jar Fraud-1.0.1.jar

# set the startup command to execute the jar
ENTRYPOINT ["java", "-jar", "/Fraud-1.0.1.jar"]
#CMD ["java","-jar","/Server.jar"]