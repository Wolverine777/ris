package app.messages;

import java.util.Set;

import app.nodes.Node;

public class UpdateNodesMessage {
    public Set<Node> updateNodes;
    
    public UpdateNodesMessage() {
        
    }
    
    public UpdateNodesMessage(Set<Node> upadteNodes) {
        this.updateNodes = upadteNodes;
    }
} 