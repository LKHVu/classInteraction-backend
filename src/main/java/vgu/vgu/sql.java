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
				int active = rs.getInt("active");
				String year = rs.getString("year");
				return new Classroom(name, rows, cols, active, year);
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
		String query = "select * from student where id =" + id;
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				String name = rs.getString("name");
				String year = rs.getString("year");
				String img = rs.getString("img");
				int exchange = rs.getInt("exchange");
				return new Student(name, img, year, exchange);
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
	public boolean createStudent(String name, String img, String year, boolean exchange) {
		String query = "insert into student (name, img, year, exchange)\r\n" + "values (?, ?, ?, ?)";
		try (PreparedStatement pstmt = c.prepareStatement(query)) {
			pstmt.setString(1, name);
			pstmt.setString(2, img);
			pstmt.setString(3, year);
			pstmt.setBoolean(4, exchange);
			pstmt.executeUpdate();
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
	public boolean createClass(String name, int row, int col) {
		String query = "insert into Class (name, numofrows, numofcolumns)\r\n" + "values (?, ?, ?)";
		try (PreparedStatement pstmt = c.prepareStatement(query)) {
			pstmt.setString(1, name);
			pstmt.setInt(2, row);
			pstmt.setInt(3, col);
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// activate a class
	public boolean activateClass(String name, String year) {
		String query = "update Class\r\n" + "set active = true, year = ?\r\n" + "where name = ?";
		try (PreparedStatement pstmt = c.prepareStatement(query)) {
			pstmt.setString(1, year);
			pstmt.setString(2, name);
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//deactivate a class
	public boolean deactivateClass(String name) {
		String query = "update Class set active = false, year = null where name = '" + name + "'";
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
