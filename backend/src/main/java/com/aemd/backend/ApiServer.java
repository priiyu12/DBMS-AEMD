package com.aemd.backend;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/*")
public class ApiServer extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        resp.setContentType("application/json");

        String path = req.getPathInfo();

        if (path != null && path.equals("/query")) {
            handleQuery(req, resp);
            return;
        }

        String name = req.getParameter("name");
        if (name == null) {
            resp.getWriter().write("{\"error\":\"missing name\"}");
            return;
        }

        resp.getWriter().write("{\"message\":\"Login OK\", \"name\":\"" + name + "\"}");
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setContentType("application/json; charset=UTF-8");

        String action = req.getParameter("action");
        if (action == null) {
            resp.getWriter().write("{\"success\":false, \"message\":\"missing action\"}");
            return;
        }

        try {
            switch (action) {

                /* ---------- INSERT VISITOR ---------- */
                case "insertVisitor": {
                    String tickettype = req.getParameter("tickettype");
                    String visitdate = req.getParameter("visitdate");
                    String feedback = req.getParameter("feedback");
                    String ratingsStr = req.getParameter("feedbackratings");

                    if (tickettype == null || tickettype.trim().isEmpty()) {
                        resp.getWriter().write("{\"success\":false, \"message\":\"tickettype required\"}");
                        return;
                    }

                    Integer rating = null;
                    if (ratingsStr != null && !ratingsStr.isEmpty()) {
                        rating = Integer.parseInt(ratingsStr);
                    }

                    int newId = CRUDOperations.insertVisitor(tickettype, visitdate, feedback, rating);
                    if (newId > 0)
                        resp.getWriter().write("{\"success\":true, \"message\":\"Visitor added\", \"id\":" + newId + "}");
                    else
                        resp.getWriter().write("{\"success\":false, \"message\":\"Insert visitor failed\"}");
                    break;
                }

                /* ---------- INSERT ARTWORK ---------- */
                case "insertArtwork": {
                    String title = req.getParameter("title");
                    String medium = req.getParameter("medium");
                    String dimensions = req.getParameter("dimensions");
                    String yearStr = req.getParameter("yearcreated");
                    String description = req.getParameter("description");
                    String imageurl = req.getParameter("imageurl");
                    String studentIdStr = req.getParameter("studentid");
                    String saleStr = req.getParameter("isforsale");

                    if (title == null || title.isEmpty() || studentIdStr == null || studentIdStr.isEmpty()) {
                        resp.getWriter().write("{\"success\":false, \"message\":\"title and studentid required\"}");
                        return;
                    }

                    Integer year = null;
                    if (yearStr != null && !yearStr.isEmpty()) year = Integer.parseInt(yearStr);

                    int studentId = Integer.parseInt(studentIdStr);
                    boolean isforsale = "on".equalsIgnoreCase(saleStr);

                    int newId = CRUDOperations.insertArtwork(title, medium, dimensions, year, description, imageurl, studentId, isforsale);

                    if (newId > 0)
                        resp.getWriter().write("{\"success\":true, \"message\":\"Artwork inserted\", \"id\":" + newId + "}");
                    else
                        resp.getWriter().write("{\"success\":false, \"message\":\"Insert artwork failed\"}");
                    break;
                }

                /* ---------- UPDATE NOTIFICATION ---------- */
                case "updateNotification": {
                    String id = req.getParameter("id");
                    String status = req.getParameter("status");

                    boolean ok = CRUDOperations.updateNotificationStatus(Integer.parseInt(id), status);
                    resp.getWriter().write("{\"success\":" + ok + "}");
                    break;
                }

                /* ---------- DELETE NOTIFICATION ---------- */
                case "deleteNotification": {
                    String id = req.getParameter("id");
                    boolean ok = CRUDOperations.deleteNotification(Integer.parseInt(id));
                    resp.getWriter().write("{\"success\":" + ok + "}");
                    break;
                }

                default:
                    resp.getWriter().write("{\"success\":false, \"message\":\"unknown action: " + action + "\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"success\":false, \"message\":\"server error " + e.getMessage() + "\"}");
        }
    }


    private void handleQuery(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String table = req.getParameter("table");

        if (!TableNames.isValid(table)) {
            resp.getWriter().write("{\"error\":\"Invalid or unsafe table\"}");
            return;
        }

        QueryOperations.TableResult result = QueryOperations.getTopAndBottom10(table);

        resp.getWriter().write(
                "{\"top10\":" + result.top10 + ",\"bottom10\":" + result.bottom10 + "}"
        );
    }
}
