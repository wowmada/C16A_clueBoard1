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

					if (cellCode.length() == 2) {
						char symbol = cellCode.charAt(1);
						switch (symbol) {
						case '<':
							cell.setDoorDirection(DoorDirection.LEFT);
							break;
						case '>':
							cell.setDoorDirection(DoorDirection.RIGHT);
							break;
						case '^':
							cell.setDoorDirection(DoorDirection.UP);
							break;
						case 'v':
						case 'V': // this is for our ClueLayout *we use V
							cell.setDoorDirection(DoorDirection.DOWN);
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

				if (!adjCell.isOccupied()) { // Stops when adjCell is a occupied place

					if (adjCell.isRoom() ) {

						targets.add(adjCell); // valid spot to land for a room (target)
					} else {

						visited.add(adjCell);

						if (numSteps == 1) { 
							targets.add(adjCell); // add as a target if numSteps is 1 (base case)
						} else {
							findAllTargets(adjCell, numSteps - 1); // recursive call and try until base case
						}
						visited.remove(adjCell); // remove adjCell from visited list
					}
				}
			}
		}
	}

	public Set<BoardCell> getTargets() {
		return targets;
	}
}
