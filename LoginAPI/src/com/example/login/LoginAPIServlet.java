package com.example.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

import com.mysql.jdbc.PreparedStatement;


/**
 * based on the article androidhive.info
 */

//success result example;
//{
//    "tag": "register",dd
//    "error": false,
//    "uid": "54f2c651c42e18.10964974",
//    "user": {
//        "name": "Ravi Tamada",
//        "email": "admin@androidhive.com",
//        "created_at": "2015-03-01 13:27:05",
//        "updated_at": null
//    }
//}
//
//{
//    "tag": "login",
//    "success": 1,
//    "error": 0,
//    "uid": "4f074eca601fb8.88015924",
//    "user": {
//        "name": "Ravi Tamada",
//        "email": "ravi8x@gmail.com",
//        "created_at": "2012-01-07 01:03:53",
//        "updated_at": null
//    }
//}


@SuppressWarnings({ "serial", "unchecked" })
public class LoginAPIServlet extends HttpServlet {

	private static final String GOOGLE_J_DRIVER = "jdbc:google:mysql://login-1044:mysql-userdata/android_api?user=root";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
	}

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		PrintWriter out = resp.getWriter();
		JSONObject obj = new JSONObject();
		Connection conn = null;
		String url;

		// Setting up materials to connect the mysql db from Google app engine
		// Must enable google-connector-j set true beforehand
		try {
			Class.forName("com.mysql.jdbc.GoogleDriver");
			url = GOOGLE_J_DRIVER;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			obj.put("error", true);
			obj.put("error_msg", "mysql_driver error");
			e.printStackTrace();
			return;
		}

		try {
			conn = DriverManager.getConnection(url);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("error", true);
			obj.put("error_msg", e);
		}

		try {
			String tag = req.getParameter("tag");

			obj.put("tag", tag);
			switch (tag) {
			case "login":
				loginFunction(req,conn,obj);
				break;
			case "register":
				registerFunction(req,conn,obj);
				break;
			default:
				obj.put("error", true);
				obj.put("error_msg", "wrong tag for the login api");
			}
		} catch (SQLException e) {
			obj.put("error", true);
			obj.put("error_msg", e);
			e.printStackTrace();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e2) {
			obj.put("error", true);
			obj.put("error_msg", e2);
			e2.printStackTrace();

		}
		out.println(obj);
	}

	private void loginFunction(HttpServletRequest req, Connection conn, JSONObject obj) throws SQLException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		String email = req.getParameter("email");
		String password = req.getParameter("password");

		String sql = "SELECT unique_id, name, email, encrypted_password, created_at, updated_at FROM users"
				+ " WHERE email ='" + email + "'";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			JSONObject userObj = new JSONObject();
			// login success
			if (PasswordHash.validatePassword(password,
					rs.getString("encrypted_password"))) {

				obj.put("unique_id", rs.getString("unique_id"));
				obj.put("success", 1);
				obj.put("error", 0);
				userObj.put("email", rs.getString("email"));
				userObj.put("name", rs.getString("name"));
				userObj.put("created_at", rs.getString("created_at"));
				userObj.put("updated_at", rs.getString("updated_at"));
				obj.put("user", userObj);

			} else {
				obj.put("success", 0);
				obj.put("error", 1);
				obj.put("error_msg", "Incorrect email or password!");
			}
		}
	}

	private void registerFunction(HttpServletRequest req, Connection conn, JSONObject obj) throws SQLException {
		String name = req.getParameter("name");
		String email = req.getParameter("email");
		String password = req.getParameter("password");

		ArrayList<String> sqlValues = UserStore.getStoreUserQuery(name, email,
				password);
		String statement = "INSERT INTO users (unique_id, name, email, encrypted_password) VALUES( ? , ? , ?, ?)";
		PreparedStatement stmt = (PreparedStatement) conn
				.prepareStatement(statement);

		stmt.setString(1, sqlValues.get(0));
		stmt.setString(2, sqlValues.get(1));
		stmt.setString(3, sqlValues.get(2));
		stmt.setString(4, sqlValues.get(3));

		int success = 2;
		try {
			success = stmt.executeUpdate();
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) {
				obj.put("error", 2);
				obj.put("success", 0);
				obj.put("error_msg", "User already existed");
			} else {
				obj.put("error", 1);
				obj.put("success", 0);
				obj.put("error_msg", "SQL Error Occured!");
			}
		}
		if (success == 1) {
			JSONObject user = new JSONObject();
			obj.put("success", success);
			obj.put("error", false);
			obj.put("unique_id", sqlValues.get(0));
			user.put("name", sqlValues.get(1));
			user.put("email", sqlValues.get(2));
			user.put("created_at", null);
			user.put("updated_at", null);
			obj.put("user", user);

			/**
			 * { "tag": "register", "error": false, "uid":
			 * "54f2c651c42e18.10964974", "user": { "name": "Ravi Tamada",
			 * "email": "admin@androidhive.com", "created_at":
			 * "2015-03-01 13:27:05", "updated_at": null } }
			 */
		}
	}
}