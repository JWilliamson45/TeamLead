/**
 * PieChart.java
 *
 * Custom widget that renders a pie chart.
 *
 * @author James Williamson
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
import android.graphics.Typeface;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class PieChart extends View
{
    /* Private member constants */
    private final int VIEW_MIN_WIDTH = 800;               /* The native width of the view */
    private final int VIEW_MIN_HEIGHT = 1800;             /* The native height of the view */
    private final int CHART_WIDTH = 700;                  /* The width of the actual pie chart */
    private final int CHART_HEIGHT = 700;                 /* The height of the actual pie chart */
    private final int CHART_PADDING_TOP = 150;            /* The padding used for the top of the pie chart */
    private final int CHART_PADDING_BOTTOM = 150;         /* The padding used for the bottom of the pie chart */
    private final int LEGEND_RECT_HEIGHT = 20;            /* The height of the legend color code rectangle */
    private final int LEGEND_RECT_WIDTH = 350;            /* The width of the legend color code rectangle */
    private final int LEGEND_RECT_PADDING_TOP = 12;       /* The padding used for the top of the legend rectangle */
    private final int LEGEND_RECT_PADDING_RIGHT = 30;     /* The padding used for the right of the legend rectangle */
    private final int LEGEND_LINE_SPACING = 120;          /* The vertical spacing between each legend entry */
    private final int TASK_NAME_TEXT_SIZE = 20;           /* Text size for display of the task name */
    private final int TASK_PCT_TEXT_SIZE = 32;            /* Text size for display of the task percentage */

    /* Private member fields */
    private Context m_Context;                            /* The associated context */
    private ArrayList<PieChartEntry> m_ChartEntries;      /* The data modeled by the chart */
    private Paint m_PaintBrush;                           /* Paint brush that draws the chart */
    private RectF m_ChartClippingRegion;                  /* The region where the chart is actually drawn */
    private int[] m_ChartColors;                          /* Colors used for drawing the pie chart segments */
    private float m_DisplayDensity;                       /* The logical density of the display */
    private DecimalFormat m_PctFormatter;                 /* Formats percentages for display on the UI */

    /**
     * Constructs a new PieChart view.
     *
     * @param context The associated context.
     */
    public PieChart( Context context )
    {
        super( context );

        /* Member initialization */
        m_Context = context;
        m_ChartEntries = new ArrayList();
        m_PaintBrush = new Paint();
        m_ChartClippingRegion = new RectF();
        m_DisplayDensity = getResources().getDisplayMetrics().density;
        m_PctFormatter = new DecimalFormat( "##0.00" );

        /* Customize paintbrush */
        m_PaintBrush.setAntiAlias( true );
        m_PaintBrush.setDither( true );
        m_PaintBrush.setColor( Color.LTGRAY );

        /* Read color list from resource file and use it to initialize array for painting the chart */
        m_ChartColors = m_Context.getResources().getIntArray( R.array.pie_chart_colors );

        /* Apply minimum dimensions for the view */
        setMinimumWidth( VIEW_MIN_WIDTH );
        setMinimumHeight( VIEW_MIN_HEIGHT );
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
            float freeChartPercentage = getFreePercentage();

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
        float percentageTally = 0.0f;

        /* These "cursors" are used to draw the various elements of the chart */
        int verticalDrawCursor = CHART_PADDING_TOP + CHART_HEIGHT;
        int horizontalDrawCursor = ( canvas.getWidth() >> 1 ) - ( CHART_WIDTH >> 1 );

        /* Specify clipping region for the actual pie chart graphic */
        m_ChartClippingRegion.set( horizontalDrawCursor, CHART_PADDING_TOP, horizontalDrawCursor + CHART_WIDTH,
                                   verticalDrawCursor );

        /* Draw full chart outline - this will be replaced by the individual segments, if there's something to draw */
        canvas.drawArc( m_ChartClippingRegion, 0, 360, true, m_PaintBrush );

        /* Done - move the cursor to prepare for drawing of the chart's legend */
        verticalDrawCursor += CHART_PADDING_BOTTOM;

        /* For each chart entry, draw the segment, and its corresponding information */
        for( PieChartEntry entry : m_ChartEntries )
        {
            /* Each segment gets a different color; grab the next one from the list */
            m_PaintBrush.setColor( m_ChartColors[entry.getId()] );

            /* Determine the start and end (sweep) angles for the arc that will be drawn */
            float startAngle = convertPercentageToDegrees( percentageTally );
            float sweepAngle = convertPercentageToDegrees( entry.getPercentage() );

            /* Draw the arc (the "true" argument implies that a filled wedge is actually what is drawn) */
            canvas.drawArc( m_ChartClippingRegion, startAngle, sweepAngle, true, m_PaintBrush );

            /* Update the running tally in order to draw subsequent segments, if applicable */
            percentageTally += entry.getPercentage();

            /* Next, draw the corresponding entry for the legend */
            m_PaintBrush.setTextSize( TASK_NAME_TEXT_SIZE * m_DisplayDensity );
            m_PaintBrush.setTypeface( Typeface.create( Typeface.DEFAULT, Typeface.NORMAL ) );

            canvas.drawRect( horizontalDrawCursor, verticalDrawCursor, horizontalDrawCursor + LEGEND_RECT_WIDTH,
                             verticalDrawCursor + LEGEND_RECT_HEIGHT, m_PaintBrush );
            canvas.drawText( entry.getName(), horizontalDrawCursor, verticalDrawCursor - LEGEND_RECT_PADDING_TOP,
                             m_PaintBrush );

            m_PaintBrush.setTextSize( TASK_PCT_TEXT_SIZE * m_DisplayDensity );
            m_PaintBrush.setTypeface( Typeface.create( Typeface.DEFAULT, Typeface.BOLD ) );

            canvas.drawText( m_PctFormatter.format( entry.getPercentage() ) + "%",
                             horizontalDrawCursor + LEGEND_RECT_WIDTH + LEGEND_RECT_PADDING_RIGHT,
                             verticalDrawCursor + LEGEND_RECT_HEIGHT, m_PaintBrush );

            /* Update cursor for subsequent drawing */
            verticalDrawCursor += LEGEND_LINE_SPACING;
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
    private Point getPointOnCircle( Point origin, float radius, double angle )
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
        float percentages = 0.0f;
        boolean validEntry = false;

        /* Add up all percentages currently within the chart */
        for( PieChartEntry e : m_ChartEntries )
        {
            percentages += e.getPercentage();
        }

        /* If all percentages present plus the new entry are less than 100%, it can be added */
        if( percentages + entry.getPercentage() <= 100.0f )
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
    private float getFreePercentage()
    {
        float percentages = 0.0f;

        /* Add up all percentages currently within the chart */
        for( PieChartEntry e : m_ChartEntries )
        {
            percentages += e.getPercentage();
        }

        if( percentages < 100.0f )
        {
            return( 100.0f - percentages );
        }
        else
        {
            return( 0.0f );
        }
    }

    /**
     * Converts the given percentage to radians, for measurement of angles on the chart.
     *
     * @param percent A given percentage (0-100%).
     * @return The corresponding angle, in radians.
     */
    private float convertPercentageToRadians( float percent )
    {
        return( (float)( percent * ( 2.0f * Math.PI ) ) / 100.0f );
    }

    /**
     * Converts the given percentage to degrees, for measurement of angles on the chart.
     *
     * @param percent A given percentage (0-100%).
     * @return The corresponding angle, in radians.
     */
    private float convertPercentageToDegrees( float percent )
    {
        return( ( percent * 360.0f ) / 100.0f );
    }
}
