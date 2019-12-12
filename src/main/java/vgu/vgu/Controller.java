/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vgu.vgu;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import org.json.JSONException;

/**
 *
 * @author DELL
 */
@RestController
@RequestMapping("/api")
public class Controller {
//
	// show all classes
	@CrossOrigin
	@GetMapping("/showclass")
	public List<Classroom> classes() throws SQLException, ParseException {
		sql s = new sql();
		List<Classroom> result = s.getClasses();
		s.closeConnection();
		return result;
	}

	// get a class
	@CrossOrigin
	@GetMapping("/class")
	public Classroom getClass(@RequestParam(value = "name") String name) {
		sql s = new sql();
		Classroom result = s.getClass(name);
		s.closeConnection();
		return result;
	}

	// get all states of a class
	@CrossOrigin
	@GetMapping("/state")
	public List<State> getState(@RequestParam(value = "name") String name) {
		sql s = new sql();
		List<State> result = s.getStates(name);
		s.closeConnection();
		return result;
	}

	// get a student from student id
	@CrossOrigin
	@GetMapping("/student")
	public Student getStudent(@RequestParam(value = "id") String id) {
		sql s = new sql();
		Student result = s.getStudent(Integer.parseInt(id));
		s.closeConnection();
		return result;
	}

	// activate a class
	@CrossOrigin
	@PutMapping("/activeclass")
	public Map<String, String> updateUser(@RequestParam(value = "name") String name,
			@RequestParam(value = "year") String year) {
		sql s = new sql();
		HashMap<String, String> map = new HashMap<String, String>();
		if (s.activateClass(name, year)) {
			map.put("Success", "Activated class successfully");
		} else {
			map.put("Failed", "Can't activate class");
		}
		s.closeConnection();
		return map;
	}

	// deactivate a class
	@CrossOrigin
	@PutMapping("/deactivateclass")
	public Map<String, String> updateUser(@RequestParam(value = "name") String name) {
		sql s = new sql();
		s.cleanState(name);
		HashMap<String, String> map = new HashMap<String, String>();
		if (s.deactivateClass(name)) {
			map.put("Success", "Deactivated class successfully");
		} else {
			map.put("Failed", "Can't deactivate class");
		}
		s.closeConnection();
		return map;
	}

	// create a student
	@CrossOrigin
	@PostMapping("/createstudent")
	public @ResponseBody HashMap<String, String> createStudent(@RequestBody String json) {
		sql s = new sql();
		HashMap<String, String> map = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, String> json_map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
			});
			String name = json_map.get("name");
			String img = json_map.get("img");
			String year = json_map.get("year");
			boolean exchange = Boolean.parseBoolean(json_map.get("exchange"));
			if (s.createStudent(name, img, year, exchange)) {
				map.put("Success", "Created student successfully");
			} else {
				map.put("Failed", "Can't create student");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("Failed", "Can't create student");
		}
		s.closeConnection();
		return map;
	}

	// create a state
	@CrossOrigin
	@PostMapping("/createstate")
	public @ResponseBody HashMap<String, String> createState(@RequestBody String json) {
		System.out.println(json);
		sql s = new sql();
		HashMap<String, String> map = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, String> json_map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
			});
			String classname = json_map.get("class");
			int row = Integer.parseInt(json_map.get("row"));
			int col = Integer.parseInt(json_map.get("col"));
			int student = Integer.parseInt(json_map.get("student"));
			if (s.createState(classname, row, col, student)) {
				map.put("Success", "Created state successfully");
			} else {
				map.put("Failed", "Can't create state");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("Failed", "Can't create state");
		}
		s.closeConnection();
		return map;
	}

	// create a class
	@CrossOrigin
	@PostMapping("/createclass")
	public @ResponseBody HashMap<String, String> createClass(@RequestBody String json) {
		sql s = new sql();
		HashMap<String, String> map = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, String> json_map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
			});
			String name = json_map.get("name");
			int row = Integer.parseInt(json_map.get("row"));
			int col = Integer.parseInt(json_map.get("col"));
			if (s.createClass(name, row, col)) {
				map.put("Success", "Created class successfully");
			} else {
				map.put("Failed", "Can't create class");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("Failed", "Can't create class");
		}
		s.closeConnection();
		return map;
	}

	// create a multiple choice answer
	@CrossOrigin
	@PostMapping("/createmultiplechoiceanswer")
	public @ResponseBody HashMap<String, String> createMultipleChoiceAnswer(@RequestBody String json) {
		sql s = new sql();
		HashMap<String, String> map = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, String> json_map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
			});
			int questionId = Integer.parseInt(json_map.get("questionId"));
			int studentId = Integer.parseInt(json_map.get("studentId"));
			String answer = json_map.get("answer");
			if (s.createMultipleChoiceAnswer(questionId, studentId, answer)) {
				map.put("Success", "Created answer successfully");
			} else {
				map.put("Failed", "Can't create answer");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("Failed", "Can't create answer");
		}
		s.closeConnection();
		return map;
	}

	// create a multiple choice question
	@CrossOrigin
	@PostMapping("/createmultiplechoicequestion")
	public @ResponseBody HashMap<String, String> createMultipleChoiceQuestion(@RequestBody String json) {
		sql s = new sql();
		HashMap<String, String> map = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, String> json_map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
			});
			String name = json_map.get("name");
			String className = json_map.get("className");
			String question = json_map.get("question");
			String A = json_map.get("A");
			String B = json_map.get("B");
			String C = json_map.get("C");
			String D = json_map.get("D");
			int time = Integer.parseInt(json_map.get("time"));
			String solution = json_map.get("solution");
			if (s.createMultipleChoiceQuestion(name, className, question, A, B, C, D, time, solution)) {
				map.put("Success", "Created question successfully");
			} else {
				map.put("Failed", "Can't create question");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("Failed", "Can't create question");
		}
		s.closeConnection();
		return map;
	}

	// find out the average of a class
	@CrossOrigin
	@GetMapping("/average")
	public @ResponseBody HashMap<String, String> getAverage(@RequestParam(value = "questionid") String questionId) {
		sql s = new sql();
		HashMap<String, String> map = new HashMap<String, String>();
		int qId = Integer.parseInt(questionId);
		Integer right = s.countAnswers(qId, "right");
		Integer wrong = s.countAnswers(qId, "wrong");
		Integer no = s.countAnswers(qId, "no");
		Integer total = right + wrong + no;
		map.put("right", right + "/" + total);
		map.put("wrong", wrong + "/" + total);
		map.put("no", no + "/" + total);
		s.closeConnection();
		return map;
	}

	// get all finished quizzes
	@CrossOrigin
	@GetMapping("/finishedquestion")
	public List<MultipleChoiceQuestion> getFinishedQuestions(@RequestParam(value = "className") String className) {
		sql s = new sql();
		List<MultipleChoiceQuestion> result = s.getQuestions(className, "finished");
		s.closeConnection();
		return result;
	}

	// get all quiz done of a student
	@CrossOrigin
	@GetMapping("/quizdone")
	public List<MultipleChoiceQuestion> getDoneQuestions(@RequestParam(value = "studentid") String studentId) {
		sql s = new sql();
		List<MultipleChoiceQuestion> result = s.getQuestionsDoneByAStudent(Integer.parseInt(studentId));
		s.closeConnection();
		return result;
	}

	// get all active quizzes
	@CrossOrigin
	@GetMapping("/activequestion")
	public List<MultipleChoiceQuestion> getActiveQuestions(@RequestParam(value = "className") String className) {
		sql s = new sql();
		List<MultipleChoiceQuestion> result = s.getQuestions(className, "active");
		s.closeConnection();
		return result;
	}

	// get name of a quiz
	@CrossOrigin
	@GetMapping("/questionname")
	public @ResponseBody HashMap<String, String> getName(@RequestParam(value = "questionid") String questionId) {
		sql s = new sql();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", s.getQuestionName(Integer.parseInt(questionId)));
		s.closeConnection();
		return map;
	}

	// activate a quiz
	@CrossOrigin
	@PostMapping("/activequiz")
	public void activateQuiz(@RequestParam(value = "questionid") String questionId) throws InterruptedException {
		sql s = new sql();
		int qId = Integer.parseInt(questionId);
		Integer time = s.getQuestionTime(qId);
		s.activateQuestion(qId);
		while (time > 0) {
			Thread.sleep(1000);
			s.decreaseTime(qId);
			time = time - 1;
		}
		s.finishQuestion(qId);
		s.createNoAnswer(qId);
		s.closeConnection();
	}

	// get inactive quizzes
	@CrossOrigin
	@GetMapping("/inactivequiz")
	public List<MultipleChoiceQuestion> getInactiveQuestions(@RequestParam(value = "className") String className) {
		sql s = new sql();
		List<MultipleChoiceQuestion> result = s.getQuestions(className, "inactive");
		s.closeConnection();
		return result;
	}

	// get question
	@CrossOrigin
	@GetMapping("/question")
	public MultipleChoiceQuestion getQuestion(@RequestParam(value = "questionid") String questionId) {
		sql s = new sql();
		MultipleChoiceQuestion result = s.getQuestion(Integer.parseInt(questionId));
		s.closeConnection();
		return result;
	}

	// get time for a quiz
	@CrossOrigin
	@GetMapping("/quiztime")
	public @ResponseBody HashMap<String, Integer> getTime(@RequestParam(value = "questionid") String questionId) {
		sql s = new sql();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("time", s.getQuestionTime(Integer.parseInt(questionId)));
		s.closeConnection();
		return map;
	}

	// get quiz review
	@CrossOrigin
	@GetMapping("/quizreview")
	public QuizReview getReview(@RequestParam(value = "questionid") String questionId,
			@RequestParam(value = "studentid") String studentId) {
		sql s = new sql();
		QuizReview result = s.getReview(Integer.parseInt(questionId), Integer.parseInt(studentId));
		s.closeConnection();
		return result;
	}
}
