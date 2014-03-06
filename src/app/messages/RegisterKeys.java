package app.messages;

import java.util.Set;

public class RegisterKeys {
	 private Set<Integer> keys;
	 private boolean add;
	 
	 public RegisterKeys(Set<Integer> keys, boolean add){
		 this.keys=keys;
		 this.add=add;
	 }
	public Set<Integer> getKeys() {
		return keys;
	}
	public boolean isAdd() {
		return add;
	}
	 
}
