import axios from 'axios';

const SELECTIONS_URL = "http://localhost:8081/api/v1/selections";

class SelectionService {

    addEmployeeToSelection = (employeeId) => {
        return axios.post(`${SELECTIONS_URL}/add-employee/${employeeId}`);
    }

    removeEmployeeFromSelection = (employeeId) => {
        return axios.delete(`${SELECTIONS_URL}/remove-employee/${employeeId}`);
    }

    clearSelection = () => {
        return axios.delete(`${SELECTIONS_URL}/clear`);
    }

    getCurrentSelection = () => {
        return axios.get(`${SELECTIONS_URL}/current`);
    }

    submitFinalSelection = () => {
        return axios.post(`${SELECTIONS_URL}/submit`);
    }

    getSelectionById = (selectionId, sortField, sortDirection, pageSize, pageNumber) => {
        return axios.get(`${SELECTIONS_URL}/${selectionId}`, {
            params: {
                sortField,
                sortDirection,
                pageSize,
                pageNumber
            }
        });
    };

    getSelectionByIdSearched = (selectionId, searchField, parameter, sortField, sortDirection, pageSize, pageNumber) => {
        return axios.get(`${SELECTIONS_URL}/${selectionId}`, {
            params: {
                searchField,
                parameter,
                sortField,
                sortDirection,
                pageSize,
                pageNumber
            }
        });
    };
}

export default new SelectionService();