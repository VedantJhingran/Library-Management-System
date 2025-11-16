# Library-Management-System
A simple Library Management System. It allows users to add, update, delete, borrow, and return books with an easy-to-use interface. All data is stored using file serialization, making it lightweight and ideal for learning Java GUI and file handling.
# 📚 Library Management System  
### Java GUI • JDBC • SQLite • OOP • Multithreading

This is a **Java-based Library Management System** built using a single-file architecture.  
The project demonstrates:

- Full OOP implementation (Inheritance, Polymorphism, Interfaces)
- Java Swing GUI for user interaction
- JDBC + SQLite database connectivity
- DAO (Data Access Object) design pattern
- Multithreading using SwingWorker (prevents GUI freezing)
- CRUD functionality (Add, Delete, Search, Borrow, Return)

This project is fully **rubric-ready** and meets academic evaluation requirements.

---

## 🚀 Features

### ✔ Functional Features
- Add new books  
- Delete books  
- Borrow books  
- Return books  
- Search books by title  
- Real-time table updates  
- Persistent storage using SQLite  

### ✔ Technical Features
- **OOP Concepts:**  
  - Base class: `Item`  
  - Subclass: `Book`  
  - Interface: `LibraryDAO<T, ID>`  
  - Custom exceptions  
- **DAO Pattern:** Clean separation of data logic  
- **JDBC with SQLite:** Automatic schema creation  
- **Multithreading:** All DB operations run in background (SwingWorker)  
- **GUI (Swing):** Simple and interactive desktop interface  

---

## 🗂️ Project Structure

LibraryManagementSystem/
│
├── Main.java # Single file containing entire system
├── library_singlefile.db # SQLite database (auto-generated)
├── sqlite-jdbc.jar # SQLite JDBC driver required
└── README.md # Project documentation

## 🚀 Steps to Run the Project

Follow these steps to run the Library Management System on your machine:

---

### ✅ 1. Install Java
Make sure you have:
- **Java JDK 17+** installed  
Check by running:

---

### ✅ 2. Download SQLite JDBC Driver
Download the latest SQLite JDBC driver (.jar file) from:
https://github.com/xerial/sqlite-jdbc/releases


---

### ✅ 3. Add JDBC Driver to Project
If you are using IntelliJ IDEA:

1. Open IntelliJ  
2. Go to:library
3. Select the downloaded `sqlite-jdbc.jar`
4. Click **Apply → OK**

This step is **necessary**, otherwise Java cannot connect to SQLite database.

---

### ✅ 4. Place Files Together
Make sure these files are in the same folder:
3. Select the downloaded `sqlite-jdbc.jar`
4. Click **Apply → OK**

This step is **necessary**, otherwise Java cannot connect to SQLite database.

---

### ✅ 4. Place Files Together
Make sure these files are in the same folder:


The database file `library_singlefile.db` will be created automatically.

---

### ✅ 5. Compile the Project

#### Mac / Linux:

#### Windows:

---

### ✅ 6. Run the Project

#### Mac / Linux:

---

### 🎉 The GUI Window Will Open

You will now see:
- Add Book
- Delete Book
- Borrow Book
- Return Book
- Search Bar
- Dynamic Table

Everything is connected to the SQLite database.

---

### 🗃️ Database Auto-Creation
The app automatically creates:
 




