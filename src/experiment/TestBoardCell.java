/*
 * Class: TestBoardCell
 * 
 * Purpose: A class that represents itself as a singular cell to a grid
 * TestBoard, has features to know if it is a room or occupied
 * 
 * Date: 10/11/2025
 * 
 * @author Adan Corral Rascon
 * 
 * Collaborators: none
 * 
 * Sources: none
 */

package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoardCell {
	private int row;
	private int col;

	private boolean occupied;
	private boolean isRoom;

	private Set<TestBoardCell> adjList = new HashSet<>();

	public TestBoardCell(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}

	public void addAdjacency(TestBoardCell cell) {
		adjList.add(cell);
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	public boolean isRoom() {
		return isRoom;
	}

	public void setRoom(boolean isRoom) {
		this.isRoom = isRoom;
	}

	public Set<TestBoardCell> getAdjList() {
		return adjList;
	}
}