/*
 * Class: BoardTestExp
 * 
 * Purpose: Tests for the 4x4 gameBoard by going through multiple scenarios
 * such as Adjacency, calcTargets spots, Room Presence, Occupied space, Mixed,
 * and Max Roll
 * 
 * Date: 10/12/2025
 * 
 * @author Adan Corral Rascon, Daniel Hoang
 * 
 * Collaborators: none
 * 
 * Sources: copied partial code from the "Example test code" from the Assignment, credits to them
 */

package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import experiment.TestBoard;
import experiment.TestBoardCell;

public class BoardTestExp {

	private TestBoard gameBoard;

	@BeforeEach
	public void setUp() {
		gameBoard = new TestBoard();
	}

	/*
	 * Test adjacencies for several different locations
	 * Test centers and edges
	 */

	// top left corner (i.e., location [0][0])
	@Test
	public void testAdjacency() {
		TestBoardCell cell = gameBoard.getCell(0,0);
		Set<TestBoardCell> testList = cell.getAdjList();
		assertTrue(testList.contains(gameBoard.getCell(1,0)));
		assertTrue(testList.contains(gameBoard.getCell(0,1)));
		assertEquals(2, testList.size());
	}

	// bottom right corner (i.e., location [3][3])
	@Test
	public void test6Adjacency() {
		TestBoardCell cell = gameBoard.getCell(3, 3);
		Set<TestBoardCell> testList = cell.getAdjList();
		assertTrue(testList.contains(gameBoard.getCell(2,3)));
		assertTrue(testList.contains(gameBoard.getCell(3,2)));
		assertEquals(2, testList.size());
	}

	// right edge
	@Test
	public void test2Adjacency() {
		TestBoardCell cell = gameBoard.getCell(2, 3);
		Set<TestBoardCell> testList = cell.getAdjList();
		assertTrue(testList.contains(gameBoard.getCell(1,3)));
		assertTrue(testList.contains(gameBoard.getCell(2,2)));
		assertTrue(testList.contains(gameBoard.getCell(3,3)));
		assertEquals(3, testList.size());
	}

	// non edge or corner
	@Test
	public void test3Adjacency() {
		TestBoardCell cell = gameBoard.getCell(1, 1);
		Set<TestBoardCell> testList = cell.getAdjList();
		assertTrue(testList.contains(gameBoard.getCell(2,1)));
		assertTrue(testList.contains(gameBoard.getCell(1,0)));
		assertTrue(testList.contains(gameBoard.getCell(1,2)));
		assertTrue(testList.contains(gameBoard.getCell(0,1)));
		assertEquals(4, testList.size());
	}

	// bottom edge
	@Test
	public void test4Adjacency() {
		TestBoardCell cell = gameBoard.getCell(3, 1);
		Set<TestBoardCell> testList = cell.getAdjList();
		assertTrue(testList.contains(gameBoard.getCell(3,2)));
		assertTrue(testList.contains(gameBoard.getCell(3,0)));
		assertTrue(testList.contains(gameBoard.getCell(2,1)));
		assertEquals(3, testList.size());
	}

	// a left edge (e.g., location [3][0])
	@Test
	public void test5Adjacency() {
		TestBoardCell cell = gameBoard.getCell(3, 0);
		Set<TestBoardCell> testList = cell.getAdjList();
		assertTrue(testList.contains(gameBoard.getCell(2,0)));
		assertTrue(testList.contains(gameBoard.getCell(3,1)));
		assertEquals(2, testList.size());
	}

	/*
	 * Test calcTargets from (0,0) with 3 steps
	 */
	@Test
	public void test1TargetsNormal() {
		TestBoardCell cell = gameBoard.getCell(0, 0);
		gameBoard.calcTargets(cell,3);
		Set<TestBoardCell> targets = gameBoard.getTargets();
		assertEquals(6, targets.size());
		assertTrue(targets.contains(gameBoard.getCell(3, 0)));
		assertTrue(targets.contains(gameBoard.getCell(2, 1)));
		assertTrue(targets.contains(gameBoard.getCell(0, 3)));
		assertTrue(targets.contains(gameBoard.getCell(1, 2)));
		assertTrue(targets.contains(gameBoard.getCell(0, 1)));
		assertTrue(targets.contains(gameBoard.getCell(1, 0)));
	}

	/*
	 * Test calcTargets from (1,1) with 2 steps
	 */
	@Test
	public void test2TargetsNormal() {
		TestBoardCell cell = gameBoard.getCell(1, 1);
		gameBoard.calcTargets(cell,2);
		Set<TestBoardCell> targets = gameBoard.getTargets();
		assertEquals(6, targets.size());
		assertTrue(targets.contains(gameBoard.getCell(0, 0)));
		assertTrue(targets.contains(gameBoard.getCell(0, 2)));
		assertTrue(targets.contains(gameBoard.getCell(1, 3)));
		assertTrue(targets.contains(gameBoard.getCell(2, 2)));
		assertTrue(targets.contains(gameBoard.getCell(2, 0)));
		assertTrue(targets.contains(gameBoard.getCell(3, 1)));
	}

	/*
	 * Test calcTargets from (3,2) with 3 steps
	 */
	@Test
	public void test3TargetsNormal() {
		TestBoardCell cell = gameBoard.getCell(3, 2);
		gameBoard.calcTargets(cell,3);
		Set<TestBoardCell> targets = gameBoard.getTargets();
		assertEquals(7, targets.size());
		assertTrue(targets.contains(gameBoard.getCell(3, 3)));
		assertTrue(targets.contains(gameBoard.getCell(3, 1)));
		assertTrue(targets.contains(gameBoard.getCell(2, 2)));
		assertTrue(targets.contains(gameBoard.getCell(2, 0)));
		assertTrue(targets.contains(gameBoard.getCell(1, 3)));
		assertTrue(targets.contains(gameBoard.getCell(1, 1)));
		assertTrue(targets.contains(gameBoard.getCell(0, 2)));
	}

	/*
	 * Test for verifying that one cell is occupied and won't display in calcTargets
	 */
	@Test
	public void testTargetsOccupied() {
		gameBoard.getCell(2, 3).setOccupied(true);
		TestBoardCell location = gameBoard.getCell(1, 2);
		gameBoard.calcTargets(location, 2);
		Set<TestBoardCell> targets = gameBoard.getTargets();
		assertFalse(targets.contains(gameBoard.getCell(2, 3)));

	}

	/*
	 * Test for verifying that a room is there (at (2,1))
	 */
	@Test
	public void testTargetsRoom() {
		gameBoard.getCell(2, 1).setRoom(true);
		TestBoardCell location = gameBoard.getCell(1, 1);
		gameBoard.calcTargets(location, 3);
		Set<TestBoardCell> targets = gameBoard.getTargets();
		assertTrue(targets.contains(gameBoard.getCell(2, 1)));
	}

	/*
	 * Test for Mixed (Occupied and Room - same location) and confirming the calcTargets 
	 */
	@Test
	public void testTargetsMixed() {
		gameBoard.getCell(2, 3).setOccupied(true);
		gameBoard.getCell(2, 1).setRoom(true);
		TestBoardCell location = gameBoard.getCell(1, 2);
		gameBoard.calcTargets(location, 3);
		Set<TestBoardCell> targets = gameBoard.getTargets();
		assertFalse(targets.contains(gameBoard.getCell(2, 3)));
		assertTrue(targets.contains(gameBoard.getCell(2, 1)));
	}

	/*
	 * Test calcTargets with the max dice roll from (2,2)
	 */
	@Test
	public void testTargetsMaxRoll() {
		TestBoardCell cell = gameBoard.getCell(2, 2);
		gameBoard.calcTargets(cell,6);
		Set<TestBoardCell> targets = gameBoard.getTargets();
		assertEquals(7,targets.size());
		assertTrue(targets.contains(gameBoard.getCell(0, 0)));
		assertTrue(targets.contains(gameBoard.getCell(0, 2)));
		assertTrue(targets.contains(gameBoard.getCell(1, 1)));
		assertTrue(targets.contains(gameBoard.getCell(1, 3)));
		assertTrue(targets.contains(gameBoard.getCell(2, 0)));
		assertTrue(targets.contains(gameBoard.getCell(3, 1)));
		assertTrue(targets.contains(gameBoard.getCell(3, 3)));
	}

}
