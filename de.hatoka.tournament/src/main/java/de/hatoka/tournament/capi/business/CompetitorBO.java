package de.hatoka.tournament.capi.business;

import de.hatoka.common.capi.business.BusinessObject;
import de.hatoka.common.capi.business.Money;

/**
 * A competitor is an player attending at a tournament.
 */
public interface CompetitorBO extends BusinessObject
{
    /**
     * Player activates the competitor, with paying the buy in.
     * @param money
     */
    void buyin(Money money);

    /**
     * @return the amount of money spend by player (buy-in and re-buy)
     */
    Money getInPlay();

    /**
     * @return the player instance
     */
    PlayerBO getPlayerBO();

    /**
     * (Current) tournament position of player.
     *
     * @return
     */
    Integer getPosition();

    /**
     * @return the amount of money won or lost by player after going out.
     */
    Money getResult();
    /**
     * Player is in.
     * @return
     */
    boolean isActive();

    /**
     * Player leaves the table and pays the given amount back. The tournament position is not modified.
     *
     * @param restAmount
     */
    void seatOpen(Money restAmount);

    /**
     * Player re-buys an amount, the amount will be added to the money in play.
     * @param reBuy
     */
    void rebuy(Money reBuy);

    /**
     * Defines the position of the player. The position is independent from "inPlayStatus"
     * @param position
     */
    void setPosition(Integer position);
}