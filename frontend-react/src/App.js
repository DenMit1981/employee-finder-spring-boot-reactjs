import "./App.css";
import LoginPage from "./components/LoginPage";
import RegisterPage from "./components/RegisterPage";
import PasswordChangePage from "./components/PasswordChangePage.jsx";
import JobsList from "./components/JobsList";
import EmployeesList from "./components/EmployeesList";
import EmployeeManagement from "./components/EmployeeManagement";
import SelectionDetails from "./components/SelectionDetails";
import UsersList from "./components/UsersList";
import AuthenticationService from "./services/AuthenticationService";

import React, { useEffect } from "react";

import {
  BrowserRouter as Router,
  Route,
  Switch,
} from "react-router-dom";

function App() {
  useEffect(() => {
    AuthenticationService.initializeAxiosWithToken();
  }, []);

  return (
    <Router>
      <Switch>
        <Route path="/" exact component={LoginPage} />
        <Route path="/register" exact component={RegisterPage} />
        <Route path="/change-password" exact component={PasswordChangePage} />
        <Route path="/jobs" component={JobsList} />
        <Route path="/employees" component={EmployeesList} />
        <Route path="/employee-management" component={EmployeeManagement} />
        <Route path="/selections/:id" component={SelectionDetails} />
        <Route path="/users" component={UsersList} />
      </Switch>
    </Router>
  );
}

export default App;
