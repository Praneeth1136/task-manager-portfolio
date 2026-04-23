import React, { useState } from 'react';
import { login, register } from '../api';
import { LogIn, UserPlus } from 'lucide-react';

const Auth = ({ onLogin }) => {
    const [isLogin, setIsLogin] = useState(true);
    const [formData, setFormData] = useState({ name: '', email: '', password: '' });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            let data;
            if (isLogin) {
                data = await login({ email: formData.email, password: formData.password });
            } else {
                data = await register(formData);
            }
            
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify({ name: data.name, email: data.email }));
            onLogin(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="glass-panel auth-card">
                <div className="auth-header text-center" style={{textAlign: 'center'}}>
                    <h1>{isLogin ? 'Welcome Back' : 'Create Account'}</h1>
                    <p>{isLogin ? 'Sign in to manage your tasks' : 'Sign up to get started'}</p>
                </div>

                {error && <div className="error-message">{error}</div>}

                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    {!isLogin && (
                        <div className="form-group">
                            <label>Full Name</label>
                            <input 
                                type="text" name="name" 
                                value={formData.name} onChange={handleChange} 
                                required placeholder="John Doe" 
                            />
                        </div>
                    )}
                    
                    <div className="form-group">
                        <label>Email Address</label>
                        <input 
                            type="email" name="email" 
                            value={formData.email} onChange={handleChange} 
                            required placeholder="you@example.com" 
                        />
                    </div>
                    
                    <div className="form-group">
                        <label>Password</label>
                        <input 
                            type="password" name="password" 
                            value={formData.password} onChange={handleChange} 
                            required placeholder="••••••••" minLength={6} 
                        />
                    </div>

                    <button type="submit" disabled={loading} style={{ marginTop: '1rem', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '0.5rem' }}>
                        {isLogin ? <LogIn size={18} /> : <UserPlus size={18} />}
                        {loading ? 'Processing...' : (isLogin ? 'Sign In' : 'Sign Up')}
                    </button>
                </form>

                <p className="auth-toggle">
                    {isLogin ? "Don't have an account? " : "Already have an account? "}
                    <span onClick={() => setIsLogin(!isLogin)}>
                        {isLogin ? 'Sign up here' : 'Sign in here'}
                    </span>
                </p>
            </div>
        </div>
    );
};

export default Auth;
