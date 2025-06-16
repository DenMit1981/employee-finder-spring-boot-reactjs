import axios from 'axios';

const USERS_URL = "http://localhost:8081/api/v1/users";

class UserService {

  getAllSortedUsersByPages = (sortField, sortDirection = "asc", pageSize, pageNumber) => {
    return axios.get(`${USERS_URL}?sortField=${sortField}&sortDirection=${sortDirection}&pageSize=${pageSize}&pageNumber=${pageNumber}`);
  };


  getAllSortedUsersSearched = (searchField, parameter, sortField, pageSize, pageNumber, sortDirection = "asc") => {
    return axios.get(`${USERS_URL}?searchField=${searchField}&parameter=${parameter}&sortField=${sortField}&sortDirection=${sortDirection}&pageSize=${pageSize}&pageNumber=${pageNumber}`);
  };

  updateUserRole = (userId, newRole) => {
    return axios.put(`${USERS_URL}/${userId}/change-role`, null, {
      params: { newRole }
    });
  }

  deleteUserById = (id) => {
    return axios.delete(`${USERS_URL}/${id}`);
  }
}

export default new UserService();