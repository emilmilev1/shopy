import { useState, useEffect } from "react";
import { Product } from "../types";
import { ApiService } from "../services/api";

export const useWarehouseData = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            setLoading(true);
            setError(null);
            const apiProducts = await ApiService.getProducts();
            setProducts(
                apiProducts.map((p) => ({ ...p, id: p.id.toString() }))
            );
        } catch (err) {
            console.error("Failed to load data:", err);
            setError(
                "Failed to load data from API. Please check your connection."
            );
            setProducts([]);
        } finally {
            setLoading(false);
        }
    };

    const addProduct = async (product: Omit<Product, "id">) => {
        try {
            const apiProduct = await ApiService.createProduct({
                name: product.name,
                price: product.price,
                quantity: product.quantity,
                location: product.location,
            });

            // Check if this is an update to an existing product
            const existingProductIndex = products.findIndex(
                (p) =>
                    p.name === product.name &&
                    p.price === product.price &&
                    p.location.x === product.location.x &&
                    p.location.y === product.location.y
            );

            if (existingProductIndex !== -1) {
                // Update existing product
                setProducts((prev) =>
                    prev.map((p, index) =>
                        index === existingProductIndex
                            ? { ...apiProduct, id: apiProduct.id.toString() }
                            : p
                    )
                );
            } else {
                // Add new product
                setProducts((prev) => [
                    ...prev,
                    { ...apiProduct, id: apiProduct.id.toString() },
                ]);
            }
        } catch (err) {
            console.error("Failed to add product:", err);
            throw new Error("Failed to add product");
        }
    };

    const updateProduct = async (product: Product) => {
        try {
            const apiProduct = await ApiService.updateProduct(
                Number(product.id),
                {
                    name: product.name,
                    price: product.price,
                    quantity: product.quantity,
                    location: product.location,
                }
            );
            setProducts((prev) =>
                prev.map((p) =>
                    p.id === product.id
                        ? { ...apiProduct, id: apiProduct.id.toString() }
                        : p
                )
            );
        } catch (err) {
            console.error("Failed to update product:", err);
            throw new Error("Failed to update product");
        }
    };

    const deleteProduct = async (productId: string) => {
        try {
            await ApiService.deleteProduct(Number(productId));
            setProducts((prev) => prev.filter((p) => p.id !== productId));
        } catch (err) {
            console.error("Failed to delete product:", err);
            throw new Error("Failed to delete product");
        }
    };

    return {
        products,
        loading,
        error,
        addProduct,
        updateProduct,
        deleteProduct,
        refreshData: loadData,
    };
};
