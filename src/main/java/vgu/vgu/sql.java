/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vgu.vgu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class sql {

	Connection c = null;
	Statement stmt = null;
	PreparedStatement pstmt = null;
	Config config = Config.getConfig();
	String url = "jdbc:mysql://" + config.getDbServer() + ":" + config.getDbPort() + "/" + config.getDbName()
			+ "?rewriteBatchedStatements=true&allowMultiQueries=true&useUnicode=yes&characterEncoding=UTF-8";

	public sql() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection(url, config.getDbUserName(), config.getDbPassword());
			System.out.println("Connected to DB OK!");
			c.isClosed();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void closeConnection() {
		try {
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// get a class
	public Classroom getClass(String name) {
		String query = "select * from Class where name='" + name + "'";
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				int rows = rs.getInt("numofrows");
				int cols = rs.getInt("numofcolumns");
				int starthour = rs.getInt("starthour");
				int startminute = rs.getInt("startminute");
				int endhour = rs.getInt("endhour");
				int endminute = rs.getInt("endminute");
				int active = rs.getInt("active");
				return new Classroom(name, rows, cols, starthour, startminute, endhour, endminute, active);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// get all states of a class
	public List<State> getStates(String className) {
		String query = "select rownum, colnum, studentId from state where className='" + className + "'";
		List<State> stateList = new ArrayList<State>();
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int row = rs.getInt("rownum");
				int col = rs.getInt("colnum");
				int student = rs.getInt("studentId");
				State state = new State(row, col, student);
				stateList.add(state);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return stateList;
	}

	// get a student from student id
	public Student getStudent(int id) {
		String query = "select name from student where id =" + id;
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return new Student(rs.getString("name"));
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// get all classes
	public List<Classroom> getClasses() throws SQLException, ParseException {
		String query = "select name from Class";
		List<Classroom> classList = new ArrayList<Classroom>();
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				Classroom c = new Classroom(rs.getString("name"));
				classList.add(c);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return classList;
	}

	// create student
	public boolean createStudent(String name) {
		String query = "insert into student (name)\r\n" + "values ('" + name + "')";
		try {
			this.stmt = c.createStatement();
			stmt.executeUpdate(query);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// create state
	public boolean createState(String className, int row, int col, int student) {
		String query = "insert into state (className, rownum, colnum, studentId)\r\n" + "values (?, ?, ?, ?)";
		if (!classIsOn(className)) {
			return false;
		} else {
			try (PreparedStatement pstmt = c.prepareStatement(query)) {
				pstmt.setString(1, className);
				pstmt.setInt(2, row);
				pstmt.setInt(3, col);
				pstmt.setInt(4, student);
				pstmt.executeUpdate();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	// create class
	public boolean createClass(String name, int row, int col, int starthour, int startminute, int endhour, int endminute) {
		String query = "insert into Class (name, numofrows, numofcolumns, starthour, startminute, endhour, endminute)\r\n"
				+ "values (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = c.prepareStatement(query)) {
			pstmt.setString(1, name);
			pstmt.setInt(2, row);
			pstmt.setInt(3, col);
			pstmt.setInt(4, starthour);
			pstmt.setInt(5, startminute);
			pstmt.setInt(6, endhour);
			pstmt.setInt(7, endminute);
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// activate, or deactivate a class
	public boolean updateClassSession(String name, int activate) {
		String query = "update Class\r\n" + "set active=" + activate + "\r\n" + "where name='" + name + "'";
		try {
			this.stmt = c.createStatement();
			stmt.executeUpdate(query);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// check if class is active
	public boolean classIsOn(String name) {
		String query = "select active from Class where name='" + name + "'";
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return rs.getBoolean("active");
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// clean state of a class
	public void cleanState(String className) {
		String query = "delete from state where className='" + className + "'";
		try {
			this.stmt = c.createStatement();
			stmt.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
