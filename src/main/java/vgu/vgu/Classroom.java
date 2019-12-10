package vgu.vgu;

public class Classroom {
	private String name;
	private int rows;
	private int cols;
	private int active;
	private String year;
	
	public Classroom(String name) {
		this.name = name;
	}
	
	public Classroom(String name, int rows, int cols, int active, String year) {
		this.name = name;
		this.rows = rows;
		this.cols = cols;
		this.active = active;
		this.year = year;
	}
	
	public String getName() {
		return name;
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getCols() {
		return cols;
	}
	
	public int getActive() {
		return active;
	}
	
	public String getYear() {
		return year;
	}
}
