package com.netcracker.cookies;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class CookieServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        String login = req.getParameter("login");
        String password = req.getParameter("password");
        PrintWriter out = resp.getWriter();
        String checkbox = req.getParameter("checkbox");

        Cookie[] cookies = req.getCookies();
        if( cookies != null ) {
            if(isLoginInCookie(login, cookies)) {
                out.print("Access granted. \n Welcome, " + login);
            }
            else if(!login.isEmpty()) {
                validation(login, password, checkbox, out, req, resp);
            }
            else {
                out.print("Please, log in again or insert only login if you`ve logged in succed earlier");
            }
        }
        else {
            validation(login, password, checkbox, out, req, resp);
        }

        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    private boolean isLoginInCookie(String login, Cookie[] cookies) {
        for(int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            if(login.equals(cookie.getName())) {
                return true;
            }
        }
        return false;
    }

    private void validation(String login, String password, String checkbox, PrintWriter out, HttpServletRequest req, HttpServletResponse resp) {
        if(isValid(login, password, req, resp)) {
            if("on".equals(checkbox)) {
                Cookie user = new Cookie(login, password);;
                resp.addCookie(user);
                out.print("Access granted. \n Welcome, " + login);
            }
            else {
                out.print("Access granted. \n Welcome, " + login);
            }
        }
    }




    protected boolean isValid(String login, String password, HttpServletRequest req, HttpServletResponse resp) {
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
                        sendError(login, req, resp);
                    }
                    break;
                }
            }
            if(!isLogin) {
                createUser(req, resp);
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

    protected void createUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher view = req.getRequestDispatcher("create.html");
        view.forward(req, resp);
    }

    protected void sendError(String login, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int n = 0;
        String nameCookie = "errors" + login;

        Cookie[] cookies = req.getCookies();
        if(cookies != null) {
            for(int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];

                if(nameCookie.equals(cookie.getName())) {
                    n = Integer.parseInt(cookie.getValue());
                }
            }
        }

        if(n>3) {
            updateUser(login, req, resp);
            return;
        }

        Cookie error = new Cookie("errors"+login, Integer.toString(n+1));
        resp.addCookie(error);

        RequestDispatcher view = req.getRequestDispatcher("error.html");
        view.forward(req, resp);
    }

    private void updateUser(String login, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Update User</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<form action=\"CreateUserServlet\" method=\"POST\">\n" +
                "    Login: <input type=\"text\" name=\"login\"value=\"" + login + "\">\n" +
                "    <br />\n" +
                "    Password: <input type=\"text\" name=\"password\" />\n" +
                "    <input type=\"submit\" value=\"Create\" />\n" +
                "</form>\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        PrintWriter out = resp.getWriter();
        deleteUser(login);
        out.print(html);
    }

    private void deleteUser(String login) {
        String line = "";
        try(FileReader fin = new FileReader(getServletContext().getRealPath("/users"));
            BufferedReader breader = new BufferedReader(fin);
            FileWriter writer = new FileWriter(getServletContext().getRealPath("/tmp"), false);
            BufferedWriter bufWriter = new BufferedWriter(writer)) {

            while ((line=breader.readLine()) != null) {
                String[] strArr = line.split("=");
                if (login.equals(strArr[0])) {
                    continue;
                }
                else {
                    bufWriter.write(line+"\n");
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        rewriteFile();
    }

    private void rewriteFile() {
        String line = "";
        try(FileReader fin = new FileReader(getServletContext().getRealPath("/tmp"));
            BufferedReader breader = new BufferedReader(fin);
            FileWriter writer = new FileWriter(getServletContext().getRealPath("/users"), false);
            BufferedWriter bufWriter = new BufferedWriter(writer)) {

            while ((line=breader.readLine()) != null) {
                String[] strArr = line.split("=");
                    bufWriter.write(line+"\n");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
