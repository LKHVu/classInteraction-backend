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

	//get a student from student id
	@CrossOrigin
	@GetMapping("/student")
	public Student getStudent(@RequestParam(value = "id") String id) {
		sql s = new sql();
		Student result = s.getStudent(Integer.parseInt(id));
		s.closeConnection();
		return result;
	}

	// activate, or deactivate a class
	@CrossOrigin 
	@PutMapping("/activeclass")
	public Map<String, String> updateUser(@RequestParam(value = "name") String name) {
		sql s = new sql();
		int active = 1;
		if (s.classIsOn(name)) {
			active = 0;
			s.cleanState(name);
		}
		HashMap<String, String> map = new HashMap<String, String>();
		if (s.updateClassSession(name, active)) {
			map.put("Success", "Changed class status successfully");
		} else {
			map.put("Failed", "Can't change class status");
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
			if (s.createStudent(name)) {
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
			int starthour = Integer.parseInt(json_map.get("starthour"));
			int startminute = Integer.parseInt(json_map.get("startminute"));
			int endhour = Integer.parseInt(json_map.get("endhour"));
			int endminute = Integer.parseInt(json_map.get("endminute"));
			if (s.createClass(name, row, col, starthour, startminute, endhour, endminute)) {
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

}
