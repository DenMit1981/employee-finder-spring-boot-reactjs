import React, { useEffect, useState } from "react";
import {
  TextField, Button, Typography, Paper
} from "@material-ui/core";
import UserService from "../services/UserService";
import { useHistory } from "react-router-dom";
import { USERS_TABLE_COLUMNS } from "../constants/tablesColumns";
import backgroundImage from '../images/users.jpg';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
} from "@material-ui/core";

const UsersList = () => {
  const [users, setUsers] = useState([]);
  const [searchField, setSearchField] = useState("name");
  const [searchValue, setSearchValue] = useState("");
  const [sortField, setSortField] = useState("id");
  const [sortDirection, setSortDirection] = useState("asc");
  const [pageSize, setPageSize] = useState(10);
  const [pageNumber, setPageNumber] = useState(0);
  const [error, setError] = useState("");
  const history = useHistory();
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(25);

  const fetchUsers = async () => {
    try {
      const fields = [
        "id", "name", "email", "companyName", "regNumber", "jobTitle", "role"
      ];

      let response = null;

      if (searchValue && searchValue.trim() !== "") {
        for (let field of fields) {
          response = await UserService.getAllSortedUsersSearched(
            field,
            searchValue,
            sortField,
            pageSize,
            pageNumber,
            sortDirection
          );
          if (response.data.length > 0) break;
        }

        if (!response || response.data.length === 0) {
          setUsers([]);
          setError("Nothing found");
          return;
        }
      } else {
        response = await UserService.getAllSortedUsersByPages(
          sortField,
          sortDirection,
          pageSize,
          pageNumber
        );
      }

      setUsers(response.data);
      setError("");
    } catch (err) {
      console.error("Error fetching employees (admin)", err);
      setError(err.response.data.info);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [searchField, searchValue, sortField, sortDirection, pageSize, pageNumber]);

  const handleSort = (field) => {
    if (sortField === field) {
      setSortDirection((prev) => (prev === "asc" ? "desc" : "asc"));
    } else {
      setSortField(field);
      setSortDirection("asc");
    }
    setPageNumber(0);
  };

  const handleDelete = async (id) => {
    try {
      await UserService.deleteUserById(id);
      fetchUsers();
    } catch (err) {
      console.error("Delete failed", err);
      setError(err.response.data.info);
    }
  };

  const handleRoleChange = async (id, newRole) => {
    try {
      await UserService.updateUserRole(id, newRole);
      fetchUsers();
    } catch (err) {
      console.error("Role update failed", err);
      setError(err.response.data.info);
    }
  };

  const getNextRole = (currentRole) => {
    const roles = ['ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN'];
    const currentIndex = roles.indexOf(currentRole);
    return roles[(currentIndex + 1) % roles.length];
  };

  const headerCellStyle = {
    fontWeight: "bold",
    textAlign: "center",
    fontSize: "1rem",
    color: "#333",
    borderRight: "1px solid #ccc",
    padding: "12px 8px",
    cursor: "pointer",
    userSelect: "none"
  };

  return (
    <div
      style={{
        width: "100%",
        backgroundImage: `url(${backgroundImage})`,
        backgroundSize: "cover",
        minHeight: "100vh",
        padding: "2rem",
        backdropFilter: "brightness(0.8)",
        backgroundPosition: "center",
        backgroundRepeat: "no-repeat",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <Paper
        elevation={6}
        style={{
          padding: "2rem",
          maxWidth: "1200px",
          margin: "auto",
          backgroundColor: "rgba(255, 255, 255, 0.9)",
          borderRadius: "1rem",
        }}
      >
        <Typography variant="h4" gutterBottom align="center" style={{ fontWeight: "bold" }}>
          Users List
        </Typography>

        <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "1.5rem" }}>
          <Button
            variant="contained"
            color="primary"
            onClick={() => history.push("/employees")}
            style={{ borderRadius: "2rem", fontWeight: "bold" }}
          >
            Employees List
          </Button>
          <Button
            variant="contained"
            color="secondary"
            onClick={() => history.push("/")}
            style={{ borderRadius: "2rem", fontWeight: "bold" }}
          >
            Logout
          </Button>
        </div>

        <div style={{ display: "flex", gap: "1rem", marginBottom: "1.5rem" }}>
          <TextField
            label="Search"
            variant="outlined"
            fullWidth
            value={searchValue}
            onChange={e => setSearchValue(e.target.value)}
          />
        </div>

        {error && <Typography color="error">{error}</Typography>}

        <Paper style={{ marginBottom: "2rem", overflowX: "auto", borderRadius: "1rem" }}>
          <Table>
            <TableHead>
              <TableRow style={{ backgroundColor: "#1976d2" }}>
                {USERS_TABLE_COLUMNS.map((col) => (
                  <TableCell
                    key={col.key}
                    onClick={() => handleSort(col.id)}
                    style={{ ...headerCellStyle, color: "white" }}
                  >
                    {col.label}
                  </TableCell>
                ))}
                <TableCell style={{ ...headerCellStyle, color: "white" }}>Actions</TableCell>
              </TableRow>
            </TableHead>

            <TableBody>
              {users.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={USERS_TABLE_COLUMNS.length + 1} align="center">
                    No users found
                  </TableCell>
                </TableRow>
              ) : (
                users
                  .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((user) => (
                    <TableRow key={user.id} hover>
                      {[user.id, user.name, user.email, user.companyName, user.regNumber, user.jobTitle, user.role].map(
                        (value, idx) => (
                          <TableCell key={idx} style={{ textAlign: "center" }}>
                            {value}
                          </TableCell>
                        )
                      )}
                      <TableCell style={{ textAlign: "center" }}>
                        <Button
                          color="secondary"
                          variant="outlined"
                          size="small"
                          onClick={() => handleDelete(user.id)}
                          style={{ marginRight: "0.5rem", borderRadius: "2rem" }}
                        >
                          Delete
                        </Button>
                        <Button
                          variant="contained"
                          color="primary"
                          size="small"
                          onClick={() => handleRoleChange(user.id, getNextRole(user.role))}
                          style={{ borderRadius: "2rem" }}
                        >
                          Toggle Role
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))
              )}
            </TableBody>
          </Table>
        </Paper>

        <div style={{ marginTop: "1rem", display: "flex", justifyContent: "center", gap: "1rem" }}>
          <Button
            variant="outlined"
            disabled={pageNumber === 0}
            onClick={() => setPageNumber(pageNumber - 1)}
            style={{ borderRadius: "2rem", padding: "0.5rem 1.5rem", fontWeight: "bold" }}
          >
            Previous
          </Button>
          <Button
            variant="outlined"
            onClick={() => setPageNumber(pageNumber + 1)}
            style={{ borderRadius: "2rem", padding: "0.5rem 1.5rem", fontWeight: "bold" }}
          >
            Next
          </Button>
        </div>
      </Paper>
    </div>
  );
};

export default UsersList;