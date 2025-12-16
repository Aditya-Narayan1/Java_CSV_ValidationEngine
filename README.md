# Java CSV Validation Engine

A Java-based CSV normalization and validation engine designed to ensure data quality using schema-driven rules.

## Overview

In real-world data processing systems, CSV files often contain missing, inconsistent, or invalid data. This project provides an automated solution to validate and clean CSV data before it is used for analytics or storage.

The system reads raw CSV data, validates each field against a predefined schema, separates valid and invalid records, and generates audit logs and statistics for transparency.

## Key Features

- Schema-driven validation (data type, required fields, regex patterns)
- Separation of clean and rejected records
- Audit logging for invalid rows
- Statistics generation for processed data
- File-based processing using core Java (no external libraries)

## Project Structure

