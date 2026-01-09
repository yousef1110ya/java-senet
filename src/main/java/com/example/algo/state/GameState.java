package com.example.algo.state;

import java.util.List;
import java.util.ArrayList;

import com.example.algo.player.Player;

public class GameState {
	/*
	 * Definitions 
	 */
	private final Cell[] board = new Cell[30];
	private final List<Piece> pieces = new ArrayList<>();
	private int currentPlayerIndex;
	private int heuristec;
	
	/*
	 * public functions 
	 */
	public Cell getCell(int index) {
		return board[index];
	}
	
	public List<Piece> getPiecesFor(Player player){
	    return pieces.stream()
	                 .filter(p -> player.equals(p.getOwner()))
	                 .toList();
	}
	
	public int calculateHeuristec(Player player) {
		// the return value is wrong , we need to calculate what we want .
		List<Piece> player_pieces = getPiecesFor(player);
		int count = 0; 
		for(Piece current : player_pieces) {
			count += current.getPosition();
		}
		return count/player_pieces.size();
	}
}
