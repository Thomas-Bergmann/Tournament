package de.hatoka.tournament.capi.business;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.hatoka.common.capi.business.Money;

public interface TournamentBO extends GameBO
{
    /**
     * @return the start date and time of the tournament
     */
    Date getStartTime();

    /**
     * set start date and time of the tournament
     */
    void setStartTime(Date date);

    /**
     * @return name of tournament
     */
    String getName();

    /**
     * Set name of tournament
     */
    void setName(String name);

    /**
     * Creates a new blind level for the tournament
     *
     * @param duration
     * @param smallBlind
     * @param bigBlind
     * @param ante
     * @return
     */
    BlindLevelBO createBlindLevel(int duration, int smallBlind, int bigBlind, int ante);

    /**
     * Creates a new blind level for the tournament
     *
     * @param duration
     * @return
     */
    PauseBO createPause(int duration);

    /**
     * @return rounds of tournament
     */
    List<TournamentRoundBO> getTournamentRounds();

    /**
     * Removes a previously created pause or blind level
     *
     * @param round
     */
    void remove(TournamentRoundBO round);

    /**
     * @return current amount of rebuy (null if no rebuy possible)
     */
    Money getCurrentRebuy();

    /**
     * Defines the upper limit of players per table. This limit has an higher priority than the lower limit.
     *
     * @return maximum amount of player at one table
     */
    int getMaximumNumberOfPlayersPerTable();

    void setMaximumNumberOfPlayersPerTable(int number);

    /**
     * The initial stack size is used to convert the buy in value to chip amount. The ratio is used for re-buy, also.
     *
     * @return initial stack size per player
     */
    int getInitialStacksize();

    void setInitialStacksize(int initialStacksize);

    /**
     * Calculate the stack size of the winner. This includes all initial stacks plus re-buy stacks.
     *
     * @return stack size of the winner
     */
    int getFinalStacksize();

    /**
     * assign all active competitors to tables
     */
    void placePlayersAtTables();

    /**
     * @return tables with assigned competitors
     */
    List<TableBO> getTables();

    /**
     * @return get players, which are inactive but take part of tournament (which are placed)
     */
    List<CompetitorBO> getPlacedCompetitors();

    /**
     * Moves players from tables with more players to smaller tables.
     *
     * @return moved competitors
     */
    Collection<CompetitorBO> levelOutTables();

    /**
     * Registration can be done without buyin, a preparation step for the tournament
     *
     * @param playerBO
     * @return
     */
    CompetitorBO register(PlayerBO playerBO);

    /**
     * Competitor pays the buy-in and is allowed to play (is active afterwards)
     *
     * @param competitorBO
     */
    void buyin(CompetitorBO competitorBO);

    /**
     * Competitor pays additional re-buy and is allowed to play (is still active)
     *
     * @param competitorBO
     */
    void rebuy(CompetitorBO competitorBO);

    /**
     * Player leaves the table and the tournament pays depends on rank (is inactive afterwards).
     *
     * @param competitorBO
     */
    void seatOpen(CompetitorBO competitorBO);

    /**
     * Creates a new rank
     *
     * @param firstPosition
     *            (mandatory)
     * @param lastPosition
     *            (optional)
     * @param percentage
     *            (optional)
     * @param amount
     *            of rank (optional)
     */
    RankBO createRank(int firstPosition, int lastPosition, BigDecimal percentage, BigDecimal amount);

    default RankBO createFixRank(int firstPosition, int lastPosition, BigDecimal amount)
    {
        return createRank(firstPosition, lastPosition, null, amount);
    }

    default RankBO createRank(int firstPosition, int lastPosition, BigDecimal percentage)
    {
        return createRank(firstPosition, lastPosition, percentage, null);
    }

    default RankBO createRank(int firstPosition, int lastPosition)
    {
        return createRank(firstPosition, lastPosition, null, null);
    }

    List<RankBO> getRanks();

    void remove(RankBO rank);

    /**
     * Starts the tournament (current round is set to 0), registration, modifications of ranks, blind levels is not
     * longer possible.
     */
    void start();

    /**
     * @param competitorID
     * @return competitor by given identifier
     */
    CompetitorBO getCompetitorBO(String competitorID);

    /**
     * Defines the amount of rebuy, the currency is defined by the buy in. The {@link TournamentRoundBO} defines that a
     * rebuy is possible.
     *
     * @param rebuy
     *            amount
     */
    void setReBuy(BigDecimal rebuy);

    /**
     * @return the defined rebuy
     */
    Money getReBuy();

    /**
     * @return the current blind level
     *         <li>in case the current level is a pause it will return null
     *         <li>in case the tournament wasn't started yet, it returns the first blind level
     *         <li>in case the tournament is over, it return null
     */
    BlindLevelBO getCurrentBlindLevel();

    /**
     * @return the next blind level
     *         <li>in case the next level is a pause, it will return null
     *         <li>in case the tournament wasn't started yet, it returns the second blind level (in case it's pause then
     *         null)
     *         <li>in case the tournament is over, it return null
     */
    BlindLevelBO getNextBlindLevel();

    /**
     * @return next pause or null in case there is no pause
     */
    PauseBO getNextPause();

    String getGroupRef();

    void setGroupRef(String groupRef);
}
