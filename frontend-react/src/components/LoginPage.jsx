import React, { useState } from "react";
import { Button, TextField, Typography, Box, Container, Paper } from "@material-ui/core";
import AuthenticationService from '../services/AuthenticationService';
import backgroundImage from '../images/login.jpg';

const LoginPage = (props) => {
  const [login, setLogin] = useState("");
  const [password, setPassword] = useState("");
  const [isForbiddenLogin, setIsForbiddenLogin] = useState(false);
  const [isForbiddenPassword, setIsForbiddenPassword] = useState(false);
  const [validErrors, setValidErrors] = useState([]);
  const [accessDeniedLoginErrors, setAccessDeniedLoginErrors] = useState("");
  const [accessDeniedPasswordErrors, setAccessDeniedPasswordErrors] = useState("");

  const register = () => {
    props.history.push(`/register`);
  };

  const changePassword = () => {
    props.history.push(`/change-password`);
  };

  const handleError = (err) => {
    const status = err.response?.status;
    const data = err.response?.data;

    switch (status) {
      case 400:
        const errorMessages = data.map(error => Object.values(error)[0]);
        setValidErrors(errorMessages.length > 0 ? errorMessages : ["Validation error"]);
        setAccessDeniedLoginErrors("");
        setAccessDeniedPasswordErrors("");
        break;

      case 404:
        setIsForbiddenLogin(true);
        setAccessDeniedLoginErrors(data?.info);
        setAccessDeniedPasswordErrors("");
        setValidErrors([]);
        break;

      case 409:
        setIsForbiddenPassword(true);
        setAccessDeniedPasswordErrors(err.response.data.info);
        setAccessDeniedLoginErrors("");
        setValidErrors([]);
        break;

      default:
        setValidErrors(["An unexpected error occurred."]);
        setAccessDeniedPasswordErrors("");
        setAccessDeniedLoginErrors("");
    }
  };

  const signIn = () => {
    AuthenticationService.login(login, password)
      .then((response) => {
        AuthenticationService.registerSuccessfulLoginForJwt(login, response.data.token);

        const role = response.data.role;

        sessionStorage.setItem("userName", response.data.username);
        sessionStorage.setItem("userRole", role);

        if (role === "ROLE_ADMIN" || role === "ROLE_SUPERADMIN") {
          props.history.push("/employees");
        } else {
          props.history.push("/jobs");
        }
      })
      .catch(handleError);
  };

  return (
    <Box style={{
      minHeight: "100vh",
      backgroundImage: `url(${backgroundImage})`,
      backgroundSize: "cover",
      backgroundPosition: "center",
      backgroundRepeat: "no-repeat",
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
      justifyContent: "center",
      padding: "16px",
    }}>
      <Typography variant="h3" style={{
        color: "#fff",
        fontWeight: "bold",
        marginBottom: "30px",
        textShadow: "2px 2px 8px rgba(0,0,0,0.6)"
      }}>
        Welcome to Employee Finder!
      </Typography>

      <Container maxWidth="xs">
        <Paper elevation={10} style={{
          padding: "32px",
          borderRadius: "16px",
          backgroundColor: "rgba(255, 255, 255, 0.95)",
          boxShadow: "0 8px 32px rgba(0, 0, 0, 0.25)",
        }}>
          <Typography variant="h5" align="center" gutterBottom style={{ fontWeight: "bold" }}>
            Login
          </Typography>

          {validErrors.length > 0 && validErrors.map((error, idx) => (
            <Typography key={idx} color="error" variant="body2" align="center">
              {error}
            </Typography>
          ))}
          {isForbiddenLogin && (
            <Typography color="error" variant="body2" align="center">
              {accessDeniedLoginErrors}
            </Typography>
          )}
          {isForbiddenPassword && (
            <Typography color="error" variant="body2" align="center">
              {accessDeniedPasswordErrors}
            </Typography>
          )}

          <Box mt={2}>
            <TextField
              label="Username"
              variant="outlined"
              fullWidth
              value={login}
              onChange={(e) => setLogin(e.target.value)}
              margin="normal"
            />
            <TextField
              label="Password"
              variant="outlined"
              type="password"
              fullWidth
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              margin="normal"
            />
          </Box>

          <Box mt={3}>
            <Button
              variant="contained"
              color="primary"
              fullWidth
              onClick={signIn}
              style={{ fontWeight: "bold", padding: "12px 0" }}
            >
              Sign In
            </Button>
          </Box>

          <Box display="flex" justifyContent="space-between" mt={2}>
            <Button color="primary" onClick={register} style={{ textTransform: "none" }}>
              Register
            </Button>
            <Button color="primary" onClick={changePassword} style={{ textTransform: "none" }}>
              Change Password
            </Button>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};

export default LoginPage;
