/*
 * Class: TestBoard
 * 
 * Purpose: A class that makes a 4x4 grid (for now) and has the logic
 * for figuring out all possible spots with calcTargets.
 * 
 * Date: 10/11/2025
 * 
 * @author Adan Corral Rascon, Daniel Hoang
 * 
 * Collaborators: none
 * 
 * Sources: none
 */

package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoard {

	private static final int ROWS = 4;
	private static final int COLS = 4;

	private TestBoardCell[][] grid = new TestBoardCell[ROWS][COLS];;
	private Set<TestBoardCell> targets = new HashSet<>();
	private Set<TestBoardCell> visited = new HashSet<>();


	public TestBoard() {
		for (int i = 0; i < ROWS; i++)
			for (int j = 0; j < COLS; j++) {
				grid[i][j] = new TestBoardCell(i, j);
			}
		addAdjacent(); // calculate the Adjacency matrix prior to calcTargets()
	}

	public void addAdjacent() {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				TestBoardCell cell = grid[i][j];

				// Going up the row, staying in the same column
				// but if i (the row) is greater than zero, don't do it
				if (i > 0) { 
					TestBoardCell aboveCell = getCell(i - 1, j);
					cell.addAdjacency(aboveCell);
				}


				// Going down one row, staying in the same column
				// if the row is at the bottom, this will not perform
				if (i < ROWS - 1) { // prevents outOfBounds
					TestBoardCell belowCell = getCell(i + 1, j);
					cell.addAdjacency(belowCell);
				}

				// Going left one column, same row this time
				// if it is all the way to the left, won't perform
				if (j > 0) {
					TestBoardCell leftCell = getCell(i,j - 1);
					cell.addAdjacency(leftCell);
				}

				// Going right one column, same row this time
				// if it is all the way to the right, won't perform
				if (j < ROWS - 1) {
					TestBoardCell rightCell = getCell(i, j + 1);
					cell.addAdjacency(rightCell);
				}

			}
		}
	}

	public void calcTargets (TestBoardCell startCell, int pathlength) {
		/*
		 * calcTargets(): Set up for recursive call
			• visited list is used to avoid backtracking. Set to empty list.
			• targets will ultimately contain the list of cells (e.g., [0][1], [1][2], [2][1], [3][0]) .
			Initially set to empty list.
			• add the start location to the visited list (so no cycle through this cell)
			• call recursive function
			• what will you name this function? I did findAllTargets()
			• what parameters does it need? I used startCell and pathLength
			calcTargets
		 */
		targets.clear();
		visited.clear();

		visited.add(startCell);
		findAllTargets(startCell, pathlength);


	}

	public void findAllTargets(TestBoardCell thisCell, int numSteps) {
		/*
		 * for each adjCell in adjacentCells
			-- if already in visited list, skip rest of this
			-- add adjCell to visited list
			-- if numSteps == 1, add adjCell to Targets
			-- else call findAllTargets() with adjCell, numSteps-1
			-- remove adjCell from visited list
		 */
		for (TestBoardCell adjCell : thisCell.getAdjList()) {
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

	public Set<TestBoardCell> getTargets() {
		return targets;
	}

	public TestBoardCell getCell(int row, int col) {
		return grid[row][col];
	}

}