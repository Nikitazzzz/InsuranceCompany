package com.example.insurancecompany.servlet.UserTasks;

import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.model.Vehicle;
import com.example.insurancecompany.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class VehicleServlet extends HttpServlet {

    private VehicleService vehicleService;

    @Override
    public void init() throws ServletException {
        this.vehicleService = (VehicleService) getServletContext().getAttribute("vehicleService");
        if (this.vehicleService == null) {
            throw new IllegalStateException("VehicleService not initialized");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        UserAccount user = (UserAccount) session.getAttribute("currentUser");
        try {
            if (user.getOwnerId() != null) {
                List<Vehicle> vehicles = vehicleService.getVehiclesByOwnerId(user.getOwnerId());
                req.setAttribute("vehicles", vehicles);
            }
            req.getRequestDispatcher("/jsp/vehicles.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Ошибка при получении списка автомобилей", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        UserAccount user = (UserAccount) session.getAttribute("currentUser");
        if (user.getOwnerId() == null) {
            resp.sendRedirect(req.getContextPath() + "/user/home");
            return;
        }

        String action = req.getParameter("action");
        if ("create".equals(action)) {
            try {
                Vehicle vehicle = new Vehicle();
                vehicle.setOwnerId(user.getOwnerId());
                vehicle.setVin(req.getParameter("vin"));
                vehicle.setReg(req.getParameter("reg"));
                vehicle.setBrand(req.getParameter("brand"));
                vehicle.setModel(req.getParameter("model"));
                vehicle.setYearManufact(Integer.parseInt(req.getParameter("yearManufact")));
                vehicle.setHorsePower(Integer.parseInt(req.getParameter("horsePower")));
                vehicle.setCategoryLic(req.getParameter("categoryLic"));
                vehicle.setCreatedAt(LocalDateTime.now());
                vehicle.setActive(true);
                vehicleService.createVehicle(vehicle);
                
                // Логирование
                try {
                    com.example.insurancecompany.service.ActionLogService actionLogService = 
                        (com.example.insurancecompany.service.ActionLogService) req.getServletContext().getAttribute("actionLogService");
                    if (actionLogService != null) {
                        String ipAddress = req.getRemoteAddr();
                        actionLogService.logAction(user.getId(), user.getRole(), "CREATE", "Vehicle", vehicle.getId(), 
                            "Добавлен автомобиль: " + vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getReg() + ")", ipAddress);
                    }
                } catch (Exception e) {}
                
                resp.sendRedirect(req.getContextPath() + "/user/vehicles");
            } catch (Exception e) {
                req.setAttribute("error", "Ошибка при добавлении автомобиля: " + e.getMessage());
                doGet(req, resp);
            }
        } else if ("update".equals(action)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                Vehicle vehicle = vehicleService.getVehicleById(id);
                if (vehicle != null && vehicle.getOwnerId() == user.getOwnerId()) {
                    vehicle.setVin(req.getParameter("vin"));
                    vehicle.setReg(req.getParameter("reg"));
                    vehicle.setBrand(req.getParameter("brand"));
                    vehicle.setModel(req.getParameter("model"));
                    vehicle.setYearManufact(Integer.parseInt(req.getParameter("yearManufact")));
                    vehicle.setHorsePower(Integer.parseInt(req.getParameter("horsePower")));
                    vehicle.setCategoryLic(req.getParameter("categoryLic"));
                    vehicleService.updateVehicle(vehicle);
                    
                    // Логирование
                    try {
                        com.example.insurancecompany.service.ActionLogService actionLogService = 
                            (com.example.insurancecompany.service.ActionLogService) req.getServletContext().getAttribute("actionLogService");
                        if (actionLogService != null) {
                            String ipAddress = req.getRemoteAddr();
                            actionLogService.logAction(user.getId(), user.getRole(), "UPDATE", "Vehicle", vehicle.getId(), 
                                "Обновлен автомобиль: " + vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getReg() + ")", ipAddress);
                        }
                    } catch (Exception e) {}
                    
                    resp.sendRedirect(req.getContextPath() + "/user/vehicles");
                } else {
                    req.setAttribute("error", "Автомобиль не найден или у вас нет прав на его редактирование");
                    doGet(req, resp);
                }
            } catch (Exception e) {
                req.setAttribute("error", "Ошибка при обновлении автомобиля: " + e.getMessage());
                doGet(req, resp);
            }
        } else if ("delete".equals(action)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                vehicleService.deleteVehicle(id);
                resp.sendRedirect(req.getContextPath() + "/user/vehicles");
            } catch (Exception e) {
                req.setAttribute("error", "Ошибка при удалении автомобиля: " + e.getMessage());
                doGet(req, resp);
            }
        }
    }
}
