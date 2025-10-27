/*
 * Class: Room
 * 
 * Purpose: representation of a room for the Board, includes the name, center
 * and label cell
 * 
 * Date: 10/21/2025
 * 
 * Author: Adan Corral Rascon
 * 
 * Collaborators: none
 * 
 * Sources: none
 */

package clueGame;

public class Room {
	private String name;

	private BoardCell centerCell;
	private BoardCell labelCell;

	public Room(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public BoardCell getCenterCell() {
		return centerCell;
	}

	public BoardCell getLabelCell() {
		return labelCell;
	}

	public void setCenterCell(BoardCell centerCell) {
		this.centerCell = centerCell;
	}

	public void setLabelCell(BoardCell labelCell) {
		this.labelCell = labelCell;
	}
}
