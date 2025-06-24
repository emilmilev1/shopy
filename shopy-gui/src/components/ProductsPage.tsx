import React, { useState } from 'react';
import { Box, Typography, Button, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, IconButton, Modal, TextField, Pagination, Dialog, DialogTitle, DialogContent, DialogContentText, DialogActions } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import type { Product } from '../types';

type ProductsPageProps = {
  products: Product[];
  addProduct: (product: Omit<Product, 'id'>) => Promise<void>;
  updateProduct: (product: Product) => Promise<void>;
  deleteProduct: (id: string) => Promise<void>;
  refreshData: () => void;
};

const modalStyle = {
  position: 'absolute' as 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 400,
  bgcolor: 'background.paper',
  boxShadow: 24,
  p: 4,
  borderRadius: 2,
};

const emptyForm = { name: '', price: 0, quantity: 0, location: { x: 0, y: 0 } };

const ProductsPage: React.FC<ProductsPageProps> = ({ products, addProduct, updateProduct, deleteProduct }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [formData, setFormData] = useState<Omit<Product, 'id'>>(emptyForm);
  const [pageNum, setPageNum] = useState(1);
  const productsPerPage = 5;
  const pageCount = Math.ceil(products.length / productsPerPage);
  const sortedProducts = [...products].sort((a, b) => Number(a.id) - Number(b.id));
  const paginatedProducts = sortedProducts.slice((pageNum - 1) * productsPerPage, pageNum * productsPerPage);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [productToDelete, setProductToDelete] = useState<string | null>(null);

  const openAddModal = () => {
    setEditingProduct(null);
    setFormData(emptyForm);
    setIsModalOpen(true);
  };
  const openEditModal = (product: Product) => {
    setEditingProduct(product);
    setFormData({
      name: product.name,
      price: product.price,
      quantity: product.quantity,
      location: { ...product.location }
    });
    setIsModalOpen(true);
  };
  const closeModal = () => setIsModalOpen(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    if (name === 'x' || name === 'y') {
      setFormData(prev => ({
        ...prev,
        location: { ...prev.location, [name]: Number(value) }
      }));
    } else if (name === 'price' || name === 'quantity') {
      setFormData(prev => ({ ...prev, [name]: Number(value) }));
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (editingProduct) {
      await updateProduct({ ...editingProduct, ...formData });
    } else {
      await addProduct(formData);
    }
    closeModal();
  };

  const handleDeleteRequest = (id: string) => {
    setProductToDelete(id);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (productToDelete) {
      await deleteProduct(productToDelete);
      setDeleteDialogOpen(false);
      setProductToDelete(null);
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false);
    setProductToDelete(null);
  };

  return (
    <Box sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold' }}>Products</Typography>
        <Button variant="contained" startIcon={<AddIcon />} onClick={openAddModal}>Add Product</Button>
      </Box>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Price</TableCell>
              <TableCell>Quantity</TableCell>
              <TableCell>Location</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {paginatedProducts.map(product => (
              <TableRow key={product.id.toString()} hover>
                <TableCell>{product.id.toString()}</TableCell>
                <TableCell>{product.name}</TableCell>
                <TableCell>{product.price}</TableCell>
                <TableCell>{product.quantity}</TableCell>
                <TableCell>{
                  product.location && typeof product.location === 'object' &&
                  typeof product.location.x === 'number' && typeof product.location.y === 'number'
                    ? `(${product.location.x}, ${product.location.y})`
                    : ''
                }</TableCell>
                <TableCell>
                  <IconButton onClick={() => openEditModal(product)}><EditIcon /></IconButton>
                  <IconButton onClick={() => handleDeleteRequest(product.id.toString())}><DeleteIcon /></IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      {/* Pagination */}
      {pageCount > 1 && (
        <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
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
      <Modal open={isModalOpen} onClose={closeModal}>
        <Box sx={modalStyle}>
          <Typography variant="h6" mb={2}>{editingProduct ? 'Edit Product' : 'Add Product'}</Typography>
          <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <TextField label="Name" name="name" value={formData.name} onChange={handleChange} required fullWidth />
            <TextField label="Price" name="price" type="number" value={formData.price} onChange={handleChange} required fullWidth />
            <TextField label="Quantity" name="quantity" type="number" value={formData.quantity} onChange={handleChange} required fullWidth />
            <TextField label="Location X" name="x" type="number" value={formData.location.x} onChange={handleChange} required fullWidth />
            <TextField label="Location Y" name="y" type="number" value={formData.location.y} onChange={handleChange} required fullWidth />
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1, mt: 2 }}>
              <Button onClick={closeModal}>Cancel</Button>
              <Button type="submit" variant="contained">Save</Button>
            </Box>
          </Box>
        </Box>
      </Modal>
      {/* Delete confirmation dialog */}
      <Dialog open={deleteDialogOpen} onClose={handleDeleteCancel}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete this product?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteCancel}>Cancel</Button>
          <Button onClick={handleDeleteConfirm} color="error" variant="contained">Delete</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default ProductsPage; 