package com.example.algo.setup;

import com.example.algo.state.Cell;
import com.example.algo.state.CellEffect;
import com.example.algo.state.NormalCell;
import com.example.algo.state.SpecialCell;
import com.example.algo.state.effect.HappinessEffect;
import com.example.algo.state.effect.HorusEffect;
import com.example.algo.state.effect.ReAtoumEffect;
import com.example.algo.state.effect.RebirthEffect;
import com.example.algo.state.effect.ThreeTruthsEffect;
import com.example.algo.state.effect.WaterEffect;

/*
 * DEV_NOTES: the returns here are wrong , the functions are only named.
 */
public class BoardFactory {
	public Cell[] createBoard() {
		Cell[] board = new Cell[30];
		for (int i = 0; i < board.length; i++) {
			int cellNumber = i + 1;
			
			if (cellNumber == 15) {
				// House of Rebirth
				board[i] = createSpecialCell(i);
			} else if (cellNumber == 26) {
				// House of Happiness
				board[i] = createSpecialCell(i);
			} else if (cellNumber == 27) {
				// House of Water
				board[i] = createSpecialCell(i);
			} else if (cellNumber == 28) {
				// House of Three Truths
				board[i] = createSpecialCell(i);
			} else if (cellNumber == 29) {
				// House of ReAtoum
				board[i] = createSpecialCell(i);
			} else if (cellNumber == 30) {
				// House of Horus
				board[i] = createSpecialCell(i);
			} else {
				// Normal Cell
				board[i] = createNormalCell(i);
			}	
		}
		return board;
	}
	
	protected Cell createNormalCell(int index) {
		return new NormalCell(index);
	}
	
	protected Cell createSpecialCell(int index) {
		CellEffect effect = null;
		int cellNumber = index + 1;

		switch (cellNumber) {
			case 15:
				effect = new RebirthEffect();
				break;
			case 26:
				effect = new HappinessEffect();
				break;
			case 27:
				effect = new WaterEffect();
				break;
			case 28:
				effect = new ThreeTruthsEffect();
				break;
			case 29:
				effect = new ReAtoumEffect();
				break;
			case 30:
				effect = new HorusEffect();
				break;
			default:
				return createNormalCell(index);
		}

		return new SpecialCell(index, effect);
	}
}
