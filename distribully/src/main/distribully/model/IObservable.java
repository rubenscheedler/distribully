package distribully.model;


public interface IObservable {
	public void addObserver(IObserver observer);
	public void removeObserver(IObserver observer);
	public void notifyObservers(Object changedObject);
}
