package com.example.insurancecompany.servlet;

import com.example.insurancecompany.dao.InsurancePhotoDao;
import com.example.insurancecompany.model.InsurancePhoto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/photo/*")
public class PhotoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Photo ID required");
            return;
        }

        try {
            int photoId = Integer.parseInt(pathInfo.substring(1));
            InsurancePhotoDao photoDao = (InsurancePhotoDao) getServletContext().getAttribute("insurancePhotoDao");
            
            if (photoDao == null) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Photo DAO not initialized");
                return;
            }

            InsurancePhoto photo = photoDao.findById(photoId);
            if (photo == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Photo not found");
                return;
            }

            String filePath = getServletContext().getRealPath("") + File.separator + photo.getFilePath();
            File file = new File(filePath);

            if (!file.exists() || !file.isFile()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Photo file not found");
                return;
            }

            // Устанавливаем заголовки
            resp.setContentType(photo.getMimeType() != null ? photo.getMimeType() : "application/octet-stream");
            resp.setContentLengthLong(file.length());
            resp.setHeader("Content-Disposition", "inline; filename=\"" + photo.getFileName() + "\"");

            // Отправляем файл
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = resp.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid photo ID");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error serving photo");
        }
    }
}

