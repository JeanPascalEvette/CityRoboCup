
import com.github.robocup_atan.atan.model.enums.Line;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jpevette
 */
public class LineObject implements Comparable {
    public Line line;
    public double direction;
    public double distance;
    public Coords coordinates;
    
    public LineObject(Line line, double direction, double distance)
    {
        this.line = line;
        this.direction = direction;
        this.distance = distance;
        coordinates = null;
    }
    
    @Override
    public int compareTo(Object o) {
        if(o.getClass()  == this.getClass())
        {
            if(this.distance < ((LineObject)o).distance)
                return -1;
            else if (this.distance > ((LineObject)o).distance)
                return 1;
            else
                return 0;
        }
        else 
            throw new UnsupportedOperationException("Can only compare with LineObjects."); //To change body of generated methods, choose Tools | Templates.
    }
}
