import axios from 'axios';

const EMPLOYEES_URL = "http://localhost:8081/api/v1/employees";

class EmployeeService {

  addEmployee = (formData) => {
    return axios.post(`${EMPLOYEES_URL}/add`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  };

  updateEmployee = (employeeId, formData) => {
    return axios.put(`${EMPLOYEES_URL}/update/${employeeId}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  };

  deleteEmployee = (employeeId) => {
    return axios.delete(`${EMPLOYEES_URL}/remove/${employeeId}`);
  };

  changeEmployeeStatus = (employeeId, newStatus) => {
    return axios.put(`${EMPLOYEES_URL}/change-status/${employeeId}`, null, {
      params: {
        status: newStatus,
      }
    });
  };

  getEmployeeById = (id) => {
    return axios.get(`${EMPLOYEES_URL}/${id}`);
  };

  getAllSortedEmployeesByPages = (sortField, pageSize, pageNumber, sortDirection = "asc") => {
    return axios.get(`${EMPLOYEES_URL}?sortField=${sortField}&sortDirection=${sortDirection}&pageSize=${pageSize}&pageNumber=${pageNumber}`);
  };

  getAllSortedEmployeesSearched = (searchField, parameter, sortField, pageSize, pageNumber, sortDirection = "asc") => {
    return axios.get(`${EMPLOYEES_URL}?searchField=${searchField}&parameter=${parameter}&sortField=${sortField}&sortDirection=${sortDirection}&pageSize=${pageSize}&pageNumber=${pageNumber}`);
  };

  getAllSortedEmployeesByPagesForUser = (sortField, pageSize, pageNumber, sortDirection = "asc") => {
    return axios.get(`${EMPLOYEES_URL}/for-user?sortField=${sortField}&sortDirection=${sortDirection}&pageSize=${pageSize}&pageNumber=${pageNumber}`);
  };

  getAllSortedEmployeesSearchedForUser = (searchField, parameter, sortField, pageSize, pageNumber, sortDirection = "asc") => {
    return axios.get(`${EMPLOYEES_URL}/for-user?searchField=${searchField}&parameter=${parameter}&sortField=${sortField}&sortDirection=${sortDirection}&pageSize=${pageSize}&pageNumber=${pageNumber}`);
  };

  filterEmployees = (filterDto, sortField, sortDirection = "asc", pageSize = 25, pageNumber = 0) => {
    const params = {
      sortField,
      sortDirection,
      pageSize,
      pageNumber,
    };

    return axios.post(`${EMPLOYEES_URL}/filter`, filterDto, { params });
  };

  filterEmployeesSearched = (
    filterDto,
    sortField,
    sortDirection = "asc",
    pageSize = 25,
    pageNumber = 0,
    searchField,
    parameter
  ) => {
    const params = {
      sortField,
      sortDirection,
      pageSize,
      pageNumber,
      searchField,
      parameter
    };

    return axios.post(`${EMPLOYEES_URL}/filter`, filterDto, { params });
  };

  filterByJobTypeAndPosition = (jobTypeId, jobPositionId, page = 0, size = 20, sortBy = "id", sortDir = "asc") => {
    return axios.get(`${EMPLOYEES_URL}/filter/job-type-position`, {
      params: { jobTypeId, jobPositionId, page, size, sortBy, sortDir }
    });
  }
}

export default new EmployeeService();