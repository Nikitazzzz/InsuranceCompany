package com.example.insurancecompany.config;

import com.example.insurancecompany.dao.*;
import com.example.insurancecompany.dao.impl.*;
import com.example.insurancecompany.service.*;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        String url = ctx.getInitParameter("db.url");
        String user = ctx.getInitParameter("db.user");
        String password = ctx.getInitParameter("db.password");

        DBConnectionManager.init(url, user, password);

        // Инициализация DAO
        UserAccountDao userAccountDao = new UserAccountDaoJdbc();
        OwnerDao ownerDao = new OwnerDaoJdbc();
        VehicleDao vehicleDao = new VehicleDaoJdbc();
        TariffDao tariffDao = new TariffDaoJdbc();
        PolicyDao policyDao = new PolicyDaoJdbc();
        InsuranceDao insuranceDao = new InsuranceDaoJdbc();
        PayoutDao payoutDao = new PayoutDaoJdbc();
        StatusDao statusDao = new StatusDaoJdbc();
        AdministratorDao administratorDao = new AdministratorDaoJdbc();
        ActionLogDao actionLogDao = new ActionLogDaoJdbc();
        InsurancePhotoDao insurancePhotoDao = new InsurancePhotoDaoJdbc();

        // Инициализация сервисов
        AuthService authService = new AuthService(userAccountDao);
        OwnerService ownerService = new OwnerService(ownerDao);
        VehicleService vehicleService = new VehicleService(vehicleDao);
        PolicyService policyService = new PolicyService(policyDao, tariffDao, ownerDao, vehicleDao);
        InsuranceService insuranceService = new InsuranceService(insuranceDao);
        AdminService adminService = new AdminService(tariffDao, insuranceDao, payoutDao, administratorDao, userAccountDao, policyDao, ownerDao, vehicleDao);
        ActionLogService actionLogService = new ActionLogService(actionLogDao);

        // Сохранение в контекст
        ctx.setAttribute("authService", authService);
        ctx.setAttribute("userAccountDao", userAccountDao);
        ctx.setAttribute("ownerDao", ownerDao);
        ctx.setAttribute("ownerService", ownerService);
        ctx.setAttribute("vehicleDao", vehicleDao);
        ctx.setAttribute("vehicleService", vehicleService);
        ctx.setAttribute("tariffDao", tariffDao);
        ctx.setAttribute("policyDao", policyDao);
        ctx.setAttribute("policyService", policyService);
        ctx.setAttribute("insuranceDao", insuranceDao);
        ctx.setAttribute("insuranceService", insuranceService);
        ctx.setAttribute("payoutDao", payoutDao);
        ctx.setAttribute("statusDao", statusDao);
        ctx.setAttribute("administratorDao", administratorDao);
        ctx.setAttribute("adminService", adminService);
        ctx.setAttribute("actionLogDao", actionLogDao);
        ctx.setAttribute("actionLogService", actionLogService);
        ctx.setAttribute("insurancePhotoDao", insurancePhotoDao);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // тут можно закрывать пулы соединений, если будут
    }
}