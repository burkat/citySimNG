//* Init class responsible for creating new games with given parameters. Will be implemented in future. */ 

package creatorcontroller;
import model.DependenciesRepresenter;
import controlnode.Node;
import controlnode.SocketNode;

public class CreatorNode extends SocketNode{
	
	public CreatorNode(Node parent, DependenciesRepresenter dr) {
		super(parent, dr);
	}

	@Override
	public void createChildren() {
		
	}

	@Override
	public String parseCommand(String command, String[] streamArgs) {
		// TODO Auto-generated method stub
		return null;
	}

}
