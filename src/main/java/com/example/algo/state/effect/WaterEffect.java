package com.example.algo.state.effect;

import com.example.algo.state.CellEffect;
import com.example.algo.state.GameState;
import com.example.algo.state.Piece;
import com.example.algo.util.GeneralUtil;

public class WaterEffect implements CellEffect {
    public void apply(Piece peice, GameState state) {
      /*
       * TODO:
       * 1- send the peice to the house of re-birth . 
       * 2- if the house of rebirth is not open we keep chekcing going less until we find an empty cell . 
       * NOTE:  if you got the question , what if there was no empty cells before the house of rebirth ? 
       * 		I would love to tell you that this case is impossible .
       */
    	GeneralUtil.sendToReBirth(peice,state);   	
    }
}
