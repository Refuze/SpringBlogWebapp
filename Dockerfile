FROM openjdk:18
EXPOSE 8080
ADD target/spring-blog-webapp.war spring-blog-webapp.war
ENTRYPOINT ["java", "-jar", "/spring-blog-webapp.war"]
