import React, { useState } from "react";
import { Button, TextField, Typography } from "@material-ui/core";
import AuthenticationService from '../services/AuthenticationService';
import backgroundImage from '../images/change-password.jpg';

const PasswordChangePage = (props) => {
  const [login, setLogin] = useState("");
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [validErrors, setValidErrors] = useState([]);
  const [changePasswordError, setChangePasswordError] = useState("");

  const handleError = (err) => {
    const status = err.response?.status;
    const data = err.response?.data;

    switch (status) {
      case 400:
        const errorMessages = data.map(error => Object.values(error)[0]);
        setValidErrors(errorMessages.length > 0 ? errorMessages : ["Validation error"]);
        setChangePasswordError("");
        break;

      case 409:
        setChangePasswordError(data?.info || "You are not authorized to change the password.");
        setValidErrors([]);
        break;

      case 404:
        setChangePasswordError(data?.info);
        setValidErrors([]);
        break;

      default:
        setValidErrors(["An unexpected error occurred."]);
        setChangePasswordError("");
    }
  };

  const changePassword = () => {

    const userDto = {
      login,
      currentPassword,
      newPassword,
      confirmPassword
    };

    AuthenticationService.changePassword(userDto)
      .then(() => {
        props.history.push("/");
      })
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
        justifyContent: "center",
        alignItems: "center",
        padding: "20px",
      }}
    >
      <div
        style={{
          backgroundColor: "rgba(255, 255, 255, 0.95)",
          padding: "30px",
          borderRadius: "20px",
          boxShadow: "0 8px 32px rgba(0, 0, 0, 0.25)",
          maxWidth: "350px",
          width: "100%",
          backdropFilter: "blur(5px)",
        }}
      >
        <div className="container__title-wrapper" style={{ marginBottom: "20px" }}>
          <Typography component="h2" variant="h4" align="center">
            Change Password
          </Typography>
        </div>

        {changePasswordError && (
          <Typography className="has-error" component="h6" variant="h6" align="center" color="error" style={{ marginBottom: "20px" }}>
            {changePasswordError}
          </Typography>
        )}

        <div className="container__form-wrapper">
          <form>
            <div className="form__input-wrapper" style={{ marginBottom: "15px" }}>
              <Typography component="h6" variant="subtitle1">
                Login
              </Typography>
              <TextField
                fullWidth
                margin="dense"
                onChange={(e) => setLogin(e.target.value)}
                label="Email"
                variant="outlined"
                placeholder="Enter your email"
              />
            </div>

            <div className="form__input-wrapper" style={{ marginBottom: "15px" }}>
              <Typography component="h6" variant="subtitle1">
                Current Password
              </Typography>
              <TextField
                fullWidth
                margin="dense"
                onChange={(e) => setCurrentPassword(e.target.value)}
                label="Current Password"
                variant="outlined"
                type="password"
                placeholder="Enter your current password"
              />
            </div>

            <div className="form__input-wrapper" style={{ marginBottom: "15px" }}>
              <Typography component="h6" variant="subtitle1">
                New Password
              </Typography>
              <TextField
                fullWidth
                margin="dense"
                onChange={(e) => setNewPassword(e.target.value)}
                label="New Password"
                variant="outlined"
                type="password"
                placeholder="Enter your new password"
              />
            </div>

            <div className="form__input-wrapper" style={{ marginBottom: "15px" }}>
              <Typography component="h6" variant="subtitle1">
                Confirm New Password
              </Typography>
              <TextField
                fullWidth
                margin="dense"
                onChange={(e) => setConfirmPassword(e.target.value)}
                label="Confirm New Password"
                variant="outlined"
                type="password"
                placeholder="Confirm your new password"
              />
            </div>
          </form>
        </div>

        <div className="container__button-wrapper" style={{ textAlign: "center", marginTop: "15px" }}>
          <Button size="large" variant="contained" color="primary" onClick={changePassword}>
            Change Password
          </Button>
        </div>

        <div className="container__button-wrapper" style={{ textAlign: "center", marginTop: "15px" }}>
          <Button size="large" variant="contained" color="secondary" onClick={() => props.history.push("/")}>
            Cancel
          </Button>
        </div>

        {validErrors.length > 0 && (
          <div className="has-error" style={{ marginTop: "15px", color: "red" }}>
            <ol>
              {validErrors.map((error, index) => (
                <li key={index}>{error}</li>
              ))}
            </ol>
          </div>
        )}
      </div>
    </div>
  );
};

export default PasswordChangePage;