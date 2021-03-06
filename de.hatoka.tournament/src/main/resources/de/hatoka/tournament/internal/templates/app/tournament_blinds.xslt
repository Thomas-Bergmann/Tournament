<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:hatoka="xalan://de.hatoka.common.capi.app.xslt.Lib"
  exclude-result-prefixes="hatoka"
>
  <xsl:import href="de/hatoka/common/capi/app/xslt/ui.xsl" />
  <xsl:param name="localizer" />
  <xsl:param name="uriInfo" />
  <xsl:output method="html" encoding="UTF-8" indent="yes" />
  <xsl:template match="/"  xmlns="http://www.w3.org/1999/xhtml">
    <form method="POST" action="actionList">
      <table class="table table-striped">
        <tr>
          <th>Select</th>
          <th>Small Blind</th>
          <th>Big Blind</th>
          <th>Ante</th>
		  <th>Re-Buy</th>
          <th>Duration (min)</th>
          <th>IsActive</th>
          <th>est. Start</th>
          <th>est. End</th>
        </tr>
        <xsl:for-each select="tournamentBlindLevelModel/blindLevels" xmlns="http://www.w3.org/1999/xhtml">
          <tr>
            <td>
              <xsl:call-template name="checkbox">
                <xsl:with-param name="name">levelID</xsl:with-param>
                <xsl:with-param name="value"><xsl:value-of select="id" /></xsl:with-param>
              </xsl:call-template>
            </td>
            <xsl:if test="pause = 'true'">
             <td colspan="3">Pause</td>
            </xsl:if>
            <xsl:if test="pause = 'false'">
              <td><xsl:call-template name="formatInteger">
                <xsl:with-param name="amount"><xsl:value-of select="smallBlind" /></xsl:with-param>
              </xsl:call-template></td>
              <td><xsl:call-template name="formatInteger">
                <xsl:with-param name="amount"><xsl:value-of select="bigBlind" /></xsl:with-param>
              </xsl:call-template></td>
              <td><xsl:if test="ante = '0'">-</xsl:if>
              <xsl:if test="ante != '0'"><xsl:call-template name="formatInteger">
                <xsl:with-param name="amount"><xsl:value-of select="ante" /></xsl:with-param>
              </xsl:call-template></xsl:if></td>
            </xsl:if>
            <td><input type="checkbox" name="reBuy" class="checkbox" disabled="disabled">
		        <xsl:attribute name="value"><xsl:value-of select="id" /></xsl:attribute>
			      <xsl:if test="rebuy = 'true'">
			        <xsl:attribute name="checked">checked</xsl:attribute>
			      </xsl:if>
            </input></td>
            <td><xsl:call-template name="formatDuration">
                <xsl:with-param name="minutes"><xsl:value-of select="duration" /></xsl:with-param>
            </xsl:call-template></td>
            <td><input type="checkbox" name="isActive" class="checkbox" disabled="disabled">
		        <xsl:attribute name="value"><xsl:value-of select="id" /></xsl:attribute>
			      <xsl:if test="active = 'true'">
			        <xsl:attribute name="checked">checked</xsl:attribute>
			      </xsl:if>
            </input></td>
            <td><xsl:call-template name="formatTime">
                <xsl:with-param name="date"><xsl:value-of select="estStartDateTime" /></xsl:with-param>
            </xsl:call-template></td>
            <td><xsl:call-template name="formatTime">
                <xsl:with-param name="date"><xsl:value-of select="estEndDateTime" /></xsl:with-param>
            </xsl:call-template></td>
          </tr>
        </xsl:for-each>
        <xsl:for-each select="tournamentBlindLevelModel/prefilled" xmlns="http://www.w3.org/1999/xhtml">
          <tr>
            <td> </td>
            <td><xsl:call-template name="input">
              <xsl:with-param name="name">smallBlind</xsl:with-param>
              <xsl:with-param name="value"><xsl:value-of select="smallBlind" /></xsl:with-param>
            </xsl:call-template></td>
            <td><xsl:call-template name="input">
              <xsl:with-param name="name">bigBlind</xsl:with-param>
              <xsl:with-param name="value"><xsl:value-of select="bigBlind" /></xsl:with-param>
            </xsl:call-template></td>
            <td><xsl:call-template name="input">
              <xsl:with-param name="name">ante</xsl:with-param>
              <xsl:with-param name="value"><xsl:value-of select="ante" /></xsl:with-param>
            </xsl:call-template></td>
            <td></td>
            <td><xsl:call-template name="input">
              <xsl:with-param name="name">duration</xsl:with-param>
              <xsl:with-param name="value"><xsl:value-of select="duration" /></xsl:with-param>
            </xsl:call-template></td>
           <td></td>
           <td><xsl:call-template name="formatTime">
                <xsl:with-param name="date"><xsl:value-of select="estStartDateTime" /></xsl:with-param>
            </xsl:call-template></td>
            <td><xsl:call-template name="formatTime">
                <xsl:with-param name="date"><xsl:value-of select="estEndDateTime" /></xsl:with-param>
            </xsl:call-template></td>
          </tr>
        </xsl:for-each>
      </table>
      <xsl:call-template name="button">
        <xsl:with-param name="name">create</xsl:with-param>
        <xsl:with-param name="buttonKey">button.create.level</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="button">
        <xsl:with-param name="name">start</xsl:with-param>
        <xsl:with-param name="buttonKey">button.start.level</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="button">
        <xsl:with-param name="name">delete</xsl:with-param>
        <xsl:with-param name="cssClass">btn</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="button">
        <xsl:with-param name="name">pause</xsl:with-param>
        <xsl:with-param name="buttonKey">button.create.pause</xsl:with-param>
        <xsl:with-param name="cssClass">btn</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="button">
        <xsl:with-param name="name">enableReBuy</xsl:with-param>
        <xsl:with-param name="buttonKey">button.rebuy.enable</xsl:with-param>
        <xsl:with-param name="cssClass">btn</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="button">
        <xsl:with-param name="name">disableReBuy</xsl:with-param>
        <xsl:with-param name="buttonKey">button.rebuy.disable</xsl:with-param>
        <xsl:with-param name="cssClass">btn</xsl:with-param>
      </xsl:call-template>
    </form>
  </xsl:template>
</xsl:stylesheet>