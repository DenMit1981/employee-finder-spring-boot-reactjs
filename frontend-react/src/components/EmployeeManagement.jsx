import React, { useState, useEffect } from "react";
import { useHistory, useLocation } from "react-router-dom";
import backgroundImage from '../images/employee-management.jpg';
import {
  GENDERS,
  LOCATIONS,
  STATUSES,
  EDUCATION_LEVELS
} from "../constants/inputsValues";
import EmployeeService from "../services/EmployeeService";
import ResumeService from "../services/ResumeService";
import JobService from "../services/JobService";
import {
  TextField,
  Button,
  Select,
  MenuItem,
  InputLabel,
  FormControl,
  Typography,
  Paper,
} from "@material-ui/core";

const EmployeeManagement = () => {
  const history = useHistory();
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const employeeIdFromQuery = queryParams.get("id");

  const [validationErrors, setValidationErrors] = useState({});
  const [globalError, setGlobalError] = useState("");
  const [message, setMessage] = useState("");

  const [employee, setEmployee] = useState({
    name: "",
    gender: "MALE",
    location: "SINGAPORE",
    jobTypeId: '',
    jobPositionId: '',
    experienceYears: "1",
    expectedSalary: "3000",
    availabilityDate: "2025-05-05",
    educationLevel: "SPM",
    age: "25",
    status: "NEW",
    resume: null
  });

  const [selectedEmployeeId, setSelectedEmployeeId] = useState(null);
  const [resumeName, setResumeName] = useState("");
  const [jobTypes, setJobTypes] = useState([]);
  const [jobPositions, setJobPositions] = useState([]);

  useEffect(() => {
    JobService.getAllJobTypes()
      .then((jobTypesResponse) => {
        setJobTypes(jobTypesResponse.data);
      })
      .catch((err) => {
        console.error("Error fetching job types", err);
        setGlobalError("Failed to load job types");
      });
  }, []);

  useEffect(() => {
    const fetchJobPositions = async () => {
      try {
        let positionsResponse;
        if (employee.jobTypeId) {
          positionsResponse = await JobService.getJobPositionsByJobTypeId(employee.jobTypeId);
        } else {
          positionsResponse = await JobService.getAllJobPositions();
        }
        setJobPositions(positionsResponse.data);
      } catch (err) {
        console.error("Error fetching job positions", err);
        setGlobalError("Failed to load job positions");
      }
    };

    fetchJobPositions();
  }, [employee.jobTypeId]);

  useEffect(() => {
    if (employeeIdFromQuery) {
      setSelectedEmployeeId(employeeIdFromQuery);

      EmployeeService.getEmployeeById(employeeIdFromQuery)
        .then(async (res) => {
          const emp = res.data;

          try {
            const jobTypesResponse = await JobService.getAllJobTypes();
            const jobTypes = jobTypesResponse.data;
            setJobTypes(jobTypes);

            const jobTypeMatch = jobTypes.find(jt => jt.name === emp.jobType);
            const jobTypeId = jobTypeMatch ? jobTypeMatch.id : '';

            const positionsResponse = await JobService.getJobPositionsByJobTypeId(jobTypeId);
            const jobPositions = positionsResponse.data;
            setJobPositions(jobPositions);

            const jobPositionMatch = jobPositions.find(jp => jp.name === emp.jobPosition);
            const jobPositionId = jobPositionMatch ? jobPositionMatch.id : '';

            setEmployee({
              name: emp.name,
              gender: emp.gender,
              location: emp.location,
              jobTypeId,
              jobPositionId,
              experienceYears: emp.experienceYears,
              expectedSalary: emp.expectedSalary?.toString() || "3000",
              availabilityDate: emp.availabilityDate
                ? convertDMYToISO(emp.availabilityDate)
                : convertDMYToISO("2025-12-12"),
              educationLevel: emp.educationLevel || "SPM",
              age: emp.age?.toString() || "25",
              status: emp.status,
              resume: null
            });

            setResumeName(emp.resume?.fileName || "");
          } catch (err) {
            console.error("Error loading job types or positions", err);
            setGlobalError("Failed to load job types or positions");
          }
        })
        .catch((err) => {
          console.error("Error fetching employee by ID", err);
          setGlobalError("Failed to load employee data");
        });
    }
  }, [employeeIdFromQuery]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === "jobTypeId") {
      setEmployee((prev) => ({
        ...prev,
        jobTypeId: value,
        jobPositionId: '',
      }));

      JobService.getJobPositionsByJobTypeId(value)
        .then((response) => {
          setJobPositions(response.data);
        })
        .catch((err) => {
          console.error("Error fetching job positions", err);
        });
    } else {
      setEmployee((prev) => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  const handleStatusChange = (e) => {
    const { value } = e.target;
    setEmployee({ ...employee, status: value });

    if (selectedEmployeeId && value) {
      EmployeeService.changeEmployeeStatus(selectedEmployeeId, value)
        .then(() => {
          console.log("Employee status updated successfully!");
        })
        .catch((error) => {
          console.error("Error changing employee status:", error);
        });
    }
  };

  const handleResumeChange = (e) => {
    setEmployee({ ...employee, resume: e.target.files[0] });
    setResumeName(e.target.files[0]?.name || "");
  };

  const handleSubmit = async () => {
    setValidationErrors({});
    setGlobalError("");

    const formData = new FormData();

    for (let key in employee) {
      if (employee[key]) formData.append(key, employee[key]);
    }

    try {
      if (selectedEmployeeId) {
        await EmployeeService.updateEmployee(selectedEmployeeId, formData);
      } else {
        await EmployeeService.addEmployee(formData);
      }
      history.push("/employees");
    } catch (error) {
      if (error.response) {
        if (error.response?.status === 400 && Array.isArray(error.response.data)) {
          const errors = {};
          error.response.data.forEach(errObj => {
            const field = Object.keys(errObj)[0];
            const message = errObj[field];
            errors[field] = message;
          });
          setValidationErrors(errors);
        } else if (error.response?.data?.info) {
          setGlobalError(error.response.data.info);
        } else {
          setGlobalError("An unexpected error occurred.");
        }
      } else if (error.message === "Network Error") {
        setGlobalError("The size of the attached file should not be greater than 5 Mb. Please select another file.");
      } else {
        setGlobalError("Something went wrong while submitting the form.");
      }
    }
  };

  const handleDeleteResume = () => {
    if (selectedEmployeeId) {
      ResumeService.deleteResume(selectedEmployeeId)
        .then(() => {
          setEmployee({
            ...employee,
            resume: null,
          });
          setMessage("Resume deleted successfully!")
          console.log("Resume deleted successfully!");
        })
        .catch((error) => {
          console.error("Error deleting resume:", error);
        });
    }
  };

  const resetForm = () => {
    setEmployee({
      name: "",
      gender: "MALE",
      location: "SINGAPORE",
      jobTypeId: 1,
      jobPositionId: 2,
      experienceYears: "1",
      expectedSalary: "3000",
      availabilityDate: "2025-05-05",
      educationLevel: "SPM",
      age: "25",
      status: "NEW",
      resume: null
    });
    setResumeName("");
    setSelectedEmployeeId(null);
  };

  const convertDMYToISO = (dmy) => {
    const [day, month, year] = dmy.split('/');
    return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        width: "100%",
        backgroundImage: `url(${backgroundImage})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundRepeat: "no-repeat",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        padding: "10px",
      }}
    >
      <Paper
        elevation={6}
        style={{
          padding: "0.5rem",
          width: "100%",
          maxWidth: "360px",
          backgroundColor: "rgba(255, 255, 255, 0.95)",
          borderRadius: "8px",
          boxShadow: "0 4px 12px rgba(0,0,0,0.12)",
          maxHeight: "90vh",
          overflowY: "auto",
          boxSizing: "border-box",
        }}
      >
        <Typography
          variant="h5"
          align="center"
          gutterBottom
          style={{ fontWeight: "600", color: "#1976d2", marginBottom: "1rem" }}
        >
          {selectedEmployeeId ? "Edit Employee" : "Add Employee"}
        </Typography>

        {globalError && (
          <Typography color="error" style={{ marginBottom: "0.5rem" }}>
            {globalError}
          </Typography>
        )}

        {Object.keys(validationErrors).length > 0 && (
          <ul style={{ color: "red", fontSize: "0.8rem", marginBottom: "0.5rem" }}>
            {Object.entries(validationErrors).map(([field, message]) => (
              <li key={field}>{message}</li>
            ))}
          </ul>
        )}

        <form noValidate>
          <TextField
            label="Name"
            name="name"
            value={employee.name}
            onChange={handleChange}
            fullWidth
            margin="dense"
            variant="outlined"
            size="small"
          />

          <FormControl fullWidth margin="dense" variant="outlined" size="small">
            <InputLabel>Gender</InputLabel>
            <Select name="gender" value={employee.gender} onChange={handleChange} label="Gender">
              {GENDERS.map((g) => (
                <MenuItem key={g.value} value={g.value}>
                  {g.label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <FormControl fullWidth margin="dense" variant="outlined" size="small">
            <InputLabel>Location</InputLabel>
            <Select name="location" value={employee.location} onChange={handleChange} label="Location">
              {LOCATIONS.map((loc) => (
                <MenuItem key={loc.value} value={loc.value}>
                  {loc.label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <FormControl fullWidth margin="dense" variant="outlined" size="small">
            <InputLabel>Job Type</InputLabel>
            <Select
              name="jobTypeId"
              value={employee.jobTypeId || ''}
              onChange={handleChange}
              label="Job Type"
            >
              <MenuItem value="">
              </MenuItem>
              {jobTypes.map((type) => (
                <MenuItem key={type.id} value={type.id}>
                  {type.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <FormControl fullWidth style={{ marginBottom: "1rem" }}>
            <InputLabel>Job Position</InputLabel>
            <Select
              name="jobPositionId"
              value={employee.jobPositionId || ''}
              onChange={handleChange}
              label="Job Position"
            >
              <MenuItem value="">
              </MenuItem>
              {jobPositions.map((position) => (
                <MenuItem key={position.id} value={position.id}>
                  {position.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <TextField
            label="Experience"
            name="experienceYears"
            type="number"
            value={employee.experienceYears}
            onChange={handleChange}
            fullWidth
            margin="dense"
            variant="outlined"
            inputProps={{ min: 0 }}
            size="small"
          />

          <TextField
            label="Salary"
            name="expectedSalary"
            type="number"
            value={employee.expectedSalary}
            onChange={handleChange}
            fullWidth
            margin="dense"
            variant="outlined"
            size="small"
          />

          <TextField
            label="Available From"
            name="availabilityDate"
            type="date"
            value={employee.availabilityDate}
            onChange={handleChange}
            fullWidth
            margin="dense"
            variant="outlined"
            InputLabelProps={{ shrink: true }}
            size="small"
          />

          <FormControl fullWidth margin="dense" variant="outlined" size="small">
            <InputLabel>Education</InputLabel>
            <Select name="educationLevel" value={employee.educationLevel} onChange={handleChange} label="Education">
              {EDUCATION_LEVELS.map((ed) => (
                <MenuItem key={ed.value} value={ed.value}>
                  {ed.label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <TextField
            label="Age"
            name="age"
            type="number"
            value={employee.age}
            onChange={handleChange}
            fullWidth
            margin="dense"
            variant="outlined"
            inputProps={{ min: 18 }}
            size="small"
          />

          {selectedEmployeeId && (
            <FormControl fullWidth margin="dense" variant="outlined" size="small">
              <InputLabel>Status</InputLabel>
              <Select name="status" value={employee.status} onChange={handleStatusChange} label="Status">
                {STATUSES.map((s) => (
                  <MenuItem key={s.value} value={s.value}>
                    {s.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          )}

          <div style={{ margin: "0.5rem 0" }}>
            <input type="file" accept="application/pdf" onChange={handleResumeChange} />
            {resumeName && (
              <Typography variant="body2" style={{ fontStyle: "italic", marginTop: "0.3rem" }}>
                {resumeName}
              </Typography>
            )}
          </div>

          <div
            style={{
              display: "flex",
              flexWrap: "wrap",
              gap: "0.5rem",
              justifyContent: "center",
              marginTop: "0.8rem",
            }}
          >
            <Button
              variant="contained"
              color="primary"
              onClick={handleSubmit}
              style={{ minWidth: "100px", borderRadius: "24px" }}
            >
              {selectedEmployeeId ? "Update" : "Add"}
            </Button>

            {selectedEmployeeId && (
              <Button
                variant="contained"
                color="secondary"
                onClick={handleDeleteResume}
                style={{ minWidth: "100px", borderRadius: "24px" }}
              >
                Delete Resume
              </Button>
            )}

            <Button
              variant="outlined"
              onClick={resetForm}
              style={{ minWidth: "100px", borderRadius: "24px", borderColor: "#1976d2", color: "#1976d2" }}
            >
              Clear
            </Button>

            <Button
              variant="outlined"
              color="primary"
              onClick={() => history.push("/employees")}
              style={{ minWidth: "100px", borderRadius: "24px" }}
            >
              Cancel
            </Button>
          </div>

          {message && (
            <Typography align="center" style={{ color: "green", marginTop: "1rem" }}>
              {message}
            </Typography>
          )}
        </form>
      </Paper>
    </div>
  );
}

export default EmployeeManagement;

