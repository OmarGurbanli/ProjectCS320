# Project GMS – CS320 Setup & Run Guide

Greetings! We’re a team of four from Özyeğin University. Follow these steps to get the Gym Management System up and running:

Prerequisites
Java 11+ (JDK installed & JAVA_HOME set)

Maven (or Gradle)

MySQL (or MariaDB) server

IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

1. Clone the Repository
   
   
   git clone https://github.com/your‑org/gms‑project.git
   cd gms-project
2. Install “resources” Package
   In your IDE, locate the resources/ folder at the project root.

Copy any external JARs or config files into src/main/resources/.

If there’s an installer script (e.g. setup-resources.sh), run:



chmod +x setup-resources.sh
./setup-resources.sh
3. Configure the Database
   Create the schema in MySQL:

sql

CREATE DATABASE gms_db CHARACTER SET utf8mb4;
Users & privileges (optional):

sql

CREATE USER 'gms_user'@'localhost' IDENTIFIED BY 'securePass';
GRANT ALL ON gms_db.* TO 'gms_user'@'localhost';
FLUSH PRIVILEGES;
4. Update Connection Properties
   Open src/main/resources/application.properties (or .yml) and set:

properties

spring.datasource.url=jdbc:mysql://localhost:3306/gms_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=gms_user
spring.datasource.password=securePass
5. Initialize the Schema & Seed Data
   If you have SQL scripts under src/main/resources/db/, they will run automatically on startup (via Spring Boot’s schema.sql & data.sql).

Otherwise, manually import:



mysql -u gms_user -p gms_db < src/main/resources/db/schema.sql
mysql -u gms_user -p gms_db < src/main/resources/db/data.sql
6. Build the Project
   From the project root, run:


mvn clean install
or, if using Gradle:

gradle build
Ensure there are no errors in the console.

7. Run the Application
  
   mvn spring-boot:run
   or:


java -jar target/gms‑project‑0.1.0.jar
By default it binds to port 8080.