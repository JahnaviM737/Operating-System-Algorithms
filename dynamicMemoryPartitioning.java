import java.util.Scanner;

class dynamicMemoryPartitioning{
	
	int numOfBlocks;
	int[] memLocation;
	int[] memBlockSize;
	int totalAvailable=0;

	int[] internalFragmentation;
	int totalInternalFragmentation;
	int[] status; //1=busy, 0=free

	int numOfJobs;
	String[] nameOfJob;
	int[] memReqByJob;
	int totalUsed=0;

	int[] assigned;//indices of this are indices of memory blocks; numbers stored in this are the job indices
				   //eg. assigned[2]=4 --> job 4 is assigned in block 2 
	int count;
	String[] unallocated;

	//------------------------------------------------------------------------------------------------------------

	void bestFit()
	{
		clear();
		//Assigning blocks to jobs
		for(int i=0;i<numOfJobs;i++)
		{
			int selectedBlock=0; //index of block where job is finally placed
			int selectedBlockMemory; //memory of block where job is finally placed
			int previous;

			int currentBlock;
			int currentBlockSize;

			int currentJob = i;
			int currentRequiredMemory = memReqByJob[i];
			previous = totalAvailable; //just so as to set an upper bound for each iteration
			boolean isAllocated=false;

			for(int j=0;j<numOfBlocks;j++)
			{
				currentBlock = j;
				currentBlockSize = memBlockSize[j];

				//first check if block size is enough for current job
				if(currentBlockSize >= currentRequiredMemory)
				{
					//check if this block is free
					//now check if this one is enough and also *less* than the previously assigned one
					if(currentBlockSize < previous && status[currentBlock]==0)
					{
						selectedBlockMemory = currentBlockSize;
						previous = selectedBlockMemory;
						selectedBlock = currentBlock;
						isAllocated = true;
					}
				}
			}
			if(isAllocated==true)
			{
				//set status of selected block to busy i.e. 1
				status[selectedBlock] = 1;
				internalFragmentation[selectedBlock] = memBlockSize[selectedBlock]-memReqByJob[currentJob];
				totalInternalFragmentation += internalFragmentation[selectedBlock];
				assigned[selectedBlock] =currentJob; //assigned to selected block is current job
				totalUsed = totalUsed + memReqByJob[currentJob];
			}
			else
			{
				//add to list of unallocated jobs
				unallocated[count] = nameOfJob[currentJob];
				count++;
			}
		}
		//IF A JOB IS NOT ALLOCATED TO A BLOCK, THE ENTIRE BLOCK SIZE IS ADDED TO TIF
		for(int i=0;i<numOfBlocks;i++)
		{
			if(status[i]==0)
			{
				totalInternalFragmentation += memBlockSize[i];
			}
		}
	}

    //------------------------------------------------------------------------------------------------------------

	void firstFit()
	{
		clear();
		//Assigning blocks to jobs
		for(int i=0;i<numOfJobs;i++)
		{
			int selectedBlock=0; //index of block where job is finally placed
			int selectedBlockMemory; //memory of block where job is finally placed

			int currentBlock;
			int currentBlockSize;

			int currentJob = i;
			int currentRequiredMemory = memReqByJob[i];
			boolean isAllocated=false;

			for(int j=0;j<numOfBlocks;j++)
			{
				currentBlock = j;
				currentBlockSize = memBlockSize[j];

				//first check if block size is enough for current job
				if(currentBlockSize >= currentRequiredMemory && status[currentBlock]==0)
				{
					
					{
						selectedBlockMemory = currentBlockSize;
						selectedBlock = currentBlock;
						isAllocated = true;
						break; //IMP!
					}
				}
			}
			if(isAllocated==true)
			{
				//set status of selected block to busy i.e. 1
				status[selectedBlock] = 1;
				internalFragmentation[selectedBlock] = memBlockSize[selectedBlock]-memReqByJob[currentJob];
				totalInternalFragmentation += internalFragmentation[selectedBlock];
				assigned[selectedBlock] = currentJob; //assigned to selected block is current job
				totalUsed = totalUsed + memReqByJob[currentJob];
			}
			else
			{
				//add to list of unallocated jobs
				unallocated[count] = nameOfJob[currentJob];
				count++;
			}
		}
		//IF A JOB IS NOT ALLOCATED TO A BLOCK, THE ENTIRE BLOCK SIZE IS ADDED TO TIF
		for(int i=0;i<numOfBlocks;i++)
		{
			if(status[i]==0)
			{
				totalInternalFragmentation += memBlockSize[i];
			}
		}
	}

	//------------------------------------------------------------------------------------------------------------

	void worstFit()
	{
		clear();
		//Assigning blocks to jobs
		for(int i=0;i<numOfJobs;i++)
		{
			int selectedBlock=0; //index of block where job is finally placed
			int selectedBlockMemory; //memory of block where job is finally placed
			int previous;

			int currentBlock;
			int currentBlockSize;

			int currentJob = i;
			int currentRequiredMemory = memReqByJob[i];
			previous = 0; //just so as to set an upper bound for each iteration
			boolean isAllocated=false;

			for(int j=0;j<numOfBlocks;j++)
			{
				currentBlock = j;
				currentBlockSize = memBlockSize[j];

				//first check if block size is enough for current job
				if(currentBlockSize >= currentRequiredMemory)
				{
					//check if this block is free
					//now check if this one is enough and also *more* than the previously assigned one
					if(currentBlockSize > previous && status[currentBlock]==0)
					{
						selectedBlockMemory = currentBlockSize;
						previous = selectedBlockMemory;
						selectedBlock = currentBlock;
						isAllocated=true;
					}
				}
			}
			if(isAllocated==true)
			{
				//set status of selected block to busy i.e. 1
				status[selectedBlock] = 1;
				internalFragmentation[selectedBlock] = memBlockSize[selectedBlock]-memReqByJob[currentJob];
				totalInternalFragmentation += internalFragmentation[selectedBlock];
				assigned[selectedBlock] =currentJob; //assigned to selected block is current job
				totalUsed = totalUsed + memReqByJob[currentJob];
			}
			else
			{
				//add to list of unallocated jobs
				unallocated[count] = nameOfJob[currentJob];
				count++;
			}

		}
		//IF A JOB IS NOT ALLOCATED TO A BLOCK, THE ENTIRE BLOCK SIZE IS ADDED TO TIF
		for(int i=0;i<numOfBlocks;i++)
		{
			if(status[i]==0)
			{
				totalInternalFragmentation += memBlockSize[i];
			}
		}
	}

	//------------------------------------------------------------------------------------------------------------

	void inputMemoryBlocksInfo()
	{
		Scanner sc = new Scanner(System.in);

		System.out.println("\nMEMORY BLOCKS INFORMATION:\n");
		System.out.print("Enter number of memory blocks available: ");
		numOfBlocks = sc.nextInt();

		memLocation = new int[numOfBlocks];
		memBlockSize = new int[numOfBlocks];
		status = new int[numOfBlocks];
		internalFragmentation = new int[numOfBlocks];
		assigned = new int[numOfBlocks];
		unallocated = new String[numOfBlocks];

		for(int i=0;i<numOfBlocks;i++)
		{
			System.out.print("Enter location of memory block " + (i+1) + " : ");
			memLocation[i] = sc.nextInt();
			System.out.print("Enter size of block " + (i+1) + " (in k) : ");
			memBlockSize[i] = sc.nextInt();
			//Note these steps
			totalAvailable = totalAvailable + memBlockSize[i];
			internalFragmentation[i] = memBlockSize[i]; //Initial and will remain unchanges in block free
		}
	}

	void inputJobInfo()
	{
		Scanner sc = new Scanner(System.in);

		System.out.println("\nINFORMATION REGARDING JOBS:\n");
		System.out.print("Enter number of jobs: ");
		numOfJobs = sc.nextInt();

		nameOfJob = new String[numOfJobs+1];//+1 so that one of the names can be none (useful in displaying output)
		memReqByJob = new int[numOfJobs+1];//+1 so that 0 can be stored for value corresponding to NONE

		for(int i=0;i<numOfJobs;i++)
		{
			System.out.print("Enter name of job " + (i+1) + " : ");
			nameOfJob[i] = sc.next();
			System.out.print("Enter memory requirement of job " + (i+1) + " (in k) : ");
			memReqByJob[i] = sc.nextInt();
		}
	}

	void divider()
	{
		System.out.print("--------------------------------------------------------------------------------------");
	}

	void divider2()
	{
		System.out.print("---------------------------------");
	}

	void displayOutput()
	{
		//-----------INPUT TABLE FOR JOBS ENTERED
		System.out.println(" Job Name | Job Size ");
		divider2();
		for(int i=0;i<numOfJobs;i++)
		{
			System.out.printf("\n%10s|%8d K",nameOfJob[i],memReqByJob[i]);
		}
		System.out.println();
		divider2();
		//------------------------------OUTPUT
		System.out.println("\nMemory Location | Memory block size | Job Name | Job Size | Status | Internal Fragmentation");
		divider();
		for(int i=0;i<numOfBlocks;i++)
		{
			System.out.printf("\n%16d|%17d K|%10s|%8d K|%8d|%10d K",memLocation[i],memBlockSize[i],nameOfJob[assigned[i]],memReqByJob[assigned[i]],status[i],internalFragmentation[i]);
		}
		System.out.println();
		divider();
		System.out.println("\nTotal memory block size available : " + totalAvailable);
		System.out.println("Total memory block used : " + totalUsed);
		System.out.println("Total internal Fragmentation : " + totalInternalFragmentation);
		if(count!=0)
		{
			System.out.print("\nUnallocated Jobs:- ");
			for(int i=0;i<count;i++)
			{
				System.out.print("  " + unallocated[i] + ", ");
			}
		}
		System.out.println();
		divider();
		System.out.println();
		divider();
	}

	void clear()
	{
		for(int i=0;i<numOfBlocks;i++)
		{
			internalFragmentation[i] = memBlockSize[i];
			status[i]=0;
			assigned[i]=numOfJobs;//sets name to NONE while printing output
		}
		nameOfJob[numOfJobs]="NONE";
		memReqByJob[numOfJobs]=0;
		totalUsed=0;
		totalInternalFragmentation=0;
		count=0;
	}

	//------------------------------------------------------------------------------------------------------------

	public static void main(String args[])
	{
		dynamicMemoryPartitioning obj = new dynamicMemoryPartitioning();

		obj.inputMemoryBlocksInfo();
		obj.inputJobInfo();

		int op;
		boolean temp=true;

		Scanner sc = new Scanner(System.in);

		while(temp==true)
		{
		System.out.println("\n-----MENU-----");
		System.out.print("1.BEST FIT\n2.WORST FIT\n3.FIRST FIT\n4.EXIT\n");
		System.out.print("\nYour choice:   ");
		op = sc.nextInt();

		switch(op)
		{
			case 1:
				obj.bestFit();
				System.out.println("ACCORDING TO BEST FIT ALGORITHM");
				obj.displayOutput();
				break;
			case 2:
				obj.worstFit();
				System.out.println("ACCORDING TO WORST FIT ALGORITHM");
				obj.displayOutput();
				break;
			case 3:
				obj.firstFit();
				System.out.println("ACCORDING TO FIRST FIT ALGORITHM");
				obj.displayOutput();
				break;
			case 4:
				temp=false;
				break;
			default:
				System.out.println("INVALID CHOICE!");
				break;				
		}
	}
}
}
