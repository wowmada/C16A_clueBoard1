/*
 * Class: BadConfigFormatException
 * 
 * Purpose: Custom exception for telling the user if the config files have
 * errors upon initializing Board
 * 
 * Date: 10/21/2025
 * 
 * Author: Adan Corral Rascon, Daniel Hoang
 * 
 * Collaborators: none
 * 
 * Sources: none
 */

package clueGame;

public class BadConfigFormatException extends Exception {

	public BadConfigFormatException() {
		super("Error: failed to configure Board");
	}

	public BadConfigFormatException(String error) {
		super(error);
	}

}
