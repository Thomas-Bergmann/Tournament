package de.hatoka.tournament.capi.entities;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(namespace = "de.hatoka.xml.tournament")
public class TournamentPersistenceModel
{
    @XmlElementWrapper(name = "players")
    @XmlElement(name = "player")
    private ArrayList<PlayerPO> playerPOs;

    @XmlElementWrapper(name = "tournaments")
    @XmlElement(name = "tournament")
    private ArrayList<TournamentPO> tournamentPOs;

    @XmlTransient
    public ArrayList<PlayerPO> getPlayerPOs()
    {
        return playerPOs;
    }

    public void setPlayerPOs(Collection<PlayerPO> players)
    {
        this.playerPOs = new ArrayList<>(players);
    }

    @XmlTransient
    public ArrayList<TournamentPO> getTournamentPOs()
    {
        return tournamentPOs;
    }

    public void setTournamentPOs(Collection<TournamentPO> tournamentPOs)
    {
        this.tournamentPOs = new ArrayList<>(tournamentPOs);
    }
}
