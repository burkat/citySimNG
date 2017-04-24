package exchangenode;

import java.util.*;

import model.DependenciesRepresenter;
import controlnode.Node;
import exchange.RepresenterMock;
import exchange.Stock;
import exchange.StockAlgorithm;
import exchange.StockTable;

public class ExchangeNode implements Node{

	private Node parent;
	private HashMap<String, Node> neighbors;
	private DependenciesRepresenter dr;
	StockTable stockTable;
	Stock stock;

	public ExchangeNode(DependenciesRepresenter dr){
		System.out.println("Created Exchange Node");
		neighbors = new HashMap<String, Node>();
		this.dr = dr;
		List<String> resourcesNames = new ArrayList<>();
		resourcesNames.add("gold");
		resourcesNames.add("silver");
		resourcesNames.add("copper");
		stock = new Stock();
		stock.init(resourcesNames);
		RepresenterMock player = new RepresenterMock(resourcesNames);
		stockTable = new StockTable(stock);
		stock.setPlayer(player);
	}


	@Override
	public Node nodeLoop() {
		stock.setWorking(true);;
 		stockTable.setVisible(true);
 		new StockAlgorithm().simulate(stock);
		return parent;
	}


	@Override
	public void setParent(String parentName, Node parent) {
		this.parent = parent;
		neighbors.put(parentName, parent);
	}


	@Override
	public void addNeighbour(String hashKey, Node neighbor) {
		neighbors.put(hashKey, neighbor);
	}
}
