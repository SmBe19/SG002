package com.smeanox.games.sg002.world.actionHandler;

import com.smeanox.games.sg002.player.Player;

/**
 * Handle a playerchange
 *
 * @author Benjamin Schimd
 */
public interface NextPlayerHandler {
	/**
	 * Called when a new player starts its round
	 *
	 * @param nextPlayer the player that started its round
	 */
	void onNextPlayer(Player nextPlayer);
}
