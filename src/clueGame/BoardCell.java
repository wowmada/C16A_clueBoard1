/*
 * Class: BoardCell
 * 
 * Purpose: A singular BoardCell for the Board, that holds many attributes, such
 * as its row, col, initial, info for characteristics
 * 
 * Date: 10/21/2025
 * 
 * Author: Adan Corral Rascon, Daniel Hoang
 * 
 * Collaborators: none
 * 
 */

package clueGame;

import java.util.HashSet;
import java.util.Set;

public class BoardCell {
	private int row;
	private int col;

	private char initial;

	private DoorDirection doorDirection = DoorDirection.NONE; // until we set a door to a certain direction, leave it at nothing

	private boolean roomLabel;
	private boolean roomCenter;

	private char secretPassage;

	private Set<BoardCell> adjList = new HashSet<>();

	public BoardCell(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}

	public void addAdj(BoardCell cell) {
		adjList.add(cell);
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public boolean isDoorway() {
		if (doorDirection == DoorDirection.NONE) {
			return false;
		} else
			return true;
	}

	public boolean isLabel() {
		return roomLabel;
	}

	public boolean isRoomCenter() {
		return roomCenter;
	}

	public DoorDirection getDoorDirection() {
		return doorDirection;
	}

	public char getSecretPassage() {
		return secretPassage;
	}

	public char getInitial() {
		return initial;
	}

	public void setInitial(char initial) {
		this.initial = initial;
	}

	public void setDoorDirection(DoorDirection doorDirection) {
		this.doorDirection = doorDirection;
	}

	public void setRoomLabel(boolean roomLabel) {
		this.roomLabel = roomLabel;
	}

	public void setRoomCenter(boolean roomCenter) {
		this.roomCenter = roomCenter;
	}

	public void setSecretPassage(char secretPassage) {
		this.secretPassage = secretPassage;
	}
}
