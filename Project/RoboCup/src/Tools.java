
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
          return new Coords(x,y);      
                
        /*
        double d = Math.sqrt(Math.abs(d1.x - d2.x) + Math.abs(d1.y - d2.y));
        double l1 = (Math.pow(dist1, 2) - Math.pow(dist2,2) + Math.pow(d,2))/(2*d);
        double h = Math.sqrt(Math.pow(dist1,2) - Math.pow(l1,2));
        double x3 = d1.x + (l1 * (d2.x - d1.x)) / d;
        double y3 = d1.y + (l1 * (d2.y - d1.y)) / d;
        double x ;
        if(plusMinus)
            x = x3 + (h * (d2.y - d1.y)) / d;
        else
            x = x3 - (h * (d2.y - d1.y)) / d;
        double y = y3 + (h * (d2.x - d1.x)) / d;
        return x;
        
        
        double e = d2.x - d1.x;                          
        double f = d2.y - d1.y;       
        double p = Math.sqrt(Math.pow(e,2) + Math.pow(f,2));  
        double k = (Math.pow(p,2) + Math.pow(dist1,2) - Math.pow(dist2,2))/(2*p);
        double x,y;
        if(plusMinus)
        {
            x = d1.x + e*k/p + (f/p)*Math.sqrt(Math.pow(dist1,2) - Math.pow(k,2));
            y = d1.y + f*k/p - (e/p)*Math.sqrt(Math.pow(dist1,2) - Math.pow(k,2));
          }
        else
        {
            x = d1.x + e*k/p - (f/p)*Math.sqrt(Math.pow(dist1,2) - Math.pow(k,2));
            y = d1.y + f*k/p + (e/p)*Math.sqrt(Math.pow(dist1,2) - Math.pow(k,2));
          }

          
          return x;
          
          
          
          
          
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
        */
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
