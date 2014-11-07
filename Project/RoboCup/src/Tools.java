
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
class Coords {
        public Coords(double newX, double newY) {this.x = newX; this.y = newY;}
        double x;
        double y;
    }
public class Tools {
    
    static Coords getPlayerPosition(ArrayList<FlagObject> alFlag, double playerDirection)
    {
        FlagObject flag = null;
        double minDist = Double.MAX_VALUE;
        for(FlagObject f : alFlag)
        {
            if(f.flag == Flag.LEFT_10 || f.flag == Flag.LEFT_20 || f.flag == Flag.LEFT_30 ||
                    f.flag == Flag.RIGHT_10 || f.flag == Flag.RIGHT_20 || f.flag == Flag.RIGHT_30)
           {
               if(minDist > f.distance)
               {
                    flag = f;
                    minDist = f.distance;
               }
           }
        }
        if(flag == null) return null;
        flag.getCoordinates(alFlag);
        if(flag.coordinates == null) return null;
        
        
        double x = (flag.distance * Math.cos(Math.toRadians(playerDirection + flag.direction)));
        double y = (flag.distance * Math.sin(Math.toRadians(playerDirection + flag.direction)));
        
        Coords toFlag = new Coords(x,y);
        Coords self = new Coords(toFlag.x - flag.coordinates.x, toFlag.y - flag.coordinates.y );
        
        
        return self;
    }
}
