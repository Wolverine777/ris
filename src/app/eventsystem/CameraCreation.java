package app.eventsystem;

import java.util.List;

import app.shader.Shader;

public class CameraCreation {
	public String id;
	public List<Shader> shaderList;
	
	public CameraCreation(String id, List<Shader> shaderList) {
		super();
		this.id = id;
		this.shaderList = shaderList;
	}
}
