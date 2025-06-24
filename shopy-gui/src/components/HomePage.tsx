import React from 'react';
import { Box, Typography, Paper } from '@mui/material';

const HomePage: React.FC = () => {
  return (
    <Box sx={{ py: 6, display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
      <Paper elevation={3} sx={{ p: 5, maxWidth: 500, textAlign: 'center' }}>
        <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 2 }}>
          Welcome to Shopy!
        </Typography>
        <Typography variant="h6" sx={{ mb: 2 }}>
          Warehouse Management System
        </Typography>
        <Typography variant="body1">
          Manage your products, place and track orders and view the picking route for your warehouse bot.
        </Typography>
      </Paper>
    </Box>
  );
};

export default HomePage; 