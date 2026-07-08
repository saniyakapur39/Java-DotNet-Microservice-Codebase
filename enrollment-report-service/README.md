enrollment-report-service
------------------------
Modernized Spring Boot microservice for daily enrollment error report (MEMRPT01).

Features:
- Reads sequential error file, tallies and classifies errors.
- Generates fixed-width report matching legacy output.
- REST endpoint: POST /v1/enrollment-error-report
- Spring Scheduler for daily job.
- MS SQL Server for audit/quarantine.
- All configuration in application.yml.
- Full test and migration strategy per design doc.

Build:
  mvn clean package

Run:
  docker-compose up --build

API:
  POST /v1/enrollment-error-report triggers report run.

Refer to docs/ for architecture, migration, and user story traceability.