import React, { useEffect, useState } from "react";
import JobService from "../services/JobService";
import { useHistory } from "react-router-dom";
import { Grid, Typography, Button, Modal, TextField, Select, MenuItem, InputLabel, FormControl, Backdrop, Fade, } from "@material-ui/core";
import { GENDERS, LOCATIONS, EDUCATION_LEVELS } from "../constants/inputsValues";
import "../App.css";
import backgroundImage from '../images/jobs-list.jpg';

const JobsList = () => {
  const [jobData, setJobData] = useState([]);
  const [selectedGender, setSelectedGender] = useState("");
  const [selectedLocation, setSelectedLocation] = useState("");
  const [filterOpen, setFilterOpen] = useState(false);
  const [filterData, setFilterData] = useState({
    name: "",
    gender: "",
    location: "",
    jobType: "",
    jobPosition: "",
    experienceYears: "",
    expectedSalary: "",
    availabilityDate: "",
    educationLevel: "",
    age: ""
  });

  const [jobTypes, setJobTypes] = useState([]);
  const [jobPositions, setJobPositions] = useState([]);

  const history = useHistory();

  useEffect(() => {
    fetchJobData();
  }, [selectedGender, selectedLocation]);

  const fetchJobData = async () => {
    try {
      let response;

      if (selectedGender && selectedLocation) {
        response = await JobService.getJobTypesWithPositionsByGenderAndLocation(
          selectedGender,
          selectedLocation
        );
      } else if (selectedGender) {
        response = await JobService.getJobTypesWithPositionsByGender(selectedGender);
      } else if (selectedLocation) {
        response = await JobService.getJobTypesWithPositionsByLocation(selectedLocation);
      } else {
        response = await JobService.getAllJobTypesWithPositions();
      }

      setJobData(response.data);
    } catch (error) {
      console.error("Failed to fetch job data", error);
    }
  };

  useEffect(() => {
    JobService.getJobTypesFromNewAndSelectedEmployees()
      .then((res) => {
        setJobTypes(res.data);
      })
      .catch((err) => {
        console.error("Error fetching job types", err);
      });
  }, []);

  useEffect(() => {
    if (filterData.jobType) {
      JobService.getJobPositionsByJobTypeIdFromNewAndSelectedEmployees(filterData.jobType)
        .then((res) => {
          setJobPositions(res.data);
        })
        .catch((err) => {
          console.error("Error fetching job positions", err);
        });
    } else {
      JobService.getAllJobPositionsFromNewAndSelectedEmployees()
        .then((res) => {
          setJobPositions(res.data);
        })
    }
  }, [filterData.jobType, filterData]);


  const handlePositionClick = (jobTypeId, positionId) => {
    const encodedPositionId = encodeURIComponent(positionId);
    const encodedGender = encodeURIComponent(selectedGender || "");
    const encodedLocation = encodeURIComponent(selectedLocation || "");

    const filterUrl = `/employees?jobTypeId=${jobTypeId}&jobPositionId=${encodedPositionId}&gender=${encodedGender}&location=${encodedLocation}`;
    history.push(filterUrl);
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilterData((prev) => ({ ...prev, [name]: value }));
  };

  const handleApplyFilter = () => {
    const params = new URLSearchParams();

    if (filterData.name) params.set("name", filterData.name);
    if (filterData.jobType) params.set("jobTypeId", filterData.jobType);
    if (filterData.jobPosition) params.set("jobPositionId", filterData.jobPosition);
    if (filterData.experienceYears) params.set("experienceYears", filterData.experienceYears);
    if (filterData.gender) params.set("gender", filterData.gender);
    if (filterData.location) params.set("location", filterData.location);
    if (filterData.educationLevel) params.set("educationLevel", filterData.educationLevel);
    if (filterData.age) params.set("age", filterData.age);
    if (filterData.expectedSalary) params.set("expectedSalary", filterData.expectedSalary);
    if (filterData.availabilityDate) params.set("availabilityDate", filterData.availabilityDate);

    history.push(`/employees?${params.toString()}`);
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        backgroundImage: `url(${backgroundImage})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundRepeat: "no-repeat",
        padding: "2rem",
        display: "flex",
        justifyContent: "center",
        alignItems: "flex-start",
      }}
    >
      <div
        style={{
          width: "50%",
          background: "rgba(255, 255, 255, 0.7)",
          padding: "2rem",
          borderRadius: "12px",
          boxShadow: "0 4px 20px rgba(0, 0, 0, 0.15)",
          backdropFilter: "blur(8px)",
          WebkitBackdropFilter: "blur(8px)",
          overflow: "hidden",
        }}
      >
        <Typography component="h6" variant="h4" align="center" gutterBottom>
          Jobs List
        </Typography>

        <div className="filters" style={{ display: "flex", gap: "1rem", marginBottom: "2rem", flexWrap: "wrap", justifyContent: "center" }}>
          <Button
            variant="outlined"
            color="primary"
            onClick={() => setFilterOpen(true)}
          >
            Filter
          </Button>

          <select
            value={selectedGender}
            onChange={(e) => setSelectedGender(e.target.value)}
            style={{ padding: "0.5rem", borderRadius: "6px", border: "1px solid #ccc" }}
          >
            <option value="">Select Gender</option>
            {GENDERS.map((g) => (
              <option key={g.value} value={g.value}>
                {g.label}
              </option>
            ))}
          </select>

          <select
            value={selectedLocation}
            onChange={(e) => setSelectedLocation(e.target.value)}
            style={{ padding: "0.5rem", borderRadius: "6px", border: "1px solid #ccc" }}
          >
            <option value="">Select Location</option>
            {LOCATIONS.map((l) => (
              <option key={l.value} value={l.value}>
                {l.label}
              </option>
            ))}
          </select>

          <Button
            variant="outlined"
            color="secondary"
            onClick={() => history.push("/")}
          >
            Logout
          </Button>
        </div>

        <div className="job-list">
          {jobData.length === 0 ? (
            <Typography align="center">No jobs found</Typography>
          ) : (
            jobData.map((jobTypeItem) => (
              <div key={jobTypeItem.jobType.id} className="job-type-block" style={{ marginBottom: "2rem" }}>
                <Typography variant="h5" color="primary">{jobTypeItem.jobType.name}</Typography>
                <ul style={{ listStyle: "none", paddingLeft: 0 }}>
                  {jobTypeItem.positions.map((position) => (
                    <li
                      key={position.id}
                      style={{
                        cursor: "pointer",
                        color: "#1976d2",
                        marginTop: "0.5rem",
                        fontWeight: "500"
                      }}
                      onClick={() => handlePositionClick(jobTypeItem.jobType.id, position.id)}
                    >
                      {position.name}
                    </li>
                  ))}
                </ul>
              </div>
            ))
          )}
        </div>

        <Modal
          open={filterOpen}
          onClose={() => setFilterOpen(false)}
          closeAfterTransition
          BackdropComponent={Backdrop}
          BackdropProps={{ timeout: 500 }}
        >
          <Fade in={filterOpen}>
            <div
              style={{
                background: "rgba(255, 255, 255, 0.9)",
                backdropFilter: "blur(10px)",
                WebkitBackdropFilter: "blur(10px)",
                padding: "1rem",
                margin: "5% auto",
                width: "90%",
                maxWidth: "420px",
                borderRadius: "12px",
                outline: "none",
                maxHeight: "80vh",
                overflowY: "auto",
                boxShadow: "0 8px 32px rgba(0, 0, 0, 0.25)",
              }}
            >
              <Typography variant="h6" align="center" gutterBottom style={{ fontWeight: "bold", color: "#333" }}>
                Advanced Filter
              </Typography>

              <Grid container spacing={1}>
                <Grid item xs={12}>
                  <TextField
                    label="Name"
                    name="name"
                    value={filterData.name}
                    onChange={handleFilterChange}
                    fullWidth
                    margin="dense"
                    variant="outlined"
                    size="small"
                    InputProps={{
                      style: {
                        background: "rgba(255,255,255,0.9)",
                        borderRadius: 8,
                      }
                    }}
                  />
                </Grid>

                <Grid item xs={6}>
                  <FormControl fullWidth variant="outlined" size="small">
                    <InputLabel>Gender</InputLabel>
                    <Select
                      name="gender"
                      value={filterData.gender}
                      onChange={handleFilterChange}
                      label="Gender"
                      style={{
                        background: "rgba(255,255,255,0.9)",
                        borderRadius: 8,
                      }}
                    >
                      {GENDERS.map((g) => (
                        <MenuItem key={g.value} value={g.value}>
                          {g.label}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={6}>
                  <FormControl fullWidth variant="outlined" size="small">
                    <InputLabel>Location</InputLabel>
                    <Select
                      name="location"
                      value={filterData.location}
                      onChange={handleFilterChange}
                      label="Location"
                      style={{
                        background: "rgba(255,255,255,0.9)",
                        borderRadius: 8,
                      }}
                    >
                      {LOCATIONS.map((l) => (
                        <MenuItem key={l.value} value={l.value}>
                          {l.label}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={6}>
                  <FormControl fullWidth variant="outlined" size="small">
                    <InputLabel>Job Type</InputLabel>
                    <Select
                      name="jobType"
                      value={filterData.jobType}
                      onChange={handleFilterChange}
                      label="Job Type"
                      style={{
                        background: "rgba(255,255,255,0.9)",
                        borderRadius: 8,
                      }}
                    >
                      {jobTypes.map((j) => (
                        <MenuItem key={j.id} value={j.id}>
                          {j.name.replace("_", " ")}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={6}>
                  <FormControl fullWidth variant="outlined" size="small">
                    <InputLabel>Job Position</InputLabel>
                    <Select
                      name="jobPosition"
                      value={filterData.jobPosition}
                      onChange={handleFilterChange}
                      label="Job Position"
                    >
                      {jobPositions.map((position) => (
                        <MenuItem key={position.id} value={position.id}>
                          {position.name}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={6}>
                  <TextField
                    label="Experience Years"
                    name="experienceYears"
                    value={filterData.experienceYears}
                    onChange={handleFilterChange}
                    fullWidth
                    margin="dense"
                    variant="outlined"
                    size="small"
                    InputProps={{
                      style: {
                        background: "rgba(255,255,255,0.9)",
                        borderRadius: 8,
                      }
                    }}
                  />
                </Grid>

                <Grid item xs={6}>
                  <TextField
                    label="Expected Salary"
                    name="expectedSalary"
                    value={filterData.expectedSalary}
                    onChange={handleFilterChange}
                    fullWidth
                    margin="dense"
                    variant="outlined"
                    size="small"
                    InputProps={{
                      style: {
                        background: "rgba(255,255,255,0.9)",
                        borderRadius: 8,
                      }
                    }}
                  />
                </Grid>

                <Grid item xs={6}>
                  <TextField
                    type="date"
                    label="Availability Date"
                    name="availabilityDate"
                    value={filterData.availabilityDate}
                    onChange={handleFilterChange}
                    InputLabelProps={{ shrink: true }}
                    fullWidth
                    margin="dense"
                    variant="outlined"
                    size="small"
                    InputProps={{
                      style: {
                        background: "rgba(255,255,255,0.9)",
                        borderRadius: 8,
                      }
                    }}
                  />
                </Grid>

                <Grid item xs={6}>
                  <FormControl fullWidth variant="outlined" size="small">
                    <InputLabel>Education Level</InputLabel>
                    <Select
                      name="educationLevel"
                      value={filterData.educationLevel}
                      onChange={handleFilterChange}
                      label="Education Level"
                      style={{
                        background: "rgba(255,255,255,0.9)",
                        borderRadius: 8,
                      }}
                    >
                      {EDUCATION_LEVELS.map((e) => (
                        <MenuItem key={e.value} value={e.value}>
                          {e.label}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={6}>
                  <TextField
                    label="Age"
                    name="age"
                    value={filterData.age}
                    onChange={handleFilterChange}
                    fullWidth
                    margin="dense"
                    variant="outlined"
                    size="small"
                    InputProps={{
                      style: {
                        background: "rgba(255,255,255,0.9)",
                        borderRadius: 8,
                      }
                    }}
                  />
                </Grid>
              </Grid>

              <div style={{ marginTop: "1rem", textAlign: "center", display: "flex", justifyContent: "center", gap: "1rem" }}>
                <Button
                  variant="contained"
                  color="primary"
                  onClick={handleApplyFilter}
                  style={{
                    padding: "0.5rem 1.5rem",
                    fontSize: "0.9rem",
                    borderRadius: "8px",
                    boxShadow: "0px 4px 10px rgba(0, 0, 0, 0.25)",
                  }}
                >
                  Apply Filter
                </Button>
                <Button
                  variant="outlined"
                  color="default"
                  onClick={() => history.push("/employees")}
                  style={{
                    padding: "0.5rem 1.5rem",
                    fontSize: "0.9rem",
                    borderRadius: "8px"
                  }}
                >
                  Show All
                </Button>
              </div>
            </div>
          </Fade>
        </Modal>
      </div>
    </div>
  );
};

export default JobsList;

