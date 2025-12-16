package com.example.insurancecompany.service;

import com.example.insurancecompany.dao.VehicleDao;
import com.example.insurancecompany.model.Vehicle;
import java.util.List;

public class VehicleService {
    private final VehicleDao vehicleDao;

    public VehicleService(VehicleDao vehicleDao) {
        this.vehicleDao = vehicleDao;
    }

    public Vehicle getVehicleById(int id) throws Exception {
        return vehicleDao.findById(id);
    }

    public List<Vehicle> getVehiclesByOwnerId(int ownerId) throws Exception {
        return vehicleDao.findByOwnerId(ownerId);
    }

    public Vehicle getVehicleByVin(String vin) throws Exception {
        return vehicleDao.findByVin(vin);
    }

    public void createVehicle(Vehicle vehicle) throws Exception {
        // Проверяем, есть ли неактивный автомобиль с таким VIN у того же владельца
        Vehicle vehicleByVin = vehicleDao.findByVinAndOwner(vehicle.getVin(), vehicle.getOwnerId());
        if (vehicleByVin != null) {
            if (vehicleByVin.isActive()) {
                throw new Exception("Автомобиль с таким VIN уже существует");
            } else {
                // Активируем существующий автомобиль и обновляем его данные
                vehicleByVin.setActive(true);
                vehicleByVin.setReg(vehicle.getReg());
                vehicleByVin.setBrand(vehicle.getBrand());
                vehicleByVin.setModel(vehicle.getModel());
                vehicleByVin.setYearManufact(vehicle.getYearManufact());
                vehicleByVin.setHorsePower(vehicle.getHorsePower());
                vehicleByVin.setCategoryLic(vehicle.getCategoryLic());
                vehicleDao.update(vehicleByVin);
                vehicle.setId(vehicleByVin.getId());
                return;
            }
        }
        
        // Проверяем, есть ли неактивный автомобиль с таким гос. номером у того же владельца
        Vehicle vehicleByReg = vehicleDao.findByRegAndOwner(vehicle.getReg(), vehicle.getOwnerId());
        if (vehicleByReg != null) {
            if (vehicleByReg.isActive()) {
                throw new Exception("Автомобиль с таким гос. номером уже существует");
            } else {
                // Активируем существующий автомобиль и обновляем его данные
                vehicleByReg.setActive(true);
                vehicleByReg.setVin(vehicle.getVin());
                vehicleByReg.setBrand(vehicle.getBrand());
                vehicleByReg.setModel(vehicle.getModel());
                vehicleByReg.setYearManufact(vehicle.getYearManufact());
                vehicleByReg.setHorsePower(vehicle.getHorsePower());
                vehicleByReg.setCategoryLic(vehicle.getCategoryLic());
                vehicleDao.update(vehicleByReg);
                vehicle.setId(vehicleByReg.getId());
                return;
            }
        }
        
        // Проверяем, есть ли активный автомобиль с таким VIN у другого владельца
        Vehicle existingByVin = vehicleDao.findByVin(vehicle.getVin());
        if (existingByVin != null && existingByVin.isActive() && existingByVin.getOwnerId() != vehicle.getOwnerId()) {
            throw new Exception("Автомобиль с таким VIN уже зарегистрирован у другого владельца");
        }
        
        // Создаем новый автомобиль
        // Если возникнет ошибка уникальности из-за неактивного автомобиля другого владельца,
        // база данных выдаст ошибку, которую мы обработаем в сервлете
        vehicleDao.create(vehicle);
    }

    public void updateVehicle(Vehicle vehicle) throws Exception {
        vehicleDao.update(vehicle);
    }

    public void deleteVehicle(int id) throws Exception {
        vehicleDao.delete(id);
    }
}
