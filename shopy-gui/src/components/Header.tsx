import React from "react";
import {
    AppBar,
    Toolbar,
    Typography,
    Button,
    Container,
    Box,
} from "@mui/material";
import { Link, useNavigate } from "react-router-dom";
import { authAPI } from "../services/api";

interface HeaderProps {
    user: any;
    setUser: (user: any) => void;
}

const Header: React.FC<HeaderProps> = ({ user, setUser }) => {
    const navigate = useNavigate();
    const handleLogout = () => {
        authAPI.logout();
        setUser(null);
        navigate("/login");
    };
    return (
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
                        component={Link}
                        to="/"
                        sx={{ flexGrow: 1, cursor: "pointer", fontWeight: "bold", textDecoration: "none", color: "inherit" }}
                    >
                        Shopy
                    </Typography>
                    <Box>
                        {user && (
                            <>
                                <Button color="inherit" component={Link} to="/products">
                                    Products
                                </Button>
                                <Button color="inherit" component={Link} to="/orders">
                                    Orders
                                </Button>
                                <Button color="inherit" component={Link} to="/routes">
                                    Routes
                                </Button>
                            </>
                        )}
                        {!user ? (
                            <>
                                <Button color="inherit" component={Link} to="/login">
                                    Login
                                </Button>
                                <Button color="inherit" component={Link} to="/signup">
                                    Sign Up
                                </Button>
                            </>
                        ) : (
                            <>
                                <Typography sx={{ mx: 2, display: "inline" }}>
                                    {user.name}
                                </Typography>
                                <Button color="inherit" onClick={handleLogout}>
                                    Logout
                                </Button>
                            </>
                        )}
                    </Box>
                </Toolbar>
            </Container>
        </AppBar>
    );
};

export default Header;
