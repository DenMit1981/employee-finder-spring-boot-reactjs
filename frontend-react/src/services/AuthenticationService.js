import axios from 'axios';

const AUTH_URL = "http://localhost:8081/api/v1/auth";
export const USER_NAME_SESSION_ATTRIBUTE_NAME = 'authenticatedUser ';

class AuthenticationService {

    registration(userDto) {
        return axios.post(`${AUTH_URL}/signup`, userDto);
    }

    login(login, password) {
        return axios.post(`${AUTH_URL}/signin`, { login, password });
    }

    registerSuccessfulLoginForJwt(login, token) {
        sessionStorage.setItem(USER_NAME_SESSION_ATTRIBUTE_NAME, login);
        this.setupAxiosInterceptors(this.createJWTToken(token));
    }

    changePassword(userDto) {
        return axios.patch(`${AUTH_URL}/change-password`, userDto);
    }

    createJWTToken(token) {
        return `Bearer ${token}`;
    }

    logout() {
        sessionStorage.removeItem(USER_NAME_SESSION_ATTRIBUTE_NAME);
        delete axios.defaults.headers.common['Authorization'];
    }

    isUserLoggedIn() {
        return sessionStorage.getItem(USER_NAME_SESSION_ATTRIBUTE_NAME) !== null;
    }

    getLoggedInUserName() {
        return sessionStorage.getItem(USER_NAME_SESSION_ATTRIBUTE_NAME) || '';
    }

    setupAxiosInterceptors(token) {
        axios.defaults.headers.common['Authorization'] = token;
    }

    initializeAxiosWithToken() {
        const token = sessionStorage.getItem('token');
        if (token) {
            this.setupAxiosInterceptors(this.createJWTToken(token));
        }
    }
}

export default new AuthenticationService();