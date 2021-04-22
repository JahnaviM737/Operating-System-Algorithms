import java.util.Scanner ;

class BankersAlgo{
	
	int numOfPro;
	int numOfRes;
	int[] avail;
	String[] resName;
	int[][] max;
	int[][] alloc;
	int[][] need;
	String[] proName;
	boolean[] status;
	String[] sequence;
	int[] wait;

//***************************************************************************************************

	void inputData()
	{
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter number of Resources:  ");
		numOfRes = sc.nextInt();
		System.out.print("Enter number of Processes:  ");
		numOfPro = sc.nextInt();
		System.out.println();

		avail = new int[numOfRes];
		resName = new String[numOfRes];
		max = new int[numOfPro][numOfRes];
		alloc = new int[numOfPro][numOfRes];
		need = new int[numOfPro][numOfRes];
		proName = new String[numOfPro];
		status = new boolean[numOfPro];
		sequence = new String[numOfPro];
		wait = new int[numOfPro]; //IMORTANT!!

		//Input of initial available resources
		for(int i=0;i<numOfRes;i++)
		{
			System.out.print("Enter name of resource " + (i+1) + " :  ");
			resName[i] = sc.next();
			System.out.print("Enter initial available instances of " + resName[i] + " :  ");
			avail[i] = sc.nextInt();
		}

		//Data regarding processes
		for(int i=0;i<numOfPro;i++)
		{
			status[i]=false;
			wait[i]=0;
			System.out.print("\nEnter name of process " + (i+1) + " :  ");
			proName[i] = sc.next();
			//Max matrix input
			for(int j=0;j<numOfRes;j++)
			{
				System.out.printf("Enter maximum requirement of %s for %s : ",resName[j],proName[i]);
				max[i][j] = sc.nextInt();
			}
			System.out.println();
			//Alloc matrix input
			for(int j=0;j<numOfRes;j++)
			{
				System.out.printf("Enter current allocation of %s for %s : ",resName[j],proName[i]);
				alloc[i][j] = sc.nextInt();
				//Calculation for need matrix
				need[i][j] = max[i][j] - alloc[i][j];
			}
			System.out.println();
		}
	}

	void divider()
	{
		System.out.printf("----------------------------------------------------------------------------------------------\n");		
	}
//****************************************************************************************************
	void displayData()
	{
		divider();
		System.out.printf("\tProcess\t|\tAllocated\t|\tMaximum\t\t|\tNeeded\n");
		divider();
		System.out.printf("\t       \t|");
		//Printing resource names
		for(int i=0;i<numOfRes;i++)
		{
			System.out.printf("   %2s  ",resName[i]);
		}
		System.out.printf("|");
		for(int i=0;i<numOfRes;i++)
		{
			System.out.printf("   %2s  ",resName[i]);
		}
		System.out.printf("|");
		for(int i=0;i<numOfRes;i++)
		{
			System.out.printf("   %2s  ",resName[i]);
		}
		System.out.println();
		divider();
		//Filling the table
		for(int i=0;i<numOfPro;i++)
		{
			System.out.printf("\t%7s\t|",proName[i]);
			for(int j=0;j<numOfRes;j++)
			{
				System.out.printf("   %2d  ",alloc[i][j]);
			}
			System.out.printf("|");
			for(int j=0;j<numOfRes;j++)
			{
				System.out.printf("   %2d  ",max[i][j]);
			}
			System.out.printf("|");
			for(int j=0;j<numOfRes;j++)
			{
				System.out.printf("   %2d  ",need[i][j]);
			}
			System.out.println();
		}
		System.out.println();
		divider();
		//Availability of resources
		System.out.println("  AVAILABLE RESOURCES  ");
		System.out.println("-----------------------");
		for(int i=0;i<numOfRes;i++)
		{
			System.out.printf("   %2s  ",resName[i]);
		}
		System.out.println("\n-----------------------");
		for(int j=0;j<numOfRes;j++)
		{
			System.out.printf("   %2d  ",avail[j]);
		}
	}
//**************************************************************************************************
	void safeSeq()
	{
		int count=0;
		int current=numOfPro, check;

		while(count < numOfPro)
		{
			//Get process with status false
			for(int i=0;i<numOfPro;i++)
			{
				if(status[i]==false && wait[i]==0)
				{
					current=i;
					//System.out.println(current);
					//If all processes have been checked once, we now change the status of wait to 0
					if(i==numOfPro-1)
					{
						for(int k=0;k<numOfPro;k++){wait[k]=0;}
					}
				break;
				}
			}
			//for current process, we need to check if need<=avail for all resources
			check=1; //initially true. If need>avail, the we set it to 0;
			for(int i=0;i<numOfRes;i++)
			{
				if(need[current][i]>avail[i])
				{
					check=0;
					break;
				}
			}
			//If for the process, check=1, we can go on as add it to the safe sequence
			if(check==1)
			{
				sequence[count] = proName[current]; //add process to safe sequencw
				status[current] = true; //Process finished hence update status
				count = count + 1; //Increment count because a process is done
				//Update available by adding alloc of process to it: avail = avail + alloc
				//avail=avail-need --> alloc=alloc+need --> avail=avail+alloc (+-need hence shortcut)
				for(int i=0;i<numOfRes;i++)
				{
					avail[i] = avail[i] + alloc[current][i];
				}
			}
			else
			{
				//For the first round of checks through processes, we set it to waiting status,
				//or else our program will go into an infinite loop
				wait[current]=1;
			}
		}
	}	
//**************************************************************************************************
	void displayOutput()
	{
		System.out.println("\n\n--->RESULT : - ");
		System.out.println("1) In all, the number of instances of each resource are: ");
		for(int i=0;i<numOfRes;i++)
		{
			System.out.printf("%s  :  %d \n",resName[i],avail[i]);
		}
		System.out.println("2) SAFE SEQUENCE: - ");
		for(int i=0;i<numOfPro;i++)
		{
			System.out.printf("  %s  ",sequence[i]);
		}
	}	
//**************************************************************************************************
	public static void main(String args[])
	{
		BankersAlgo obj = new BankersAlgo();
		obj.inputData();
		obj.displayData();
		obj.safeSeq();
		obj.displayOutput();
	}
}