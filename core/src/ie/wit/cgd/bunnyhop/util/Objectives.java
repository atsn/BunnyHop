package ie.wit.cgd.bunnyhop.util;

public class Objectives
{

	public enum ObjectiveType
	{
		COLECT_COINS, TIMELIMIT,COLLECT_CARROTS,GET_SCORE, NONE;
	}

	private ObjectiveType objectiveType;
	public int parameterToMeet;
	public float parameterCounter;

	public Objectives()
	{
		init();
	}

	public void init()
	{
		objectiveType = ObjectiveType.NONE;
		parameterToMeet = 0;
	}

	public void setObjective(int Parameter, ObjectiveType Type)
	{
		objectiveType = Type;
		parameterToMeet = Parameter;
		
		if (Type == ObjectiveType.TIMELIMIT)
		{
			parameterCounter = parameterToMeet+1;
		}
	}

	public boolean ObjectiveMet()
	{
		switch (objectiveType)
		{
		case TIMELIMIT :
			return parameterCounter >= 0;
		case COLECT_COINS :
		case COLLECT_CARROTS: 
		case GET_SCORE:
			return parameterToMeet <= parameterCounter;
		case NONE :  return true;
		}
		return false;
	}

	public void update(float deltatime, boolean goalCollected)
	{
		if (objectiveType == ObjectiveType.TIMELIMIT && !goalCollected)
		{
			parameterCounter -= deltatime/2;
		}
	}
	
	public boolean isobectivetype(ObjectiveType type)
	{
		return objectiveType == type;
	}
	
	public boolean levellost(Boolean goalCollected)
	{
		if (objectiveType == ObjectiveType.TIMELIMIT && !goalCollected){
			return parameterCounter < 0 ;
		}
		else if (objectiveType == ObjectiveType.COLECT_COINS && goalCollected)
		{
			return !ObjectiveMet();
		}
		else if (objectiveType == ObjectiveType.COLLECT_CARROTS && goalCollected)
		{
			return !ObjectiveMet();
		}
		else if (objectiveType == ObjectiveType.GET_SCORE && goalCollected)
		{
			return !ObjectiveMet();
		}
		
		return false;
	}
}
