import React, { useEffect, useState } from "react";
import {
    Box,
    Typography,
    Button,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    IconButton,
    Pagination,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
    Chip,
    Alert,
} from "@mui/material";
import { Order } from "../types";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import { ordersAPI, authAPI, productsAPI } from "../services/api";
import { Product } from "../types";

const ORDERS_PER_PAGE = 5;

const OrdersPage: React.FC = () => {
    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const currentUser = authAPI.getCurrentUser();
    const [products, setProducts] = useState<Product[]>([]);
    const [productsLoading, setProductsLoading] = useState(false);

    const [pageNum, setPageNum] = useState(1);
    const pageCount = Math.ceil(orders.length / ORDERS_PER_PAGE);
    const paginatedOrders = orders.slice(
        (pageNum - 1) * ORDERS_PER_PAGE,
        pageNum * ORDERS_PER_PAGE
    );

    const [modalOpen, setModalOpen] = useState(false);
    const [items, setItems] = useState<
        { productId: string; quantity: number }[]
    >([{ productId: "", quantity: 1 }]);
    const [creating, setCreating] = useState(false);

    const fetchOrders = async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await ordersAPI.getAll();
            setOrders(data);
        } catch (err: any) {
            setError(err.message || "Failed to fetch orders");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchOrders();
    }, []);

    // check when products are loaded
    // useEffect(() => {
    //     console.log('Products loaded:', products.length, products);
    // }, [products]);

    const handleOpenModal = async () => {
        setItems([{ productId: "", quantity: 1 }]);
        setModalOpen(true);

        setProductsLoading(true);
        try {
            const data = await productsAPI.getAll();
            setProducts(data);
        } catch (err: any) {
            console.error("Failed to fetch products:", err);
            setProducts([]);
        } finally {
            setProductsLoading(false);
        }
    };
    const handleCloseModal = () => {
        setModalOpen(false);
    };

    const handleItemChange = (
        idx: number,
        field: "productId" | "quantity",
        value: string | number
    ) => {
        setItems((prev) =>
            prev.map((item, i) =>
                i === idx
                    ? {
                          ...item,
                          [field]: field === "quantity" ? Number(value) : value,
                      }
                    : item
            )
        );
    };
    const handleAddItem = () => {
        setItems((prev) => [...prev, { productId: "", quantity: 1 }]);
    };
    const handleRemoveItem = (idx: number) => {
        setItems((prev) =>
            prev.length > 1 ? prev.filter((_, i) => i !== idx) : prev
        );
    };

    const handleCreateOrder = async () => {
        setCreating(true);
        try {
            for (const item of items) {
                if (!item.productId)
                    throw new Error("Please select a product for each item.");
            }

            const orderItems = items.map(({ productId, quantity }) => {
                const product = products.find(
                    (p) => p.id.toString() === productId
                );
                if (!product) throw new Error("Product not found");
                return { productName: product.name, quantity };
            });
            await ordersAPI.create({ items: orderItems });
            setModalOpen(false);
            fetchOrders();
            setPageNum(1);
        } catch (err: any) {
            alert(
                "Failed to create order: " + (err.message || "Unknown error")
            );
        } finally {
            setCreating(false);
        }
    };

    const selectedProductIds = items.map((item) => item.productId);

    const getStatusColor = (status: string) => {
        switch (status.toLowerCase()) {
            case "success":
                return "success";
            case "fail":
                return "error";
            default:
                return "default";
        }
    };

    return (
        <Box sx={{ py: 4 }}>
            {currentUser && (
                <Alert severity="info" sx={{ mb: 3 }}>
                    <Typography variant="subtitle1" sx={{ fontWeight: "bold" }}>
                        Welcome, {currentUser.name}!
                    </Typography>
                    <Typography variant="body2">
                        You are viewing your personal orders. Each user can only
                        see their own orders.
                    </Typography>
                </Alert>
            )}

            <Box
                sx={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    mb: 3,
                }}
            >
                <Typography variant="h4" sx={{ fontWeight: "bold" }}>
                    My Orders
                </Typography>
                <Button
                    variant="contained"
                    color="success"
                    onClick={handleOpenModal}
                    startIcon={<AddIcon />}
                    disabled={false}
                >
                    Create Order
                </Button>
            </Box>
            {loading ? (
                <Box sx={{ p: 4 }}>Loading...</Box>
            ) : error ? (
                <Box sx={{ p: 4, color: "error.main" }}>{error}</Box>
            ) : (
                <>
                    <TableContainer component={Paper}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Order ID</TableCell>
                                    <TableCell>Status</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {paginatedOrders.map((order) => (
                                    <TableRow key={order.id} hover>
                                        <TableCell>{order.id}</TableCell>
                                        <TableCell>
                                            <Chip
                                                label={order.status}
                                                color={
                                                    getStatusColor(
                                                        order.status
                                                    ) as any
                                                }
                                                size="small"
                                            />
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                    {orders.length === 0 && !loading && (
                        <Box sx={{ p: 4, textAlign: "center" }}>
                            <Typography variant="h6" color="text.secondary">
                                No orders found. Create your first order!
                            </Typography>
                            {products.length === 0 && (
                                <Typography
                                    variant="body2"
                                    color="text.secondary"
                                    sx={{ mt: 1 }}
                                >
                                    You need to add products first before
                                    creating orders.
                                </Typography>
                            )}
                        </Box>
                    )}
                    {pageCount > 1 && (
                        <Box
                            sx={{
                                display: "flex",
                                justifyContent: "flex-end",
                                mt: 2,
                            }}
                        >
                            <Pagination
                                count={pageCount}
                                page={pageNum}
                                onChange={(_, value) => setPageNum(value)}
                                color="primary"
                                shape="rounded"
                                showFirstButton
                                showLastButton
                            />
                        </Box>
                    )}
                </>
            )}
            {/* Create Order Modal */}
            <Dialog
                open={modalOpen}
                onClose={handleCloseModal}
                maxWidth="md"
                fullWidth
            >
                <DialogTitle>Create New Order</DialogTitle>
                <DialogContent>
                    <Box sx={{ mt: 2 }}>
                        {/* Debug info */}
                        <Box
                            sx={{
                                mb: 2,
                                p: 1,
                                bgcolor: "grey.100",
                                borderRadius: 1,
                            }}
                        >
                            <Typography variant="body2" color="text.secondary">
                                {productsLoading
                                    ? "Loading products..."
                                    : `Available products: ${products.length}`}
                            </Typography>
                            {!productsLoading && products.length === 0 && (
                                <Typography variant="body2" color="error">
                                    No products available. Please add products
                                    first.
                                </Typography>
                            )}
                        </Box>

                        {items.map((item, idx) => (
                            <Box
                                key={idx}
                                sx={{
                                    display: "flex",
                                    gap: 2,
                                    mb: 2,
                                    alignItems: "center",
                                }}
                            >
                                <FormControl fullWidth>
                                    <InputLabel>Product</InputLabel>
                                    <Select
                                        value={item.productId}
                                        onChange={(e) =>
                                            handleItemChange(
                                                idx,
                                                "productId",
                                                e.target.value
                                            )
                                        }
                                        label="Product"
                                        disabled={productsLoading}
                                    >
                                        {productsLoading ? (
                                            <MenuItem disabled>
                                                Loading products...
                                            </MenuItem>
                                        ) : (
                                            products.map((product) => (
                                                <MenuItem
                                                    key={product.id}
                                                    value={product.id.toString()}
                                                    disabled={selectedProductIds.includes(
                                                        product.id.toString()
                                                    )}
                                                >
                                                    {product.name} - $
                                                    {product.price} (Stock:{" "}
                                                    {product.quantity})
                                                </MenuItem>
                                            ))
                                        )}
                                    </Select>
                                </FormControl>
                                <TextField
                                    type="number"
                                    label="Quantity"
                                    value={item.quantity}
                                    onChange={(e) =>
                                        handleItemChange(
                                            idx,
                                            "quantity",
                                            e.target.value
                                        )
                                    }
                                    sx={{ width: 120 }}
                                    inputProps={{ min: 1 }}
                                />
                                <IconButton
                                    onClick={() => handleRemoveItem(idx)}
                                    disabled={items.length === 1}
                                    color="error"
                                >
                                    <DeleteIcon />
                                </IconButton>
                            </Box>
                        ))}
                        <Button
                            onClick={handleAddItem}
                            startIcon={<AddIcon />}
                            variant="outlined"
                        >
                            Add Item
                        </Button>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseModal}>Cancel</Button>
                    <Button
                        onClick={handleCreateOrder}
                        variant="contained"
                        disabled={creating}
                    >
                        {creating ? "Creating..." : "Create Order"}
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default OrdersPage;
