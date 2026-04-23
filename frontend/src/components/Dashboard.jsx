import React, { useState, useEffect, useCallback } from 'react';
import { getTasks, createTask, updateTask, deleteTask } from '../api';
import { LogOut, Plus, Trash2, Edit2, Calendar, LayoutList } from 'lucide-react';

const Dashboard = ({ onLogout }) => {
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filter, setFilter] = useState({ status: '', priority: '', sortBy: 'dueDate' });
    const userProfile = JSON.parse(localStorage.getItem('user') || '{}');

    // Modal State
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [taskForm, setTaskForm] = useState({ title: '', description: '', status: 'TODO', priority: 'MEDIUM', dueDate: '' });
    const [modalError, setModalError] = useState('');

    const fetchTasks = useCallback(async () => {
        setLoading(true);
        try {
            // Strip empty filters
            const cleanFilter = Object.fromEntries(Object.entries(filter).filter(([_, v]) => v !== ''));
            const data = await getTasks(cleanFilter);
            setTasks(data);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    }, [filter]);

    useEffect(() => {
        fetchTasks();
    }, [fetchTasks]);

    const handleFilterChange = (e) => {
        setFilter({ ...filter, [e.target.name]: e.target.value });
    };

    const openModal = (task = null) => {
        setModalError('');
        if (task) {
            setEditingId(task.id);
            setTaskForm({
                title: task.title,
                description: task.description || '',
                status: task.status,
                priority: task.priority,
                dueDate: task.dueDate || ''
            });
        } else {
            setEditingId(null);
            setTaskForm({ title: '', description: '', status: 'TODO', priority: 'MEDIUM', dueDate: '' });
        }
        setIsModalOpen(true);
    };

    const handleSaveTask = async () => {
        try {
            setModalError('');
            if (editingId) {
                await updateTask(editingId, taskForm);
            } else {
                await createTask(taskForm);
            }
            setIsModalOpen(false);
            fetchTasks();
        } catch (err) {
            setModalError(err.message);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Are you sure you want to delete this task?')) return;
        try {
            await deleteTask(id);
            fetchTasks();
        } catch (err) {
            console.error("Failed to delete", err);
        }
    };

    return (
        <div>
            <div className="dashboard-header">
                <div>
                    <h1>My Tasks</h1>
                    <p style={{ color: 'var(--text-secondary)' }}>Welcome back, {userProfile.name}</p>
                </div>
                <div style={{ display: 'flex', gap: '1rem' }}>
                    <button onClick={() => openModal()} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <Plus size={18} /> New Task
                    </button>
                    <button onClick={onLogout} className="ghost" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <LogOut size={18} /> Logout
                    </button>
                </div>
            </div>

            {/* Filter Bar */}
            <div className="glass-panel" style={{ padding: '1rem 1.5rem', marginBottom: '2rem', display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'center' }}>
                <span style={{ fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <LayoutList size={18} /> Filters:
                </span>
                <select name="status" value={filter.status} onChange={handleFilterChange} style={{ width: 'auto' }}>
                    <option value="">All Statuses</option>
                    <option value="TODO">To Do</option>
                    <option value="IN_PROGRESS">In Progress</option>
                    <option value="COMPLETED">Completed</option>
                </select>
                <select name="priority" value={filter.priority} onChange={handleFilterChange} style={{ width: 'auto' }}>
                    <option value="">All Priorities</option>
                    <option value="HIGH">High</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="LOW">Low</option>
                </select>
                <select name="sortBy" value={filter.sortBy} onChange={handleFilterChange} style={{ width: 'auto', marginLeft: 'auto' }}>
                    <option value="dueDate">Sort by Due Date</option>
                    <option value="priority">Sort by Priority</option>
                </select>
            </div>

            {/* Tasks Grid */}
            {loading ? (
                <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>Loading tasks...</div>
            ) : tasks.length === 0 ? (
                <div className="glass-panel" style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-secondary)' }}>
                    <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📝</div>
                    <h3>No tasks found</h3>
                    <p>Get started by creating a new task.</p>
                </div>
            ) : (
                <div className="task-grid">
                    {tasks.map(task => (
                        <div key={task.id} className="glass-panel task-card">
                            <div className="task-header">
                                <h3 className="task-title">{task.title}</h3>
                            </div>
                            <p className="task-desc">{task.description || 'No description provided.'}</p>
                            
                            <div className="badges">
                                <span className={`badge ${task.status.toLowerCase()}`}>{task.status.replace('_', ' ')}</span>
                                <span className={`badge ${task.priority.toLowerCase()}`}>{task.priority} Priority</span>
                            </div>

                            {task.dueDate && (
                                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                                    <Calendar size={14} /> Due: {task.dueDate}
                                </div>
                            )}

                            <div className="task-actions">
                                <button className="ghost" onClick={() => openModal(task)}><Edit2 size={16} /></button>
                                <button className="danger" onClick={() => handleDelete(task.id)}><Trash2 size={16} /></button>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {/* Task Modal */}
            {isModalOpen && (
                <div className="modal-overlay">
                    <div className="glass-panel modal-content">
                        <div className="modal-header">
                            {editingId ? 'Edit Task' : 'Create New Task'}
                        </div>
                        
                        {modalError && <div className="error-message" style={{ marginBottom: '1rem' }}>{modalError}</div>}

                        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            <div className="form-group">
                                <label>Title</label>
                                <input 
                                    value={taskForm.title} 
                                    onChange={(e) => setTaskForm({...taskForm, title: e.target.value})} 
                                    placeholder="Task title" 
                                />
                            </div>
                            <div className="form-group">
                                <label>Description</label>
                                <textarea 
                                    value={taskForm.description} 
                                    onChange={(e) => setTaskForm({...taskForm, description: e.target.value})} 
                                    placeholder="Task details" rows={3}
                                />
                            </div>
                            <div style={{ display: 'flex', gap: '1rem' }}>
                                <div className="form-group" style={{ flex: 1 }}>
                                    <label>Status</label>
                                    <select value={taskForm.status} onChange={(e) => setTaskForm({...taskForm, status: e.target.value})}>
                                        <option value="TODO">To Do</option>
                                        <option value="IN_PROGRESS">In Progress</option>
                                        <option value="COMPLETED">Completed</option>
                                    </select>
                                </div>
                                <div className="form-group" style={{ flex: 1 }}>
                                    <label>Priority</label>
                                    <select value={taskForm.priority} onChange={(e) => setTaskForm({...taskForm, priority: e.target.value})}>
                                        <option value="LOW">Low</option>
                                        <option value="MEDIUM">Medium</option>
                                        <option value="HIGH">High</option>
                                    </select>
                                </div>
                            </div>
                            <div className="form-group">
                                <label>Due Date</label>
                                <input 
                                    type="date" 
                                    value={taskForm.dueDate} 
                                    onChange={(e) => setTaskForm({...taskForm, dueDate: e.target.value})} 
                                />
                            </div>
                        </div>

                        <div className="modal-actions">
                            <button className="ghost" onClick={() => setIsModalOpen(false)}>Cancel</button>
                            <button onClick={handleSaveTask}>Save Task</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Dashboard;
