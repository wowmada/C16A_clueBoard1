/*
 * Class: Board
 * 
 * Purpose: Clue gameBoard that initializes itself with the given Layout and Setup Files
 * Creates the grid, boardCell in each and room.
 * 
 * Date: 10/21/2025
 * 
 * Author: Adan Corral Rascon, Daniel Hoang
 * 
 * Collaborators: none
 * 
 */

package clueGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import experiment.TestBoardCell;

import java.io.File;
import java.io.FileNotFoundException;

public class Board {
	private BoardCell[][] grid;

	private int numRows;
	private int numColumns;

	private String layoutConfigFile;
	private String setupConfigFile;

	private Map<Character, Room> roomMap = new HashMap<>();
	private Set<BoardCell> targets = new HashSet<>();
	private Set<BoardCell> visited = new HashSet<>();

	private static Board theInstance = new Board();

	private Board() {
		super();
	}

	public static Board getInstance() {
		return theInstance;
	}

	public void initialize() {
		try {
			loadSetupConfig();
			loadLayoutConfig();
			calcAdjacencies();
		}
		catch(BadConfigFormatException e) {
			System.out.println("Bad Configuration Error: " + e.getMessage());
		}
	}

	public void loadSetupConfig() throws BadConfigFormatException {
		// initialize the map
		roomMap = new HashMap<>();

		try (Scanner fileScanner = new Scanner(new File(setupConfigFile))) {

			while (fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine().trim();

				// skip blank lines
				if (line.isEmpty()) {
					continue;
				}
				// skip comment lines
				if (line.startsWith("//")) {
					continue;
				}

				String[] parts = line.split(",");
				if (parts.length != 3) {
					throw new BadConfigFormatException("Setup line does not have 3 parts: " + line);
				}

				String type = parts[0].trim();
				String name = parts[1].trim();
				String initialStr = parts[2].trim();

				if (initialStr.length() != 1) {
					throw new BadConfigFormatException("Room initial must be a single character: " + line);
				}

				char initial = initialStr.charAt(0);

				switch (type) {
				case "Room":
					Room room = new Room(name);
					roomMap.put(initial, room);
					break;
				case "Walkway":
					// hallways to the rooms
					roomMap.put(initial, new Room("Walkway"));
					break;
				case "Unused":
					// empty spaces we can't enter or
					roomMap.put(initial, new Room("Unused"));
					break;
				case "Space":
					Room spaceRoom = new Room(name);
					roomMap.put(initial, spaceRoom);
					break;
				default:
					throw new BadConfigFormatException("Unknown type in setup file: " + type);
				}
			}

		} 
		catch (FileNotFoundException e) {
			throw new BadConfigFormatException("Setup file not found: " + setupConfigFile);
		}
	}

	public void loadLayoutConfig() throws BadConfigFormatException {
		try {
			Scanner in = new Scanner(new File(layoutConfigFile));
			ArrayList<String[]> rows = new ArrayList<>();

			while (in.hasNextLine()) {
				// trim removes trailing spaces
				String line = in.nextLine();
				line = line.trim();
				// skips blank lines
				if (line.isEmpty()) {
					continue;
				}
				// splits line into columns by commas
				String[] cols = line.split(",");

				for (int k = 0; k < cols.length; k++) {
					cols[k] = cols[k].trim();
				}
				rows.add(cols);
			}
			in.close();

			numRows = rows.size();
			numColumns = 0;

			for (String[] row : rows) {
				if (row.length > numColumns) {
					numColumns = row.length;
				}
			}

			for (String[] row : rows) {
				if (row.length == numColumns) {
					continue;
				}
				throw new BadConfigFormatException("Error: Does not have the same number of columns in every row");
			}

			grid = new BoardCell[numRows][numColumns];

			for (int i = 0; i < numRows; i++) {
				for (int j = 0; j < numColumns; j++) {
					String[] currentRow = rows.get(i);
					String cellCode = currentRow[j];
					char initial = cellCode.charAt(0);

					if (roomMap.containsKey(initial)) {

					} else {
						throw new BadConfigFormatException("Error: Initial " + initial + " not found in setup file");
					}

					BoardCell cell = new BoardCell(i, j);
					cell.setInitial(initial);
					
					// Determine type of cell
					if (initial == 'X' || initial == ' ') {
					    cell.setUnused(true);
					    cell.setRoom(false);
					} else if (initial == 'W') {
					    cell.setRoom(false);
					    cell.setUnused(false);
					} else {
					    cell.setRoom(true);
					    cell.setUnused(false);
					}

					if (cellCode.length() == 2) {
						char symbol = cellCode.charAt(1);
						switch (symbol) {
						case '<':
							cell.setDoorDirection(DoorDirection.LEFT);
							cell.setDoorway(true);
							break;
						case '>':
							cell.setDoorDirection(DoorDirection.RIGHT);
							cell.setDoorway(true);
							break;
						case '^':
							cell.setDoorDirection(DoorDirection.UP);
							cell.setDoorway(true);
							break;
						case 'v':
						case 'V': // this is for our ClueLayout *we use V
							cell.setDoorDirection(DoorDirection.DOWN);
							cell.setDoorway(true);
							break;
						case '#':
							cell.setRoomLabel(true);
							roomMap.get(initial).setLabelCell(cell);
							break;
						case '*':
							cell.setRoomCenter(true);
							roomMap.get(initial).setCenterCell(cell);
							break;
						default:
							if (Character.isLetter(symbol)) {
								cell.setSecretPassage(symbol);
							} else {
								throw new BadConfigFormatException("Bad cell entry: " + cellCode);
							}
						}
					}
					grid[i][j] = cell;
				}
			}
		} 
		catch (FileNotFoundException e) {
			throw new BadConfigFormatException("Error: file not found: " + layoutConfigFile);
		}
	}

	public void setConfigFiles(String layoutConfig, String setupConfig) {
		this.layoutConfigFile = "data/" + layoutConfig;
		this.setupConfigFile = "data/" + setupConfig;
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public BoardCell getCell(int row, int col) {
		return grid[row][col];
	}

	public Room getRoom(BoardCell cell) {
		return roomMap.get(cell.getInitial());
	}

	public Room getRoom(char initial) {
		return roomMap.get(initial);
	}

	public Set<BoardCell> getAdjList(int row, int col) {
		return grid[row][col].getAdjList();
	}
	
	public void calcAdjacencies() {
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				BoardCell cell = grid[i][j];

				Set<BoardCell> adjList = new HashSet<>();

				if (cell.isUnused()) {
					cell.setAdjList(adjList);
					continue;
				}

				// check UP
				if (i > 0) {
					BoardCell above = grid[i - 1][j];
					validNeighbor(cell, above, adjList);
				}

				// check DOWN
				if (i < numRows - 1) {
					BoardCell below = grid[i + 1][j];
					validNeighbor(cell, below, adjList);
				}

				// check LEFT
				if (j > 0) {
					BoardCell left = grid[i][j - 1];
					validNeighbor(cell, left, adjList);
				}

				// check RIGHT
				if (j < numColumns - 1) {
					BoardCell right = grid[i][j + 1];
					validNeighbor(cell, right, adjList);
				}

				cell.setAdjList(adjList);
			}
		}	
	}
	
	/*
	 *Used in calcAdjacencies to check for cases before adding to adj list
	 *Cases include: walkways, doorway, and spaces like walls or nothing
	 */
	private void validNeighbor(BoardCell cell, BoardCell neighbor, Set<BoardCell> adjList) {
		// Skip unused cells (blank or nothing cells)
	    if (neighbor.isUnused()) return;
	    
	    // Negative means left or up, Positive means down or right
	    int rowDiff = neighbor.getRow() - cell.getRow();
	    int colDiff = neighbor.getCol() - cell.getCol();

	    // If cell = walkway
	    if (!cell.isRoom()) {
	        // if cell's neighbor is also a walkway
	        if (!neighbor.isRoom()) {
	            adjList.add(neighbor);
	        }
	        // If cell is a walkway and connected to a door, make sure you can
	        // only go in if the door is facing the walkway
	        else if (neighbor.isDoorway()) {
	            switch (neighbor.getDoorDirection()) {
	                case UP:    
	                	if (rowDiff == -1 && colDiff == 0) {
	                		adjList.add(neighbor);
	                	}
	                	break;
	                	
	                case DOWN:  
	                	if (rowDiff == 1  && colDiff == 0) {
	                		adjList.add(neighbor); 
	                	}
	                	break;
	                	
	                case LEFT:  
	                	if (rowDiff == 0  && colDiff == -1) {
	                		adjList.add(neighbor); 
	                	}
	                	break;
	                	
	                case RIGHT: 
	                	if (rowDiff == 0  && colDiff == 1) {
	                		adjList.add(neighbor); 
	                	}
	                	break;
	                	
	                default:
	                	break;
	            }
	        }
	        return;
	    }
	    
	    // If cell is a doorway, only connect in the direction it faces
	    if (cell.isDoorway()) {
	        switch (cell.getDoorDirection()) {
	        
	            case UP:    
	            	if (rowDiff == -1 && colDiff == 0) {
	            		adjList.add(neighbor); 
	            	}
	            	break;
	            	
	            case DOWN:  
	            	if (rowDiff == 1  && colDiff == 0) {
	            		adjList.add(neighbor); 
	            	}
	            	break;
	            	
	            case LEFT:  
	            	if (rowDiff == 0  && colDiff == -1) {
	            		adjList.add(neighbor); 
	            	}
	            	break;
	            	
	            case RIGHT: 
	            	if (rowDiff == 0  && colDiff == 1) {
	            		adjList.add(neighbor); 
	            	}
	            	break;
	            
	            default:
	            	break;
	        }
	        
	        // if cell is door and neighbor is room, then enter room 
	        if (neighbor.isRoom() && !neighbor.isDoorway()) { 
	            adjList.add(neighbor);                      
	        }
	        return;	        
	        
	    }
	    
	    // If cell is a room 
	    if (cell.isRoom() && !cell.isDoorway()) {
	        if (neighbor.isDoorway()) {                     
	            adjList.add(neighbor); 
	        }
	        
	        
		    // If cell is a secret passage
		    if (cell.getSecretPassage() != '\0') {
		        BoardCell passageDest = getRoom(cell.getSecretPassage()).getCenterCell();
		        if (passageDest != null) {
		            adjList.add(passageDest);
		        }
		    }
		    
		    return;
	    }
	    
	}

	public void calcTargets(BoardCell startCell, int pathlength) {
		/*
		 *Set up for recursive function findAllTargets
		 *clear target and visited list before calculating each time
		 */
		targets.clear();
		visited.clear();

		visited.add(startCell);
		findAllTargets(startCell, pathlength);
			
	}
	
	public void findAllTargets(BoardCell thisCell, int numSteps) {
		/*
		 * for each adjCell in adjacentCells
			-- if already in visited list, skip rest of this
			-- add adjCell to visited list
			-- if numSteps == 1, add adjCell to Targets
			-- else call findAllTargets() with adjCell, numSteps-1
			-- remove adjCell from visited list
		 */
		for (BoardCell adjCell : thisCell.getAdjList()) {
			if (!visited.contains(adjCell)) { // Stops when adjCell is already in visited

				if (!adjCell.isOccupied() || adjCell.isRoom()) { // Stops when adjCell is a occupied place
					visited.add(adjCell);
				    if (numSteps == 1 || adjCell.isRoom()) {
				        targets.add(adjCell);
				    } else {
				        findAllTargets(adjCell, numSteps - 1);
				    }
				    visited.remove(adjCell);
				}
			}
		}
	}

	public Set<BoardCell> getTargets() {
		return targets;
	}
}
