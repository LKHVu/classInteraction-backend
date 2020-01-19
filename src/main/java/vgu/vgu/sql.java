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
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

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
	public User getStudent(int id) {
		String query = "select * from users where id =" + id;
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				String name = rs.getString("name");
				String year = rs.getString("year");
				String img = rs.getString("img");
				int exchange = rs.getInt("exchange");
				return new User(name, img, year, exchange);
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
	public boolean createStudent(String name, String img, String year, boolean exchange, String login,
			String password) {
		String query = "insert into users (name, img, year, exchange, login, password, role)\r\n"
				+ "values (?, ?, ?, ?, ?, ?, 3)";
		try (PreparedStatement pstmt = c.prepareStatement(query)) {
			pstmt.setString(1, name);
			pstmt.setString(2, img);
			pstmt.setString(3, year);
			pstmt.setBoolean(4, exchange);
			pstmt.setString(5, login);
			pstmt.setString(6, password);
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// create state
	public String createState(String className, int row, int col, int student) {
		deleteSeat(student);
		String query = "insert into state (className, rownum, colnum, studentId)\r\n" + "values (?, ?, ?, ?)";
		if (!classIsOn(className)) {
			return "Class is off";
		} else {
			try (PreparedStatement pstmt = c.prepareStatement(query)) {
				pstmt.setString(1, className);
				pstmt.setInt(2, row);
				pstmt.setInt(3, col);
				pstmt.setInt(4, student);
				pstmt.executeUpdate();
				return "Seat is taken successfully";
			} catch (SQLIntegrityConstraintViolationException ex) {
				return "Seat is occupied already";
			} catch (Exception e) {
				e.printStackTrace();
				return "Can't take seat";
			}
		}
	}
	
	// delete seat of a student
	public void deleteSeat(int student) {
		String query = "select * from state where studentId=" + student;
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				deleteSeat_(student);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteSeat_(int student) {
		String query = "delete from state where studentId=" + student;
		try {
			this.stmt = c.createStatement();
			stmt.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
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

	// create a multiple choice question
	public boolean createMultipleChoiceQuestion(String name, String className, String question, String A, String B,
			String C, String D, int time, String solution) {
		String query = "insert into MultipleChoiceQuestion (name, class, question, A, B, C, D, `time`, answer)\r\n"
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = c.prepareStatement(query)) {
			pstmt.setString(1, name);
			pstmt.setString(2, className);
			pstmt.setString(3, question);
			pstmt.setString(4, A);
			pstmt.setString(5, B);
			pstmt.setString(6, C);
			pstmt.setString(7, D);
			pstmt.setInt(8, time);
			pstmt.setString(9, solution);
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// create a multiple choice answer
	public boolean createMultipleChoiceAnswer(int questionId, int studentId, String answer) {
		if (questionIsFinished(questionId)) {
			return false;
		} else {
			String query = "insert into MultipleChoiceAnswer (questionId, studentId, answer)\r\n" + "values (?, ?, ?)";
			try (PreparedStatement pstmt = c.prepareStatement(query)) {
				pstmt.setInt(1, questionId);
				pstmt.setInt(2, studentId);
				pstmt.setString(3, answer);
				pstmt.executeUpdate();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
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

	// deactivate a class
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

	// count how many right, wrong, or no answers
	public Integer countAnswers(int questionId, String choice) {
		String query = "select count(*) as count from (\r\n"
				+ "select a.questionId, a.studentId from MultipleChoiceQuestion q\r\n"
				+ "join MultipleChoiceAnswer a on q.id = a.questionid\r\n" + "where q.id = " + questionId;
		if (choice.equals("right")) {
			query += " and q.answer=a.answer) b";
		} else if (choice.equals("wrong")) {
			query += " and q.answer<>a.answer and a.answer<>'N') b";
		} else if (choice.equals("no")) {
			query += " and a.answer='N') b";
		}
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return rs.getInt("count");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// show questions based on state
	public List<MultipleChoiceQuestion> getQuestions(String className, String choice) {
		String query = "select id, name from MultipleChoiceQuestion where class = '" + className + "' ";
		if (choice.equals("finished")) {
			query += "and finished=true";
		} else if (choice.equals("inactive")) {
			query += "and active = false and finished = false";
		} else if (choice.equals("active")) {
			query += "and active=true";
		}
		List<MultipleChoiceQuestion> result = new ArrayList<MultipleChoiceQuestion>();
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				result.add(new MultipleChoiceQuestion(rs.getInt("id"), rs.getString("name")));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// show the name of the quiz
	public String getQuestionName(int questionId) {
		String query = "select name from MultipleChoiceQuestion where id=" + questionId;
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return rs.getString("name");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// activate a quiz
	public void activateQuestion(int questionId) {
		String query = "update MultipleChoiceQuestion\r\n" + "set active=true\r\n" + "where id=" + questionId;
		try {
			this.stmt = c.createStatement();
			stmt.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// decrease time for a quiz
	public void decreaseTime(int questionId) {
		String query = "update MultipleChoiceQuestion\r\n" + "set `time`=`time`-1\r\n" + "where id=" + questionId;
		try {
			this.stmt = c.createStatement();
			stmt.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// finish a quiz
	public void finishQuestion(int questionId) {
		String query = "update MultipleChoiceQuestion\r\n" + "set active=false, finished=true\r\n" + "where id="
				+ questionId;
		try {
			this.stmt = c.createStatement();
			stmt.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// get time of a quiz
	public Integer getQuestionTime(int questionId) {
		String query = "select time from MultipleChoiceQuestion where id=" + questionId;
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return rs.getInt("time");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// check if quiz is finished
	public Boolean questionIsFinished(int questionId) {
		String query = "select finished from MultipleChoiceQuestion where id=" + questionId;
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return rs.getBoolean("finished");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// find what class a quiz belongs to
	public String findClassForAQuestion(int questionId) {
		String query = "select class from MultipleChoiceQuestion where id=" + questionId;
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return rs.getString("class");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// find the student Ids who did not answer the question
	public List<Integer> findStudentsWhoDidNotAnswer(int questionId) {
		String className = findClassForAQuestion(questionId);
		String query = "select id from users\r\n" + "where id in (\r\n" + "select studentId from state\r\n"
				+ "where className = ?)\r\n" + "and id not in (\r\n" + "select studentId from MultipleChoiceAnswer\r\n"
				+ "where questionId = ?)";
		List<Integer> result = new ArrayList<Integer>();
		try (PreparedStatement pstmt = c.prepareStatement(query)) {
			pstmt.setString(1, className);
			pstmt.setInt(2, questionId);
			ResultSet rs = null;
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(rs.getInt("id"));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// create answer "N" for student who didn't have any answers
	public void createNoAnswer(int questionId) {
		List<Integer> idList = findStudentsWhoDidNotAnswer(questionId);
		String query = "insert into MultipleChoiceAnswer (questionId, studentId, answer)\r\n" + "values (?, ?, 'N')";
		try (PreparedStatement pstmt = c.prepareStatement(query)) {
			for (int id : idList) {
				pstmt.setInt(1, questionId);
				pstmt.setInt(2, id);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// get a quiz
	public MultipleChoiceQuestion getQuestion(int questionId) {
		String query = "select * from MultipleChoiceQuestion where id=" + questionId;
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				String name = rs.getString("name");
				String question = rs.getString("question");
				String A = rs.getString("A");
				String B = rs.getString("B");
				String C = rs.getString("C");
				String D = rs.getString("D");
				int time = rs.getInt("time");
				String solution = rs.getString("answer");
				int active = rs.getInt("active");
				int finished = rs.getInt("finished");
				return new MultipleChoiceQuestion(name, question, A, B, C, D, time, solution, active, finished);

			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// get all quizzes of a student
	public List<MultipleChoiceQuestion> getQuestionsDoneByAStudent(int studentId) {
		String query = "select q.name, q.id from MultipleChoiceAnswer a\r\n"
				+ "join MultipleChoiceQuestion q on a.questionId = q.id\r\n" + "where a.studentId = " + studentId;
		List<MultipleChoiceQuestion> result = new ArrayList<MultipleChoiceQuestion>();
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				result.add(new MultipleChoiceQuestion(rs.getInt("id"), rs.getString("name")));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// get review of a quiz
	public QuizReview getReview(int questionId, int studentId) {
		String query = "select q.name, q.question, q.A, q.B, q.C, q.D, q.answer as solution, a.answer from MultipleChoiceAnswer a \r\n"
				+ "join MultipleChoiceQuestion q on a.questionId = q.id\r\n" + "where a.studentId = ? and q.id = ?";
		try (PreparedStatement pstmt = c.prepareStatement(query)) {
			pstmt.setInt(1, studentId);
			pstmt.setInt(2, questionId);
			ResultSet rs = null;
			rs = pstmt.executeQuery();
			if (rs.next()) {
				String quizName = rs.getString("name");
				String question = rs.getString("question");
				String A = rs.getString("A");
				String B = rs.getString("B");
				String C = rs.getString("C");
				String D = rs.getString("D");
				String solution = rs.getString("solution");
				String answer = rs.getString("answer");
				return new QuizReview(quizName, question, A, B, C, D, solution, answer);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// get answer by seat during the test
	public List<AnswerBySeat> getAnswersBySeat(int questionId) {
		String query = "select stu.name, stu.img, s.rownum, s.colnum, a.answer, q.answer as solution from MultipleChoiceAnswer a\r\n"
				+ "join state s using (studentId)\r\n" + "join MultipleChoiceQuestion q on a.questionId = q.id \r\n"
				+ "join users stu on a.studentId = stu.id\r\n" + "where questionId=" + questionId;
		List<AnswerBySeat> result = new ArrayList<AnswerBySeat>();
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String name = rs.getString("name");
				String img = rs.getString("img");
				int row = rs.getInt("rownum");
				int col = rs.getInt("colnum");
				String answer = rs.getString("answer");
				String solution = rs.getString("solution");
				result.add(new AnswerBySeat(name, img, row, col, answer, solution));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// check if that class is having an attention call of not
	public Boolean thereIsACall(String className) {
		String query = "select * from state\r\n" + "where attention = true\r\n" + "and className ='" + className + "'";
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// check what student is in what class
	public String getClassName(int studentId) {
		String query = "select className from state where studentId =" + studentId;
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return rs.getString("className");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// set mode of attention
	public String callForAttention(int studentId) {
		if (thereIsACall(getClassName(studentId))) {
			return "There is already a call from another student. Please wait";
		} else {
			String query = "update state\r\n" + "set attention=true\r\n" + "where studentId=?";
			try (PreparedStatement pstmt = c.prepareStatement(query)) {
				pstmt.setInt(1, studentId);
				pstmt.executeUpdate();
				return "You are now calling for attention";

			} catch (Exception e) {
				e.printStackTrace();
				return "Can't call for attention";

			}
		}
	}
	
	public boolean closeAttention() {
		String query = "update state set attention = false where attention = true";
		try {
			this.stmt = c.createStatement();
			stmt.executeUpdate(query);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// which students are calling for attention
	public int[] checkAttention(String className) {
		String query = "select rownum, colnum, studentId from state where attention=true and className='" + className
				+ "'";
		int[] result = new int[3];
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				result[0] = rs.getInt("rownum");
				result[1] = rs.getInt("colnum");
				result[2] = rs.getInt("studentId");
			} else {
				result[0] = 0;
				result[1] = 0;
				result[2] = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result[0] = 0;
			result[1] = 0;
			result[2] = 0;
		}
		return result;
	}

	// check if the student is accepted or not
	public boolean checkAccepted(int studentId) {
		String query = "select accepted from state where studentId=" + studentId;
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return rs.getBoolean("accepted");
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// accept attention call
	public boolean setAccepted(boolean choice) {
		String query = "update state\r\n" + "set accepted=?\r\n" + "where attention=true";
		try (PreparedStatement pstmt = c.prepareStatement(query)) {
			pstmt.setBoolean(1, choice);
			pstmt.executeUpdate();
			if (choice) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (choice) {
				return false;
			} else {
				return false;
			}
		}
	}

	// generate payload for JWT
	public JSONObject generatePayload(String login, String password) {
		String query = "select id, role from users where login = '" + login + "' and password = '" + password + "'";
		try {
			this.stmt = c.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				JSONObject payload = new JSONObject();
				payload.put("id", rs.getInt("id"));
				payload.put("role", rs.getInt("role"));
				return payload;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
