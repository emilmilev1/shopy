import React from "react";
import {
    AppBar,
    Toolbar,
    Typography,
    Button,
    Container,
    Box,
} from "@mui/material";

type HeaderProps = {
    setPage: (page: "products" | "orders" | "routes") => void;
};

const Header: React.FC<HeaderProps> = ({ setPage }) => (
    <AppBar
        position="static"
        color="default"
        elevation={1}
        sx={{ backgroundColor: "white" }}
    >
        <Container maxWidth="lg">
            <Toolbar>
                <Typography
                    variant="h6"
                    component="div"
                    sx={{ flexGrow: 1, cursor: "pointer", fontWeight: "bold" }}
                    onClick={() => setPage("products")}
                >
                    Shopy
                </Typography>
                <Box>
                    <Button color="inherit" onClick={() => setPage("products")}>
                        Products
                    </Button>
                    <Button color="inherit" onClick={() => setPage("orders")}>
                        Orders
                    </Button>
                    <Button color="inherit" onClick={() => setPage("routes")}>
                        Routes
                    </Button>
                </Box>
            </Toolbar>
        </Container>
    </AppBar>
);

export default Header;
