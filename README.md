# AmazinBookstore
## Build Status
[![Java CI with Maven](https://github.com/jaydonhaghighi/SYSC4806_AmazinBookstore/actions/workflows/maven.yml/badge.svg)](https://github.com/jaydonhaghighi/SYSC4806_AmazinBookstore/actions/workflows/maven.yml)
## Project Overview
AmazinBookstore is an online bookstore management system that allows users to browse, search for books, and manage their shopping cart for purchasing. The system also provides functionality for bookstore owners to upload and edit book information.

## Product Backlog
This project uses GitHub Projects to manage tasks. The current Kanban board is divided into the following columns:
- **Backlog**: Features and improvements that are planned but not yet started.
- **In Progress**: Features that are currently being developed.
- **Completed**: Features that have been fully developed and tested.
## Current Kanban State
- **Backlog**:
  - Implement book recommendation list

- **In Progress**:
  - Create and connect prod database
  - Add signup and login feature with authentication
  - Develop more test cases (test APIs)
  - Initialize default customer and default admin users
  - Deploy to Azure (dev, staging, prod)
  - UML class and sequence diagrams

- **Completed**:
  - Build Book, Cart and CartItem models
  - Build Checkout and PurchaseItem models
  - Build User and Role models
  - Build RESTful controllers for a SPA: BookController, CartController, PurchaseController, UserController
  - Create a switch between Admin and Customer view
  - Implement ability to remove one, multiple, or clear items from cart
  - Database schema diagram
  - Fix error occurring when book is purchased, it cannot be updated or deleted (decoupling)
  - Update README

## Next Sprint Plan
### Milestone 2: Alpha Release
- **Demo Date**: November 25th
- For the alpha release, the system should be somewhat usable, although not feature-complete. Users should be able to use several related features of the app to accomplish reasonable tasks.
- **Key Features to Implement**:
  1. Ensure basic book browsing, searching, and purchasing functions.
  2. Ensure filtering and sorting features are implemented.
  3. Implement user registration and authentication
  4. Implement book recommendation based on user purchases
  5. Secure endpoints
  6. Improve the look of the user interface

## Database Schema
The following is the current schema of the database used in the AmazinBookstore project:
![Database schema milestone 1.png](Diagrams%2FDatabase%20schema%20milestone%201.png)

## UML Diagram
The following is the current UML diagram used in the AmazinBookstore project:
![UML Diagram Miletone 1.png](Diagrams/UML%20Diagram%20Milestone%201.png)