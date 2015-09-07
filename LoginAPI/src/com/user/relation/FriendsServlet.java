package com.user.relation;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.simple.JSONObject;

@SuppressWarnings("serial")
public class FriendsServlet extends HttpServlet {

	// int v, requested pages.
	
	private int v = 0;
	
	//JSONObject Instance to record the outputs
	private JSONObject obj;
	
	//servlet request with parameters(post)
	private HttpServletRequest req;
	
	//db connection using jconnector driver
	private Connection conn;
	
	private static final String GOOGLE_J_DRIVER = "jdbc:google:mysql://login-1044:mysql-userdata/android_api?user=root";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		v++;
		
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}

	/**
	 * request json example { "tag": "add" or "remove", "unique_id":
	 * "4f074eca601fb8.88015924", "friends": { "name": "Ravi Tamda" // name only
	 * exists in tags : remove, add } }
	 */
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		v++;
		PrintWriter out = resp.getWriter();

		obj = new JSONObject();
		String url;
		this.req = req;

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

		// connecting to mysql db using jconnect driver
		try {
			conn = DriverManager.getConnection(url);
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("error",true);
			obj.put("error_msg", e);
		}

		// get uid of the application user
		String tag = req.getParameter("tag");

		try {
			int uid1 = getUID();
			
			switch (tag) {
			case "parse":
				parseFriends(uid1);
				break;
			case "add":
				int uidToAdd = getFriendUID();
				addFriendRelationToDB(uid1, uidToAdd);
				break;
			case "remove":
				int uidToRemove = getFriendUID();
				removeFriendRelationFromDB(uid1, uidToRemove);
				break;
			default:
				obj.put("error", true);
				obj.put("error_msg", "no tag");
				break;
			}
		} catch (SQLException e) {
			obj.put("error", true);
			obj.put("error_msg",e);
		}
		
		out.println(obj);
	}

	/**
	 * get uid of the application user from unique_id
	 * 
	 * @return
	 * @throws SQLException
	 */
	private int getUID() throws SQLException {
		String unique_id = req.getParameter("unique_id");
		String sql1 = "SELECT uid FROM users" + " WHERE unique_id='"
				+ unique_id + "'";

		Statement stmt = conn.createStatement();
		ResultSet rs1 = stmt.executeQuery(sql1);

		int uid1 = -1;
		while (rs1.next()) {
			uid1 = rs1.getInt(1);
		}
		if (uid1 == -1) {
			obj.put("error", true);
			obj.put("error_msg", "fail to get the user's uid");
			return -1;
		}
		stmt.close();
		return uid1;
	}

	/**
	 * get uid of the friend from name parameter
	 * 
	 * @return
	 * @throws SQLException
	 */
	private int getFriendUID() throws SQLException {
		String f_name = req.getParameter("name");
		int uid2 = -1;
		Statement stmt = conn.createStatement();

		if (!f_name.equals("")) {
			String sql2 = "SELECT uid FROM users" + " WHERE name='" + f_name
					+ "'";

			ResultSet rs2 = stmt.executeQuery(sql2);
			while (rs2.next()) {
				uid2 = rs2.getInt(1);
			}
			if (uid2 == -1) {
				obj.put("error", true);
				obj.put("error_msg", "fail to get the friend's uid");
			}
		}
		stmt.close();
		return uid2;
	}

	/**
	 * 
	 * @param uid1
	 *            = application user id
	 * @param uid2
	 *            = friend user id
	 * @throws SQLException
	 */
	private void addFriendRelationToDB(int uid1, int uid2) throws SQLException {
		String sql3 = "INSERT INTO friends (user_id,friend_id,createdAt,updatedAt) VALUES( ? , ? , ?, ?)";

		PreparedStatement p_stmt = (PreparedStatement) conn
				.prepareStatement(sql3);
		PreparedStatement p_stmt_reverse = (PreparedStatement) conn
				.prepareStatement(sql3);

		p_stmt.setInt(1, uid1);
		p_stmt.setInt(2, uid2);
		p_stmt.setString(3, null);
		p_stmt.setString(4, null);

		p_stmt_reverse.setInt(2, uid1);
		p_stmt_reverse.setInt(1, uid2);
		p_stmt_reverse.setString(3, null);
		p_stmt_reverse.setString(4, null);

		int success = 2;

		try {
			success = p_stmt.executeUpdate();
			success = p_stmt_reverse.executeUpdate();
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) {
				obj.put("error", 2);
				obj.put("success", 0);
				obj.put("error_msg", "Relation already exist");
			} else {
				obj.put("error", 1);
				obj.put("success", 0);
				obj.put("error_msg", "SQL Error Occured!");
			}
		}

		p_stmt.close();
		p_stmt_reverse.close();

		if (success == 1) {
			obj.put("success", success);
		}
	}

	/**
	 * Delete friend relation from the DB.
	 * 
	 * @param uid1
	 * @param uid2
	 * @throws SQLException
	 */
	private void removeFriendRelationFromDB(int uid1, int uid2)
			throws SQLException {
		String sql_remove = "DELETE FROM friends WHERE user_id = '" + uid1
				+ "' AND friend_id = '" + uid2 + "'";
		String sql_remove2 = "DELETE FROM friends WHERE user_id = '" + uid2
				+ "' AND friend_id = '" + uid1 + "'";

		Statement stmt_remove = conn.createStatement();
		stmt_remove.execute(sql_remove);
		stmt_remove.execute(sql_remove2);
	}

	/**
	 * Parse Friends of the application user
	 * 
	 * @throws SQLException
	 */
	private void parseFriends(int uid1) throws SQLException {
		Statement stmt_parse = conn.createStatement();

		String sql_parse = "SELECT users.name, users.email FROM users INNER JOIN friends ON friends.friend_id = users.uid WHERE friends.user_id = '"
				+ uid1 + "'";
		ResultSet rs_parse = stmt_parse.executeQuery(sql_parse);

		int i = 0;
		while (rs_parse.next()) {
			i++;
			JSONObject friend = new JSONObject();

			friend.put("name", rs_parse.getString(1));
			friend.put("email", rs_parse.getString(2));
			obj.put("friend" + i, friend);
		}
		obj.put("success",true);
		stmt_parse.close();
	}
};
