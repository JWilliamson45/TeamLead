/**
 * PieChart.java
 *
 * Custom widget that renders a pie chart.
 *
 * @author James Williamson
 * @version 0.3.0
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.View;
import java.util.ArrayList;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class PieChart extends View
{
    /* Private member constants */
    private final int CHART_MIN_WIDTH = 600;                 /* The native width of the chart */
    private final int CHART_MIN_HEIGHT = CHART_MIN_WIDTH;    /* This chart must be square */

    /* Private member fields */
    private Context m_Context;                               /* The associated context */
    private ArrayList<PieChartEntry> m_ChartEntries;         /* The data modeled by the chart */
    private Paint m_PaintBrush;                              /* Paint brush that draws the chart */
    private Point m_ChartOrigin;                             /* The center of the chart, used for positioning */
    private RectF m_ChartClippingRegion;                     /* The region where the chart is actually drawn */

    /* Colors used for drawing the pie chart segments */
    private final int[] m_ChartColors =
    {
        // TODO: 3/20/2017 Should this be in XML instead?
        Color.BLUE,
        Color.RED,
        Color.YELLOW,
        Color.GREEN,
        Color.MAGENTA,
        Color.CYAN,
        Color.GRAY,
        Color.BLACK
    };

    /**
     * Constructs a new PieChart view.
     *
     * @param context The associated context.
     */
    public PieChart( Context context )
    {
        super( context );
        m_Context = context;
        m_ChartEntries = new ArrayList();
        m_PaintBrush = new Paint();
        m_ChartOrigin = new Point();
        m_ChartClippingRegion = new RectF();

        /* Apply minimum dimensions for the view */
        setMinimumWidth( CHART_MIN_WIDTH );
        setMinimumHeight( CHART_MIN_HEIGHT );
    }

    /**
     * Adds the entry to the PieChart to be graphically represented when the chart is painted. If there is no space
     * available on the chart, the entry is not added. If space remains, but the entry will not fit as-is, the
     * entry is truncated before added. Note that this may adversely affect the integrity of the data if precision
     * is required, so careful scaling is advised before adding entries.
     *
     * @param entry The entry to add to the chart.
     * @return True if the entry was added, false otherwise.
     */
    public boolean addEntry( PieChartEntry entry )
    {
        boolean success = false;

        /* First, validate the entry - is there enough space to add it to the chart? */
        if( validateEntry( entry ) == true )
        {
            m_ChartEntries.add( entry );
            success = true;
        }
        else
        {
            /*
             * Not enough space to add the entry, so if possible, truncate it. This is intended to make it easier
             * to add a final portion of the pie chart when rounding "noise" means the validation may fail and it
             * might not precisely fit.
             */
            int freeChartPercentage = getFreePercentage();

            if( freeChartPercentage > 0 )
            {
                entry.setPercentage( freeChartPercentage );
                m_ChartEntries.add( entry );
                success = true;
            }
        }

        return( success );
    }

    /**
     * Measure the view and its content to determine the measured width and the measured height.
     *
     * @param widthMeasureSpec Horizontal space requirements as imposed by the parent.
     * @param heightMeasureSpec Vertical space requirements as imposed by the parent.
     */
    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
    {
        int width;
        int height;

        /* Measure width */
        if( MeasureSpec.getMode( widthMeasureSpec ) == MeasureSpec.UNSPECIFIED )
        {
            /* Use suggested size */
            width = getSuggestedMinimumWidth();
        }
        else
        {
            /* Take what is offered */
            width = MeasureSpec.getSize( widthMeasureSpec );
        }

        /* Measure height */
        if( MeasureSpec.getMode( heightMeasureSpec ) == MeasureSpec.UNSPECIFIED )
        {
            /* Use suggested size */
            height = getSuggestedMinimumHeight();
        }
        else
        {
            /* Take what is offered */
            height = MeasureSpec.getSize( heightMeasureSpec );
        }

        /* Tell the parent how much space is needed */
        setMeasuredDimension( width, height );
    }

    /**
     * Draws the PieChart.
     *
     * @param canvas The canvas on which the chart will be drawn.
     */
    @Override protected void onDraw( Canvas canvas )
    {
        /* Running total of chart entry percentages used to draw multiple pie chart segments */
        int percentageTally = 0;

        /* Determine the drawing rectangle, based on origin and set dimensions */
        m_ChartOrigin.set( ( canvas.getWidth() / 2 ), ( canvas.getHeight() / 2 ) );

        m_ChartClippingRegion.set( ( m_ChartOrigin.x - ( CHART_MIN_WIDTH / 2 ) ),
                                   ( m_ChartOrigin.y - ( CHART_MIN_HEIGHT / 2 ) ),
                                   ( m_ChartOrigin.x + ( CHART_MIN_WIDTH / 2 ) ),
                                   ( m_ChartOrigin.y + ( CHART_MIN_HEIGHT / 2 ) ) );

        /* Draw the segments of the chart */
        for( PieChartEntry entry : m_ChartEntries )
        {
            /* Each segment gets a different color; grab the next one from the list */
            m_PaintBrush.setColor( m_ChartColors[entry.getId()] );

            /* Determine the start and end (sweep) angles for the arc that will be drawn */
            float startAngle = (float)convertPercentageToDegrees( percentageTally );
            float sweepAngle = (float)convertPercentageToDegrees( entry.getPercentage() );

            /* Draw the arc (the "true" argument implies that a filled wedge is actually what is drawn) */
            canvas.drawArc( m_ChartClippingRegion, startAngle, sweepAngle, true, m_PaintBrush );

            /* Update the running tally in order to draw subsequent segments, if applicable */
            percentageTally += entry.getPercentage();
        }
    }

    /**
     * Uses the parametric equation for a circle to return a point along the circumference, given the origin, radius,
     * and angle.
     *
     * @param origin The center point of the circle.
     * @param radius The radius of the circle.
     * @param angle The angle, in radians, of the line to the point.
     * @return The calculated point on the circumference.
     */
    private Point getPointOnCircle( Point origin, int radius, double angle )
    {
        Point p = new Point();

        p.x = (int)( origin.x + radius * cos( angle ) );
        p.y = (int)( origin.y + radius * sin( angle ) );

        return( p );
    }

    /**
     * Validates a pie chart entry; if adding the entry causes the total percentages of all elements combined to exceed
     * one hundred percent, the entry is not added (and thus, not drawn).
     *
     * @param entry The entry to be validated.
     * @return True if the entry is valid, false otherwise.
     */
    private boolean validateEntry( PieChartEntry entry )
    {
        int percentages = 0;
        boolean validEntry = false;

        /* Add up all percentages currently within the chart */
        for( PieChartEntry e : m_ChartEntries )
        {
            percentages += e.getPercentage();
        }

        /* If all percentages present plus the new entry are less than 100%, it can be added */
        if( percentages + entry.getPercentage() <= 100 )
        {
            validEntry = true;
        }

        return( validEntry );
    }

    /**
     * Returns the unallocated percentage of the pie chart.
     *
     * @return The free, unallocated percentage of the pie chart.
     */
    private int getFreePercentage()
    {
        int percentages = 0;

        /* Add up all percentages currently within the chart */
        for( PieChartEntry e : m_ChartEntries )
        {
            percentages += e.getPercentage();
        }

        if( percentages < 100 )
        {
            return( 100 - percentages );
        }
        else
        {
            return( 0 );
        }
    }

    /**
     * Converts the given percentage to radians, for measurement of angles on the chart.
     *
     * @param percent A given percentage (0-100%).
     * @return The corresponding angle, in radians.
     */
    private double convertPercentageToRadians( int percent )
    {
        return( ( percent * ( 2 * Math.PI ) ) / 100 );
    }

    /**
     * Converts the given percentage to degrees, for measurement of angles on the chart.
     *
     * @param percent A given percentage (0-100%).
     * @return The corresponding angle, in radians.
     */
    private double convertPercentageToDegrees( int percent )
    {
        return( ( percent * 360 ) / 100 );
    }
}
