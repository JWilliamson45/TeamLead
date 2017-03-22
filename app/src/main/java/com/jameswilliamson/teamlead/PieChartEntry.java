/**
 * PieChartEntry.java
 *
 * Models a single item that can be inserted to a PieChart view.
 *
 * @author James Williamson
 * @version 0.3.0
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

public class PieChartEntry
{
    /* Private member fields */
    private String m_EntryName;                /* The name of the chart entry */
    private float m_EntryPercentage;           /* The percentage that the entry represents of the total */
    private int m_EntryId;                     /* A handle associated with the entry */

    /**
     * Creates a new PieChart entry.
     *
     * @param name The name of the chart entry.
     * @param percentage The weight given to the entry, in terms of percentage (0-100% supported).
     * @param id A ID handle associated with the entry.
     */
    public PieChartEntry( String name, float percentage, int id )
    {
        m_EntryName = name;
        m_EntryPercentage = percentage;
        m_EntryId = id;
    }

    /**
     * Returns the name of the entry.
     *
     * @return The name of the chart entry.
     */
    public String getName()
    {
        return( m_EntryName );
    }

    /**
     * Returns the percentage of the entry.
     *
     * @return The percentage of the chart that the entry represents (0-100%).
     */
    public float getPercentage()
    {
        return( m_EntryPercentage );
    }

    /**
     * Sets the percentage of the entry.
     *
     * @param percentage The percentage of the chart that the entry represents (0-100%).
     */
    public void setPercentage( float percentage )
    {
        m_EntryPercentage = percentage;
    }

    /**
     * Returns the ID of the entry.
     *
     * @return The ID handle associated with the entry.
     */
    public int getId()
    {
        return( m_EntryId );
    }
}
