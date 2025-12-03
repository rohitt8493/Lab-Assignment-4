# ⚙️ Java Lab Assignment 4: Advanced Data Management System

## Project Overview

This project implements an **Advanced Data Management System** in Java. It builds upon previous assignments by integrating **Java Generics** and the **Collections Framework** to create a highly flexible and type-safe solution for managing different types of records (e.g., Students, Courses) efficiently.


***

## ✨ Key Features Implemented

The system focuses on achieving high code reusability and type safety using advanced Java concepts:

### 1. Generic Class Implementation
* **Generic Repository (`Repository<T>`):** Implements a generic class that can handle any type of data object (`T`), enabling the creation of a single class for managing lists of `Student` objects, `Course` objects, or any future data type.
* **Type Safety:** Ensures compile-time type checking, preventing the accidental insertion of mismatched object types into a collection.

### 2. Collections Framework Utilization
* **Data Storage:** Utilizes a **`java.util.HashMap`** (or `ArrayList`/`LinkedList`) to efficiently store and retrieve records. For example, a `HashMap<Integer, T>` is used for quick lookup of records by their unique ID.
* **Iterators:** Employs the **Iterator interface** for safe and efficient traversal and manipulation (adding/removing) of elements within the collections.

### 3. Interface Design and Separation of Concerns
* **Data Service Interface:** Defines a generic service interface (`DataService<T>`) that outlines common operations (add, remove, search) that any managed data type must support, promoting modular and scalable architecture.

### 4. Integration with Previous Concepts (Optional)
* The solution maintains the robust **Exception Handling** structure (`try-catch-finally`) and custom exceptions .

***

| **Name** | Sanchit Yadav |
| **Roll Number** | 2401010162 |
