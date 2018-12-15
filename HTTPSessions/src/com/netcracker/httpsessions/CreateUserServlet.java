package com.netcracker.httpsessions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class CreateUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");


        PrintWriter out = resp.getWriter();
        if(saveUser(login, password)) {
            out.print("Account created");
        }
        else {
            out.print("Login exists");
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }


    private boolean saveUser(String login, String password) throws IOException {

    if(!checkLogin(login)) {
        try(FileWriter writer = new FileWriter(getServletContext().getRealPath("/users"), true);
            BufferedWriter bufWriter = new BufferedWriter(writer)) {
            String account = "\n"+login + "=" + password;

            bufWriter.write(account);

            return true;
        }

    }

     return false;

    }

    private boolean checkLogin(String login) throws IOException {
        String line = "";
        try(FileReader fin = new FileReader(getServletContext().getRealPath("/users"));
            BufferedReader breader = new BufferedReader(fin)) {

            while ((line=breader.readLine()) != null) {
                String[] strArr = line.split("=");
                if(login.equals(strArr[0])) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
