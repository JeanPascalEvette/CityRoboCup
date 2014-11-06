
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

    static Double get2ndSide(Flag flag, ArrayList<Flag> alFlag)
    {
        Coords res = null;
        for(int i = 0; i < flagList.length; i++)
        {
            if(flagList[i].equals(flag))
            {
                res = flagValues[i];
                for(Flag f : alFlag)
                {
                    if(f == Flag.CENTER || f == Flag.LEFT || f == Flag.RIGHT) continue;
                    for(int u = 0; u < flagList.length; u++)
                    {
                        if(flagList[u].equals(f))
                        {
                            Coords res2 = flagValues[u];
                            if(res.x == Double.MIN_VALUE && res2.x != Double.MIN_VALUE)
                                return res2.x;
                            else if(res.y == Double.MIN_VALUE && res2.y != Double.MIN_VALUE)
                                return res2.y;
                            break;
                        }
                    }
                        
                }
                break;
            }
        }
        return 0.0;
    }
    
    
    static Coords getFlag(ArrayList<Flag> alFlag)
    {
        Flag flag = null;
        for(Flag f : alFlag)
        {
            if(f != Flag.CENTER
                    && f != Flag.LEFT
                    && f != Flag.RIGHT)
            {flag = f; break;}
        }
        
        Coords res = null;
                int leftCount = 0, rightCount = 0,
                    ownCount = 0, otherCount = 0;
        for(int i = 0; i < flagList.length; i++)
        {
            if(flagList[i].equals(flag))
            {
                res = flagValues[i];
            break;
            }
        }
        for(Flag f : alFlag)
        {
            if(f.equals(flag)) continue;
            if(f == Flag.CENTER || f == Flag.LEFT || f == Flag.RIGHT) continue;
            if(flag == Flag.LEFT_10 || flag == Flag.LEFT_20 || flag == Flag.LEFT_30 || 
                    flag == Flag.RIGHT_10 || flag == Flag.RIGHT_20 || flag == Flag.RIGHT_30)
            {
                if(f == Flag.OTHER_10 || f == Flag.OTHER_20 || f == Flag.OTHER_30 || f == Flag.OTHER_40 || f == Flag.OTHER_50)
                {
                    otherCount++;
                }
                else if(f == Flag.OWN_10 || f == Flag.OWN_20 || f == Flag.OWN_30 || f == Flag.OWN_40 || f == Flag.OWN_50)
                {
                    ownCount++;
                }
            }
            else
            {
                if(f == Flag.LEFT_10 || f == Flag.LEFT_20 || f == Flag.LEFT_30)
                {
                    leftCount++;
                }
                else if(f == Flag.RIGHT_10 || f == Flag.RIGHT_20 || f == Flag.RIGHT_30)
                {
                    rightCount++;
                }
            }
        }
        
                    if(leftCount > rightCount && 
                            leftCount > ownCount && 
                            leftCount > otherCount)
                    {
                        res.y = -32.0;
                    }
                    else if(rightCount > leftCount && 
                            rightCount > ownCount && 
                            rightCount > otherCount)
                    {
                        res.y = 32.0;
                    }
                    else if(ownCount > leftCount && 
                            ownCount > rightCount && 
                            ownCount > otherCount)
                    {
                        res.x = 54.0;
                    }
                    else if(otherCount > leftCount && 
                            otherCount > rightCount && 
                            otherCount > ownCount)
                    {
                        res.x = -54.0;
                    }
                    else
                    {
                        return null;
                    }
        return res;
    }
}
