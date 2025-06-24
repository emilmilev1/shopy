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

const SignUpPage: React.FC = () => {
    const [form, setForm] = useState({
        name: "",
        email: "",
        telephone: "",
        address: "",
        password: "",
        confirmPassword: "",
    });
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
        if (
            !form.name ||
            !form.email ||
            !form.telephone ||
            !form.address ||
            !form.password ||
            !form.confirmPassword
        ) {
            setError("All fields are required.");
            setLoading(false);
            return;
        }
        if (form.password !== form.confirmPassword) {
            setError("Passwords do not match.");
            setLoading(false);
            return;
        }
        try {
            const { user } = await authAPI.register({
                name: form.name,
                email: form.email,
                telephone: form.telephone,
                address: form.address,
                password: form.password,
            });
            setSuccess(true);
            setTimeout(() => navigate("/login"), 2000);
        } catch (err: any) {
            setError(err.response?.data || err.message || "Registration failed.");
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
                    Sign Up
                </Typography>
                {error && (
                    <Alert severity="error" sx={{ mb: 2 }}>
                        {error}
                    </Alert>
                )}
                {success && (
                    <Alert severity="success" sx={{ mb: 2 }}>
                        Registration successful! Redirecting to login...
                    </Alert>
                )}
                <Box
                    component="form"
                    onSubmit={handleSubmit}
                    sx={{ display: "flex", flexDirection: "column", gap: 2 }}
                >
                    <TextField
                        label="Name"
                        name="name"
                        value={form.name}
                        onChange={handleChange}
                        required
                        fullWidth
                    />
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
                        label="Telephone"
                        name="telephone"
                        value={form.telephone}
                        onChange={handleChange}
                        required
                        fullWidth
                    />
                    <TextField
                        label="Address"
                        name="address"
                        value={form.address}
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
                    <TextField
                        label="Confirm Password"
                        name="confirmPassword"
                        type="password"
                        value={form.confirmPassword}
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
                        Register
                    </Button>
                </Box>
            </Paper>
        </Box>
    );
};

export default SignUpPage;
