import React, { useState } from "react";
import { Container, Box } from "@mui/material";
import Header from "./components/Header";
import ProductsPage from "./components/ProductsPage";
import RoutePage from "./components/RoutePage";
import { useWarehouseData } from "./hooks/useWarehouseData";

export default function App() {
    const [page, setPage] = useState<"products" | "orders">("products");
    const {
        products,
        loading,
        error,
        addProduct,
        updateProduct,
        deleteProduct,
        refreshData,
    } = useWarehouseData();

    let content = null;
    if (loading) {
        content = <Box sx={{ p: 4 }}>Loading...</Box>;
    } else if (error) {
        content = <Box sx={{ p: 4, color: "error.main" }}>{error}</Box>;
    } else {
        switch (page) {
            case "products":
                content = (
                    <>
                        <ProductsPage
                            products={products}
                            addProduct={addProduct}
                            updateProduct={updateProduct}
                            deleteProduct={deleteProduct}
                            refreshData={refreshData}
                        />
                    </>
                );
                break;
            case "orders":
                content = <RoutePage products={products} />;
                break;
            default:
                content = null;
        }
    }

    return (
        <Box sx={{ bgcolor: "grey.50", minHeight: "100vh" }}>
            <Header setPage={(page: "products" | "orders") => setPage(page)} />
            <Container maxWidth="lg">{content}</Container>
        </Box>
    );
}
