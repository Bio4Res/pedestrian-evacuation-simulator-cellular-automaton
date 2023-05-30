package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.CellularAutomaton;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.CellularAutomatonParameters;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.Statistics;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.floorField.DijkstraStaticFloorFieldWithMooreNeighbourhood;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.neighbourhood.MooreNeighbourhood;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.pedestrian.PedestrianParameters;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.scenario.Scenario;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.environment.Environment;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.environment.EnvironmentFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.statistics.Random.random;

/**
 * Main simulation class.
 *
 * @author Pepe Gallardo
 */
public class MainFromDomain {
  public static void main(String[] args) throws FileNotFoundException, JsonException {
    String filename = (args.length == 0) ? "data/environments/environment-example.json" : args[0];
    FileReader reader = new FileReader(filename);
    JsonObject json = (JsonObject) Jsoner.deserialize(reader);
    Environment environment = EnvironmentFactory.buildFromJSON(json);

    Scenario scenario = new Scenario.FromDomainBuilder(environment.getDomain(1))
        .cellDimension(1.5)
        .floorField(DijkstraStaticFloorFieldWithMooreNeighbourhood::of)
        .build();

    var cellularAutomatonParameters =
        new CellularAutomatonParameters.Builder()
            .scenario(scenario) // use this scenario
            .secondsTimeLimit(60 * 10) // 10 minutes is time limit for simulation
            .neighbourhood(MooreNeighbourhood::of) // use Moore's Neighbourhood for automaton
            .pedestrianVelocity(1.3) // a pedestrian walks at 1.3 m/s
            .GUITimeFactor(15) // perform GUI animation x15 times faster than real time
            .build();

    var automaton = new CellularAutomaton(cellularAutomatonParameters);

    // place pedestrians
    var pedestrianParameters =
        new PedestrianParameters.Builder()
            .fieldAttractionBias(random.nextDouble(0.85, 2.50))
            .crowdRepulsion(random.nextDouble(1.00, 1.50))
            .build();

    var numberOfPedestrians = random.nextInt(150, 600);
    automaton.addPedestriansUniformly(numberOfPedestrians, pedestrianParameters);

    automaton.runGUI(); // automaton.run() to run without GUI
    Statistics statistics = automaton.computeStatistics();
    System.out.println(statistics);

    // write trace to json file
    var jsonTrace = automaton.jsonTrace();
    String fileName = "data/traces/trace.json";
    try (FileWriter fileWriter = new FileWriter(fileName)) {
      fileWriter.write(Jsoner.prettyPrint(jsonTrace.toJson()));
      fileWriter.flush();
      System.out.println(String.format("Trace written to file %s successfully.", fileName));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
