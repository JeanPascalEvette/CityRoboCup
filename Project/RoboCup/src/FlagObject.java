
import com.github.robocup_atan.atan.model.enums.Flag;
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
public class FlagObject implements Comparable {
    public Flag flag;
    public double direction;
    public double distance;
    public Coords coordinates;
    
    public FlagObject(Flag flag, double direction, double distance)
    {
        this.flag = flag;
        this.direction = direction;
        this.distance = distance;
        coordinates = null;
    }
    
    
    public void getCoordinates(ArrayList<FlagObject> alFlag)
    {
        int otherCount = 0, ownCount = 0;
        Coords myCoords = null;
        if(flag == Flag.LEFT_10) myCoords = new Coords(Double.MIN_VALUE,10);
        else if(flag == Flag.LEFT_20) myCoords = new Coords(Double.MIN_VALUE,20);
        else if(flag == Flag.LEFT_30) myCoords = new Coords(Double.MIN_VALUE,30);
        else if(flag == Flag.RIGHT_10) myCoords = new Coords(Double.MIN_VALUE,-10);
        else if(flag == Flag.RIGHT_20) myCoords = new Coords(Double.MIN_VALUE,-20);
        else if(flag == Flag.RIGHT_30) myCoords = new Coords(Double.MIN_VALUE,-30);
        else if(flag == Flag.OTHER_10) myCoords = new Coords(10,Double.MIN_VALUE);
        else if(flag == Flag.OTHER_20) myCoords = new Coords(20,Double.MIN_VALUE);
        else if(flag == Flag.OTHER_30) myCoords = new Coords(30,Double.MIN_VALUE);
        else if(flag == Flag.OTHER_40) myCoords = new Coords(40,Double.MIN_VALUE);
        else if(flag == Flag.OTHER_50) myCoords = new Coords(50,Double.MIN_VALUE);
        else if(flag == Flag.OWN_10) myCoords = new Coords(-10,Double.MIN_VALUE);
        else if(flag == Flag.OWN_20) myCoords = new Coords(-20,Double.MIN_VALUE);
        else if(flag == Flag.OWN_30) myCoords = new Coords(-30,Double.MIN_VALUE);
        else if(flag == Flag.OWN_40) myCoords = new Coords(-40,Double.MIN_VALUE);
        else if(flag == Flag.OWN_50) myCoords = new Coords(-50,Double.MIN_VALUE);
        if(myCoords == null) return;
        
        
        for(FlagObject f : alFlag)
        {
              if(f.flag == Flag.OTHER_10 || f.flag == Flag.OTHER_20 || f.flag == Flag.OTHER_30 || f.flag == Flag.OTHER_40 || f.flag == Flag.OTHER_50)
            {
                otherCount++;
            }
            else if(f.flag == Flag.OWN_10 || f.flag == Flag.OWN_20 || f.flag == Flag.OWN_30 || f.flag == Flag.OWN_40 || f.flag == Flag.OWN_50)
           {
                ownCount++;
            }
        }
        
        if(otherCount > ownCount)
        {
            myCoords.x = 54.0;
        }
        else if(ownCount > otherCount)
        {
            myCoords.x = -54.0;
        }
        else
        {
            return;
        }
        coordinates = myCoords;
    }

    @Override
    public int compareTo(Object o) {
        if(o.getClass()  == this.getClass())
        {
            if(this.distance < ((FlagObject)o).distance)
                return -1;
            else if (this.distance > ((FlagObject)o).distance)
                return 1;
            else
                return 0;
        }
        else 
            throw new UnsupportedOperationException("Can only compare with FlagObjects."); //To change body of generated methods, choose Tools | Templates.
    }
}
