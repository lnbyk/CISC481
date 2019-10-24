import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class MAC {
	
	//below I am going to write a really simple bfs solution for this problem

	//Problem Description
	
	/*	From exercise 3.9 in the book: The missionaries and cannibals problem is usually stated as follows.
	 *  Three missionaries and three cannibals are on one side of a river, along with a boat that can hold 
	 *  one or two people. Find a way to get everyone to the other side without ever leaving a group of
	 *   missionaries in one place outnumbered by the cannibals in that place. 
	 */
	
	// initial state : A : {M, M, M, C, C, C, B}    B: {}
	// goal state : A:{}   B: {M, M, M, C, C, C, B}
	
	public static void missionariesAndCannibals(List<int[]> initial) {
		// used to reord the states we have been before;
		Set<String> visited = new HashSet<>();
		// initial a queue in order to perform a BFS
		Queue<List<int[]>> queue = new LinkedList<>();
		queue.offer(initial); //put the initial state in the queue;
		// ready to perform bfs 
		
		int level = 0;
		String gap = "                                         ";
		while(!queue.isEmpty()) {
			int size = queue.size();
			System.out.print(gap.substring(level * 5));
			level++;
			for(int i = 0; i < size; i++) {
				List<int[]> cur = queue.poll();
				visited.add(Arrays.toString(cur.get(0)) + Arrays.toString(cur.get(1)));
				System.out.print(Arrays.toString(cur.get(0)) + Arrays.toString(cur.get(1)) + "   ");
				List<List<int[]>> states = nextState(cur);
				for(List<int[]> l : states) {
					if(isGoal(l)) {
						System.out.println(Arrays.toString(l.get(0)) + Arrays.toString(l.get(1)));
						return;
					}
					else if(!visited.contains(Arrays.toString(l.get(0)) + Arrays.toString(l.get(1)))) {
						visited.add(Arrays.toString(l.get(0)) + Arrays.toString(l.get(1)));
						queue.offer(l);
					}
				}
			}
			System.out.println();
			
		}
		System.out.println("Nothing is here");
		
	}

	private static boolean isGoal(List<int[]> l) {
		return (l.get(0)[0] == 0 && l.get(0)[1] == 0 && l.get(0)[2] == 0);
	}
	
	// generator next states list
	private static List<List<int[]>> nextState(List<int[]> curState){
		List<List<int[]>> res = new ArrayList<>();
		List<int[]> actions = possibleActions(curState);
		for(int[] action : actions) {
			int[] A = curState.get(0), B = curState.get(1);
			int m1 = A[0] + action[0], c1 = A[1] + action[1], b1 = A[2] + action[2];
			int m2 = B[0] - action[0], c2 = B[1] - action[1], b2 = B[2] - action[2];
			if((m1 >= c2 || m1 == 0)&& (m2 >= c2 || m2 == 0)) {
				List<int[]> valid = new ArrayList<>();
				valid.add(new int[] {m1, c1, b1});
				valid.add(new int[] {m2, c2, b2});
				res.add(valid);
			}
		}
		
		return res;
	}
	
	// possible actions at current state
	private static List<int[]> possibleActions(List<int[]> curState) {
		List<int[]> res = new ArrayList<>();
		int[] A = curState.get(0), B = curState.get(1);
		boolean hasBoat = A[2] == 1;
		for(int i = 0; i <= 2; i++) {
			if(A[0] >= i && A[1] >= (2 - i) && hasBoat)
				res.add(new int[] {- i, i - 2, -1});
			else if(B[0] >= i && B[1] >= (2 - i) && !hasBoat)
				res.add(new int[] {i, 2 - i, 1});
		}
		for(int i = 0; i <= 1; i++) {
			if(A[0] >= i && A[1] >= (1 - i) && hasBoat)
				res.add(new int[] {- i, i - 1, -1});
			else if(B[0] >= i && B[1] >= (1 - i) && !hasBoat)
				res.add(new int[] {i, 1 - i, 1});
		}
		
		
		return res;
	}
	
	public static void main(String[] args) {
		List<int[]> cur = new ArrayList<>();
		cur.add(new int[] {3,3,1});
		cur.add(new int[] {0, 0, 0});
		for(int[] i : possibleActions(cur)) {
			System.out.println(Arrays.toString(i));
		}
		
//		for(List<int[]> l : nextState(cur)) {
//			System.out.println(Arrays.toString(l.get(0)) + " " + Arrays.toString(l.get(1)));
//		}
		
		missionariesAndCannibals(cur);
	}
 }
