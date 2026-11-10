InterviewIQ

An AI-powered interview preparation application that helps developers practice technical interviews, receive structured feedback, and identify areas for improvement.

Live Demo

Launch InterviewIQ

Monitoring Dashboard

View InterviewIQ Grafana Dashboard

Overview

InterviewIQ allows candidates to paste a job description and generate role-specific interview questions. Candidates can submit answers and receive AI-powered evaluations based on clarity, specificity, and technical depth.

Interview results and scores are stored in PostgreSQL, allowing candidates to track their performance.

Key Features

* Job Description Analysis: Extracts relevant technical skills from a pasted job description.
* AI Question Generation: Generates interview questions based on the role and required skills.
* Answer Evaluation: Evaluates responses based on clarity, specificity, and technical depth.
* Performance Tracking: Stores interview attempts and evaluation scores.
* Skill-Based Feedback: Helps identify weaker areas for additional practice.
* Application Monitoring: Tracks application availability, API performance, JVM metrics, and interview evaluation metrics.

Technology Stack

Layer	Technologies
Frontend	React, TypeScript, Vite
Backend	Java 21, Spring Boot
AI Integration	Google Gemini API
Database	PostgreSQL
ORM	Hibernate, Spring Data JPA
Database Migrations	Flyway
Monitoring	Prometheus, Grafana Cloud
CI/CD	GitHub Actions
Deployment	Vercel, Render

Architecture

                  User
                    |
                    v
           React + TypeScript
                 Vercel
                    |
                    v
           Spring Boot REST API
                  Render
                    |
             +------+------+
             |             |
             v             v
          Gemini API   PostgreSQL
                            |
                            v
                       Hibernate/JPA
                            |
                          Flyway
           Application Monitoring
                    |
                    v
                Prometheus
                    |
                    v
               Grafana Cloud

Application Workflow

1. The candidate pastes a job description.
2. The backend analyzes the description and extracts relevant skills.
3. Gemini generates role-specific interview questions.
4. The candidate submits answers.
5. The AI evaluates each answer across three dimensions:
    * Clarity
    * Specificity
    * Technical Depth
6. Evaluation scores are saved in PostgreSQL.
7. The dashboard provides visibility into interview activity and performance.

Backend API

Production Backend:

https://interview-coach-ad04.onrender.com

Health Endpoint:

https://interview-coach-ad04.onrender.com/actuator/health

Local Development

Prerequisites

* Java 21
* Node.js and npm
* PostgreSQL
* Git

Backend Setup

1. Clone the repository.
2. Navigate to the backend directory.
3. Configure the PostgreSQL connection and required AI API key using environment variables.
4. Start the Spring Boot application.

Frontend Setup

1. Navigate to the frontend directory.
2. Install dependencies:

npm install

3. Configure the frontend API base URL to point to the backend.
4. Start the development server:

npm run dev

Monitoring

The application uses Prometheus and Grafana Cloud to monitor:

* Application availability
* API request rate
* API response time
* JVM heap and non-heap memory
* CPU utilization
* Interview activity and evaluation scores

Open the Live Grafana Dashboard

CI/CD

GitHub Actions is used to automate build and validation workflows.

Purpose

This project demonstrates an end-to-end application integrating a modern frontend, Java backend, relational database, external AI service, cloud deployment, and application monitoring.