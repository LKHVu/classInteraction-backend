package vgu.vgu;

public class MultipleChoiceQuestion {
	private int id;
	private String name;
	private String className;
	private String question;
	private String A;
	private String B;
	private String C;
	private String D;
	private int time;
	private String solution;
	private int active;
	private int finished;

	public MultipleChoiceQuestion(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public MultipleChoiceQuestion(String name, String question, String A, String B, String C, String D, int time,
			String solution, int active, int finished) {
		this.name = name;
		this.question = question;
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
		this.time = time;
		this.solution = solution;
		this.active = active;
		this.finished = finished;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
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

	public int getTime() {
		return time;
	}

	public String getSolution() {
		return solution;
	}

	public int getActive() {
		return active;
	}

	public int getFinished() {
		return finished;
	}
}
