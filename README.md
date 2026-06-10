## ⚠ Portfolio Project

PowerLab is a personal project developed to demonstrate backend architecture with Java, Spring Boot and Angular.

The code is publicly visible for educational and portfolio purposes only.
Commercial use is not permitted without explicit permission.

---

# PowerLab 🏋️‍♂️

**Strength Training Management Platform (MVP)**

PowerLab is a web application for strength athletes and coaches to plan and track structured training programs.

The goal of this project is to model complex training programming structures while providing a clean and scalable backend architecture.

This repository showcases a **production-style backend architecture** using modern Java and Spring Boot technologies.

> **Disclaimer**: PowerLab is an informational and tracking tool. It is **not** medical or fitness advice.
> Consult a qualified professional before starting any training program. Use at your own risk.

---

# 🚀 Core Features

## Exercise Management

Create and manage a structured catalog of exercises including:

- movement pattern
- implement
- stance / grip
- loading method
- height adjustments

This allows modelling **exercise variations commonly used in strength training**.

---

## Training Program Templates

Define structured programming blocks:

Block → Week → Day → Prescribed Sets

Each template can define:

- target sets
- reps
- intensity (RPE / % / RIR)
- rest time
- exercise variations

---

## Workout Execution

Generate real workout sessions from templates and record:

- performed sets
- weight used
- reps completed
- intensity (RPE / RIR)

Each set is stored individually to allow **fine-grained performance tracking**.

---

## Training History

Retrieve past workouts by:

- workout id
- date
- date ranges

This allows athletes to review past sessions and track performance evolution.

---

# 🏗 Architecture

The backend follows a **layered architecture** with clear separation of responsibilities:

Controller → Service → Repository → Database

DTO ↔ Entity mapping handled via **MapStruct**.

### Architectural Principles

- Entities are **never exposed directly**
- DTOs are used for all API responses
- Aggregates are loaded efficiently to avoid N+1 queries
- Mapping logic is centralized with **MapStruct**
- REST endpoints follow consistent resource hierarchies

Example endpoints:

/api/workouts/{workoutId}
/api/workouts/by-date?date=YYYY-MM-DD
/api/exercises
/api/templates

---

# 🧠 Domain Model Overview

Core aggregates:

Exercise

TrainingSessionTemplate
└── TrainingSetTemplate

WorkoutSession
└── WorkoutEntry
└── WorkoutSet

This structure allows modelling:

- prescribed training
- executed training
- performance metrics

---

# 🛠 Tech Stack

## Backend

- **Java 21**
- **Spring Boot**
- **Spring Web**
- **Spring Data JPA / Hibernate**
- **MapStruct**
- **Maven**

## Database

- **PostgreSQL**

## Frontend

- **Angular** (in progress)

---

# 📡 API Documentation

Once the application is running, Swagger documentation is available at:

http://localhost:8080/swagger-ui.html

---

# 🏁 Running the Project

## Prerequisites

- Java 21
- Maven
- PostgreSQL

---

## Database Setup

Create database:

powerlab

Update database credentials in:

src/main/resources/application.properties

---

## Run the Application

mvn clean spring-boot:run

The API will be available at:

http://localhost:8080

---

# 🔮 Future Roadmap

Planned features beyond MVP:

## Authentication

- JWT authentication
- user accounts
- multi-athlete support

## Advanced Strength Analytics

- estimated 1RM progression
- volume and intensity trends
- fatigue monitoring

## Training Insights

- weak point detection
- exercise performance correlations

---

# 📄 License

All rights reserved.

This repository is publicly visible for **portfolio and educational purposes only**.

The source code may not be copied, modified, distributed, or used commercially without explicit permission from the author.

---

# 👤 Author

**Juan Pedro Mancera**

Backend Developer specialized in **Java & Spring Boot**
