package org.spbu.histology.fxyz;

public class PolygonPoint extends TPoint
{
    protected PolygonPoint _next;
    protected PolygonPoint _previous;
    
    public PolygonPoint( double x, double y )
    {
        super( x, y );
    }

    public PolygonPoint( double x, double y, double z )
    {
        super( x, y, z );
    }

    public void setPrevious( PolygonPoint p )
    {
        _previous = p;
    }

    public void setNext( PolygonPoint p )
    {
        _next = p;
    }

    public PolygonPoint getNext()
    {
        return _next;
    }

    public PolygonPoint getPrevious()
    {
        return _previous;
    }
}