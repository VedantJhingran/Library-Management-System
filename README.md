# README FILE
# Library Management System (Java GUI + Servlet)

This is a Java GUI-based Library Management System built using Java Swing, SQLite (JDBC), and Servlets.
The project demonstrates GUI development along with backend servlet integration as required by the rubric.

---

## Features
- Java Swing based GUI
- SQLite database using JDBC
- Servlet for backend data access
- JSON response from servlet
- Modular code structure

---

## Technologies Used
- Java (JDK 8 or above)
- Java Swing
- SQLite
- JDBC
- Servlet API
- Apache Tomcat

---

## Project Structure

model/
Data models

dao/
Database access layer

service/
Business logic

gui/
Swing GUI

servlet/
Servlet backend

WEB-INF/
web.xml

---

## How to Run the Project

Step 1: Prerequisites
- JDK installed
- Apache Tomcat installed
- Any Java IDE (Eclipse / IntelliJ / NetBeans)

---

Step 2: Add Required JAR Files
- sqlite-jdbc.jar
- servlet-api.jar or jakarta.servlet-api.jar

---

Step 3: Run the GUI Application
1. Open the project in IDE
2. Navigate to gui/Main.java
3. Run Main.java
4. The Library Management System GUI window will open

---

Step 4: Run the Servlet
1. Deploy the project on Apache Tomcat
2. Start the Tomcat server
3. Open browser and visit:
   http://localhost:8080/YourProjectName/books
4. Book data will be displayed in JSON format

---

## Output
- GUI displays library data in a table
- Servlet returns book data in JSON format
- SQLite database file is created automatically on first run

---

## Rubric Mapping
GUI Based Project: Java Swing GUI
Servlet Implementation: BookServlet
Code Quality and Execution: Proper package structure and layered design
Innovation or Extra Effort: JSON based servlet response

---

## Conclusion
This project combines Java Swing based GUI with servlet backend integration and satisfies all rubric requirements.
