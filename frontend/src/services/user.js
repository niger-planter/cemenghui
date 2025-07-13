import axios from 'axios';

export default {
  register(data) {
    return axios.post('/api/user/register', data);
  },
  login(data) {
    return axios.post('/api/user/login', data);
  },
  getUserInfo(id) {
    return axios.get(`/api/user/info/${id}`);
  },
  updateUser(data) {
    return axios.post('/api/user/update', data);
  },
  changePassword(data) {
    return axios.post('/api/user/password', data);
  },
  listUsers(params) {
    return axios.get('/api/user/list', { params });
  },
  setUserStatus(data) {
    return axios.post('/api/user/status', data);
  },
  deleteUser(data) {
    return axios.post('/api/user/delete', data);
  }
}; 