import axios from 'axios';

const RESUMES_URL = "http://localhost:8081/api/v1/resumes";

class ResumeService {

    downloadResume = (employeeId) => {
        return axios.get(`${RESUMES_URL}/download/${employeeId}`, {
            responseType: 'blob'
        });
    }

    deleteResume = (employeeId) => {
        return axios.delete(`${RESUMES_URL}/remove/${employeeId}`);
    }
}

export default new ResumeService();