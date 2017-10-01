package py4jmediator;

import entities.Building;
import entities.Resource;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

public interface ViewModel {
	
	public MainMenuViewModel getMainMenuViewModel();
	public CreatorViewModel getCreatorViewModel();
	public GameMenuViewModel getGameMenuViewModel();
	public LoaderViewModel getLoaderViewModel ();
	public MapViewModel getMapViewModel();

	public interface MapViewModel{
		public void displayMap();
		public void init(List<Resource> resources, List<Building> buildings, String texture_one, String texture_two,
						 Map<String, Integer> initialResourcesValues, Map<String, Integer> initialResourcesIncomes);
		public void updateResourcesValues(Map<String, Integer> actualResourcesValues,
										  Map<String, Integer> actualResourcesIncomes);
		public void resumeGame();
	}

	public interface MainMenuViewModel{
		public void displayMainMenu();
	}

	public interface GameMenuViewModel{
		public void displayGameMenu();
	}

	public interface CreatorViewModel{
		public void displayCreator();
		public void displayDependenciesGraph(JSONObject jsonGraph);
		public void displayMsg(String errorMsg);
	}

	public interface LoaderViewModel{
		public void displayLoader();
		public void displayDependenciesGraph(JSONObject graphDesc);
		public void displayPossibleDependenciesSets(Set<String> possibleSets);
	}
}