import React, { useState } from "react";
import { Box, TextField, Button, Typography, Paper } from "@mui/material";
import { ApiService } from "../services/api";

export default function OrderStatusChecker() {
    const [orderId, setOrderId] = useState("");
    const [status, setStatus] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);

    const checkStatus = async () => {
        setStatus(null);
        setError(null);
        try {
            const data = await ApiService.getOrderStatus(Number(orderId));
            setStatus(data.status);
        } catch (err: any) {
            setError(err.message || "Error fetching order status");
        }
    };

    return (
        <Paper sx={{ p: 4, maxWidth: 400, mx: "auto", mt: 6 }}>
            <Typography variant="h5" gutterBottom>
                Check Order Status
            </Typography>
            <Box display="flex" gap={2} mb={2}>
                <TextField
                    label="Order ID"
                    value={orderId}
                    onChange={(e) => setOrderId(e.target.value)}
                    type="number"
                    fullWidth
                />
                <Button variant="contained" onClick={checkStatus}>
                    Check
                </Button>
            </Box>
            {status && (
                <Typography color="primary">
                    Status: <b>{status}</b>
                </Typography>
            )}
            {error && <Typography color="error">{error}</Typography>}
        </Paper>
    );
}
