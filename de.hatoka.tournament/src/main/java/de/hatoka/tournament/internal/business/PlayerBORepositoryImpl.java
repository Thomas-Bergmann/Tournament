package de.hatoka.tournament.internal.business;

import java.util.List;
import java.util.stream.Collectors;

import de.hatoka.common.capi.dao.UUIDGenerator;
import de.hatoka.tournament.capi.business.PlayerBO;
import de.hatoka.tournament.capi.business.PlayerBORepository;
import de.hatoka.tournament.capi.business.TournamentBusinessFactory;
import de.hatoka.tournament.capi.dao.PlayerDao;
import de.hatoka.tournament.capi.entities.PlayerPO;

public class PlayerBORepositoryImpl implements PlayerBORepository
{
    private final String accountRef;
    private final PlayerDao playerDao;
    private final TournamentBusinessFactory factory;
    private final UUIDGenerator externalRefGenerator;

    public PlayerBORepositoryImpl(String accountRef, PlayerDao playerDao, UUIDGenerator externalRefGenerator, TournamentBusinessFactory factory)
    {
        this.accountRef = accountRef;
        this.playerDao = playerDao;
        this.externalRefGenerator = externalRefGenerator;
        this.factory = factory;
    }


    @Override
    public PlayerBO create(String name)
    {
        return create(externalRefGenerator.generate(), name);
    }

    @Override
    public PlayerBO create(String externalRef, String name)
    {
        PlayerPO playerPO = playerDao.createAndInsert(accountRef, externalRef, name);
        return getBO(playerPO);
    }

    @Override
    public PlayerBO findByName(String name)
    {
        PlayerPO playerPO = playerDao.findByName(accountRef, name);
        if (playerPO == null)
        {
            return null;
        }
        return getBO(playerPO);
    }

    @Override
    public PlayerBO findByExternalRef(String externalRef)
    {
        PlayerPO playerPO = playerDao.findByExternalRef(accountRef, externalRef);
        if (playerPO == null)
        {
            return null;
        }
        return getBO(playerPO);
    }


    private PlayerBO getBO(PlayerPO playerPO)
    {
        return factory.getPlayerBO(playerPO);
    }

    @Override
    public PlayerBO getPlayerByID(String id)
    {
        PlayerPO playerPO = playerDao.getById(id);
        if (playerPO == null)
        {
            return null;
        }
        if (!accountRef.equals(playerPO.getAccountRef()))
        {
            throw new IllegalArgumentException("tournament not assigned to account");
        }
        return getBO(playerPO);
    }

    @Override
    public List<PlayerBO> getPlayers()
    {
        return playerDao.getByAccountRef(accountRef).stream().map(playerPO -> getBO(playerPO)).collect(Collectors.toList());
    }
}
