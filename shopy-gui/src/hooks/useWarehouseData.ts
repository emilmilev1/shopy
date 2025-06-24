import { useState, useEffect } from "react";
import { Product } from "../types";
import { productsAPI } from "../services/api";

export function useWarehouseData(user?: any) {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!user) {
            setProducts([]);
            setLoading(false);
            setError(null);
            return;
        }
        setLoading(true);
        setError(null);
        productsAPI
            .getAll()
            .then(setProducts)
            .catch((err) => setError(err.message || "Failed to load products"))
            .finally(() => setLoading(false));
    }, [user]);

    const addProduct = async (product: Omit<Product, "id">) => {
        try {
            const apiProduct = await productsAPI.create({
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
            const apiProduct = await productsAPI.update(Number(product.id), {
                newQuantity: product.quantity,
                newPrice: product.price,
                newLocation: product.location,
            });
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
            await productsAPI.delete(Number(productId));
            setProducts((prev) => prev.filter((p) => p.id.toString() !== productId));
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
        refreshData: () => {
            if (!user) {
                setProducts([]);
                setLoading(false);
                setError(null);
                return;
            }
            setLoading(true);
            setError(null);
            productsAPI
                .getAll()
                .then(setProducts)
                .catch((err) =>
                    setError(err.message || "Failed to load products")
                )
                .finally(() => setLoading(false));
        },
    };
}
