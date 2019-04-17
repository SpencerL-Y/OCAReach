package automata;

public interface Transition {
	public State getFrom();
	public State getTo();
	public String getLabel();
	public void print();
}
