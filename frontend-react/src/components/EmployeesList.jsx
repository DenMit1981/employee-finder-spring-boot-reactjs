import React, { useEffect, useState } from "react";
import EmployeeService from "../services/EmployeeService";
import SelectionService from "../services/SelectionService";
import ResumeService from "../services/ResumeService";
import { useLocation, useHistory } from "react-router-dom";
import { EMPLOYEES_TABLE_COLUMNS, FILTERED_EMPLOYEES_TABLE_COLUMNS } from "../constants/tablesColumns";

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Button,
  Typography,
  Divider,
} from "@material-ui/core";
import icons from 'glyphicons';

const EmployeesList = () => {
  const location = useLocation();
  const history = useHistory();
  const userRole = sessionStorage.getItem("userRole");
  const [employees, setEmployees] = useState([]);
  const [selectedCandidates, setSelectedCandidates] = useState([]);
  const [message, setMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [searchValue, setSearchValue] = useState("");
  const [sortField, setSortField] = useState("id");
  const [sortDirection, setSortDirection] = useState("asc");
  const [pageSize, setPageSize] = useState(10);
  const [pageNumber, setPageNumber] = useState(0);

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);

    const jobTypeId = queryParams.get("jobType");
    const jobPositionId = queryParams.get("jobPosition");
    const genderParam = queryParams.get("gender") || null;
    const locationParam = queryParams.get("location") || null;

    if (jobTypeId && jobPositionId) {
      EmployeeService.filterByJobTypeAndPosition(
        jobTypeId,
        jobPositionId,
        genderParam,
        locationParam,
        pageNumber,
        pageSize,
        sortField,
        sortDirection
      )
        .then(res => {
          setEmployees(res.data.content || res.data);
          setErrorMessage("");
        })
        .catch(() => setErrorMessage("Error fetching filtered employees."));
    }
  }, [location.search, sortField, sortDirection, pageSize, pageNumber]);

  useEffect(() => {
    const fetchFilteredAdmin = async () => {
      try {
        if (!searchValue || searchValue.trim() === "") {
          const response = await EmployeeService.getAllSortedEmployeesByPages(
            sortField,
            pageSize,
            pageNumber,
            sortDirection
          );
          setEmployees(response.data.content || response.data);
          setErrorMessage("");
          return;
        }

        const fields = [
          "id", "name", "jobPosition", "gender", "location",
          "jobType", "expectedSalary", "availabilityDate", "educationLevel", "age", "status"
        ];

        let response = null;

        for (let field of fields) {
          const res = await EmployeeService.getAllSortedEmployeesSearched(
            field,
            searchValue,
            sortField,
            pageSize,
            pageNumber,
            sortDirection
          );

          const data = res.data;
          const dataArray = Array.isArray(data) ? data : data?.content;

          if (Array.isArray(dataArray) && dataArray.length > 0) {
            response = res;
            setErrorMessage("");
            break;
          }
        }

        if (response) {
          setEmployees(response.data.content || response.data);
        } else {
          setEmployees([]);
          setErrorMessage("Nothing found");
        }

      } catch (err) {
        console.error("Error fetching employees (admin)", err);
        setErrorMessage(err.response.data.info);
      }
    };

    const fetchFilteredNonAdmin = async () => {
      try {
        const queryParams = new URLSearchParams(location.search);
        const searchValueFromURL = queryParams.get("search") || "";
        const search = searchValueFromURL || searchValue;

        const filterDto = {
          name: queryParams.get("name") || null,
          gender: queryParams.get("gender") || null,
          location: queryParams.get("location") || null,
          jobTypeId: queryParams.get("jobTypeId") || null,
          jobPositionId: queryParams.get("jobPositionId") || null,
          experienceYears: queryParams.get("experienceYears") || null,
          expectedSalary: queryParams.get("expectedSalary") || null,
          availabilityDate: queryParams.get("availabilityDate") || null,
          educationLevel: queryParams.get("educationLevel") || null,
          age: queryParams.get("age") || null,
        };

        const hasFilterParams = Object.values(filterDto).some(v => v !== null && v !== "");
        const hasSearch = !!search;
        let response = null;

        const searchFields = [
          "id", "name", "jobPosition", "gender", "location",
          "jobType", "expectedSalary", "availabilityDate", "educationLevel", "age"
        ];

        if (hasFilterParams && hasSearch) {
          for (let field of searchFields) {
            const res = await EmployeeService.filterEmployeesSearched(
              filterDto, sortField, sortDirection, pageSize, pageNumber, field, search
            );
            const dataArray = Array.isArray(res.data) ? res.data : res.data?.content;
            if (Array.isArray(dataArray) && dataArray.length > 0) {
              response = res;
              break;
            }
          }
        }

        if (!response && hasFilterParams) {
          const res = await EmployeeService.filterEmployees(
            filterDto, sortField, sortDirection, pageSize, pageNumber
          );
          const dataArray = Array.isArray(res.data) ? res.data : res.data?.content;
          if (Array.isArray(dataArray) && dataArray.length > 0) {
            response = res;
          }
        }

        if (!response && hasSearch) {
          for (let field of searchFields) {
            const res = await EmployeeService.getAllSortedEmployeesSearchedForUser(
              field, search, sortField, pageSize, pageNumber, sortDirection
            );
            const dataArray = Array.isArray(res.data) ? res.data : res.data?.content;
            if (Array.isArray(dataArray) && dataArray.length > 0) {
              response = res;
              setErrorMessage("");
              break;
            }
          }
        }

        if (!response && !hasSearch && !hasFilterParams) {
          response = await EmployeeService.getAllSortedEmployeesByPagesForUser(
            sortField, pageSize, pageNumber, sortDirection
          );
        }

        const finalData = response?.data?.content || response?.data || [];
        setEmployees(finalData);
        setErrorMessage(finalData.length === 0 ? "Nothing found" : "");

      } catch (err) {
        console.error("❌ Error fetching employees (non-admin)", err);
        setErrorMessage(err.response.data.info);
      }
    };

    if (isAdmin) {
      fetchFilteredAdmin();
    } else {
      fetchFilteredNonAdmin();
    }
  }, [location.search, searchValue, sortField, sortDirection, pageSize, pageNumber]);

  useEffect(() => {
    SelectionService.getCurrentSelection()
      .then((res) => {
        const selection = res.data;
        if (selection && selection.candidates) {
          setSelectedCandidates(selection.candidates);
        }
      })
      .catch((err) => {
        console.error("Error fetching current selection", err);
      });
  }, []);

  const handleSort = (field) => {
    if (sortField === field) {
      setSortDirection((prev) => (prev === "asc" ? "desc" : "asc"));
    } else {
      setSortField(field);
      setSortDirection("asc");
    }
    setPageNumber(0);
  };

  const handleAddToSelection = (employeeId) => {
    SelectionService.addEmployeeToSelection(employeeId)
      .then((res) => {
        const selection = res.data;
        if (selection && selection.candidates) {
          setSelectedCandidates(selection.candidates);
          setMessage("Employee added to selection.");
          setErrorMessage("");
        } else {
          setErrorMessage("Unexpected response format.");
        }
      })
      .catch((err) => {
        console.error("Error adding employee to selection", err);
        setErrorMessage(err.response?.data.info || "Failed to add employee.");
        setMessage("");
      });
  };

  const handleDeleteFromSelection = (employeeId) => {
    SelectionService.removeEmployeeFromSelection(employeeId)
      .then((res) => {
        setSelectedCandidates(res.data.candidates || []);
        setMessage("Candidate removed from selection.");
        setErrorMessage("");
      })
      .catch((err) => {
        console.error("Error removing employee", err);
        setErrorMessage("Failed to remove candidate.");
        setMessage("");
      });
  };

  const handleSubmitSelection = () => {
    SelectionService.submitFinalSelection()
      .then((res) => {
        const selection = res.data;
        if (selection && selection.id) {
          setMessage("Selection submitted successfully!");
          setSelectedCandidates([]);
          history.push(`/selections/${selection.id}`);
        } else {
          setErrorMessage("Unexpected response format. No ID returned.");
        }
      })
      .catch((err) => {
        console.error("Error submitting selection", err);
        setErrorMessage("Failed to submit selection.");
      });
  };

  const handleLogout = () => {
    SelectionService.clearSelection()
      .then(() => {
        setSelectedCandidates([]);
        history.push("/");
      })
      .catch((err) => {
        console.error("Error clearing selection", err);
        setErrorMessage("Failed to log out properly.");
      });
  };

  const handleBackToJobs = () => {
    history.push("/jobs");
  };

  const handleResumeDownload = async (employeeId) => {
    try {
      const response = await ResumeService.downloadResume(employeeId);
      const contentDisposition = response.headers["content-disposition"];
      let fileName = "resume";
      if (contentDisposition) {
        const match = contentDisposition.match(/filename="?(.+?)"?/);
        if (match && match[1]) fileName = decodeURIComponent(match[1]);
      }
      const contentType = response.headers["content-type"] || "application/octet-stream";
      const blob = new Blob([response.data], { type: contentType });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", fileName);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error("Error downloading resume:", err);
      alert("Failed to download resume.");
    }
  };

  const handleEditEmployee = (employeeId) => {
    history.push(`/employee-management?id=${employeeId}`);
  };

  const isAdmin = ["ROLE_ADMIN", "ROLE_SUPERADMIN"].includes(userRole);

  const pageStyles = {
    minHeight: "100vh",
    backgroundColor: "#ADD8E6",
    backgroundSize: "cover",
    backgroundPosition: "center",
    padding: "2rem",
    backdropFilter: "blur(4px)",
  };

  const tableContainerStyles = {
    backgroundColor: "rgba(255, 255, 255, 0.9)",
    borderRadius: "10px",
    boxShadow: "0px 4px 20px rgba(0,0,0,0.1)",
    overflowX: "auto",
  };
  const tableHeaderStyles = {
    backgroundColor: "#1976d2",
    color: "#fff",
    fontWeight: "bold",
    cursor: "pointer",
  };
  const tableCellStyles = {
    backgroundColor: "white",
    color: "#333",
  };

  const buttonStyles = {
    contained: {
      backgroundColor: "#1976d2",
      color: "white",
      borderRadius: "8px",
      textTransform: "none",
      padding: "8px 16px",
      fontWeight: "bold",
    },
    outlined: {
      borderColor: "blue",
      color: "blue",
      borderRadius: "8px",
      textTransform: "none",
      padding: "8px 16px",
      fontWeight: "bold",
    },
    primary: {
      backgroundColor: "blue",
      color: "white",
      borderRadius: "8px",
      textTransform: "none",
      padding: "8px 16px",
      fontWeight: "bold",
    },
    secondary: {
      backgroundColor: "#d32f2f",
      color: "white",
      borderRadius: "8px",
      textTransform: "none",
      padding: "8px 16px",
      fontWeight: "bold",
    },
  };

  const headerStyles = {
    color: "#0d47a1",
    marginBottom: "2rem",
    fontWeight: "bold",
  };

  const messageStyles = {
    success: { color: "#2e7d32", marginBottom: "1rem", fontWeight: "bold" },
    error: { color: "#d32f2f", marginBottom: "1rem", fontWeight: "bold" },
  };

  return (
    <div style={pageStyles}>
      <Typography variant="h4" align="center" style={headerStyles}>
        Employees List
      </Typography>

      {message && (
        <Typography variant="h6" style={messageStyles.success}>
          {message}
        </Typography>
      )}

      {errorMessage && (
        <Typography variant="h6" style={messageStyles.error}>
          {errorMessage}
        </Typography>
      )}

      {userRole === "ROLE_SUPERADMIN" && (
        <div style={{ textAlign: "right", marginBottom: "1rem" }}>
          <Button variant="outlined" style={buttonStyles.outlined} onClick={() => history.push("/users")}>
            Users List
          </Button>
        </div>
      )}

      {isAdmin ? (
        <>
          <div style={{ display: "flex", gap: "1rem", marginBottom: "1rem" }}>
            <TextField label="Search" value={searchValue} onChange={e => setSearchValue(e.target.value)} />
          </div>

          <div style={tableContainerStyles}>
            <Table>
              <TableHead>
                <TableRow>
                  {EMPLOYEES_TABLE_COLUMNS.map((col) => (
                    <TableCell
                      key={col.key}
                      onClick={() => handleSort(col.key)}
                      style={tableHeaderStyles}
                    >
                      {col.label}
                    </TableCell>
                  ))}
                  <TableCell style={tableHeaderStyles}>Resume</TableCell>
                  <TableCell style={tableHeaderStyles}>Edit</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {employees
                  .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((employee) => (
                    <TableRow key={employee.id}>
                      {[
                        employee.id,
                        employee.name,
                        employee.gender,
                        employee.location,
                        employee.jobType,
                        employee.jobPosition,
                        employee.experienceYears,
                        employee.expectedSalary,
                        employee.availabilityDate,
                        employee.educationLevel,
                        employee.age,
                        employee.status
                      ].map((value, idx) => (
                        <TableCell key={idx} style={tableCellStyles}>
                          {value}
                        </TableCell>
                      ))}
                      <TableCell style={tableCellStyles}>
                        {employee.resume?.fileName ? (
                          <Button color="primary" onClick={() => handleResumeDownload(employee.id)}>
                            {employee.resume.fileName}
                          </Button>
                        ) : (
                          "No resume"
                        )}
                      </TableCell>
                      <TableCell style={tableCellStyles}>
                        <Button style={buttonStyles.contained} onClick={() => handleEditEmployee(employee.id)}>
                          {icons.edit}
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
              </TableBody>
            </Table>
          </div>

          <div style={{ marginTop: "1rem", display: "flex", gap: "1rem", justifyContent: "center" }}>
            <Button disabled={pageNumber === 0} style={buttonStyles.outlined} onClick={() => setPageNumber(pageNumber - 1)}>Previous</Button>
            <Button style={buttonStyles.outlined} onClick={() => setPageNumber(pageNumber + 1)}>Next</Button>
          </div>

          <div style={{ textAlign: "center", marginTop: "2rem" }}>
            <Button variant="contained" style={buttonStyles.contained} onClick={() => history.push("/employee-management")}>
              Add Employee
            </Button>
          </div>
        </>
      ) : (
        <>
          <div style={{ display: "flex", gap: "1rem", marginBottom: "1rem" }}>
            <TextField label="Search" value={searchValue} onChange={e => setSearchValue(e.target.value)} />
          </div>

          <div style={tableContainerStyles}>
            <Table>
              <TableHead>
                <TableRow>
                  {FILTERED_EMPLOYEES_TABLE_COLUMNS.map((col) => (
                    <TableCell
                      key={col.key}
                      onClick={() => handleSort(col.key)}
                      style={tableHeaderStyles}
                    >
                      {col.label}
                    </TableCell>
                  ))}
                  <TableCell style={tableHeaderStyles}>Resume</TableCell>
                  <TableCell style={tableHeaderStyles}>Add to Selection</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {employees
                  .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((employee) => (
                    <TableRow key={employee.id}>
                      {[
                        employee.id,
                        employee.name,
                        employee.gender,
                        employee.location,
                        employee.jobType,
                        employee.jobPosition,
                        employee.experienceYears,
                        employee.expectedSalary,
                        employee.availabilityDate,
                        employee.educationLevel,
                        employee.age
                      ].map((value, idx) => (
                        <TableCell key={idx} style={tableCellStyles}>
                          {value}
                        </TableCell>
                      ))}
                      <TableCell style={tableCellStyles}>
                        {employee.resume?.fileName ? (
                          <Button color="primary" onClick={() => handleResumeDownload(employee.id)}>
                            {employee.resume.fileName}
                          </Button>
                        ) : (
                          "No resume"
                        )}
                      </TableCell>
                      <TableCell style={tableCellStyles}>
                        <Button style={buttonStyles.contained} onClick={() => handleAddToSelection(employee.id)}>
                          {icons.plus}
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
              </TableBody>
            </Table>
          </div>

          <div style={{ marginTop: "1rem", display: "flex", gap: "1rem", justifyContent: "center" }}>
            <Button disabled={pageNumber === 0} style={buttonStyles.outlined} onClick={() => setPageNumber(pageNumber - 1)}>Previous</Button>
            <Button style={buttonStyles.outlined} onClick={() => setPageNumber(pageNumber + 1)}>Next</Button>
          </div>

          <Divider style={{ margin: "2rem 0" }} />
          <Typography variant="h5" align="center" style={headerStyles}>
            Selected Candidates
          </Typography>

          <div style={tableContainerStyles}>
            <Table>
              <TableHead>
                <TableRow>
                  {[
                    "Name", "Gender", "Location", "Job Type", "Job Position",
                    "Experience (Years)", "Expected Salary", "Availability Date",
                    "Education Level", "Age", "Resume", "Delete"
                  ].map((header, idx) => (
                    <TableCell key={idx} style={tableHeaderStyles}>
                      {header}
                    </TableCell>
                  ))}
                </TableRow>
              </TableHead>
              <TableBody>
                {selectedCandidates.map((candidate) => (
                  <TableRow key={candidate.id}>
                    {[
                      candidate.name,
                      candidate.gender,
                      candidate.location,
                      candidate.jobType,
                      candidate.jobPosition,
                      candidate.experienceYears,
                      candidate.expectedSalary,
                      candidate.availabilityDate,
                      candidate.educationLevel,
                      candidate.age
                    ].map((value, idx) => (
                      <TableCell key={idx} style={tableCellStyles}>
                        {value}
                      </TableCell>
                    ))}
                    <TableCell style={tableCellStyles}>
                      {candidate.resume?.fileName ? (
                        <Button color="primary" onClick={() => handleResumeDownload(candidate.id)}>
                          {candidate.resume.fileName}
                        </Button>
                      ) : "No resume"}
                    </TableCell>
                    <TableCell style={tableCellStyles}>
                      <Button style={buttonStyles.secondary} onClick={() => handleDeleteFromSelection(candidate.id)}>
                        {icons.cross}
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>

          <div style={{ textAlign: "center", marginTop: "2rem" }}>
            <Button
              variant="contained"
              style={buttonStyles.primary}
              onClick={handleSubmitSelection}
              disabled={selectedCandidates.length === 0}
            >
              Submit Final Selection
            </Button>

            <Button variant="outlined" style={{ ...buttonStyles.outlined, marginLeft: "1rem" }} onClick={handleBackToJobs}>
              Back To Job List
            </Button>
          </div>
        </>
      )}

      <div style={{ textAlign: "center", marginTop: "2rem" }}>
        <Button variant="outlined" style={buttonStyles.secondary} onClick={handleLogout}>
          Log Out
        </Button>
      </div>
    </div>
  );
}

export default EmployeesList;

