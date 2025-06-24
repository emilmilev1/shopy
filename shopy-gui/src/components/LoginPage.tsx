import React, { useState } from "react";
import {
    Box,
    Typography,
    TextField,
    Button,
    Paper,
    Alert,
} from "@mui/material";
import { authAPI } from "../services/api";
import { useNavigate } from "react-router-dom";

interface LoginPageProps {
    setUser: (user: any) => void;
}

const LoginPage: React.FC<LoginPageProps> = ({ setUser }) => {
    const [form, setForm] = useState({ email: "", password: "" });
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setSuccess(false);
        setLoading(true);
        if (!form.email || !form.password) {
            setError("Email and password are required.");
            setLoading(false);
            return;
        }
        try {
            const response = await authAPI.login(form);
            setUser(response.user);
            setSuccess(true);
            setTimeout(() => navigate("/"), 1000);
        } catch (err: any) {
            setError(err.response?.data || err.message || "Login failed.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box
            sx={{
                py: 6,
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                minHeight: "60vh",
            }}
        >
            <Paper elevation={3} sx={{ p: 5, maxWidth: 400, width: "100%" }}>
                <Typography variant="h5" sx={{ fontWeight: "bold", mb: 2 }}>
                    Login
                </Typography>
                {error && (
                    <Alert severity="error" sx={{ mb: 2 }}>
                        {error}
                    </Alert>
                )}
                {success && (
                    <Alert severity="success" sx={{ mb: 2 }}>
                        Login successful!
                    </Alert>
                )}
                <Box
                    component="form"
                    onSubmit={handleSubmit}
                    sx={{ display: "flex", flexDirection: "column", gap: 2 }}
                >
                    <TextField
                        label="Email"
                        name="email"
                        type="email"
                        value={form.email}
                        onChange={handleChange}
                        required
                        fullWidth
                    />
                    <TextField
                        label="Password"
                        name="password"
                        type="password"
                        value={form.password}
                        onChange={handleChange}
                        required
                        fullWidth
                    />
                    <Button
                        type="submit"
                        variant="contained"
                        color="primary"
                        disabled={loading}
                    >
                        Login
                    </Button>
                </Box>
            </Paper>
        </Box>
    );
};

export default LoginPage;
