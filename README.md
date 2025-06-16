Employee Finder

A full-stack employee recruitment platform using **Spring Boot**, **React**, **PostgreSQL**, **Kafka**, **MinIO**, and **Docker**

## 🧾 Name of project: employee-finder

## 🚀 Tech Stack

- **Backend**: Java 17, Spring Boot, Kafka, Liquibase, MinIO, PostgreSQL
- **Frontend**: ReactJS
- **Infrastructure**: Docker, Docker Compose
- **Others**: JWT Authentication, Mail Sending (SMTP), Swagger UI

## 🧾 Features

- Roles: USER, ADMIN, SUPERADMIN
- Company registration/login
- Employee filtering by job, gender, location, experience, etc.
- Kafka events for registration and selection
- Resume upload and download via MinIO
- Email notifications to ADMIN and SUPERADMIN about registration of a new user(company) and submitted selection
- Swagger API docs
- Admin panel for employees management
- Superadmin panel for employees/users management

## 📦 Project Structure

employee-finder/
├── backend-java/ # Spring Boot app
├── frontend-react/ # React app
├── resumes/ # Candidates resumes
├── docker-compose.yml
└── README.md

service	        URL
Frontend	    http://localhost:3000
Backend API	    http://localhost:8081
Swagger UI	    http://localhost:8081/employee-finder/swagger-ui.html
Kafka UI	    http://localhost:9000
MinIO Console	http://localhost:9001

## ⚙️Prerequisites

- Docker & Docker Compose installed
- Ports 8081, 5432, 80, 29092, 9002, 9001, 9000 available
- MinIO bucket: `user-files`

## 🛠️ How to Run

- Build and run docker-compose with logs: docker compose up --build

- Build and run docker-compose without logs in detached mode: docker compose up -d --build

- Run docker-compose with logs: docker-compose up

- Stop and remove services: docker-compose down

- Build maven project: mvn clean install


## 🧾 Logins/passwords/emails/jobTitles/roles of users

Wally Rouda / 1111 / wally@yopmail.com / SUPERADMIN / ROLE_SUPERADMIN  
Boss / 2222 / empire@yopmail.com / ADMIN / ROLE_ADMIN  
Denis / 3333 / den@yopmail.com / ADMIN / ROLE_ADMIN
Alice / 1234 / alice.johnson@yopmail.com / HR_MANAGER / ROLE_USER
Bob / 4321 / bob.smith@yopmail.com / RECRUITMENT_SPECIALIST / ROLE_USER
Clara / 5678 / clara.lee@yopmail.com / TECHNICAL_RECRUITER / ROLE_USER
David / P@ssword1 / david.wright@yopmail.com / PROJECT_HIRING_COORDINATOR / ROLE_USER
Eva / 221182 / eva.martinez@yopmail.com / USER / ROLE_USER

## 📦 REST controllers:

-- AuthenticationController: 

registration (POST):
http://localhost:8081/api/v1/auth/signup
Body: UserRegisterRequestDto – contains user registration data
Description: Registers a new user and returns user details

authentication (POST):
http://localhost:8081/api/v1/auth/signin
Body: UserLoginRequestDto – contains login credentials
Description: Authenticates the user and returns a JWT token

changePassword (PATCH):
http://localhost:8081/api/v1/auth/change-password
Body: UserUpdateRequestDto – contains user email, old password, and new password
Description: Changes the current user’s password

-- EmployeeController:

add (POST):
http://localhost:8081/api/v1/employees/add
Content-Type: multipart/form-data
Body: EmployeeRequestDto + resume file (MultipartFile)
Description: Adds a new employee with an attached resume (ADMIN, SUPERADMIN)

update (PUT):
http://localhost:8081/api/v1/employees/update/{employeeId}
Path Variable: employeeId – employee's ID
Content-Type: multipart/form-data
Body: EmployeeRequestDto + resume file
Description: Updates the employee's information and resume (ADMIN, SUPERADMIN)

changeStatus (PUT):
http://localhost:8081/api/v1/employees/change-status/{employeeId}?status=SELECTED
Path Variable: employeeId – employee's ID
Query Parameter: status – new status value (NEW, SELECTED, HIRED)
Description: Changes the status of an employee (ADMIN, SUPERADMIN)

delete (DELETE):
http://localhost:8081/api/v1/employees/remove/{employeeId}
Path Variable: employeeId – employee's ID
Description: Deletes an employee by ID (ADMIN, SUPERADMIN)

getById (GET):
http://localhost:8081/api/v1/employees/{employeeId}
Path Variable: employeeId – employee's ID
Description: Retrieves detailed information about an employee by their ID

getAll (GET):
http://localhost:8081/api/v1/employees
Query Parameters:
searchField (default: default)
parameter (default: "")
status (optional)
sortField (default: id)
sortDirection (default: asc)
pageSize (default: 25)
pageNumber (default: 0)
Description: Retrieves all employees with optional search, filter, sorting, and pagination (ADMIN, SUPERADMIN)

getAllForUser (GET):
http://localhost:8081/api/v1/employees/for-user
Query Parameters: same as getAll (except status is internally fixed to NEW and SELECTED)
Description: Retrieves employees with status NEW or SELECTED for the currently logged-in user (USER)

filterEmployees (POST):
http://localhost:8081/api/v1/employees/filter
Body: EmployeeFilterRequestDto – filter criteria
Query Parameters: sortField, sortDirection, pageSize, pageNumber, searchField, parameter
Description: Filters employees based on complex criteria, with optional search and pagination (USER)

filterByJobTypeAndPosition (GET):
http://localhost:8081/api/v1/employees/filter/job-type-position
Query Parameters: jobTypeId, jobPositionId, gender, location, sortField, sortDirection, pageSize, pageNumber, searchField, parameter
Description: Filters employees by job type, position, gender, location, and optionally search text (USER)

-- JobController 

getAllJobTypes (GET)
http://localhost:8081/api/v1/jobs/job-types
Description: Returns a list of all available job types

getJobTypesFromNewEmployees (GET)
http://localhost:8081/api/v1/jobs/job-types/from-employees
Description: Returns job types based only on employees with status NEW or SELECTED

getAllJobPositions (GET)
http://localhost:8081/api/v1/jobs/job-positions
Description: Returns a list of all available job positions

getAllJobPositionsFromNewEmployees (GET)
http://localhost:8081/api/v1/jobs/job-positions/from-employees
Description: Returns job positions found among employees with status NEW or SELECTED

getJobPositionsByJobType (GET)
http://localhost:8081/api/v1/jobs/job-types-positions?jobTypeId=1
Query Parameter: jobTypeId – ID of the job type
Description: Returns all job positions that belong to the given job type

getJobPositionsByJobTypeFromNewAndSelectedEmployees (GET)
http://localhost:8081/api/v1/jobs/job-types-positions/from-employees?jobTypeId=1
Query Parameter: jobTypeId – ID of the job type
Description: Returns job positions of the given job type from employees with status NEW or SELECTED

getAllJobTypesWithPositions (GET)
http://localhost:8081/api/v1/jobs/job-types-positions/all
Description: Returns all job types with their related job positions.

getJobTypesWithPositionsByGender (GET)
http://localhost:8081/api/v1/jobs/job-types-positions/gender?gender=m
Query Parameter: gender – Gender to filter by (MALE, FEMALE)
Description: Returns job types and positions filtered by specified gender.

getJobTypesWithPositionsByLocation (GET)
http://localhost:8081/api/v1/jobs/job-types-positions/location?location=s
Query Parameter: location – Location to filter by (SINGAPORE, MALAYSIA, VIETNAM, PHILIPPINES, INDONESIA)
Description: Returns job types and positions filtered by a specific location

getJobTypesWithPositionsByGenderAndLocation (GET)
http://localhost:8081/api/v1/jobs/job-types-positions/gender-location?gender=f&location=v
Query Parameters: gender, location
Description: Returns job types and their positions filtered by both gender and location

--ResumeController

download (GET)
http://localhost:8081/api/v1/resumes/download/{employeeId}
Path Variable: employeeId – ID of the employee
Description: Downloads the employee's resume in PDF/DOC/DOCX format as an attachment. The file is returned with proper content type and headers

deleteResume (DELETE)
http://localhost:8081/api/v1/resumes/remove/{employeeId}
Path Variable: employeeId – ID of the employee
Description: Deletes the resume file associated with the given employee (ADMIN, SUPERADMIN)

-- SelectionController (for role USER)

addEmployeeToSelection (POST)
http://localhost:8081/api/v1/selections/add-employee/{employeeId}
Path Variable: employeeId – ID of the employee
Description: Adds the specified employee to the current company's selection list (USER)

removeEmployeeFromSelection (DELETE)
http://localhost:8081/api/v1/selections/remove-employee/{employeeId}
Path Variable: employeeId – ID of the employee
Description: Removes the specified employee from the current company's selection list (USER) 

clearSelection (DELETE)
http://localhost:8081/api/v1/selections/clear
Description: Clears the entire selection list for the current user. Typically used when the company logs out or resets the selection (USER)

getCurrentSelection (GET)
http://localhost:8081/api/v1/selections/current
Description: Returns the current in-progress (unsubmitted) selection for the logged-in user.

submitFinalSelection (POST)
http://localhost:8081/api/v1/selections/submit
Description: Finalizes the current selection and sends it to administrators via email notification.

getById (GET)
http://localhost:8081/api/v1/selections/{selectionId}
Path Variable: selectionId – ID of the selection
Query Parameters (optional):
searchField – Field to search by (e.g., "name", "jobTitle")
parameter – Search value
sortField – Field to sort by (default: id)
sortDirection – Sorting direction (asc or desc)
pageSize – Number of records per page
pageNumber – Page number
Description: Returns the details of a specific submitted selection, including selected candidates, with sort, optional search and pagination.

-- UserController (for role SUPERADMIN)

getAll (GET)
http://localhost:8081/api/v1/users
Query Parameters (optional):
searchField – Field to search by (default: "default")
parameter – Value for search (default: "")
sortField – Field to sort by (default: "id")
sortDirection – Sorting order: "asc" or "desc" (default: "asc")
pageSize – Number of users per page (default: 25)
pageNumber – Page number to retrieve (default: 0)
Description: Returns a paginated, optionally filtered and sorted list of users

changeRole (PUT)
http://localhost:8081/api/v1/users/{userId}/change-role
Path Variable: userId – ID of the user
Request Param: newRole – New role to assign (e.g. ADMIN, USER, etc.)
Description: Updates the role of a specific user.

getById (GET)
http://localhost:8081/api/v1/users/{userId}
Path Variable: userId – ID of the user
Description: Retrieves detailed information for the specified user by ID

deleteById (DELETE)
http://localhost:8081/api/v1/users/{userId}
Path Variable: userId – ID of the user
Description: Deletes the user with the given ID from the system