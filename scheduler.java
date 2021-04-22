import java.util.Scanner;

/*
Logic fr waiting time of each process
Each time a process P in written in the ganttChart, the integer before it is added to waiting time and the one 
after it is subtracted from waiting time. Last occurence (burst time<time slice), no subraction.

WT in SJF
At the end, add the TAT(before subtracting arrival time frm it) as it gets subtracted in the last occurence
*/

/*
The two dimentional array object is defined as follows:
Number of columns = number of processes to be scheduled
Row 0: Contains names of processes
Row 1: Burst Time
Row 2: Arrival time
Row 3: Priority Number
Row 4: Waiting Time
Row 5: Turnaround Time
Row 6: Response time
*/

class processScheduling 
{

	int numOfProcesses, totalBS=0;
	Object[][] processes;
	int ts,blocks;               //quantum/time slice for RR
	Object[] ganttChart = new Object[50];

	//ALGORITHM CALLS-------------------------------------------------
	//First Come First Serve
	void fCFS()
	{
		//GENERATING gantt CHART
		int counter=0;
		ganttChart[0]=0;
		int j=1; //for alloting values in ganttchart array object
		for(int i=0;i<numOfProcesses;i++)
		{
			processes[6][i] = (Integer)ganttChart[j-1]; //Assigning response time
			ganttChart[j] = (String)processes[0][i];
			j++;
			counter = counter + (Integer)processes[1][i];
			ganttChart[j] = counter;
			j++;
		}

		blocks=j;

		waitingTime(); 
		turnAroundTime();
		System.out.println("SCHEDULING ACCORDING TO FIRST COME FIRST SERVE ALGORITHM");

		displayganttChart();
		displayOutputInfo();
		System.out.println();
		System.out.println("Average waiting time is: " + avgWT() + " ms");
		System.out.println("Average turnaround time is: " + avgTAT() + " ms");
		System.out.println("Average responce time is: " + avgRT() + " ms");

		divider();
	}

//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

	//Shortest Job First
	void sJF()
	{
		inputArrivalTime(); //row 2 [2][i]
		displayInputInfo();
		
		//Array of burst times so that we can manipulate without losing input info
		int[] temp = new int[numOfProcesses];
		for(int i=0;i<numOfProcesses;i++)
		{
			temp[i] = (Integer)processes[1][i];
			//Side by side initializing WT with 0 to avoid null pointer exception later
			processes[4][i]=0;
			//Initializing RT,TAT with -1 as a checking condition to be used later
			processes[6][i]=-1;
			processes[5][i]=-1;
		}

		//Generating ganttchart
		int j=0;//Index for ganttchart
		int arvTime = 0, exeFor=0,sj=0;
		int counter = (Integer)processes[2][0];
		ganttChart[j] = (Integer)processes[2][0];
		j++;
		//First round of iteration while going through arrival times
		for(int i=0;i<numOfProcesses-1;i++)
		{
			int diffAT = (Integer)processes[2][i+1] - (Integer)processes[2][i];
			while(diffAT!=0)
			{
			sj = shortest(temp,i+1); 
			ganttChart[j] = processes[0][sj];
			j++;
			processes[4][sj] = (Integer)processes[4][sj] + counter; //adding value appering before P to WT

			exeFor = diffAT<temp[sj] ? diffAT : temp[sj];
			if(diffAT>temp[sj])
			{
				//Before next process arrives, we have CPU free for diffAt-temp[sj] time
				diffAT = diffAT - temp[sj];
			}
			else
			{
				diffAT = 0;
			}
			//THE ABOVE IF-ELSE BLOCK IS VVIMP!
			temp[sj] = temp[sj] - exeFor;
			counter = counter + exeFor;
			processes[4][sj] = (Integer)processes[4][sj] - counter; //subtracting value appering after P from WT

			//TAT checking
			if(temp[sj]==0 && (Integer)processes[5][sj]==-1)
			{
				processes[5][sj] = counter;	
			}
			ganttChart[j] = counter;
			j++;
		}
		}

		//Now same as SJF non preemptive
		while(counter!=(totalBS+((Integer)processes[2][0])))
		{
			sj = shortest(temp,numOfProcesses);
			ganttChart[j] = processes[0][sj];
			j++;
			processes[4][sj] = (Integer)processes[4][sj] + counter; //adding value appering before P to WT
			counter = counter + temp[sj];
			processes[4][sj] = (Integer)processes[4][sj] - counter; //subtracting value appering after P from WT

			ganttChart[j] = counter;
			j++;
			temp[sj]=0;
			//TAT checking
			if((Integer)processes[5][sj]==-1)
			{
				processes[5][sj] = counter;	
			}
		}

		blocks = j;

		//Responce time calculation
		for(int i=0;i<numOfProcesses;i++)
		{
			for(int k=0;k<blocks;k++)
			{
				if((Integer)processes[6][i]==-1 && ganttChart[k]==processes[0][i])
				{
					processes[6][i] = ganttChart[k-1];
				}
			}
		}
		//Subtracting Arrival time from all calculations
		for(int i=0;i<numOfProcesses;i++)
		{
			//Add TAT to WT(logic above) before subtracting AT frm it
			processes[4][i] = (Integer)processes[4][i] - (Integer)processes[2][i] + (Integer)processes[5][i];
			processes[5][i] = (Integer)processes[5][i] - (Integer)processes[2][i];
			processes[6][i] = (Integer)processes[6][i] - (Integer)processes[2][i];
		}

		System.out.println("SCHEDULING ACCORDING TO SHORTEST JOB FIRST ALGORITHM");

		displayganttChart();
		displayOutputInfo();
		System.out.println();
		System.out.println("Average waiting time is: " + avgWT() + " ms");
		System.out.println("Average turnaround time is: " + avgTAT() + " ms");
		System.out.println("Average responce time is: " + avgRT() + " ms");

		divider();
	}

//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

	//Priority
	void priority()
	{
		inputArrivalTime();
		inputPriority();
		displayInputInfo();

		//Array of burst times and priority so that we can manipulate without losing input info
		//Row 0 : Burst times
		//Row 1 : Priority
		int[][] temp = new int[2][numOfProcesses];
		for(int i=0;i<numOfProcesses;i++)
		{
			temp[0][i] = (Integer)processes[1][i];
			temp[1][i] = (Integer)processes[3][i];
			//Side by side initializing WT with 0 to avoid null pointer exception later
			processes[4][i]=0;
			//Initializing RT,TAT with -1 as a checking condition to be used later
			processes[6][i]=-1;
			processes[5][i]=-1;
		}

		//Generating ganttchart
		int j=0;//Index for ganttchart
		int arvTime = 0, exeFor=0,mp=0;
		int counter = (Integer)processes[2][0];
		ganttChart[j] = (Integer)processes[2][0];
		j++;

		//First iteration round with arrival times
		for(int i=0;i<numOfProcesses-1;i++)
		{
			int diffAT = (Integer)processes[2][i+1] - (Integer)processes[2][i];
			while(diffAT!=0)
			{
			mp = highestP(temp,i+1); 
			ganttChart[j] = processes[0][mp];
			j++;
			processes[4][mp] = (Integer)processes[4][mp] + counter; //adding value appering before P to WT

			exeFor = diffAT<temp[0][mp] ? diffAT : temp[0][mp];
			if(diffAT>temp[0][mp])
			{
				//Before next process arrives, we have CPU free for diffAt-temp[sj] time
				diffAT = diffAT - temp[0][mp];
			}
			else
			{
				diffAT = 0;
			}
			//THE ABOVE IF-ELSE BLOCK IS VVIMP!
			temp[0][mp] = temp[0][mp] - exeFor;
			counter = counter + exeFor;
			processes[4][mp] = (Integer)processes[4][mp] - counter; //subtracting value appering after P from WT

			//TAT checking
			if(temp[0][mp]==0 && (Integer)processes[5][mp]==-1)
			{
				processes[5][mp] = counter;	
			}
			ganttChart[j] = counter;
			j++;
		}
		}

		//Now same as priority non preemptive
		while(counter!=(totalBS+((Integer)processes[2][0])))
		{
			mp = highestP(temp,numOfProcesses);
			ganttChart[j] = processes[0][mp];
			j++;
			processes[4][mp] = (Integer)processes[4][mp] + counter; //adding value appering before P to WT
			counter = counter + temp[0][mp];
			processes[4][mp] = (Integer)processes[4][mp] - counter; //subtracting value appering after P from WT

			ganttChart[j] = counter;
			j++;
			temp[0][mp]=0;
			//TAT checking
			if((Integer)processes[5][mp]==-1)
			{
				processes[5][mp] = counter;	
			}
		}

		blocks = j;

		//Responce time calculation
		for(int i=0;i<numOfProcesses;i++)
		{
			for(int k=0;k<blocks;k++)
			{
				if((Integer)processes[6][i]==-1 && ganttChart[k]==processes[0][i])
				{
					processes[6][i] = ganttChart[k-1];
				}
			}
		}
		//Subtracting Arrival time from all calculations
		for(int i=0;i<numOfProcesses;i++)
		{
			//Add TAT to WT(logic above) before subtracting AT frm it
			processes[4][i] = (Integer)processes[4][i] - (Integer)processes[2][i] + (Integer)processes[5][i];
			processes[5][i] = (Integer)processes[5][i] - (Integer)processes[2][i];
			processes[6][i] = (Integer)processes[6][i] - (Integer)processes[2][i];
		}

		System.out.println("SCHEDULING ACCORDING TO PRIORITY ALGORITHM");

		displayganttChart();
		displayOutputInfo();
		System.out.println();
		System.out.println("Average waiting time is: " + avgWT() + " ms");
		System.out.println("Average turnaround time is: " + avgTAT() + " ms");
		System.out.println("Average responce time is: " + avgRT() + " ms");

		divider();
	}

//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

	//Round Robin
	void roundRobin()
	{
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter time slice (quantum)  :");
		ts = sc.nextInt();

		//Array of burst times so that we can manipulate without losing input info
		int[] temp = new int[numOfProcesses];
		for(int i=0;i<numOfProcesses;i++)
		{
			temp[i] = (Integer)processes[1][i];
			//Side by side initializing WT with 0 to avoid null pointer exception later
			processes[4][i]=0;
		}

		//GENERATING gantt CHART
		int counter=0;
		ganttChart[0]=0;
		int j=1; //for alloting values in ganttchart array object
		while(counter!=totalBS)
		{
			for(int i=0;i<numOfProcesses;i++)
			{
				if(temp[i]!=0)
				{
					ganttChart[j] = (String)processes[0][i];
					j++;
					if(temp[i]>ts)
					{
						processes[4][i] = (Integer)processes[4][i] + counter; //adding value appering before P to WT
						counter = counter + ts;
						ganttChart[j] = counter;
						j++;
						temp[i] = temp[i] - ts;
						processes[4][i] = (Integer)processes[4][i] - counter; //subtracting value appering after P from WT
					}
					else
					{
						processes[4][i] = (Integer)processes[4][i] + counter; //adding value appering before P to WT
						counter = counter + temp[i];
						ganttChart[j] = counter;
						j++;
						temp[i]=0;
						//Once the burst time is 0, the value in counter becomes TAT of ith process
						processes[5][i]=counter;
					}
				}
			}
		}

		blocks = j;

		//PRINTING gantt CHART
		System.out.println("SCHEDULING ACCORDING TO ROUND ROBIN ALGORITHM");
		displayganttChart();
		responseTime();
		displayOutputInfo();

		//RESULTS
		System.out.println("\n\n");
		System.out.println("Average waiting time is: " + avgWT() + " ms");
		System.out.println("Average turnaround time is: " + avgTAT() + " ms");
		System.out.println("Average responce time is: " + avgRT() + " ms");
	}

	//--------------------------------------INPUT AND OUTPUT METHODS-----------------------------------------

	void inputProcesses()
	{
		processes = new Object[7][numOfProcesses];
		Scanner sc = new Scanner(System.in);
		for(int i=0;i<numOfProcesses;i++)
		{
			System.out.print("Name of process " + (i+1) + " : ");
			processes[0][i] = sc.next();
			System.out.print("Burst time of process " + (i+1) + " (in miliseconds): ");
			processes[1][i] = sc.nextInt();
			//Initialize arrival time with 0
			processes[2][i] = 0;

			totalBS = totalBS + (Integer)processes[1][i];
			System.out.println();
		}
	}

	void inputArrivalTime()
	{
		Scanner sc = new Scanner(System.in);
		for(int i=0;i<numOfProcesses;i++)
		{
			System.out.print("Arrival time of process " + (i+1) + " (in miliseconds): ");
			processes[2][i] = sc.nextInt();
			System.out.println();
		}
	}

	void inputPriority()
	{
		Scanner sc = new Scanner(System.in);
		for(int i=0;i<numOfProcesses;i++)
		{
			System.out.print("Priority number of process " + (i+1) + " : ");
			processes[3][i] = sc.nextInt();
			System.out.println();
		}
	}

	void displayInputInfo()
	{
		System.out.printf("----------------------------------------------------------------------------------------------\n");		
		System.out.printf("\t\tProcess\t\t|\t\tBurst Time\t|\t\tArrival Time\n");
		System.out.printf("----------------------------------------------------------------------------------------------\n");
		for(int i=0;i<numOfProcesses;i++)
		{
			System.out.printf("\t\t%7s\t\t|\t\t%2d ms \t\t|\t\t%2d ms\n",processes[0][i],processes[1][i],processes[2][i]);
		}
		System.out.printf("----------------------------------------------------------------------------------------------\n");
		System.out.println();
	}

	void displayOutputInfo()
	{
		System.out.printf("-----------------------------------------------------------------------------------------------------------------------------------------------\n");
		System.out.printf("\t\tProcess\t\t|\t\tWaiting Time\t\t|\tTurn around time\t|\t\tResponce Time\t\t\n");
		System.out.printf("-----------------------------------------------------------------------------------------------------------------------------------------------\n");
		for(int i=0;i<numOfProcesses;i++)
		{
			System.out.printf("\t\t%7s\t\t|\t\t%4d ms\t\t\t|\t\t%4d ms\t\t|\t\t%4d ms\t\t\n",processes[0][i],processes[4][i],processes[5][i],processes[6][i]);
		}
		System.out.printf("-----------------------------------------------------------------------------------------------------------------------------------------------\n");
		System.out.println();
	}

	void displayganttChart()
	{
		for(int i=0;i<blocks;i++)
		{System.out.print("____");}
	    System.out.println();
	    for(int i=0;i<blocks;i++)
	    {
	    	if(i%2==0)
	    	{continue;}
	        else
	        {System.out.print("  " + ganttChart[i] + "  |");}
	    }

	    System.out.println();
	    for(int i=0;i<blocks;i++)
		{System.out.print("----");}
	    System.out.println();
	    for(int i=0;i<blocks;i++)
	    {
	    	if(i%2!=0)
	    	{continue;}
	        else
	        {System.out.printf( "%-7d",ganttChart[i]);}
	    }
	    System.out.println("\n");
	}

	void divider()
	{
		System.out.println("*******************************************************************************************************************************************************************");
	}

	//-----------------------------------CALCULATIONS-----------------------------------------------------------
	//Waiting thime for FCFS
    void waitingTime() //n is number of processes
    {
    	processes[4][0]=0;
    	for(int i=1;i<numOfProcesses;i++)
    	{
    		processes[4][i]=0;
    		for(int j=0;j<i;j++)
    		{
    			//Waiting time of a process is sum of burst times of processes before it
    			processes[4][i] = (Integer)processes[4][i] + (Integer)processes[1][j];
    		}
    	}
    }

	double avgWT() //n is  umber of processes
	{
		double result;
		int total=0; //Addition of all waiting times
		for(int i=0;i<numOfProcesses;i++)
		{
			total = total + (Integer)processes[4][i];
		}
		result = (double)total/numOfProcesses;
		return result;
	} 

	//TAT for FCFS
	void turnAroundTime()
	{
		for(int i=0;i<numOfProcesses;i++)
    	{
    		processes[5][i]=0;
    		for(int j=0;j<=i;j++)
    		{
    			//Waiting time of a process is sum of burst times of processes before it
    			processes[5][i] = (Integer)processes[5][i] + (Integer)processes[1][j];
    		}
    	}
	}

	double avgTAT()
	{
		double result;
		int total=0; //Addition of all waiting times
		for(int i=0;i<numOfProcesses;i++)
		{
			total = total + (Integer)processes[5][i];
		}
		result = (double)total/numOfProcesses;
		return result;
	}

	//For RR only
	void responseTime()
	{
		for(int i=0;i<numOfProcesses;i++)
		{
			processes[6][i] = (Integer)ganttChart[2*i];
		}
	}

	double avgRT()
	{
		int total = 0;
		double result;
		for(int i=0;i<numOfProcesses;i++)
		{
			total = total + (Integer)processes[6][i];
		}
		result = (double)total/numOfProcesses;
		return result;
	}

	int shortest(int[] arr, int n)
	{
		int min = 0; //Just initilizing, changes later
		//Find 1st non zero burst time
		for(int i=0;i<n;i++)
		{
			if(arr[i]!=0)
			{
				min = i;
				break;
			}
		}
		for(int i=0; i<n;i++)
		{
			if(arr[i]<arr[min] && arr[i]!=0)
			{
				min = i;
			}
		}
		return min;
	}

	int highestP(int[][] arr, int n)
	{
		int maxp=0;//maxp returned with index of highest priority process
		//Find 1st non zero burst time
		for(int i=0;i<n;i++)
		{
			if(arr[0][i]!=0)
			{
				maxp = i;
				break;
			}
		}
		for(int i=0;i<n;i++)
		{
			if(arr[1][i]<arr[1][maxp] && arr[0][i]!=0)
			{
				maxp = i;
			}
		}
		return maxp;
	}
}
//----------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------

class scheduler{

	public static void main(String args[])
	{
		processScheduling obj = new processScheduling();

        Scanner sca = new Scanner(System.in);

		System.out.print("Enter the number of processes:  ");
		obj.numOfProcesses = sca.nextInt();

		obj.inputProcesses();

		boolean temp = true;

		while(temp==true)
	    {

	    System.out.println("\n\n------------ALGORITHMS------------");
		System.out.printf("1.First Come First Serve\n2.Shortest Job First\n3.Priority\n4.Round Robin\n5.Exit\n");

		System.out.print("\nYour choice:  ");
		int choice = sca.nextInt();

		switch(choice)
		{
			case 1:
				obj.displayInputInfo();
				obj.fCFS();
			    break;
			case 2:
				obj.sJF();
				break;
			case 3:
				obj.priority();
				break;
			case 4:
				obj.displayInputInfo();
				obj.roundRobin();
				break;
			case 5:
			    temp = false;
				break;
			default:
				System.out.println("Invalid Choice.");
				break;				    
		}

	    }
	}
}