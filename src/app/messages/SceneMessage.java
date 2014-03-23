package app.messages;

import app.nodes.Camera;
import app.nodes.Node;

public class SceneMessage {
    public Node start;
    public Camera cam;
    
    public SceneMessage() {}
    
    public SceneMessage(Node start, Camera cam) {
        this.start = start;
        this.cam = cam;
    }
}