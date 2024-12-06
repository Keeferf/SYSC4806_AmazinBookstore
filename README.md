# AmazinBookstore
## Build Status
[![Java CI with Maven](https://github.com/keeferf/SYSC4806_AmazinBookstore/actions/workflows/maven.yml/badge.svg)](https://github.com/keeferf/SYSC4806_AmazinBookstore/actions/workflows/maven.yml)
[![Main AmazinBookstore Application CI](https://github.com/keeferf/SYSC4806_AmazinBookstore/actions/workflows/main_amazinbookstoreapplication.yml/badge.svg)](https://github.com/keeferf/SYSC4806_AmazinBookstore/actions/workflows/main_amazinbookstoreapplication.yml)

## Project Overview
AmazinBookstore is an online bookstore management system that allows users to browse, search for books, and manage their shopping cart for purchasing. The system also provides functionality for bookstore owners to upload and edit book information.

## Product Backlog
This project uses GitHub Projects to manage tasks. The current Kanban board is divided into the following columns:
- **Backlog**: Features and improvements that are planned but not yet started.
- **In Progress**: Features that are currently being developed.
- **Completed**: Features that have been fully developed and tested.
## Current Kanban State
- **Backlog**:

- **In Progress**:
  - Create and connect prod database

- **Completed**:
  - Initialize default customer and default admin users
  - Add signup and login feature with authentication
  - Develop more test cases (test APIs)
  - UML class and sequence diagrams
  - Build Book, Cart and CartItem models
  - Build Checkout and PurchaseItem models
  - Build User and Role models
  - Build RESTful controllers for a SPA: BookController, CartController, PurchaseController, UserController
  - Create a switch between Admin and Customer view
  - Implement ability to remove one, multiple, or clear items from cart
  - Database schema diagram
  - Fix error occurring when book is purchased, it cannot be updated or deleted (decoupling)
  - Update README
  - Deploy to Azure (dev, staging, prod)
  - Implement book recommendation list

## Next Sprint Plan
### Milestone 3: Final Demo
- **Demo Date**: December 6th
- For the final sprint of your project you must decide on the final scope of the product: a set of features that can be implemented within the given timeline and makes the product usable and useful. The user interface should not have any dangling links to non-implemented features.
- **Key Features to Implement**:
  1. Ensure basic book browsing, searching, and purchasing functions.
  2. Ensure filtering and sorting features are implemented.
  3. Implement user registration and authentication
  4. Implement book recommendation based on user purchases
  5. Secure endpoints
  6. Improve the look of the user interface
  7. Implementation of MongoDB Atlas

## Database Schema
The following is the current schema of the database used in the AmazinBookstore project:
![Database schema milestone 3.png](Diagrams%2FDatabase%20schema%20milestone%203.png)

## UML Diagram
The following is the current UML diagram used in the AmazinBookstore project:
![UML Diagram Milestone 3.png](Diagrams/UML%20Diagram%20Milestone%203.png)
