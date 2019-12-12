package vgu.vgu;

public class QuizReview {
	private String quizName;
	private String question;
	private String A;
	private String B;
	private String C;
	private String D;
	private String solution;
	private String answer;
	
	public QuizReview(String quizName, String question, String A, String B, String C, String D, String solution, String answer) {
		this.quizName = quizName;
		this.question = question;
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
		this.solution = solution;
		this.answer = answer;
	}
	
	public String getQuizName() {
		return quizName;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public String getA() {
		return A;
	}
	
	public String getB() {
		return B;
	}
	
	public String getC() {
		return C;
	}
	
	public String getD() {
		return D;
	}
	
	public String getSolution() {
		return solution;
	}
	
	public String getAnswer() {
		return answer;
	}
}
