package com.netcracker.httpsessions;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;

public class MyFirstServlet extends HttpServlet {
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.request = req;
        this.response = resp;

        String login = this.request.getParameter("login");
        String password = this.request.getParameter("password");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession();

        if(!session.isNew()) {
            out.print("Access granted. \n Welcome, " + session.getAttribute("login"));
        }

        else if(isValid(login, password)) {
            out.print("Access granted. \n Welcome, " + login);
            session.setAttribute("login", login);
            session.setAttribute("password", password);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    protected boolean isValid(String login, String password) {
        String line = "";
        boolean valid = false;
        boolean isLogin = false;

        try(FileReader fin = new FileReader(getServletContext().getRealPath("/users"));
            BufferedReader breader = new BufferedReader(fin)) {

            while ((line=breader.readLine()) != null) {
                String[] strArr = line.split("=");
                if(login.equals(strArr[0])) {
                    isLogin = true;
                    if(password.equals(strArr[1])) {
                        valid = true;
                    }
                    else {
                        sendError();
                    }
                    break;
                }
            }
            if(!isLogin) {
                createUser();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }

        return valid;
    }

    protected void createUser() throws ServletException, IOException {
        RequestDispatcher view = this.request.getRequestDispatcher("create.html");
        view.forward(this.request, this.response);
    }

    protected void sendError() throws ServletException, IOException {
        RequestDispatcher view = this.request.getRequestDispatcher("error.html");
        view.forward(this.request, this.response);
    }
}
