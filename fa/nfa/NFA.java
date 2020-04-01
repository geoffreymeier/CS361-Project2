package fa.nfa;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import fa.dfa.DFA;

/**
 * Implementation of NFA class to be used in p1p2
 * 
 * @author elenasherman
 * @author geoffreymeier
 * @author parkererway
 *
 */
public class NFA implements NFAInterface {
	private Set<NFAState> states;
	private NFAState start;
	private Set<Character> ordAbc;

	public NFA() {
		states = new LinkedHashSet<NFAState>();
		ordAbc = new LinkedHashSet<Character>();
	}

	@Override
	public void addStartState(String name) {
		NFAState s = checkIfExists(name);
		if (s == null) {
			s = new NFAState(name);
			addState(s);
		} else {
			System.out.println("WARNING: A state with name " + name + " already exists in the NFA");
		}
		start = s;
	}

	@Override
	public void addState(String name) {
		NFAState s = checkIfExists(name);
		if (s == null) {
			s = new NFAState(name);
			addState(s);
		} else {
			System.out.println("WARNING: A state with name " + name + " already exists in the NFA");
		}
	}

	@Override
	public void addFinalState(String name) {
		NFAState s = checkIfExists(name);
		if (s == null) {
			s = new NFAState(name, true);
			addState(s);
		} else {
			System.out.println("WARNING: A state with name " + name + " already exists in the NFA");
		}
	}

	private void addState(NFAState s) {
		states.add(s);
	}

	@Override
	public void addTransition(String fromState, char onSymb, String toState) {
		NFAState from = checkIfExists(fromState);
		NFAState to = checkIfExists(toState);
		if (from == null) {
			System.err.println("ERROR: No NFA state exists with name " + fromState);
			System.exit(2);
		} else if (to == null) {
			System.err.println("ERROR: No NFA state exists with name " + toState);
			System.exit(2);
		}
		from.addTransition(onSymb, to);

		if (!ordAbc.contains(onSymb)) {
			ordAbc.add(onSymb);
		}
	}

	/**
	 * Check if a state with such name already exists
	 * 
	 * @param name
	 * @return null if no state exist, or NFAState object otherwise.
	 */
	private NFAState checkIfExists(String name) {
		NFAState ret = null;
		for (NFAState s : states) {
			if (s.getName().equals(name)) {
				ret = s;
				break;
			}
		}
		return ret;
	}

	@Override
	public Set<NFAState> getStates() {
		return states;
	}

	@Override
	public Set<NFAState> getFinalStates() {
		Set<NFAState> ret = new LinkedHashSet<NFAState>();
		for (NFAState s : states) {
			if (s.isFinal()) {
				ret.add(s);
			}
		}
		return ret;
	}

	@Override
	public NFAState getStartState() {
		return start;
	}

	@Override
	public Set<NFAState> getToState(NFAState from, char onSymb) {
		return from.getTo(onSymb);
	}

	@Override
	public Set<Character> getABC() {
		return ordAbc;
	}

	@Override
	public DFA getDFA() {
		return null;
	}

	@Override
	public Set<NFAState> eClosure(NFAState s) {
		//return values
		LinkedHashSet<NFAState> ret = new LinkedHashSet<NFAState>();
		// DFS stack
		Stack<NFAState> stack = new Stack<NFAState>();

		ret.add(s);	//add state itself to return set
		stack.add(s);

		//perform Depth-First Search
		while (!stack.empty()) {
			Set<NFAState> states = stack.pop().getTo('e');
			for (NFAState state : states) {
				if (!ret.contains(state)) {
					ret.add(state);
					stack.add(state);
				}
			}
		}

		return ret;
	}
}
