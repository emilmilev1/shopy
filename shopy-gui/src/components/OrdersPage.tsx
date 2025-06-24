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
} from "@mui/material";
import { ApiService } from "../services/api";
import { Order } from "../types";
import { useWarehouseData } from "../hooks/useWarehouseData";
import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";

const ORDERS_PER_PAGE = 5;

const OrdersPage: React.FC = () => {
    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const { products } = useWarehouseData();

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
            const data = await ApiService.getOrders();
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

    const handleOpenModal = () => {
        setItems([{ productId: "", quantity: 1 }]);
        setModalOpen(true);
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
            // Validate all items have a product selected
            for (const item of items) {
                if (!item.productId)
                    throw new Error("Please select a product for each item.");
            }
            // Map productId to productName for API
            const orderItems = items.map(({ productId, quantity }) => {
                const product = products.find((p) => p.id.toString() === productId);
                if (!product) throw new Error("Product not found");
                return { productName: product.name, quantity };
            });
            await ApiService.createOrder(orderItems);
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

    // Prevent selecting the same product twice in the same order
    const selectedProductIds = items.map((item) => item.productId);

    return (
        <Box sx={{ py: 4 }}>
            <Box
                sx={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    mb: 3,
                }}
            >
                <Typography variant="h4" sx={{ fontWeight: "bold" }}>
                    Orders
                </Typography>
                <Button
                    variant="contained"
                    color="success"
                    onClick={handleOpenModal}
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
                                    <TableCell>ID</TableCell>
                                    <TableCell>Status</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {paginatedOrders.map((order) => (
                                    <TableRow key={order.id} hover>
                                        <TableCell>{order.id}</TableCell>
                                        <TableCell>{order.status}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
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
                maxWidth="xs"
                fullWidth
            >
                <DialogTitle>Create Order</DialogTitle>
                <DialogContent>
                    {items.map((item, idx) => (
                        <Box
                            key={idx}
                            sx={{
                                display: "flex",
                                gap: 1,
                                alignItems: "center",
                                mb: 1,
                            }}
                        >
                            <FormControl size="small" fullWidth required>
                                <InputLabel id={`product-select-label-${idx}`}>
                                    Product
                                </InputLabel>
                                <Select
                                    labelId={`product-select-label-${idx}`}
                                    value={item.productId}
                                    label="Product"
                                    onChange={(e) =>
                                        handleItemChange(
                                            idx,
                                            "productId",
                                            e.target.value
                                        )
                                    }
                                >
                                    {products
                                        .filter(
                                            (p) =>
                                                !selectedProductIds.includes(
                                                    p.id.toString()
                                                ) ||
                                                item.productId ===
                                                    p.id.toString()
                                        )
                                        .map((product) => (
                                            <MenuItem
                                                key={product.id}
                                                value={product.id.toString()}
                                            >
                                                {product.name}
                                            </MenuItem>
                                        ))}
                                </Select>
                            </FormControl>
                            <TextField
                                label="Quantity"
                                type="number"
                                value={item.quantity}
                                onChange={(e) =>
                                    handleItemChange(
                                        idx,
                                        "quantity",
                                        e.target.value
                                    )
                                }
                                size="small"
                                sx={{ width: 100 }}
                                required
                            />
                            <IconButton
                                onClick={() => handleRemoveItem(idx)}
                                disabled={items.length === 1}
                            >
                                <DeleteIcon />
                            </IconButton>
                        </Box>
                    ))}
                    <Button
                        startIcon={<AddIcon />}
                        onClick={handleAddItem}
                        sx={{ mt: 1 }}
                    >
                        Add Product
                    </Button>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseModal}>Cancel</Button>
                    <Button
                        onClick={handleCreateOrder}
                        variant="contained"
                        color="success"
                        disabled={creating}
                    >
                        Create
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default OrdersPage;
