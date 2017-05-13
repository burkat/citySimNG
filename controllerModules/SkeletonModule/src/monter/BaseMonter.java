package monter;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.DependenciesRepresenter;
import controlnode.DispatchCenter;
import controlnode.Node;

public class BaseMonter implements SystemMonter{

	private List<String> textDescriptions;
	protected HashMap<String, Node> nodes;
	private HashMap<String, Boolean> monterConfig;
	protected DispatchCenter dispatchCenter;
	
	public BaseMonter(String filePath, String[] args, DispatchCenter dispatchCenter){
		setConfigFlags(args);
		BufferedReader br = null;
		this.dispatchCenter = dispatchCenter == null ? new DispatchCenter() : dispatchCenter;
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
			textDescriptions = new LinkedList<String>();
			String line;
			while((line = br.readLine())!= null){
				textDescriptions.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		nodes = new HashMap<String, Node>();
		
	}
	
	public BaseMonter(String filePath, String[] args){
		this(filePath, args, null);
	}

	private void setConfigFlags(String[] args){
		if (args == null) return;
		monterConfig = new HashMap<String, Boolean>();
		String[] argNamesTmp = new String[]{"-noide"};
		List<String> argNames = Arrays.asList(argNamesTmp);
		/*Reset monterConfig map -> all possible flags set to false*/
		for (String arg: argNames){
			monterConfig.put(arg, false);
		}
		for (String arg : args){
			if (argNames.contains(arg)){
				monterConfig.put(arg, true);
			}
		}
	}
	
	protected void readNodes(String[] nodeDesc, ArrayList<String> modulesNames){
		String projectName = nodeDesc[0];
		String nodeClassName = nodeDesc[1];
		String nodeHashKey = nodeDesc[2];
		modulesNames.add(nodeHashKey);
		
		URLClassLoader urlLoader = null; 
		System.out.println("Read and create node " + nodeClassName + " with a hash key " + nodeHashKey);
     	try {
     		/*Using java reflection to dynamically load Node instances from other modules(which are set up as separate projects)*/
			String currentLocation = System.getProperty("user.dir");
			System.out.println("Current location: " + currentLocation);
			String urlStr = "file:///" + currentLocation + "/" +  projectName + "/bin/"; //+ nodeClassName + ".class";//"MenuModule\\bin\\menunode\\MenuNode.class";
			System.out.println("Url: " + urlStr);
			URL[] urls = {new URL (urlStr)};
			urlLoader = new URLClassLoader(urls);
			Class<?> nodeClass = urlLoader.loadClass(nodeClassName);
			Constructor<?> constructor = nodeClass.getConstructor(DispatchCenter.class, String.class);
			Node node = (Node)constructor.newInstance(dispatchCenter, nodeHashKey);
			nodes.put(nodeHashKey, node);
		} catch (Exception e) {
			System.err.println("Not found: " + nodeClassName);
			e.printStackTrace();
		} 
     	finally{
     		try {
				urlLoader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
     	}	
	}
	
	/*
	 * method for creating relationships between Node instances;
	 * expects a two-element String array, where the first element 
	 * contains "from" key referencing the parent node, 
	 * and the second is the node to which the parent node passes control
	 * e.g. there exists an edge MenuNode -> CreatorNode: edgeDesc[0] = MenuNode, edgeDesc[1] = CreatorNode
	 */
	protected void readEdges(String[] edgeDesc){
		Node fromNode =  nodes.get(edgeDesc[0]);
		Node toNode = nodes.get(edgeDesc[1]);
		fromNode.addNeighbour(edgeDesc[1], toNode);
		toNode.setParent(edgeDesc[0], fromNode);
	}
		
	/*
	 * (non-Javadoc)
	 * @see monter.SystemMonter#mount()
	 * 
	 */
	@Override
	public Node mount(ArrayList<String> modulesNamesList) {
		
		String mode = ""; /*text establishing the mode of parsing*/
		String mainNodeName = ""; 
		/* 
		 * ^ hashKey of the main node received from the configuration file; 
		 * is used to return proper Node instance, from which control starts to flow 
		 */
		
		for(String description : textDescriptions){
			if(description.startsWith("#")) {
				/*dismiss comments*/
				continue; 
			}

			if (description.replaceAll("\\s+", "").equals("")) {
				/*if there are no alpha numerical characters, we can skip this "description*/
				continue; /*dismiss empty lines*/
			}
			
			String[] descriptionParts = description.split(" ");

			/*Phase one: establish if mode changed*/
			if(descriptionParts[0].equals("Mode")){
				mode = descriptionParts[1];
				continue;
			}
			
			/* 
			 * Act accordingly to the mode selected earlier; 
			 * by now we know that a line of text is not a comment;
			 * thus it is expected to be a valid part of the description, structured in  
			 * a legitimate way, so current mode of mounting can understand it 
			*/
			if (mode.equals("Nodes")){
				readNodes(descriptionParts, modulesNamesList);
			}
			else if(mode.equals("Edges")){
				readEdges(descriptionParts);
			}
			else if(mode.equals("StartNode")){ 
				/*
				 * expecting, that the line of text stored under the variable
				 * "description", is an actual hashKey of the node, mentioned at least once
				 * during the operation of previous modes
				 */
				
				mainNodeName = description;
			}
			else{
				System.out.println("Invalid mode selected, kernel panic");
				return null;
			}
		}
		dispatchCenter.createDB(modulesNamesList);
		return nodes.get(mainNodeName);

	}

}