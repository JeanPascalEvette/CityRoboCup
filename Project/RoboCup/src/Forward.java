
//~--- non-JDK imports --------------------------------------------------------

import com.github.robocup_atan.atan.model.ActionsPlayer;
import com.github.robocup_atan.atan.model.enums.Line;
import com.github.robocup_atan.atan.model.enums.PlayMode;
import java.util.ArrayList;

import org.apache.log4j.Logger;

//~--- JDK imports ------------------------------------------------------------

import java.util.Random;

/**
 * A simple controller. It implements the following simple behaviour. If the
 * client sees nothing (it might be out of the field) it turns 180 degree. If
 * the client sees the own goal and the distance is less than 40 and greater
 * than 10 it turns to his own goal and dashes. If it cannot see the own goal
 * but can see the ball it turns to the ball and dashes. If it sees anything but
 * not the ball or the own goals it dashes a little bit and turns a fixed amount
 * of degree to the right.
 *
 * @author Atan
 */
public class Forward extends Player {

public enum STATE { // FSM Implementation
		REST, // Inactive/Default state
		GOTO_BALL, // Going towards the ball
		BALL_CARRIER, // Currently carrying the ball
		ATTACKING, // Not carrying the ball but supporting the attack
		GO_BACK	//Going back to original position
	}

    protected static int    count         = 0;
    private int             forwardId     = 0;
    private double          directionCenterLine = 0;
	private STATE myState;
	private int counter;
    /**
     * Constructs a new simple client.
     */
    public Forward(ArrayList<Player> t, int number) {
        super(t, number);
        myState = STATE.REST;
        directionCenterLine = Double.MAX_VALUE;
        random = new Random(System.currentTimeMillis() + count);
        forwardId = count++;
        switch (forwardId) {
                case 0 :
                    startingX = -10;startingY = 10;
                    break;
                case 1 :
                    startingX = -10;startingY = -10;
                    break;
            }
    }

   /** {@inheritDoc} */
    @Override
    public void preInfo() {
        super.preInfo();
        directionCenterLine = Double.MAX_VALUE;
    }

    /** {@inheritDoc} */
    @Override
    public void postInfo() {
Update();    
//    super.postInfo();
    //    if (canSeeBall) {
    //        canSeeBallAction();
    //    } else {
    //        canSeeNothingAction();
    //    }
    }
    
    public void Update()
	{
		switch(myState)
		{
			case REST : HandleRest(); break;
			case GOTO_BALL : HandleGoToBall(); break;
			case BALL_CARRIER : HandleBallCarrier(); break;
			case ATTACKING : HandleAttacking(); break;
			case GO_BACK : HandleGoBack(); break;
		}
	}
	
	public void HandleRest() {
		if(canSeeBall)//
		{
			if(checkIfClosestToBall()){
				myState = STATE.GOTO_BALL; return; }// If you are the closest go for the ball
			else if(checkIfTeamMateHasBall()){
				myState = STATE.ATTACKING; return; } // Else support the attack
                        else{
                            getPlayer().turn(directionBall);
                        }
		}
		else
		getPlayer().turn(60); // If you don't tknow where the ball is turn around
	}
	public void HandleGoToBall() {
		if(!canSeeBall) { myState = STATE.GO_BACK;counter = 0; return; } // If you lose sight of the ball go back to orginial position
		if(distanceBall <= 0.7) // If you are at <0.7 from the ball then you are now the carrier
		{
			myState = STATE.BALL_CARRIER; return;
		}
                else // Else go towards the ball
		{
                    getPlayer().turn(directionBall);
                    getPlayer().dash(100);
		}
	}
	
	
	public void HandleBallCarrier() {
		if(!canSeeBall) // If you lose sight of the ball go back to original position
		{
			myState = STATE.GO_BACK; counter = 0;
			return;
		}
                if (hasToPass()) {
                    shootTowardsClosestPlayer();
                }
                else if(canSeeOwnGoal) // If some opponent is close try to pass it to a teammate
		{
                    getPlayer().turn(180);
                }
                else if(closestPlayerDistance < 1.5) // If some opponent is close try to pass it to a teammate
		{
			shootTowardsClosestPlayer();
		}
                else if(distanceBall < 0.7 && distanceOtherGoal<25 && directionOtherGoal != Double.MAX_VALUE) // If you are in range of the opponents goal shoot
		{
                        getPlayer().kick(100, directionOtherGoal);
                }
                else if(distanceBall < 0.7 && directionOtherGoal != Double.MAX_VALUE) // If you are out of range but know where the opponent's goal is go towards it
		{
                        getPlayer().kick(15, directionOtherGoal);
                        getPlayer().dash(10);
                }
                else if(distanceBall < 0.7 && directionOtherGoal == Double.MAX_VALUE && closestPlayer != null) // If you don't know where to go just go anywhere
		{
                    shootTowardsClosestPlayer();
                }
                else if(distanceBall < 0.7 && directionOtherGoal == Double.MAX_VALUE) // If you don't know where to go just go anywhere
		{
                        getPlayer().kick(15, 0);
                        getPlayer().dash(10);
                }
                else if(canSeeBall) // Else if you can still see the ball - got towards it
                {
                    getPlayer().turn(directionBall);
                    getPlayer().dash(100);
                }
                else // If you cannot see the ball, turn around.
                {
                    getPlayer().turn(60);
                }
	}
	public void HandleAttacking() {
	if(canSeeBall)
		{
			if(checkIfClosestToBall()) // If you are now the closest to the ball - become the carrier
				{ myState = STATE.BALL_CARRIER; return; }
			if(distanceOtherGoal < Double.MAX_VALUE) // Else if you know where the opponent's goal is - go towards it
			{
				getPlayer().turn(directionOtherGoal);
				getPlayer().dash(100);
			}
			else if(checkIfTeamMateHasBall()) // Else if you know where the ballcarrier is then go towards him
			{
				getPlayer().turn(directionBall);
				getPlayer().dash(50);
			}
                        else // Else go back
			{
				myState = STATE.GO_BACK; return;
			}
		}
        else // else go back
		{
			myState = STATE.GO_BACK; return;
		}
	}

	public void HandleGoBack() {
		if(canSeeBall)
		{
			if(checkIfClosestToBall()) // If you are the closest go for the ball
				{ myState = STATE.GOTO_BALL; return; }
			else if(checkIfTeamMateHasBall()) // else if a teammate has the ball support him
				{ myState = STATE.ATTACKING; return; }
		} // else if you cannot see the ball or no teammate has it
		if(counter++<4){getPlayer().turn(90); } // turn a few times to try to see it
                else  {goDefaultPos();} // else return to default position
		
	}
    
        
    private boolean checkIfTeamMateHasBall() {
        for(Player p : myTeam)
        {
            if(p.distanceBall < 3) // Check whether any player is 3m from the ball
                return true;
        }
        return false;
    }
    

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return "Forward";
    }
    
    /**
     * This is the action performed when the player can see the ball.
     * It involves running at it and kicking it...
     */
    protected void canSeeBallAction() {  
        if(checkIfClosestToBall()) // Trying to figure out if he can get the ball
        {
            if (distanceBall < 0.7) { // Can shoot
              /*  if (currentPlayMode == PlayMode.FREE_KICK_FAULT_OWN || // Avoid passing the ball to yourself if in freekick/kickoff mode
                        currentPlayMode == PlayMode.FREE_KICK_OWN || 
                        currentPlayMode == PlayMode.KICK_OFF_OWN) {
                    shootTowardsClosestPlayer();*/
               // getPlayer().dash(30);
                                
                 if(closestPlayerOtherDistance < 1.5) // If an enemy is close pass the ball to a teammate
                {
                    shootTowardsClosestPlayer();
                }
                else // else just kick it forward and go get it
                {
                    checksBothSides = -1;
                    if(canSeeOtherGoal) // Shoot towards goal
                    {
                        if(distanceOtherGoal > 20)
                            getPlayer().kick(10, directionOtherGoal);
                        else
                            getPlayer().kick(100, directionOtherGoal);
                    }
                    else // If you cant see the opponents goal try to find it by looking around.
                    {
                       getPlayer().turn(50);
                    }
                }
            }
            else // Else if you're not in shooting range - run towards the ball
            {
            turnTowardBall();
            int distDash = 80;
            if(distanceBall < 0.9) // slow as you get close this was 3?
                distDash = 50;
            getPlayer().dash(distDash);
            }
        }
        else // Not allowed to get the ball, do something else
        {
            if(canSeeOtherGoal) // If you can see the opponents goal try to help attack
            {
                getPlayer().turn(directionOtherGoal);
                getPlayer().dash(100);
            }
            else // else turn
            {
                getPlayer().turn(60);
            }
        }
    }

  

    /**
     * If the player can see nothing, it turns 180 degrees.
     */
    protected void canSeeNothingAction() {
        goDefaultPos();
    }

    /**
     * If the player can see its own goal, it goes and stands by it...
     */
    protected void canSeeOwnGoalAction() {
        getPlayer().dash(this.randomDashValueFast());
        turnTowardOwnGoal();
    }
    

    /** {@inheritDoc} */
    @Override
    public void infoSeeLine(Line line, double distance, double direction, double distChange, double dirChange,
                            double bodyFacingDirection, double headFacingDirection) {
        super.infoSeeLine(line, distance, direction, distChange, dirChange, bodyFacingDirection, headFacingDirection);
        if(line == Line.CENTER) {
            directionCenterLine = direction;
        }
    }

    
    private void goDefaultPos() { // try to go back to the default position 
        if(directionCenterLine != Double.MAX_VALUE)
        {
            getPlayer().turn(directionCenterLine);
            getPlayer().dash(50);
        }
        else
        {
            getPlayer().turn(90);
        }
    }
}
