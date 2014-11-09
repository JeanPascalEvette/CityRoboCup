
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
    
    static double getPlayerDirection(ArrayList<FlagObject> alFlag)
    {
        double direction = Double.MAX_VALUE;
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
        if(flag == null) return Double.MAX_VALUE;
        flag.getCoordinates(alFlag);
        if(flag.coordinates == null) return Double.MAX_VALUE;
        
        //flag.direction
        
        return direction;
    }
    
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
        x = formula(flag.coordinates, flag2.coordinates, flag.distance, flag2.distance, plusMinus);
        y = formula(new Coords(flag2.coordinates.y, flag2.coordinates.x), new Coords(flag.coordinates.y, flag.coordinates.x), flag2.distance, flag.distance, plusMinus);
        Coords c = new Coords(x,y);
        return c;
    }
    
    
    static private double formula(Coords d1, Coords d2, double dist1, double dist2, boolean plusMinus)
    {
        double d = (Math.sqrt(Math.pow(d2.x - d1.x, 2) + Math.pow(d2.y - d1.y,2)));
        double x1 = ((0.5)*(d2.x+d1.x));
        double x2 =((0.5)*(d2.x-d1.x)*(Math.pow(dist1,2)-Math.pow(dist2,2))/Math.pow(d,2));
        double x3 = 2*(d2.y-d1.y);
        double x4 = (Math.pow(dist1+dist2,2)-Math.pow(d,2));
        double K = ((0.25)*Math.sqrt(x4)*((Math.pow(d, 2))-Math.pow(dist1-dist2,2)));
        if(plusMinus)
            return x1 + x2 + x3 * K/Math.pow(d, 2);
        else
            return  x1 + x2 - x3 *K/Math.pow(d, 2);
        
    }
    
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
        double x2 = (flag.distance * Math.cos(Math.toRadians(playerDirection - flag.direction)));
        double x3 = (flag.distance * Math.cos(Math.toRadians(flag.direction - playerDirection)));
        double x4 = (flag.distance * Math.cos(Math.toRadians(flag.direction)));
        double x5 = (flag.distance * Math.cos(Math.toRadians(playerDirection)));
        double y = (flag.distance * Math.sin(Math.toRadians(playerDirection + flag.direction)));
        double y2 = (flag.distance * Math.sin(Math.toRadians(playerDirection - flag.direction)));
        double y3 = (flag.distance * Math.sin(Math.toRadians(flag.direction - playerDirection)));
        double y4 = (flag.distance * Math.sin(Math.toRadians(flag.direction)));
        double y5 = (flag.distance * Math.sin(Math.toRadians(playerDirection)));
        
        Coords toFlag = new Coords(x,y);
        Coords self = new Coords(toFlag.x - flag.coordinates.x, toFlag.y - flag.coordinates.y );
        Coords self2 = new Coords(x2 - flag.coordinates.x, y2 - flag.coordinates.y );
        Coords self3 = new Coords(x3 - flag.coordinates.x, y3 - flag.coordinates.y );
        Coords self4 = new Coords(x4 - flag.coordinates.x, y4 - flag.coordinates.y );
        Coords self5 = new Coords(x5 - flag.coordinates.x, y5 - flag.coordinates.y );
        
        
        return self;
    }
}
