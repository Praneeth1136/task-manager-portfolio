const API_BASE = 'http://localhost:1111/api';

const getToken = () => localStorage.getItem('token');

export const register = async (userData) => {
    const res = await fetch(`${API_BASE}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData)
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Registration failed');
    return data;
};

export const login = async (credentials) => {
    const res = await fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials)
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Login failed');
    return data;
};

export const getTasks = async (filters = {}) => {
    const query = new URLSearchParams(filters).toString();
    const url = query ? `${API_BASE}/tasks?${query}` : `${API_BASE}/tasks`;
    
    const res = await fetch(url, {
        headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    if (!res.ok) throw new Error('Failed to fetch tasks');
    return res.json();
};

export const createTask = async (taskData) => {
    const res = await fetch(`${API_BASE}/tasks`, {
        method: 'POST',
        headers: { 
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}`
        },
        body: JSON.stringify(taskData)
    });
    if (!res.ok) throw new Error('Failed to create task');
    return res.json();
};

export const updateTask = async (id, taskData) => {
    const res = await fetch(`${API_BASE}/tasks/${id}`, {
        method: 'PUT',
        headers: { 
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}`
        },
        body: JSON.stringify(taskData)
    });
    if (!res.ok) throw new Error('Failed to update task');
    return res.json();
};

export const deleteTask = async (id) => {
    const res = await fetch(`${API_BASE}/tasks/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    if (!res.ok) throw new Error('Failed to delete task');
    return true;
};
