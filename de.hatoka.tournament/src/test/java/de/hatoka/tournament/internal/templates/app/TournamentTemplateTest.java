package de.hatoka.tournament.internal.templates.app;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.hatoka.common.capi.app.model.ActionVO;
import de.hatoka.common.capi.app.model.MoneyVO;
import de.hatoka.common.capi.app.model.SelectOptionVO;
import de.hatoka.common.capi.app.xslt.Lib;
import de.hatoka.common.capi.app.xslt.XSLTRenderer;
import de.hatoka.common.capi.business.CountryHelper;
import de.hatoka.common.capi.business.Money;
import de.hatoka.common.capi.resource.LocalizationBundle;
import de.hatoka.common.capi.resource.LocalizationConstants;
import de.hatoka.common.capi.resource.ResourceLoader;
import de.hatoka.common.capi.resource.ResourceLocalizer;
import de.hatoka.tournament.internal.app.models.BlindLevelVO;
import de.hatoka.tournament.internal.app.models.CompetitorVO;
import de.hatoka.tournament.internal.app.models.PlayerVO;
import de.hatoka.tournament.internal.app.models.RankVO;
import de.hatoka.tournament.internal.app.models.TableVO;
import de.hatoka.tournament.internal.app.models.TournamentBigScreenModel;
import de.hatoka.tournament.internal.app.models.TournamentBlindLevelModel;
import de.hatoka.tournament.internal.app.models.TournamentConfigurationModel;
import de.hatoka.tournament.internal.app.models.TournamentListModel;
import de.hatoka.tournament.internal.app.models.TournamentPlayerListModel;
import de.hatoka.tournament.internal.app.models.TournamentRankModel;
import de.hatoka.tournament.internal.app.models.TournamentTableModel;
import de.hatoka.tournament.internal.app.models.TournamentVO;

public class TournamentTemplateTest
{
    private static final String RESOURCE_PREFIX = "de/hatoka/tournament/internal/templates/app/";
    private static final XSLTRenderer RENDERER = new XSLTRenderer();
    private static final ResourceLoader RESOURCE_LOADER = new ResourceLoader();

    @BeforeClass
    public static void initClass()
    {
        XMLUnit.setIgnoreWhitespace(true);
    }

    private static String getResource(String resource) throws IOException
    {
        return RESOURCE_LOADER.getResourceAsString(RESOURCE_PREFIX + resource);
    }

    private static String wrapXMLRoot(String content) throws IOException
    {
        return "<body>\n" + content + "\n</body>";
    }

    private static Map<String, Object> getParameter()
    {
        Map<String, Object> result = new HashMap<>();
        result.put(Lib.XSLT_LOCALIZER, new ResourceLocalizer(
                        new LocalizationBundle(RESOURCE_PREFIX + "tournament", Locale.US, CountryHelper.TZ_BERLIN)));
        return result;
    }

    private static TournamentVO getTournamentVO(String id, String name, Date date)
    {
        return TournamentViewObjectHelper.getTournamentVO(id, name, date);
    }

    private static CompetitorVO getCompetitorVO(String id, String name, String playerID)
    {
        return TournamentViewObjectHelper.getCompetitorVO(id, name, playerID);
    }

    private static PlayerVO getPlayerVO(String id, String name)
    {
        return TournamentViewObjectHelper.getPlayerVO(id, name);
    }

    @Test
    public void testTournamentPlayers() throws Exception
    {
        TournamentPlayerListModel model = new TournamentPlayerListModel();
        model.setTournament(getTournamentVO("123456", "Test 1", parseDate("2011-11-25T08:42")));
        final CompetitorVO competitorVO = getCompetitorVO("1234578", "Player 1", "playerid-1");
        competitorVO.setTableNo(1111);
        competitorVO.setSeatNo(7);
        model.getCompetitors().add(competitorVO);
        CompetitorVO competitor2VO = getCompetitorVO("1234579", "Player 2", "playerid-2");
        competitor2VO.setResult(new MoneyVO(Money.valueOf("-1", "USD")));
        competitor2VO.setActive(false);
        model.getCompetitors().add(competitor2VO);
        model.getUnassignedPlayers().add(getPlayerVO("1234581", "Player 3"));
        String content = RENDERER.render(model, RESOURCE_PREFIX + "tournament_players.xslt", getParameter());
        // String content = new XMLRenderer().render(model);

        // Assert.assertEquals("players not listed correctly", getResource("tournament_players.result.xml"), content);
        XMLAssert.assertXMLEqual("players not listed correctly", getResource("tournament_players.result.xml"), content);
    }

    @Test
    public void testTournamentNoUnassignedPlayers() throws Exception
    {
        TournamentPlayerListModel model = new TournamentPlayerListModel();
        model.setTournament(getTournamentVO("123456", "Test 1", parseDate("2011-11-25T08:42")));
        model.getCompetitors().add(getCompetitorVO("1234578", "Player 1", "playerid-1"));
        CompetitorVO competitorVO = getCompetitorVO("1234579", "Player 2", "playerid-2");
        competitorVO.setResult(new MoneyVO(Money.valueOf("-1", "USD")));
        competitorVO.setActive(false);
        model.getCompetitors().add(competitorVO);
        String content = RENDERER.render(model, RESOURCE_PREFIX + "tournament_player_add.xslt", getParameter());

        // Assert.assertEquals("players not listed correctly",getResource("tournament_no_unassigned.result.xml"),
        // content);
        XMLAssert.assertXMLEqual("players unassigned incorrectly",
                        wrapXMLRoot(getResource("tournament_no_unassigned.result.xml")), wrapXMLRoot(content));
    }

    @Test
    public void testTournaments() throws Exception
    {
        TournamentListModel model = new TournamentListModel();
        model.getTournaments().add(getTournamentVO("123456", "Test 1", parseDate("2011-11-25T08:45")));
        model.getTournaments().add(getTournamentVO("123457", "Test 2", parseDate("2012-11-25T08:45")));
        String content = RENDERER.render(model, RESOURCE_PREFIX + "tournament_list.xslt", getParameter());
        Assert.assertEquals("tournaments not listed correctly", getResource("tournament_list.result.xml"), content);
        XMLAssert.assertXMLEqual("tournaments not listed correctly", getResource("tournament_list.result.xml"),
                        content);
    }

    @Test
    public void testTournamentBlindLevels() throws Exception
    {
        TournamentBlindLevelModel model = new TournamentBlindLevelModel();
        model.setTournament(getTournamentVO("123456", "Test 1", parseDate("2011-11-25T18:00")));
        List<BlindLevelVO> blindLevels = model.getBlindLevels();
        BlindLevelVO level1 = TournamentViewObjectHelper.getBlindLevelVO("1", 50, 100, 0, 30, true);
        level1.setEstStartDateTime(parseDate("2011-11-25T18:05")); // start 5 min later
        level1.setActive(true);
        blindLevels.add(level1);
        blindLevels.add(TournamentViewObjectHelper.getBlindLevelVO("2", 100, 200, 0, 30, true));
        blindLevels.add(TournamentViewObjectHelper.getPauseVO("3", 10, true));
        blindLevels.add(TournamentViewObjectHelper.getBlindLevelVO("4", 250, 500, 0, 30, false));
        model.getPrefilled().add(TournamentViewObjectHelper.getBlindLevelVO(500, 1000, 0, 30, false));
        model.fillTime();
        String content = RENDERER.render(model, RESOURCE_PREFIX + "tournament_blinds.xslt", getParameter());
        // String content = new XMLRenderer().render(model);

        // Assert.assertEquals("players not listed correctly", getResource("tournament_blinds.result.xml"), content);
        XMLAssert.assertXMLEqual("players not listed correctly", getResource("tournament_blinds.result.xml"), content);
    }

    @Test
    public void testTournamentRanks() throws Exception
    {
        TournamentRankModel model = new TournamentRankModel();
        model.setTournament(getTournamentVO("123456", "Test 1", parseDate("2011-11-25T18:00")));
        List<RankVO> blindLevels = model.getRanks();
        blindLevels.add(TournamentViewObjectHelper.getFixPriceRankVO("1", 1, 1, BigDecimal.valueOf(100)));
        blindLevels.add(TournamentViewObjectHelper.getPercentageRankVO("2", 2, 2, BigDecimal.valueOf(0.50),
                        BigDecimal.valueOf(50)));
        blindLevels.add(TournamentViewObjectHelper.getPercentageCalcRankVO("3", 3, 5, BigDecimal.valueOf(0.25),
                        BigDecimal.valueOf(25)));
        blindLevels.add(TournamentViewObjectHelper.getPercentageCalcRankVO("4", 6, 10, BigDecimal.valueOf(0.25),
                        BigDecimal.valueOf(25)));
        model.getPrefilled().add(new RankVO("new"));
        String content = RENDERER.render(model, RESOURCE_PREFIX + "tournament_ranks.xslt", getParameter());
        // String content = new XMLRenderer().render(model);

        Assert.assertEquals("content not correct rendered", getResource("tournament_ranks.result.xml"), content);
        XMLAssert.assertXMLEqual("content not correct rendered", getResource("tournament_ranks.result.xml"), content);
    }

    private static Date parseDate(String dateString) throws ParseException
    {
        SimpleDateFormat result = new SimpleDateFormat(LocalizationConstants.XML_DATEFORMAT_MINUTES);
        result.setTimeZone(CountryHelper.TZ_BERLIN);
        return result.parse(dateString);
    }

    @Test
    public void testTournamentConfiguration() throws Exception
    {
        TournamentConfigurationModel model = new TournamentConfigurationModel();
        TournamentVO tournamentVO = getTournamentVO("123456", "Test 1", parseDate("2011-11-25T18:00"));
        tournamentVO.setBuyIn(new MoneyVO(Money.valueOf("5", "USD")));
        tournamentVO.setReBuy(new MoneyVO(Money.valueOf("3", "USD")));

        List<SelectOptionVO> options = model.getReBuyOption().getOptions();
        options.add(new SelectOptionVO("single", true));
        options.add(new SelectOptionVO("multi", false));
        options.add(new SelectOptionVO("no", false));
        model.setTournament(tournamentVO);
        String content = RENDERER.render(model, RESOURCE_PREFIX + "tournament_general.xslt", getParameter());
        // String content = new XMLRenderer().render(model);

        Assert.assertEquals("overview not correct", getResource("tournament_general.result.xml"), content);
        XMLAssert.assertXMLEqual("overview not correct", getResource("tournament_general.result.xml"), content);
    }

    @Test
    public void testTournamentTables() throws Exception
    {
        TournamentTableModel model = new TournamentTableModel();
        TournamentVO tournamentVO = getTournamentVO("123456", "Test 1", parseDate("2011-11-25T18:00"));
        tournamentVO.setBuyIn(new MoneyVO(Money.valueOf("5", "USD")));
        model.getTables().addAll(getTables());
        model.getPlacedCompetitors().add(getInActiveCompetitor("109", "Darth Mouth", "209", 9));
        model.setTournament(tournamentVO);
        String content = RENDERER.render(model, RESOURCE_PREFIX + "tournament_tables.xslt", getParameter());
        // String content = new XMLRenderer().render(model);

        Assert.assertEquals("overview not correct", getResource("tournament_tables.result.xml"), content);
        XMLAssert.assertXMLEqual("overview not correct", getResource("tournament_tables.result.xml"), content);
    }

    @Test
    public void testTournamentBigScreen() throws Exception
    {
        TournamentBigScreenModel model = new TournamentBigScreenModel();
        model.setCurrentTime(parseDate("2011-11-25T18:35"));
        model.setNextPauseTime(parseDate("2011-11-25T19:15"));
        model.setDuration(Duration.ofMillis(parseDate("2011-11-25T18:50").getTime() - parseDate("2011-11-25T18:43").getTime()));
        TournamentVO tournamentVO = getTournamentVO("123456", "Test 1", parseDate("2011-11-25T18:00"));
        tournamentVO.setBuyIn(new MoneyVO(Money.valueOf("5", "USD")));
        model.setCurrentAmountPlayer(15);
        model.setMaxAmountPlayers(20);
        model.setAverageStackSize(2500);
        model.setCurrentBlindLevel(TournamentViewObjectHelper.getBlindLevelVO("2", 200, 400, 0, 30, true));
        model.setNextBlindLevel(TournamentViewObjectHelper.getBlindLevelVO("3", 250, 500, 0, 30, true));

        String content = RENDERER.render(model, RESOURCE_PREFIX + "tournament_bigscreen.xslt", getParameter());
        // String content = new XMLRenderer().render(model);

        // Assert.assertEquals("overview not correct", getResource("tournament_bigscreen.result.xml"), content);
        XMLAssert.assertXMLEqual("overview not correct", getResource("tournament_bigscreen.result.xml"), content);
    }

    private static Collection<TableVO> getTables()
    {
        List<TableVO> result = new ArrayList<>();
        TableVO table = new TableVO();
        table.setNumber(1);
        table.getCompetitors().add(getActiveCompetitor("100", "Obi-Wan", "200"));
        table.getCompetitors().add(getActiveCompetitor("101", "Trinity", "201"));
        result.add(table);
        table = new TableVO();
        table.setNumber(2);
        table.getCompetitors().add(getActiveCompetitor("102", "Neo", "202"));
        result.add(table);
        return result;
    }

    private static CompetitorVO getActiveCompetitor(String id, String name, String playerID)
    {
        CompetitorVO result = getCompetitorVO(id, name, playerID);
        result.getActions().add(new ActionVO("rebuy", URI.create("http://local/rebuy?competitor=" + id), "repeat"));
        result.getActions().add(new ActionVO("seatopen", URI.create("http://local/seatOpen?competitor=" + id),
                        "remove-circle"));
        return result;
    }

    private static CompetitorVO getInActiveCompetitor(String id, String name, String playerID, Integer position)
    {
        CompetitorVO result = getCompetitorVO(id, name, playerID);
        result.setPosition(position);
        result.setResult(new MoneyVO(Money.valueOf("3", "USD")));
        result.getActions().add(new ActionVO("rebuy", URI.create("http://local/rebuy?competitor=" + id), "repeat"));
        return result;
    }
}
