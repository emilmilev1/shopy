import React, { useState, useEffect } from "react";
import { Container, Box } from "@mui/material";
import Header from "./components/Header";
import ProductsPage from "./components/ProductsPage";
import RoutePage from "./components/RoutePage";
import { useWarehouseData } from "./hooks/useWarehouseData";
import OrdersPage from "./components/OrdersPage";
import HomePage from "./components/HomePage";
import SignUpPage from "./components/SignUpPage";
import LoginPage from "./components/LoginPage";
import {
    BrowserRouter as Router,
    Routes,
    Route,
    Navigate,
} from "react-router-dom";
import { authAPI } from "./services/api";

function ProtectedRoute({
    user,
    children,
}: {
    user: any;
    children: React.ReactNode;
}) {
    if (!user) {
        return <Navigate to="/login" replace />;
    }
    return <>{children}</>;
}

function AuthRoute({
    user,
    children,
}: {
    user: any;
    children: React.ReactNode;
}) {
    if (user) {
        return <Navigate to="/" replace />;
    }
    return <>{children}</>;
}

export default function App() {
    const [user, setUser] = useState<any>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Check if user is authenticated on app load
        const checkAuth = () => {
            if (authAPI.isAuthenticated()) {
                const currentUser = authAPI.getCurrentUser();
                if (currentUser) {
                    setUser(currentUser);
                }
            }
            setLoading(false);
        };

        checkAuth();
    }, []);

    const warehouseData = useWarehouseData(user);
    const {
        products,
        loading: productsLoading,
        error,
        addProduct,
        updateProduct,
        deleteProduct,
        refreshData,
    } = warehouseData;

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <Router>
            <Box sx={{ bgcolor: "grey.50", minHeight: "100vh" }}>
                <Header user={user} setUser={setUser} />
                <Container maxWidth="lg">
                    <Routes>
                        <Route path="/" element={<HomePage />} />
                        <Route
                            path="/signup"
                            element={
                                <AuthRoute user={user}>
                                    <SignUpPage />
                                </AuthRoute>
                            }
                        />
                        <Route
                            path="/login"
                            element={
                                <AuthRoute user={user}>
                                    <LoginPage setUser={setUser} />
                                </AuthRoute>
                            }
                        />
                        <Route
                            path="/products"
                            element={
                                <ProtectedRoute user={user}>
                                    <ProductsPage
                                        products={products}
                                        addProduct={addProduct}
                                        updateProduct={updateProduct}
                                        deleteProduct={deleteProduct}
                                        refreshData={refreshData}
                                    />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/orders"
                            element={
                                <ProtectedRoute user={user}>
                                    <OrdersPage />
                                </ProtectedRoute>
                            }
                        />
                        <Route
                            path="/routes"
                            element={
                                <ProtectedRoute user={user}>
                                    <RoutePage products={products} />
                                </ProtectedRoute>
                            }
                        />
                    </Routes>
                </Container>
            </Box>
        </Router>
    );
}
