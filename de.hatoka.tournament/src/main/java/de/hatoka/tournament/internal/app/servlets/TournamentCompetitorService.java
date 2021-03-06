package de.hatoka.tournament.internal.app.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import de.hatoka.common.capi.app.FrameRenderer;
import de.hatoka.common.capi.app.servlet.AbstractService;
import de.hatoka.tournament.capi.business.CompetitorBO;
import de.hatoka.tournament.capi.business.PlayerBO;
import de.hatoka.tournament.capi.business.TournamentBusinessFactory;
import de.hatoka.tournament.internal.app.actions.PlayerAction;
import de.hatoka.tournament.internal.app.actions.TournamentAction;
import de.hatoka.tournament.internal.app.models.TournamentPlayerListModel;

@Path("/tournament/{id}/competitors")
public class TournamentCompetitorService extends AbstractService
{
    private static final String RESOURCE_PREFIX = "de/hatoka/tournament/internal/templates/app/";
    public static final String METHOD_NAME_LIST = "list";

    @PathParam("id")
    private String tournamentID;

    public TournamentCompetitorService()
    {
        super(RESOURCE_PREFIX);
    }

    private PlayerAction getPlayerAction()
    {
        return new PlayerAction(getUserRef(), getInstance(TournamentBusinessFactory.class));
    }

    private TournamentAction getTournamentAction()
    {
        TournamentBusinessFactory factory = getInstance(TournamentBusinessFactory.class);
        return new TournamentAction(getUserRef(), tournamentID, factory);
    }

    @POST
    @Path("/actionList")
    public Response actionPlayerList(@FormParam("competitorID") List<String> identifiers,
                    @FormParam("delete") String deleteButton, @FormParam("buyin") String buyInButton)
    {
        if (isButtonPressed(deleteButton))
        {
            return unassignPlayers(identifiers);
        }
        if (isButtonPressed(buyInButton))
        {
            return buyInPlayers(identifiers);
        }
        return redirect(METHOD_NAME_LIST, tournamentID);
    }

    @POST
    @Path("/assign")
    public Response assignPlayer(@FormParam("playerID") String playerID)
    {
        TournamentAction action = getTournamentAction();
        runInTransaction(() -> {
            PlayerBO playerBO = getPlayerAction().getPlayer(playerID);
            action.register(playerBO);
        });
        return redirectAddPlayer();
    }

    @POST
    @Path("/create")
    public Response createPlayer(@FormParam("name") String name, @FormParam("buyin") String buttonBuyIn)
    {
        TournamentAction action = getTournamentAction();
        runInTransaction(() -> {
            PlayerBO playerBO = getPlayerAction().createPlayer(name);
            CompetitorBO competitor = action.register(playerBO);
            if (isButtonPressed(buttonBuyIn))
            {
                action.buyInPlayers(Arrays.asList(competitor.getID()));
            }
        });
        return redirectAddPlayer();
    }

    @GET
    @Path("/list.html")
    public Response list()
    {
        TournamentAction action = getTournamentAction();
        final TournamentPlayerListModel model = action.getPlayerListModel(
                        getUriBuilder(TournamentListService.class, METHOD_NAME_LIST).build(),
                        getUriBuilder(TournamentCompetitorService.class, METHOD_NAME_LIST));
        try
        {
            String content = renderStyleSheet(model, "tournament_players.xslt", getXsltProcessorParameter("tournament"));
            return Response.status(200).entity(renderFrame(content, "players")).build();
        }
        catch(IOException e)
        {
            return render(e);
        }
    }

    @GET
    @Path("/addPlayer.html")
    public Response addPlayer()
    {
        TournamentAction action = getTournamentAction();
        final TournamentPlayerListModel model = action.getPlayerListModel(
                        getUriBuilder(TournamentListService.class, METHOD_NAME_LIST).build(),
                        getUriBuilder(TournamentCompetitorService.class, METHOD_NAME_LIST));
        try
        {
            String content = renderStyleSheet(model, "tournament_player_add.xslt",
                            getXsltProcessorParameter("tournament"));
            return Response.status(200).entity(renderFrame(content, "players")).build();
        }
        catch(IOException e)
        {
            return render(e);
        }
    }

    @POST
    @Path("/buyIn")
    public Response buyInPlayers(@FormParam("competitorID") List<String> identifiers)
    {
        TournamentAction action = getTournamentAction();
        runInTransaction(() -> action.buyInPlayers(identifiers));
        return redirect(METHOD_NAME_LIST, tournamentID);
    }

    private Response redirectAddPlayer()
    {
        return Response.seeOther(getUriBuilder(TournamentCompetitorService.class, "addPlayer").build(tournamentID))
                        .build();
    }

    @POST
    @Path("/unassign")
    public Response unassignPlayers(@FormParam("competitorID") List<String> identifiers)
    {
        TournamentAction action = getTournamentAction();
        runInTransaction(() -> action.unassignPlayers(identifiers));
        return redirect(METHOD_NAME_LIST, tournamentID);
    }

    private String renderFrame(String content, String subItem)
    {
        return getInstance(FrameRenderer.class).renderFame(content, "tournament", tournamentID, subItem);
    }
}