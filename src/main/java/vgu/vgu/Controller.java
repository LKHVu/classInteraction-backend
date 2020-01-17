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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

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
	public List<Classroom> classes(@RequestHeader("Authorization") String token) throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		if (jwtUtil.validateRole(token, 1) || jwtUtil.validateRole(token, 3) || jwtUtil.validateRole(token, 2)) {
			sql s = new sql();
			List<Classroom> result = s.getClasses();
			s.closeConnection();
			return result;
		} else {
			return null;
		}
	}

	// get a class
	@CrossOrigin
	@GetMapping("/class")
	public Classroom getClass(@RequestHeader("Authorization") String token, @RequestParam(value = "name") String name)
			throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		if (jwtUtil.validateRole(token, 2) || jwtUtil.validateRole(token, 3)) {
			sql s = new sql();
			Classroom result = s.getClass(name);
			s.closeConnection();
			return result;
		} else {
			return null;
		}
	}

	// get all states of a class
	@CrossOrigin
	@GetMapping("/state")
	public List<State> getState(@RequestHeader("Authorization") String token, @RequestParam(value = "name") String name)
			throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		if (jwtUtil.validateRole(token, 2) || jwtUtil.validateRole(token, 3)) {
			sql s = new sql();
			List<State> result = s.getStates(name);
			s.closeConnection();
			return result;
		} else {
			return null;
		}
	}

	// get a student from student id
	@CrossOrigin
	@GetMapping("/student")
	public User getStudent(@RequestHeader("Authorization") String token, @RequestParam(value = "id") String id)
			throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		if (jwtUtil.validateRole(token, 2)) {
			sql s = new sql();
			User result = s.getStudent(Integer.parseInt(id));
			s.closeConnection();
			return result;
		} else {
			return null;
		}
	}

	// activate a class
	@CrossOrigin
	@PutMapping("/activeclass")
	public Map<String, String> updateUser(@RequestHeader("Authorization") String token,
			@RequestParam(value = "name") String name, @RequestParam(value = "year") String year) throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, String> map = new HashMap<String, String>();
		if (jwtUtil.validateRole(token, 2)) {
			sql s = new sql();
			if (s.activateClass(name, year)) {
				map.put("Success", "Activated class successfully");
			} else {
				map.put("Failed", "Can't activate class");
			}
			s.closeConnection();
		} else {
			map.put("Failed", "Unauthorized");
		}
		return map;
	}

	// deactivate a class
	@CrossOrigin
	@PutMapping("/deactivateclass")
	public Map<String, String> updateUser(@RequestHeader("Authorization") String token,
			@RequestParam(value = "name") String name) throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, String> map = new HashMap<String, String>();
		if (jwtUtil.validateRole(token, 2)) {
			sql s = new sql();
			s.cleanState(name);
			if (s.deactivateClass(name)) {
				map.put("Success", "Deactivated class successfully");
			} else {
				map.put("Failed", "Can't deactivate class");
			}
			s.closeConnection();
		} else {
			map.put("Failed", "Unauthorized");
		}
		return map;
	}

	// create a student
	@CrossOrigin
	@PostMapping("/createstudent")
	public @ResponseBody HashMap<String, String> createStudent(@RequestHeader("Authorization") String token,
			@RequestBody String json) throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, String> map = new HashMap<String, String>();
		if (jwtUtil.validateRole(token, 1)) {
			sql s = new sql();
			ObjectMapper mapper = new ObjectMapper();
			try {
				Map<String, String> json_map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
				});
				String name = json_map.get("name");
				String img = json_map.get("img");
				String year = json_map.get("year");
				String login = json_map.get("login");
				String password = json_map.get("password");
				boolean exchange = Boolean.parseBoolean(json_map.get("exchange"));
				if (s.createStudent(name, img, year, exchange, login, password)) {
					map.put("Success", "Created student successfully");
				} else {
					map.put("Failed", "Can't create student");
				}
			} catch (Exception e) {
				e.printStackTrace();
				map.put("Failed", "Can't create student");
			}
			s.closeConnection();
		} else {
			map.put("Failed", "Unauthorized");
		}

		return map;
	}

	// create a state
	@CrossOrigin
	@PostMapping("/createstate")
	public @ResponseBody HashMap<String, String> createState(@RequestHeader("Authorization") String token,
			@RequestBody String json) throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, String> map = new HashMap<String, String>();
		if (jwtUtil.validateRole(token, 3)) {
			sql s = new sql();
			ObjectMapper mapper = new ObjectMapper();
			try {
				Map<String, String> json_map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
				});
				String classname = json_map.get("class");
				int row = Integer.parseInt(json_map.get("row"));
				int col = Integer.parseInt(json_map.get("col"));
				int student = Integer.parseInt(json_map.get("student"));
				map.put("Result", s.createState(classname, row, col, student));
			} catch (Exception e) {
				e.printStackTrace();
				map.put("Result", "Can't take seat");
			}
			s.closeConnection();
		} else {
			map.put("Result", "Unauthorized");
		}
		return map;
	}

	// create a class
	@CrossOrigin
	@PostMapping("/createclass")
	public @ResponseBody HashMap<String, String> createClass(@RequestHeader("Authorization") String token,
			@RequestBody String json) throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, String> map = new HashMap<String, String>();
		if (jwtUtil.validateRole(token, 1)) {
			sql s = new sql();
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
		} else {
			map.put("Failed", "Unauthorized");
		}

		return map;
	}

	// create a multiple choice answer
	@CrossOrigin
	@PostMapping("/createmultiplechoiceanswer")
	public @ResponseBody HashMap<String, String> createMultipleChoiceAnswer(
			@RequestHeader("Authorization") String token, @RequestBody String json) throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, String> map = new HashMap<String, String>();
		if (jwtUtil.validateRole(token, 3)) {
			sql s = new sql();
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
		} else {
			map.put("Failed", "Unauthorized");
		}
		return map;
	}

	// create a multiple choice question
	@CrossOrigin
	@PostMapping("/createmultiplechoicequestion")
	public @ResponseBody HashMap<String, String> createMultipleChoiceQuestion(
			@RequestHeader("Authorization") String token, @RequestBody String json) throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, String> map = new HashMap<String, String>();
		if (jwtUtil.validateRole(token, 2)) {
			sql s = new sql();
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
		} else {
			map.put("Failed", "Unauthorized");
		}
		return map;
	}

	// find out the average of a class
	@CrossOrigin
	@GetMapping("/average")
	public @ResponseBody HashMap<String, String> getAverage(@RequestHeader("Authorization") String token,
			@RequestParam(value = "questionid") String questionId) throws NumberFormatException, Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, String> map = new HashMap<String, String>();
		if (jwtUtil.validateRole(token, 2)) {
			sql s = new sql();
			int qId = Integer.parseInt(questionId);
			Integer right = s.countAnswers(qId, "right");
			Integer wrong = s.countAnswers(qId, "wrong");
			Integer no = s.countAnswers(qId, "no");
			Integer total = right + wrong + no;
			map.put("right", right + "/" + total);
			map.put("wrong", wrong + "/" + total);
			map.put("no", no + "/" + total);
			s.closeConnection();
		} else {
			map.put("Failed", "Unauthorized");
		}
		return map;
	}

	// get all finished quizzes
	@CrossOrigin
	@GetMapping("/finishedquestion")
	public List<MultipleChoiceQuestion> getFinishedQuestions(@RequestHeader("Authorization") String token,
			@RequestParam(value = "className") String className) throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		if (jwtUtil.validateRole(token, 2)) {
			sql s = new sql();
			List<MultipleChoiceQuestion> result = s.getQuestions(className, "finished");
			s.closeConnection();
			return result;
		} else {
			return null;
		}
	}

	// get all quiz done of a student
	@CrossOrigin
	@GetMapping("/quizdone")
	public List<MultipleChoiceQuestion> getDoneQuestions(@RequestHeader("Authorization") String token,
			@RequestParam(value = "studentid") String studentId) throws NumberFormatException, Exception {
		JwtUtil jwtUtil = new JwtUtil();
		if (jwtUtil.validateRole(token, 3)) {
			sql s = new sql();
			List<MultipleChoiceQuestion> result = s.getQuestionsDoneByAStudent(Integer.parseInt(studentId));
			s.closeConnection();
			return result;
		} else {
			return null;
		}
	}

	// get all active quizzes
	@CrossOrigin
	@GetMapping("/activequestion")
	public List<MultipleChoiceQuestion> getActiveQuestions(@RequestHeader("Authorization") String token,
			@RequestParam(value = "className") String className) throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		if (jwtUtil.validateRole(token, 2) || jwtUtil.validateRole(token, 3)) {
			sql s = new sql();
			List<MultipleChoiceQuestion> result = s.getQuestions(className, "active");
			s.closeConnection();
			return result;
		} else {
			return null;
		}
	}

	// get name of a quiz
	@CrossOrigin
	@GetMapping("/questionname")
	public @ResponseBody HashMap<String, String> getName(@RequestHeader("Authorization") String token,
			@RequestParam(value = "questionid") String questionId) throws NumberFormatException, Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, String> map = new HashMap<String, String>();
		if (jwtUtil.validateRole(token, 2)) {
			sql s = new sql();
			map.put("name", s.getQuestionName(Integer.parseInt(questionId)));
			s.closeConnection();
		} else {
			map.put("Failed", "Unauthorized");
		}
		return map;
	}

	// activate a quiz
	@CrossOrigin
	@PostMapping("/activequiz")
	public void activateQuiz(@RequestHeader("Authorization") String token,
			@RequestParam(value = "questionid") String questionId) throws NumberFormatException, Exception {
		JwtUtil jwtUtil = new JwtUtil();
		if (jwtUtil.validateRole(token, 2)) {
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
	}

	// get inactive quizzes
	@CrossOrigin
	@GetMapping("/inactivequiz")
	public List<MultipleChoiceQuestion> getInactiveQuestions(@RequestHeader("Authorization") String token,
			@RequestParam(value = "className") String className) throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		if (jwtUtil.validateRole(token, 2)) {
			sql s = new sql();
			List<MultipleChoiceQuestion> result = s.getQuestions(className, "inactive");
			s.closeConnection();
			return result;
		} else {
			return null;
		}
	}

	// get question
	@CrossOrigin
	@GetMapping("/question")
	public MultipleChoiceQuestion getQuestion(@RequestHeader("Authorization") String token,
			@RequestParam(value = "questionid") String questionId) throws NumberFormatException, Exception {
		JwtUtil jwtUtil = new JwtUtil();
		if (jwtUtil.validateRole(token, 2) || jwtUtil.validateRole(token, 3)) {
			sql s = new sql();
			MultipleChoiceQuestion result = s.getQuestion(Integer.parseInt(questionId));
			s.closeConnection();
			return result;
		} else {
			return null;
		}
	}

	// get time for a quiz
	@CrossOrigin
	@GetMapping("/quiztime")
	public @ResponseBody HashMap<String, Integer> getTime(@RequestHeader("Authorization") String token,
			@RequestParam(value = "questionid") String questionId) throws NumberFormatException, Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		if (jwtUtil.validateRole(token, 2) || jwtUtil.validateRole(token, 3)) {
			sql s = new sql();
			map.put("time", s.getQuestionTime(Integer.parseInt(questionId)));
			s.closeConnection();
		} else {
			map.put("Unauthorized", 0);
		}
		return map;
	}

	// get quiz review
	@CrossOrigin
	@GetMapping("/quizreview")
	public QuizReview getReview(@RequestHeader("Authorization") String token,
			@RequestParam(value = "questionid") String questionId, @RequestParam(value = "studentid") String studentId)
			throws NumberFormatException, Exception {
		JwtUtil jwtUtil = new JwtUtil();
		if (jwtUtil.validateRole(token, 2) || jwtUtil.validateRole(token, 3)) {
			sql s = new sql();
			QuizReview result = s.getReview(Integer.parseInt(questionId), Integer.parseInt(studentId));
			s.closeConnection();
			return result;
		} else {
			return null;
		}
	}

	// get answers by seat
	@CrossOrigin
	@GetMapping("/answerbyseat")
	public List<AnswerBySeat> getAnswersBySeat(@RequestHeader("Authorization") String token,
			@RequestParam(value = "questionid") String questionId) throws NumberFormatException, Exception {
		JwtUtil jwtUtil = new JwtUtil();
		if (jwtUtil.validateRole(token, 2)) {
			sql s = new sql();
			List<AnswerBySeat> result = s.getAnswersBySeat(Integer.parseInt(questionId));
			s.closeConnection();
			return result;
		} else {
			return null;
		}
	}

	// set attention for a student
	@CrossOrigin
	@PutMapping("/callattention")
	public Map<String, String> callForAttention(@RequestHeader("Authorization") String token,
			@RequestParam(value = "studentid") String studentId) throws NumberFormatException, Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, String> map = new HashMap<String, String>();
		if (jwtUtil.validateRole(token, 3)) {
			sql s = new sql();
			
			map.put("Result", s.callForAttention(Integer.parseInt(studentId)));
			s.closeConnection();
		} else {
			map.put("Failed", "Unauthorized");
		}
		return map;
	}
	
	//close an attention call
	@CrossOrigin
	@PutMapping("/closeattention")
	public Map<String, String> closeAttention(@RequestHeader("Authorization") String token) throws NumberFormatException, Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, String> map = new HashMap<String, String>();
		if (jwtUtil.validateRole(token, 2) || jwtUtil.validateRole(token, 3)) {
			sql s = new sql();
			if (s.closeAttention()) {
				map.put("Success", "Close attention call successfully");
			} else {
				map.put("Failed", "Can't close attention call");
			}
			s.closeConnection();
		} else {
			map.put("Failed", "Unauthorized");
		}
		return map;
	}

	// which students are calling for attention
	@CrossOrigin
	@GetMapping("/checkattention")
	public Map<String, Integer> checkAttention(@RequestHeader("Authorization") String token,
			@RequestParam(value = "classname") String className) throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		if (jwtUtil.validateRole(token, 2)) {
			sql s = new sql();
			int[] result = s.checkAttention(className);
			map.put("row", result[0]);
			map.put("col", result[1]);
			map.put("studentId", result[2]);
			s.closeConnection();
		} else {
			map.put("Unauthorized", 0);
		}
		return map;
	}

	// set accepted for a student
	@CrossOrigin
	@PutMapping("/setaccepted")
	public Map<String, String> setAccepted(@RequestHeader("Authorization") String token,
			@RequestParam(value = "turn") String turn)
			throws NumberFormatException, Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, String> map = new HashMap<String, String>();
		if (jwtUtil.validateRole(token, 2) || jwtUtil.validateRole(token, 3)) {
			sql s = new sql();
			Boolean choice = null;
			if (turn.equals("on")) {
				choice = true;
			} else if (turn.equals("off")) {
				choice = false;
			}
			if (s.setAccepted(choice)) {
				map.put("Success", "Set accepted successfully");
			} else {
				map.put("Failed", "Set accepted failed");
			}
			s.closeConnection();
		} else {
			map.put("Failed", "Unauthorized");
		}
		return map;
	}

	// check if the student is accepted for the call or not
	@CrossOrigin
	@GetMapping("/checkaccepted")
	public Map<String, Boolean> checkAccepted(@RequestHeader("Authorization") String token,
			@RequestParam(value = "studentid") String studentId) throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		if (jwtUtil.validateRole(token, 3)) {
			sql s = new sql();
			map.put("Result", s.checkAccepted(Integer.parseInt(studentId)));
			s.closeConnection();
		} else {
			map.put("Result", false);
		}
		return map;
	}

	// login
	@CrossOrigin
	@PostMapping("/login")
	public Map<String, String> login(@RequestBody String json) throws JSONException, Exception {
		JwtUtil jwtUtil = new JwtUtil();
		String token = jwtUtil.generateToken(new JSONObject(json));
		HashMap<String, String> map = new HashMap<String, String>();
		if (token.equals("no")) {
			map.put("Failed", "Login failed");
		} else {
			map.put("token", token);
		}
		return map;
	}

}
