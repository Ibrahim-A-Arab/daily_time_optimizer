
public class Task {

	String name;
	int time; 				//=>why not use byte? because byte + byte = int in java, so it doesn't really make a big diff
	int productivity;
	
	public Task(String name, int time, int productivity) {
		this.name = name;
		this.time = time;
		this.productivity = productivity;
	}
	
	public double getTimeHours() {
	    return time / 2.0;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getProductivity() {
		return productivity;
	}

	public void setProductivity(int productivity) {
		this.productivity = productivity;
	}
}
