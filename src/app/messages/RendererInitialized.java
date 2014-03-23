package app.messages;

import app.shader.Shader;

public class RendererInitialized {

	public Shader shader;
	public Shader texShader;
	
	public RendererInitialized(Shader shader, Shader tex) {
		this.shader = shader;
		this.texShader=tex;
	}
}
