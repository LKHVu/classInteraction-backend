package vgu.vgu;

public class Classroom {
	private String name;
	private int rows;
	private int cols;
	private int starthour;
	private int startminute;
	private int endhour;
	private int endminute;
	private int active;
	
	public Classroom(String name) {
		this.name = name;
	}
	
	public Classroom(String name, int rows, int cols, int starthour, int startminute, int endhour, int endminute, int active) {
		this.name = name;
		this.rows = rows;
		this.cols = cols;
		this.starthour = starthour;
		this.startminute = startminute;
		this.endhour = endhour;
		this.endminute = endminute;
		this.active = active;
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
	
	public int getStartHour() {
		return starthour;
	}
	
	public int getStartMinute() {
		return startminute;
	}
	
	public int getEndHour() {
		return endhour;
	}
	
	public int getEndMinute() {
		return endminute;
	}
	
	public int getActive() {
		return active;
	}
}
