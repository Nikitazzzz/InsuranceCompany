package com.example.insurancecompany.service;

import com.example.insurancecompany.dao.OwnerDao;
import com.example.insurancecompany.model.Owner;
import java.util.List;

public class OwnerService {
    private final OwnerDao ownerDao;

    public OwnerService(OwnerDao ownerDao) {
        this.ownerDao = ownerDao;
    }

    public Owner getOwnerById(int id) throws Exception {
        return ownerDao.findById(id);
    }

    public Owner getOwnerByEmail(String email) throws Exception {
        return ownerDao.findByEmail(email);
    }

    public List<Owner> getAllOwners() throws Exception {
        return ownerDao.findAll();
    }

    public void createOwner(Owner owner) throws Exception {
        ownerDao.create(owner);
    }

    public void updateOwner(Owner owner) throws Exception {
        ownerDao.update(owner);
    }
}
