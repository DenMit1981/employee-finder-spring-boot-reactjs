import axios from 'axios';

const JOBS_URL = "http://localhost:8081/api/v1/jobs"

class JobService {

    getAllJobTypes = () => {
        return axios.get(`${JOBS_URL}/job-types`);
    }

    getJobTypesFromNewAndSelectedEmployees = () => {
        return axios.get(`${JOBS_URL}/job-types/from-employees`);
    }

    getAllJobPositions = () => {
        return axios.get(`${JOBS_URL}/job-positions`);
    }

    getAllJobPositionsFromNewAndSelectedEmployees = () => {
        return axios.get(`${JOBS_URL}/job-positions/from-employees`);
    }

    getJobPositionsByJobTypeId = (jobTypeId) => {
        return axios.get(`${JOBS_URL}/job-types-positions?jobTypeId=${jobTypeId}`);
    }

    getJobPositionsByJobTypeIdFromNewAndSelectedEmployees = (jobTypeId) => {
        return axios.get(`${JOBS_URL}/job-types-positions/from-employees?jobTypeId=${jobTypeId}`);
    }

    getAllJobTypesWithPositions = () => {
        return axios.get(`${JOBS_URL}/job-types-positions/all`);
    }

    getJobTypesWithPositionsByGender = (gender) => {
        return axios.get(`${JOBS_URL}/job-types-positions/gender?gender=${gender}`);
    }

    getJobTypesWithPositionsByLocation = (location) => {
        return axios.get(`${JOBS_URL}/job-types-positions/location?location=${location}`);
    }

    getJobTypesWithPositionsByGenderAndLocation = (gender, location) => {
        return axios.get(`${JOBS_URL}/job-types-positions/gender-location?location=${location}&gender=${gender}`);
    }
}

export default new JobService();