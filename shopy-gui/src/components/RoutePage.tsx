import React, { useState } from "react";
import { Box, Typography, Button, Paper, TextField } from "@mui/material";
import type { Product } from "../types";
import { ordersAPI, routesAPI } from "../services/api";

type RoutePageProps = {
    products: Product[];
};

const RoutePage: React.FC<RoutePageProps> = ({ products }) => {
    const [orderId, setOrderId] = useState("");
    const [status, setStatus] = useState<string | null>(null);
    const [route, setRoute] = useState<number[][] | null>(null);
    const [loading, setLoading] = useState(false);

    const handleCheck = async () => {
        setStatus(null);
        setRoute(null);
        setLoading(true);
        try {
            const orderStatus = await ordersAPI.getStatusById(Number(orderId));
            setStatus(orderStatus.status);
            const routeData = await routesAPI.getById(Number(orderId));
            setRoute(routeData.visitedLocations);
        } catch (err: any) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box
            sx={{
                py: 4,
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
            }}
        >
            <Paper sx={{ p: 4, maxWidth: 400, width: "100%", mb: 4 }}>
                <Typography variant="h5" gutterBottom>
                    Check Order Route
                </Typography>
                <Box display="flex" gap={2} mb={2}>
                    <TextField
                        label="Order ID"
                        value={orderId}
                        onChange={(e) => setOrderId(e.target.value)}
                        type="number"
                        fullWidth
                    />
                    <Button
                        variant="contained"
                        onClick={handleCheck}
                        disabled={loading || !orderId}
                    >
                        Check
                    </Button>
                </Box>
                {status && (
                    <Typography color="primary">
                        Status: <b>{status}</b>
                    </Typography>
                )}
                {/* {error && <Typography color="error">{error}</Typography>} */}
            </Paper>
            {route && (
                <Paper sx={{ p: 3, maxWidth: 400, width: "100%" }}>
                    <Typography variant="h6" mb={2}>
                        Route Steps
                    </Typography>
                    <Box component="ol" sx={{ pl: 3, mb: 0 }}>
                        {route.map((loc, idx) => (
                            <li key={idx}>
                                <Typography variant="body2">
                                    Step {idx + 1}: ({loc[0]}, {loc[1]})
                                </Typography>
                            </li>
                        ))}
                    </Box>
                </Paper>
            )}
        </Box>
    );
};

export default RoutePage;
