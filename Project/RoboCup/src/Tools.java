
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
    static Flag[] flagList = {  
        Flag.LEFT_10,Flag.LEFT_20,Flag.LEFT_30,
        Flag.OTHER_10,Flag.OTHER_20,Flag.OTHER_30,Flag.OTHER_40,Flag.OTHER_50,
        Flag.OWN_10,Flag.OWN_20,Flag.OWN_30,Flag.OWN_40,Flag.OWN_50,
        Flag.RIGHT_10,Flag.RIGHT_20,Flag.RIGHT_30
                                    };
    static Coords[] flagValues = {  
        new Coords(Double.MIN_VALUE,10),new Coords(Double.MIN_VALUE,20),new Coords(Double.MIN_VALUE,30),
        new Coords(10,Double.MIN_VALUE),new Coords(20,Double.MIN_VALUE),new Coords(30,Double.MIN_VALUE),new Coords(40,Double.MIN_VALUE),new Coords(50,Double.MIN_VALUE),
        new Coords(-10,Double.MIN_VALUE),new Coords(-20,Double.MIN_VALUE),new Coords(-30,Double.MIN_VALUE),new Coords(-40,Double.MIN_VALUE),new Coords(-50,Double.MIN_VALUE),
        new Coords(Double.MIN_VALUE,-10),new Coords(Double.MIN_VALUE,-20),new Coords(Double.MIN_VALUE,-30)
                                    };

    static Coords doCircleThingy(ArrayList<FlagObject> alFlag, boolean plusMinus)
    {
        FlagObject flag = null;
        FlagObject flag2 = null;
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
        minDist = Double.MAX_VALUE;
        for(FlagObject f : alFlag)
        {
            if(f == flag) continue;
            if(f.flag == Flag.LEFT_10 || f.flag == Flag.LEFT_20 || f.flag == Flag.LEFT_30 ||
                    f.flag == Flag.RIGHT_10 || f.flag == Flag.RIGHT_20 || f.flag == Flag.RIGHT_30)
           {
               if(minDist > f.distance)
               {
                    flag2 = f;
                    minDist = f.distance;
               }
           }
        }
        if(flag == null) return null;
        if(flag2 == null) return null;
        flag.getCoordinates(alFlag);
        flag2.getCoordinates(alFlag);
        if(flag.coordinates == null) return null;
        if(flag2.coordinates == null) return null;
        double x,y;
        Coords c = formula(flag.coordinates, flag2.coordinates, flag.distance, flag2.distance, plusMinus);
        return c;
    }
    
    
    static private Coords formula(Coords d1, Coords d2, double dist1, double dist2, boolean plusMinus)
    {
         
        double d = Math.sqrt(Math.pow((d2.x-d1.x),2)+Math.pow((d2.y-d1.y),2));
        double K = (0.25)*Math.sqrt((d+dist1+dist2)*(-d+dist1+dist2)*(d-dist1+dist2)*(d+dist1-dist2));
        Coords C = new Coords((d2.x+d1.x)/2, (d2.y+d1.y)/2);
        double CD = (0.5)*(Math.pow(dist1,2)-Math.pow(dist2,2))/d; 
        
        double x,y;

        if(plusMinus)
        {
        x = (0.5)*(d2.x+d1.x) + (0.5)*(d2.x-d1.x)*(Math.pow(dist1,2)-Math.pow(dist2,2))/Math.pow(d,2) + 2*(d2.y-d1.y)*K/Math.pow(d,2);
        y = (0.5)*(d2.y+d1.y) + (0.5)*(d2.y-d1.y)*(Math.pow(dist1,2)-Math.pow(dist2,2))/Math.pow(d,2) - 2*(d2.x-d1.x)*K/Math.pow(d,2);
        }
        else
        {
        x = (0.5)*(d2.x+d1.x) + (0.5)*(d2.x-d1.x)*(Math.pow(dist1,2)-Math.pow(dist2,2))/Math.pow(d,2) - 2*(d2.y-d1.y)*K/Math.pow(d,2);
        y = (0.5)*(d2.y+d1.y) + (0.5)*(d2.y-d1.y)*(Math.pow(dist1,2)-Math.pow(dist2,2))/Math.pow(d,2) + 2*(d2.x-d1.x)*K/Math.pow(d,2);    
        }
        return new Coords(x*.9,-y);      // Minor adjustements to increase accuracy
    }     
}
