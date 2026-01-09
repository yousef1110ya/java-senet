package com.example.algo.util;

import com.example.algo.state.Cell;
import com.example.algo.state.GameState;
import com.example.algo.state.Piece;
import com.example.algo.state.SpecialCell;

public class GeneralUtil {
	public static void printBoard(GameState state) {
		int rows = 3;
	    int cols = 10;

	    // First, find max cell content length for alignment
	    int maxLen = 0;
	    for (int i = 0; i < state.board.length; i++) {
	        Cell cell = state.board[i];
	        Piece piece = state.pieces.stream()
	        		.filter(p -> p.getPosition() == i).findFirst()
	        		.orElse(null);
	        String content = piece != null ? piece.getOwner().getName()
	                          : (cell instanceof SpecialCell ? "(Special)" : "(Empty)");
	        
	        if (content.length() > maxLen) maxLen = content.length();
	    }

	    for (int row = 0; row < rows; row++) {
	        for (int col = (row == 1 ? cols - 1 : 0); (row == 1 ? col >= 0 : col < cols); col += (row == 1 ? -1 : 1)) {
	            int index = row * cols + col;
	            Cell cell = state.board[index];
	            Piece piece = state.pieces.stream()
	            		.filter(p -> p.getPosition() == index).findFirst()
	            		.orElse(null);

	            String content = piece != null ? piece.getOwner().getName()
	                              : (cell instanceof SpecialCell ? "(Special)" : "(Empty)");

	            System.out.printf("[%02d:%-" + maxLen + "s] ", index, content);
	        }
	        System.out.println();
	    }
	}
	
	public static boolean checkGameOver(GameState state) {
		return state.pieces.isEmpty(); 
	}
	
	
}
