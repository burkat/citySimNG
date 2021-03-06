package graph;

import java.util.HashSet;
import java.util.Set;

import entities.Resource;

public class ResourceNode extends GraphNode{

	private Set<String> assignedDomesticBuildings;
	private Resource resource;
	
	public ResourceNode(Resource resource) {
		this.resource = resource;
		assignedDomesticBuildings = new HashSet<String>();
	}
	
	public void setAssignedBuilding(String assignedBuildingName){
		//assignedBuilding = assignedBuildingName;
	}

	public Resource getResource(){
		return resource;
	}

	@Override
	public String getSuccessorName() {
		return resource.getSuccessor();
	}

	@Override
	public String getPredeccessorName() {
		return resource.getPredecessor();
	}

	@Override
	public String getConcatenatedDescription() {
		return resource.toString();
	}

	@Override
	public String getTexturePath() {
		return resource.getTexturePath();
	}

	@Override
	public String getName() {
		return resource.getName();
	}
	
	

}
