package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.floorField;

import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.neighbourhood.VonNeumannNeighbourhood;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.scenario.Scenario;

/**
 * Class for representing a static floor field proportional to the shortest distance of each cell to its closest
 * exit. Uses Von Newman's neighbourhood among cells in grid in order to find the shortest paths to exits.
 *
 * @author Pepe Gallardo
 */
public class DijkstraStaticFloorFieldWithVonNewmanNeighbourhood extends DijkstraStaticFloorField {
  public DijkstraStaticFloorFieldWithVonNewmanNeighbourhood(Scenario scenario) {
    super(scenario, VonNeumannNeighbourhood::of);
  }

  public static DijkstraStaticFloorFieldWithVonNewmanNeighbourhood of(Scenario scenario) {
    return new DijkstraStaticFloorFieldWithVonNewmanNeighbourhood(scenario);
  }
}
