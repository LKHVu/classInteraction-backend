package vgu.vgu;

public class State {
	private int id;
	private String className;
	private int row;
	private int col;
	private int student;
	
	public State(int row, int col, int student) {
		this.row = row;
		this.col = col;
		this.student = student;
	}
	
	public int getId() {
		return id;
	}
	
	public String getClassName() {
		return className;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public int getStudent() {
		return student;
	}
}
