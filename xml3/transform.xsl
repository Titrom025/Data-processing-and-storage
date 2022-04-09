<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" />
    <xsl:template match="/*[local-name()='people']">
        <html>
            <body>
                <table border="1">
                    <tr>
                        <th>Person info</th>
                        <th>Father info</th>
                        <th>Mother info</th>
                        <th>Brothers info</th>
                        <th>Sisters info</th>
                    </tr>
                    <xsl:apply-templates select="person"/>
                </table>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="person">
        <xsl:if test="count(parents/father-ref) > 0 and count(parents/mother-ref) > 0 and
            (count(id(parents/father-ref/@person-id)/parents/father-ref) > 0 or
             count(id(parents/father-ref/@person-id)/parents/mother-ref) > 0 or
             count(id(parents/mother-ref/@person-id)/parents/father-ref) > 0 or
             count(id(parents/mother-ref/@person-id)/parents/mother-ref) > 0) and
            count(siblings) > 0">
            <tr>
                <td>
                    <xsl:call-template name="person-description">
                        <xsl:with-param name="id" select="@id"/>
                    </xsl:call-template>
                </td>
                <td>
                    <xsl:if test="count(parents/father-ref) > 0">
                        <xsl:call-template name="person-description">
                            <xsl:with-param name="id" select="parents/father-ref/@person-id"/>
                        </xsl:call-template>
                    </xsl:if>
                </td>
                <td>
                    <xsl:if test="count(parents/mother-ref) > 0">
                        <xsl:call-template name="person-description">
                            <xsl:with-param name="id" select="parents/mother-ref/@person-id"/>
                        </xsl:call-template>
                    </xsl:if>
                </td>
                <td>
                    <xsl:for-each select="siblings/brother-ref">
                        <xsl:call-template name="person-description">
                            <xsl:with-param name="id" select="@person-id"/>
                        </xsl:call-template>
                        <p/>
                    </xsl:for-each>
                </td>
                <td>
                    <xsl:for-each select="siblings/sister-ref">
                        <xsl:call-template name="person-description">
                            <xsl:with-param name="id" select="@person-id"/>
                        </xsl:call-template>
                        <p/>
                    </xsl:for-each>
                </td>
            </tr>
        </xsl:if>
    </xsl:template>
    <xsl:template name="person-description">
        <xsl:param name="id"/>
        <div>
            <div><b>id: </b><xsl:value-of select="$id"/></div>
            <div><b>name: </b><xsl:value-of select="id($id)/@person-name"/></div>
            <div><b>gender: </b><xsl:value-of select="id($id)/@person-gender"/></div>
        </div>

        <xsl:if test="count(id($id)/parents/father-ref) > 0">
            <div><b>father: </b><xsl:value-of select="id(id($id)/parents/father-ref/@person-id)/@person-name"/></div>
        </xsl:if>

        <xsl:if test="count(id($id)/parents/mother-ref) > 0">
            <div><b>mother: </b><xsl:value-of select="id(id($id)/parents/mother-ref/@person-id)/@person-name"/></div>
        </xsl:if>

        <xsl:if test="count(id($id)/siblings/brother-ref) > 0">
            <div><b>brothers: </b>
                <ul style="margin: 0;">
                <xsl:for-each select="id($id)/siblings/brother-ref">
                     <li>
                         <xsl:value-of select="id(@person-id)/@person-name"/>
                         <xsl:if test="count(id($id)/siblings/brother-ref) > 0">,</xsl:if>
                     </li>
                </xsl:for-each>
                </ul>
            </div>
        </xsl:if>

        <xsl:if test="count(id($id)/siblings/sister-ref) > 0">
            <div><b>sisters: </b>
                <ul style="margin: 0;">
                <xsl:for-each select="id($id)/siblings/sister-ref">
                    <li>
                        <xsl:value-of select="id(@person-id)/@person-name"/>
                        <xsl:if test="count(id($id)/siblings/sister-ref) > 0">,</xsl:if>
                    </li>
                </xsl:for-each>
                </ul>
            </div>
        </xsl:if>

        <xsl:if test="count(id($id)/children/daughter-ref) > 0">
            <div><b>daughters: </b>
                <ul style="margin: 0;">
                <xsl:for-each select="id($id)/children/daughter-ref">
                    <li>
                        <xsl:value-of select="id(@person-id)/@person-name"/>
                        <xsl:if test="count(id($id)/children/daughter-ref) > 0">,</xsl:if>
                    </li>
                </xsl:for-each>
                </ul>
            </div>
        </xsl:if>

        <xsl:if test="count(id($id)/children/son-ref) > 0">
            <div><b>sons: </b>
                <ul style="margin: 0;">
                <xsl:for-each select="id($id)/children/son-ref">
                    <li>
                        <xsl:value-of select="id(@person-id)/@person-name"/>
                        <xsl:if test="count(id($id)/children/son-ref) > 0">,</xsl:if>
                    </li>
                </xsl:for-each>
                </ul>
            </div>
        </xsl:if>

        <xsl:if test="count(id(id($id)/parents/father-ref/@person-id)/parents/father-ref) > 0 or
             count(id(id($id)/parents/mother-ref/@person-id)/parents/father-ref) > 0">
            <div><b>grand-fathers: </b>
                <ul style="margin: 0;">
                    <xsl:if test="count(id(id($id)/parents/father-ref/@person-id)/parents/father-ref) > 0">
                        <li><xsl:value-of select="id(id(id($id)/parents/father-ref/@person-id)/parents/father-ref/@person-id)/@person-name"/></li>
                    </xsl:if>
                    <xsl:if test="count(id(id($id)/parents/mother-ref/@person-id)/parents/father-ref) > 0">
                        <li><xsl:value-of select="id(id(id($id)/parents/mother-ref/@person-id)/parents/father-ref/@person-id)/@person-name"/></li>
                    </xsl:if>
                </ul>
            </div>
        </xsl:if>

        <xsl:if test="count(id(id($id)/parents/father-ref/@person-id)/parents/mother-ref) > 0 or
             count(id(id($id)/parents/mother-ref/@person-id)/parents/mother-ref) > 0">
            <div><b>grand-mothers: </b>
                <ul style="margin: 0;">
                    <xsl:if test="count(id(id($id)/parents/father-ref/@person-id)/parents/mother-ref) > 0">
                        <li><xsl:value-of select="id(id(id($id)/parents/father-ref/@person-id)/parents/mother-ref/@person-id)/@person-name"/></li>
                    </xsl:if>
                    <xsl:if test="count(id(id($id)/parents/mother-ref/@person-id)/parents/mother-ref) > 0">
                        <li><xsl:value-of select="id(id(id($id)/parents/mother-ref/@person-id)/parents/mother-ref/@person-id)/@person-name"/></li>
                    </xsl:if>
                </ul>
            </div>
        </xsl:if>

        <div>
            <b>uncles: </b>
            <ul style="margin: 0;">
                <xsl:for-each select="id(id($id)/parents/father-ref/@person-id)/siblings/brother-ref">
                    <li><xsl:value-of select="id(@person-id)/@person-name"/></li>
                </xsl:for-each>
                <xsl:for-each select="id(id($id)/parents/mother-ref/@person-id)/siblings/brother-ref">
                    <li><xsl:value-of select="id(@person-id)/@person-name"/></li>
                </xsl:for-each>
            </ul>
        </div>

        <div>
            <b>aunts: </b>
            <ul style="margin: 0;">
                <xsl:for-each select="id(id($id)/parents/father-ref/@person-id)/siblings/sister-ref">
                    <li><xsl:value-of select="id(@person-id)/@person-name"/></li>
                </xsl:for-each>
                <xsl:for-each select="id(id($id)/parents/mother-ref/@person-id)/siblings/sister-ref">
                    <li><xsl:value-of select="id(@person-id)/@person-name"/></li>
                </xsl:for-each>
            </ul>
        </div>
    </xsl:template>
</xsl:stylesheet>