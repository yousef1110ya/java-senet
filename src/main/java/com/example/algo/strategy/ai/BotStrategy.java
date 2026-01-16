package com.example.algo.strategy.ai;

import java.util.ArrayList;
import java.util.List;

import com.example.algo.move.*;
import com.example.algo.player.Player;
import com.example.algo.rules.RuleEngine;
import com.example.algo.state.GameState;
import com.example.algo.state.Piece;
import com.example.algo.strategy.MoveStrategy;

/*
 * DEV_NOTE: I added a package for each one to prevent deleting code 
 * 			 when I do it like this we can test multipul algos at the 
 * 			 same time without deleting the old ones 
 */
public class BotStrategy implements MoveStrategy {
	/*
	 * definitions
	 */
	private static final int MAX_DEPTH = 3; // we can change this later guys...

	// also these values can be used for tuning our stupid algo
	private static final int POSITION_WEIGHT = 10;
	private static final int OPONENT_PENALTY = 5;
	private static final int WIN_BONUS = 10000;
	private static final int SPECIAL_CELL_BONUS = 50;

	public MovePiece chooseMove(GameState state, Player player, int stick) {
		List<MovePiece> moves = generateMoves(state, player, stick);

		if (moves.isEmpty()) {
			// No legal moves available - skip turn
			return null;
		}

		if (moves.size() == 1) {
			return moves.get(0);
		}

		MovePiece bestMove = null;
		int bestValue = Integer.MIN_VALUE;

		// Try all moves and evaluate the best one
		for (MovePiece move : moves) {
			// IMPORTANT: Create a fresh clone for each move evaluation
			// The move's piece reference points to the original state, so we need to
			// execute on a clone to avoid modifying the original state
			GameState nexState = state.clone();

			// Find the corresponding piece in the cloned state
			Piece clonedPiece = null;
			for (Piece p : nexState.pieces) {
				if (p.getOwner().equals(move.getPiece().getOwner()) &&
						p.getPosition() == move.getPiece().getPosition()) {
					clonedPiece = p;
					break;
				}
			}

			if (clonedPiece == null) {
				// Piece not found in clone - skip this move
				continue;
			}

			// Create a new move with the cloned piece
			MovePiece clonedMove = new MovePiece(clonedPiece, move.getTargetIndex());
			clonedMove.execute(nexState);
			nexState.switchPlayer();

			// call Expectiminimax because the next turn is for the opponent
			int value = expectiminimax(nexState, MAX_DEPTH - 1, player, false);

			if (value > bestValue) {
				bestValue = value;
				bestMove = move; // Keep reference to original move (with original piece)
			}
		}

		return bestMove != null ? bestMove : moves.get(0); // fallback to first move if no better move found
	}

	private int expectiminimax(GameState state, int depth, Player maximizingPlayer, boolean isMaxNode) {
		if (depth == 0 || isTerminal(state)) {
			return evaluate(state, maximizingPlayer);
		}

		if (isMaxNode) {
			return maxValue(state, depth, maximizingPlayer);
		} else {
			return minValue(state, depth, maximizingPlayer);
		}
	}

	private int minValue(GameState state, int depth, Player maximizingPlayer) {
		// minValue = opponent's turn (minimizing for us)
		return chanceValue(state, depth, maximizingPlayer, false);
	}

	private int maxValue(GameState state, int depth, Player maximizingPlayer) {
		// maxValue = our turn (maximizing for us)
		return chanceValue(state, depth, maximizingPlayer, true);
	}

	private int chanceValue(GameState state, int depth, Player maximizingPlayer, boolean isOurTurn) {
		double expectedValue = 0.0;

		// Probabilities for stick throws
		double[] probabilities = {
				0.25, // 1: 4/16
				0.375, // 2: 6/16
				0.25, // 3: 4/16
				0.0625, // 4: 1/16
				0.0625 // 5: 1/16
		};

		int[] stickValues = { 1, 2, 3, 4, 5 };

		for (int i = 0; i < stickValues.length; i++) {
			int stickThrow = stickValues[i];
			double probability = probabilities[i];

			Player currentPlayer = isOurTurn ? maximizingPlayer : getOpponent(state, maximizingPlayer);

			List<MovePiece> moves = generateMoves(state, currentPlayer, stickThrow);

			if (moves.isEmpty()) {
				// No legal moves, skip turn
				GameState nextState = state.clone();
				nextState.switchPlayer();
				int value = expectiminimax(nextState, depth - 1, maximizingPlayer, !isOurTurn);
				expectedValue += probability * value;
			} else {
				int bestValue;

				if (isOurTurn) {
					// Maximizing player
					bestValue = Integer.MIN_VALUE;
					for (MovePiece move : moves) {
						GameState nextState = state.clone();

						// CRITICAL FIX: Find the corresponding piece in cloned state
						Piece clonedPiece = findPieceInState(nextState, move.getPiece());

						if (clonedPiece != null) {
							// Create move with cloned piece
							MovePiece clonedMove = new MovePiece(clonedPiece, move.getTargetIndex());
							clonedMove.execute(nextState);
							nextState.switchPlayer();

							int value = expectiminimax(nextState, depth - 1, maximizingPlayer, false);
							bestValue = Math.max(bestValue, value);
						}
					}
				} else {
					// Minimizing player
					bestValue = Integer.MAX_VALUE;
					for (MovePiece move : moves) {
						GameState nextState = state.clone();

						// CRITICAL FIX: Find the corresponding piece in cloned state
						Piece clonedPiece = findPieceInState(nextState, move.getPiece());

						if (clonedPiece != null) {
							// Create move with cloned piece
							MovePiece clonedMove = new MovePiece(clonedPiece, move.getTargetIndex());
							clonedMove.execute(nextState);
							nextState.switchPlayer();

							int value = expectiminimax(nextState, depth - 1, maximizingPlayer, true);
							bestValue = Math.min(bestValue, value);
						}
					}
				}

				expectedValue += probability * bestValue;
			}
		}

		return (int) expectedValue;
	}

	/**
	 * Helper method to find the corresponding piece in a cloned state
	 */
	private Piece findPieceInState(GameState state, Piece originalPiece) {
		for (Piece p : state.pieces) {
			if (p.getOwner().equals(originalPiece.getOwner()) &&
					p.getPosition() == originalPiece.getPosition()) {
				return p;
			}
		}
		return null;
	}

	private int evaluate(GameState state, Player maximizingPlayer) {
		int score = 0;
		Player opponent = getOpponent(state, maximizingPlayer);

		// Get pieces for both players
		List<Piece> myPieces = state.getPiecesFor(maximizingPlayer);
		List<Piece> opponentPieces = state.getPiecesFor(opponent);

		// === POSITION EVALUATION (SYMMETRIC) ===
		// Reward our progress, penalize opponent's progress EQUALLY
		for (Piece piece : myPieces) {
			int position = piece.getPosition();
			if (position > 30) {
				score += WIN_BONUS;
			} else {
				score += position * POSITION_WEIGHT;
			}
		}

		for (Piece piece : opponentPieces) {
			int position = piece.getPosition();
			if (position > 30) {
				score -= WIN_BONUS;
			} else {
				// FIXED: Same weight as our pieces
				score -= position * POSITION_WEIGHT;
			}
		}

		// === SPECIAL CELL BONUSES (SYMMETRIC) ===
		// Cell 15 (House of Rebirth) - EQUAL importance
		if (hasPieceOnCell(state, maximizingPlayer, 15)) {
			score += SPECIAL_CELL_BONUS;
		}
		if (hasPieceOnCell(state, opponent, 15)) {
			// FIXED: Full penalty, not half
			score -= SPECIAL_CELL_BONUS;
		}

		// Cell 26 (House of Happiness) - SYMMETRIC evaluation
		if (hasPieceOnCell(state, maximizingPlayer, 26)) {
			score += SPECIAL_CELL_BONUS;
		}
		// FIXED: Added opponent evaluation for cell 26
		if (hasPieceOnCell(state, opponent, 26)) {
			score -= SPECIAL_CELL_BONUS;
		}

		// === END GAME BONUS (26-30 cells) ===
		for (Piece piece : myPieces) {
			if (piece.getPosition() >= 26 && piece.getPosition() <= 30) {
				score += 20;
			}
		}
		// FIXED: Penalize opponent's end-game position
		for (Piece piece : opponentPieces) {
			if (piece.getPosition() >= 26 && piece.getPosition() <= 30) {
				score -= 20;
			}
		}

		// === EARLY GAME PENALTY (SYMMETRIC) ===
		// Penalize slow development for both players
		for (Piece piece : myPieces) {
			if (piece.getPosition() < 10) {
				score -= 10;
			}
		}
		// FIXED: Reward when opponent has slow pieces
		for (Piece piece : opponentPieces) {
			if (piece.getPosition() < 10) {
				score += 10;
			}
		}

		// === ADDITIONAL STRATEGIC FACTORS ===
		// Count pieces still in play (more pieces = better position)
		int myActivePieces = 0;
		int opponentActivePieces = 0;

		for (Piece piece : myPieces) {
			if (piece.getPosition() <= 30)
				myActivePieces++;
		}
		for (Piece piece : opponentPieces) {
			if (piece.getPosition() <= 30)
				opponentActivePieces++;
		}

		// Slight bonus for having more active pieces early in game
		score += (myActivePieces - opponentActivePieces) * 5;

		return score;
	}

	private boolean isTerminal(GameState state) {
		for (Player player : state.players) {
			List<Piece> pieces = state.getPiecesFor(player);
			boolean allRemoved = true;

			for (Piece piece : pieces) {
				if (piece.getPosition() <= 30) {
					allRemoved = false;
					break;
				}
			}

			if (allRemoved) {
				return true;
			}
		}
		return false;
	}

	private Player getOpponent(GameState state, Player player) {
		for (Player p : state.players) {
			if (!p.equals(player)) {
				return p;
			}
		}
		return null;
	}

	private boolean hasPieceOnCell(GameState state, Player player, int cellNumber) {
		List<Piece> pieces = state.getPiecesFor(player);
		for (Piece piece : pieces) {
			if (piece.getPosition() == cellNumber) {
				return true;
			}
		}
		return false;
	}

	private List<MovePiece> generateMoves(GameState state, Player player, int stickThrow) {
		List<MovePiece> moves = new ArrayList<>();
		List<Piece> playerPieces = state.getPiecesFor(player);

		for (Piece piece : playerPieces) {
			int currentPos = piece.getPosition();

			// Skip pieces that have already exited
			if (currentPos > 30) {
				continue;
			}

			int targetPos = currentPos + stickThrow;

			// Allow moves within board (1-30) OR exiting moves (31+) if piece is in last 5
			// cells (26-30)
			if (targetPos <= 30 || (currentPos >= 26 && currentPos <= 30)) {
				MovePiece move = new MovePiece(piece, targetPos);

				if (RuleEngine.isLegal(move, state)) {
					moves.add(move);
				}
			}
		}

		return moves;
	}

}
