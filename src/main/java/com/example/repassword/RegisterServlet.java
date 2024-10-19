package com.example.repassword;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final List<User> userList = Collections.synchronizedList(new ArrayList<>());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String username = request.getParameter("username").trim();
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String gender = request.getParameter("gender");
        String ageStr = request.getParameter("age");
        int age;

        StringBuilder errorMessages = new StringBuilder();
        if (username.isEmpty()) {
            errorMessages.append("用户名不能为空！<br>");
        }
        if (password.isEmpty()) {
            errorMessages.append("密码不能为空！<br>");
        }
        if (!password.equals(confirmPassword)) {
            errorMessages.append("两次输入的密码不一致！<br>");
        }
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0) {
                errorMessages.append("年龄必须为正整数！<br>");
            }
        } catch (NumberFormatException e) {
            errorMessages.append("年龄输入无效！<br>");
            age = 0; // 默认值
        }

        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"zh-CN\">");
        out.println("<head>");
        out.println("<meta charset=\"UTF-8\">");
        out.println("<title>注册结果</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println("table { border-collapse: collapse; width: 80%; margin-top: 20px; }");
        out.println("table, th, td { border: 1px solid #333; }");
        out.println("th, td { padding: 8px 12px; text-align: center; }");
        out.println("a { display: inline-block; margin-top: 20px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        if (errorMessages.length() > 0) {
            out.println("<h3 style=\"color:red;\">注册失败：</h3>");
            out.println("<p>" + errorMessages.toString() + "</p>");
            out.println("<a href=\"register.html\">返回注册页面</a>");
        } else {
            synchronized (userList) {
                // 检查用户名是否已存在
                boolean exists = userList.stream()
                        .anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
                if (exists) {
                    out.println("<h3 style=\"color:red;\">注册失败：用户名已存在！</h3>");
                    out.println("<a href=\"register.html\">返回注册页面</a>");
                } else {
                    // 创建用户对象并添加到用户列表
                    User newUser = new User(username, password, gender, age);
                    userList.add(newUser);

                    // 显示注册成功的消息
                    out.println("<h3 style=\"color:green;\">注册成功！</h3>");

                    // 显示所有已注册用户的信息
                    out.println("<h3>当前所有注册用户信息：</h3>");
                    out.println("<table>");
                    out.println("<tr><th>用户名</th><th>性别</th><th>年龄</th></tr>");
                    for (User user : userList) {
                        out.println("<tr>");
                        out.println("<td>" + escapeHtml(user.getUsername()) + "</td>");
                        out.println("<td>" + escapeHtml(user.getGender()) + "</td>");
                        out.println("<td>" + user.getAge() + "</td>");
                        out.println("</tr>");
                    }
                    out.println("</table>");
                    out.println("<a href=\"register.html\">返回注册页面</a>");
                }
            }
        }

        out.println("</body>");
        out.println("</html>");
    }


    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    // 用户类，用于保存用户信息
    private static class User {
        private final String username;
        private final String password;
        private final String gender;
        private final int age;

        public User(String username, String password, String gender, int age) {
            this.username = username;
            this.password = password;
            this.gender = gender;
            this.age = age;
        }

        public String getUsername() {
            return username;
        }

        public String getGender() {
            return gender;
        }

        public int getAge() {
            return age;
        }
    }
}
