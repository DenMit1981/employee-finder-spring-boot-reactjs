import React, { useEffect, useState, useRef } from "react";
import { useParams, useHistory } from "react-router-dom";
import SelectionService from "../services/SelectionService";
import ResumeService from "../services/ResumeService";
import { CANDIDATES_TABLE_COLUMNS } from "../constants/tablesColumns";
import backgroundImage from '../images/selection.jpg';
import {
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Button,
  TextField
} from "@material-ui/core";
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

const SelectionDetails = () => {
  const { id } = useParams();
  const history = useHistory();
  const [selection, setSelection] = useState({ candidates: [] });
  const [searchField, setSearchField] = useState("name");
  const [searchValue, setSearchValue] = useState("");
  const [sortField, setSortField] = useState("id");
  const [sortDirection, setSortDirection] = useState("asc");
  const [pageSize, setPageSize] = useState(10);
  const [pageNumber, setPageNumber] = useState(0);
  const [error, setError] = useState("");
  const printRef = useRef();
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(25);

  const fetchSelection = async () => {
    try {
      const fields = ["id", "name", "gender", "location", "jobType", "jobPosition"];

      let response = null;

      if (searchValue && searchValue.trim() !== "") {
        for (let field of fields) {
          response = await SelectionService.getSelectionByIdSearched(
            id,
            field,
            searchValue,
            sortField,
            sortDirection,
            pageSize,
            pageNumber
          );
          if (response.data.candidates && response.data.candidates.length > 0) {
            break;
          }
        }

        if (!response || response.data.candidates.length === 0) {
          setSelection({
            candidates: [],
            ...response?.data,
          });
          setError("Nothing found.");
          return;
        }
      } else {
        response = await SelectionService.getSelectionById(
          id,
          sortField,
          sortDirection,
          pageSize,
          pageNumber
        );
      }

      setSelection(response.data);
      setError("");
    } catch (err) {
      console.error("Error fetching selection:", err);
      setError(err.response?.data?.info || "Failed to fetch selection.");
    }
  };

  useEffect(() => {
    fetchSelection();
  }, [
    id,
    searchField,
    searchValue,
    sortField,
    sortDirection,
    pageSize,
    pageNumber
  ]);

  const handleSort = (field) => {
    if (sortField === field) {
      setSortDirection((prev) => (prev === "asc" ? "desc" : "asc"));
    } else {
      setSortField(field);
      setSortDirection("asc");
    }
    setPageNumber(0);
  };

  const handleResumeDownload = async (employeeId) => {
    try {
      const response = await ResumeService.downloadResume(employeeId);
      const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `resume_${employeeId}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error('Error downloading resume:', err);
      alert('Failed to download resume.');
    }
  };

  const handleExportToPDF = () => {
    const doc = new jsPDF();
    doc.setFontSize(16);
    doc.text(`Selection Details (ID: ${selection.id})`, 14, 20);

    const tableColumn = [
      "Name",
      "Gender",
      "Location",
      "Job Type",
      "Job Position",
      "Resume",
    ];

    const tableRows = selection.candidates.map((c) => [
      c.name,
      c.gender,
      c.location,
      c.jobType,
      c.jobPosition,
      c.resume?.fileName || "No resume",
    ]);

    autoTable(doc, {
      head: [tableColumn],
      body: tableRows,
      startY: 30,
    });

    doc.save(`selection_${selection.id}.pdf`);
  };

  const handlePrint = () => {
    const printContent = printRef.current.innerHTML;
    const win = window.open("", "", "width=800,height=600");
    win.document.write(`
      <html>
        <head>
          <title>Print Selection</title>
          <style>
            table {
              width: 100%;
              border-collapse: collapse;
              margin-top: 20px;
            }
            th, td {
              border: 1px solid #999;
              padding: 8px;
              text-align: left;
            }
            h1 {
              text-align: center;
            }
          </style>
        </head>
        <body>
          <h1>Selection Details (ID: ${selection.id})</h1>
          ${printContent}
        </body>
      </html>
    `);
    win.document.close();
    win.print();
  };

  const handleLogout = () => {
    history.push("/");
  };

  const handleBack = () => {
    history.push("/jobs");
  };

  const buttonStyle = (color) => ({
    backgroundColor: color,
    color: "#fff",
    padding: "0.5rem 1rem",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontWeight: "bold",
    boxShadow: "0 4px 6px rgba(0,0,0,0.1)",
    transition: "background-color 0.3s",
  });

  const headerCellStyle = {
    fontWeight: "bold",
    backgroundColor: "#f5f5f5",
    cursor: "pointer",
    textAlign: "center",
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        background: `url('${backgroundImage}')`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundRepeat: "no-repeat",
        display: "flex",
        justifyContent: "center",
        alignItems: "start",
        padding: "40px 20px",
        position: "relative",
      }}
    >
      <div
        style={{
          backgroundColor: "rgba(255, 255, 255, 0.7)",
          backdropFilter: "blur(5px)",
          padding: "20px",
          borderRadius: "8px",
          boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
        }}
      >
        <div style={{ maxWidth: "1100px", width: "100%", padding: "2rem", borderRadius: "12px", boxShadow: "0 8px 24px rgba(0,0,0,0.15)" }}>
          <Typography variant="h4" align="center" gutterBottom style={{ fontWeight: "bold", color: "#333" }}>
            Selection Details:
          </Typography>

          <Typography variant="h5" align="center" gutterBottom>
            Company: {selection.companyName}
          </Typography>

          <Typography variant="h5" align="center" gutterBottom>
            Reg Number: {selection.regNumber}
          </Typography>

          <div
            style={{
              display: "flex",
              justifyContent: "center",
              flexWrap: "wrap",
              gap: "1rem",
              marginBottom: "2rem",
            }}
          >
            <button onClick={handleExportToPDF} style={buttonStyle("#1976d2")}>
              Export to PDF
            </button>
            <button onClick={handlePrint} style={buttonStyle("#4caf50")}>
              Print
            </button>
            <button onClick={handleBack} style={buttonStyle("#9c27b0")}>
              Back to Jobs
            </button>
            <button onClick={handleLogout} style={buttonStyle("#f44336")}>
              Logout
            </button>
          </div>

          <div style={{ marginBottom: "1.5rem", display: "flex", justifyContent: "center" }}>
            <TextField
              label="Search"
              value={searchValue}
              onChange={(e) => setSearchValue(e.target.value)}
              variant="outlined"
              size="small"
              style={{ width: "300px" }}
            />
          </div>

          {error && (
            <Typography color="error" align="center" style={{ marginBottom: "1rem" }}>
              {error}
            </Typography>
          )}

          <div ref={printRef}>
            <Paper elevation={3} style={{ marginBottom: "2rem", overflowX: "auto" }}>
              <Table>
                <TableHead>
                  <TableRow>
                    {CANDIDATES_TABLE_COLUMNS.map((col) => (
                      <TableCell
                        key={col.key}
                        onClick={() => handleSort(col.key)}
                        className="table-header-cell"
                        style={headerCellStyle}
                      >
                        {col.label}
                      </TableCell>
                    ))}
                    <TableCell style={headerCellStyle}>Resume</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {selection.candidates.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={CANDIDATES_TABLE_COLUMNS.length + 1} align="center">
                        No candidates found
                      </TableCell>
                    </TableRow>
                  ) : (
                    selection.candidates
                      .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                      .map((candidate) => (
                        <TableRow key={candidate.id} hover>
                          {[candidate.id, candidate.name, candidate.gender, candidate.location, candidate.jobType, candidate.jobPosition].map(
                            (value, idx) => (
                              <TableCell key={idx} style={{ textAlign: "center" }}>
                                {value}
                              </TableCell>
                            )
                          )}
                          <TableCell style={{ textAlign: "center" }}>
                            {candidate.resume?.fileName ? (
                              <Button color="primary" onClick={() => handleResumeDownload(candidate.id)}>
                                {candidate.resume.fileName}
                              </Button>
                            ) : (
                              "No resume"
                            )}
                          </TableCell>
                        </TableRow>
                      ))
                  )}
                </TableBody>
              </Table>
            </Paper>

            <div style={{ display: "flex", justifyContent: "center", gap: "1rem" }}>
              <Button
                variant="contained"
                color="primary"
                disabled={pageNumber === 0}
                onClick={() => setPageNumber(pageNumber - 1)}
              >
                Previous
              </Button>
              <Button variant="contained" color="primary" onClick={() => setPageNumber(pageNumber + 1)}>
                Next
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SelectionDetails;
