package vgu.vgu;

public class AnswerBySeat {
	private String name;
	private String img;
	private int row;
	private int col;
	private String answer;
	private String solution;
	
	public AnswerBySeat(String name, String img, int row, int col, String answer, String solution) {
		this.name = name;
		this.img = img;
		this.row = row;
		this.col = col;
		this.answer = answer;
		this.solution = solution;
	}
	
	public AnswerBySeat(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public String getName() {
		return name;
	}
	
	public String getImg() {
		return img;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public String getSolution() {
		return solution;
	}
}
