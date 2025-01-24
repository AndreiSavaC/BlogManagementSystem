# Blog Management System

## Description

The **Blog Management System** is a Java EE application developed to manage blog posts, categories, tags, and user accounts within a blogging platform. This project was created with the primary goal of learning and mastering the **Payara** server, utilizing various Java EE technologies such as EJBs, JMS, JSF, and JPA to build a robust and scalable application.

## Features

- **User Management**: Register, activate, and manage user accounts with role-based access (ADMIN and USER).
- **Blog Post Management**: Create, edit, view, and delete blog posts with categories and tags.
- **Scheduled Tasks**: Automatically check for new blog posts every hour using EJB timers.
- **Email Notifications**: Send activation and confirmation emails using JMS and Message-Driven Beans.
- **Validation**: Ensure data integrity with Jakarta Bean Validation.
- **Logging**: Comprehensive logging using Java's built-in `Logger`.
- **Security**: Secure authentication and authorization mechanisms.
- **Responsive UI**: User-friendly interfaces built with JSF and Bootstrap.

## Project Structure

- **com.example.entity**: Contains JPA entity classes representing the database tables.
- **com.example.service**: Implements business logic using EJBs and provides interfaces for services.
- **com.example.jms**: Handles JMS messaging for email notifications.
- **com.example.util**: Utility classes such as hashing utilities.
- **com.example.web**: JSF managed beans and web-related components.
- **resources**: JSF XHTML pages for the user interface.
- **META-INF**: Contains `persistence.xml` for JPA configuration.

## Project Purpose

This project was developed for educational purposes to:

- **Learn Payara Server**: Gain hands-on experience with deploying and managing Java EE applications on Payara.
- **Understand Java EE Components**: Deepen knowledge of EJBs, JMS, JSF, CDI, and JPA.
- **Implement Best Practices**: Apply SOLID principles, design patterns, and layered architecture for maintainable code.
- **Explore Messaging Systems**: Utilize JMS for asynchronous email notifications.
- **Enhance Security Skills**: Implement secure authentication and authorization mechanisms.
