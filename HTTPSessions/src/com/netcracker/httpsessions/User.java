package com.netcracker.httpsessions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;

public class User {
    private String login;
    private String password;

    public User(HttpServletRequest req, HttpServletResponse resp) {
        this.login = req.getParameter("login");
        this.password = req.getParameter("password");
    }

}
