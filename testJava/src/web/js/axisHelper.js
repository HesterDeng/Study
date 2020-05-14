import Vue from "vue";

const axios = require('axios');

function axiosHelper() {
    axios.defaults.baseURL = Vue.prototype.c$.api;
    axios.defaults.headers.common['Authorization'] = window.localStorage.getItem("Authorization");
    axios.defaults.headers.post['Content-Type'] = 'application/json';
    axios.defaults.headers.put['Content-Type'] = 'application/json';
    axios.defaults.headers.common['Authorization'] = localStorage.getItem('Authorization');
    return axios;
}

export default axiosHelper;