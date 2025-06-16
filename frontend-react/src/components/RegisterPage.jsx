import React, { useState } from "react";
import { Button, TextField, Typography, Select, MenuItem, InputLabel, FormControl as MUIFormControl } from "@material-ui/core";
import AuthenticationService from "../services/AuthenticationService";
import { JOB_TITLES } from "../constants/inputsValues";
import backgroundImage from '../images/register.jpg';

const RegisterPage = (props) => {
  const [name, setName] = useState("");
  const [companyName, setCompanyName] = useState("");
  const [regNumber, setRegNumber] = useState("");
  const [jobTitle, setJobTitle] = useState("HR_MANAGER");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [email, setEmail] = useState("");
  const [registerValidErrors, setRegisterValidErrors] = useState([]);
  const [isUserPresent, setIsUserPresent] = useState(false);
  const [userIsPresentErrors, setUserIsPresentErrors] = useState("");

  const handleError = (err) => {
    const status = err.response?.status;
    const data = err.response?.data;

    switch (status) {
      case 400:
        const errorMessages = data.map(error => Object.values(error)[0]);
        setRegisterValidErrors(errorMessages.length > 0 ? errorMessages : ["Validation error"]);
        setUserIsPresentErrors("");
        break;

      case 409:
        setIsUserPresent(true);
        setUserIsPresentErrors(data?.info || "User already exists.");
        setRegisterValidErrors([]);
        break;

      default:
        setRegisterValidErrors(["An unexpected error occurred."]);
        setUserIsPresentErrors("");
    }
  };

  const signUp = () => {

    const userDto = {
      name,
      company: {
        companyName,
        regNumber
      },
      jobTitle,
      password,
      confirmPassword,
      email
    };

    AuthenticationService.registration(userDto)
      .then(() => props.history.push("/"))
      .catch(handleError);
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        backgroundImage: `url(${backgroundImage})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundRepeat: "no-repeat",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        padding: "20px",
      }}
    >
      <div
        style={{
          backgroundColor: "rgba(255, 255, 255, 0.9)",
          padding: "30px",
          borderRadius: "12px",
          width: "350px",
          boxShadow: "0 8px 32px rgba(0,0,0,0.25)",
        }}
      >
        <Typography
          variant="h5"
          align="center"
          gutterBottom
          style={{ fontWeight: "bold", color: "#3f51b5" }}
        >
          Register for Employee Finder!
        </Typography>

        {isUserPresent && <Typography color="error">{userIsPresentErrors}</Typography>}
        {registerValidErrors.length > 0 && (
          <div>
            <ol>
              {registerValidErrors.map((error, index) => (
                <li key={index} style={{ color: "red" }}>{error}</li>
              ))}
            </ol>
          </div>
        )}

        <form>
          <TextField
            fullWidth
            margin="dense"
            label="User Name"
            variant="outlined"
            placeholder="Enter your name"
            onChange={(e) => setName(e.target.value)}
          />

          <TextField
            fullWidth
            margin="dense"
            label="Company Name"
            variant="outlined"
            placeholder="Enter company name"
            onChange={(e) => setCompanyName(e.target.value)}
          />

          <TextField
            fullWidth
            margin="dense"
            label="Reg Number"
            variant="outlined"
            placeholder="Enter registration number"
            onChange={(e) => setRegNumber(e.target.value)}
          />

          <MUIFormControl variant="outlined" fullWidth margin="dense">
            <InputLabel>Job Title</InputLabel>
            <Select
              value={jobTitle}
              onChange={(e) => setJobTitle(e.target.value)}
              label="Job Title"
            >
              {JOB_TITLES.map((job) => (
                <MenuItem key={job.value} value={job.value}>{job.label}</MenuItem>
              ))}
            </Select>
          </MUIFormControl>

          <TextField
            fullWidth
            margin="dense"
            type="password"
            label="Password"
            variant="outlined"
            placeholder="Enter your password"
            onChange={(e) => setPassword(e.target.value)}
          />

          <TextField
            fullWidth
            margin="dense"
            type="password"
            label="Confirm Password"
            variant="outlined"
            placeholder="Confirm your password"
            onChange={(e) => setConfirmPassword(e.target.value)}
          />

          <TextField
            fullWidth
            margin="dense"
            label="Email"
            variant="outlined"
            placeholder="Enter your email"
            onChange={(e) => setEmail(e.target.value)}
          />

          <Button
            fullWidth
            size="large"
            variant="contained"
            color="primary"
            style={{ marginTop: "15px" }}
            onClick={signUp}
          >
            Sign Up
          </Button>

          <Button
            fullWidth
            size="large"
            variant="contained"
            color="secondary"
            style={{ marginTop: "15px" }}
            onClick={() => props.history.push("/")}
          >
            Cancel
          </Button>
        </form>
      </div>
    </div>
  );
}

export default RegisterPage;