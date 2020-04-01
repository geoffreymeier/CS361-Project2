package fa.nfa;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;
// import java.util.ArrayList;
// import java.lang.Math;

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

	/**
	 * Gets the set of "to" states based on a set of "from" states
	 * 
	 * @param from
	 * @param onSymb
	 * @return
	 */
	private Set<NFAState> getToState(Set<NFAState> from, char onSymb) {
		Set<NFAState> ret = new HashSet<>();
		for (NFAState s : from) {
			ret.addAll(getToState(s, onSymb));
		}

		return ret;
	}

	@Override
	public Set<Character> getABC() {
		return ordAbc;
	}

	@Override
	public DFA getDFA() {
		DFA dfa = new DFA();
		//create a stack to keep track of states we still need to visit
		Stack<Set<NFAState>> stack = new Stack<Set<NFAState>>();
		//create a set of sets of states to keep track of which states have already been added
		Set<Set<NFAState>> states = new HashSet<Set<NFAState>>();

		Set<NFAState> start = eClosure(this.start);
		dfa.addStartState(start.toString());
		if (isFinal(start)) dfa.addFinalState(start.toString()); //check if start state is final
		states.add(start);
		stack.push(start);

		// create set for alphabet, removing e if necessary
		Set<Character> abc = ordAbc;
		abc.remove('e');

		while (!stack.isEmpty()) {
			Set<NFAState> from = stack.pop();
			for (char sym : abc) {
				Set<NFAState> to = new HashSet<NFAState>();
				to = eClosure(getToState(from, sym));

				if (!states.contains(to)) {
					if (isFinal(to))
						dfa.addFinalState(to.toString());
					else
						dfa.addState(to.toString());

					states.add(to);
					stack.push(to);
				}

				dfa.addTransition(from.toString(), sym, to.toString());
			}
		}

		return dfa;
	}

	/**
	 * Determines if, for a set of NFA states, any of them are final.
	 * 
	 * @param states set of states
	 * @return true if at least one is final, false otherwise
	 */
	private boolean isFinal(Set<NFAState> states) {
		boolean ret = false;

		for (NFAState s : states) {
			ret = ret || s.isFinal();
		}

		return ret;
	}

	// private Set<Set<NFAState>> powerSet() {
	// 	Set<Set<NFAState>> ret = new HashSet<Set<NFAState>>();

	// 	for (int i = 0; i < Math.pow(2, states.size()); i++) {
	// 		Set<NFAState> s = new HashSet<NFAState>();
	// 		for (int j = 0; j < states.size(); j++) {
	// 			if (((i >> j) & 1) == 1) {
	// 				s.add(new ArrayList<>(states).get(j));
	// 			}
	// 		}

	// 		ret.add(s);
	// 	}

	// 	return ret;
	// }

	private Set<NFAState> eClosure(Set<NFAState> s) {
		Set<NFAState> ret = new HashSet<NFAState>();

		for (NFAState nfaState : s) {
			ret.addAll(eClosure(nfaState));
		}

		return ret;
	}

	@Override
	public Set<NFAState> eClosure(NFAState s) {
		// return values
		LinkedHashSet<NFAState> ret = new LinkedHashSet<NFAState>();
		// DFS stack
		Stack<NFAState> stack = new Stack<NFAState>();

		ret.add(s); // add state itself to return set
		stack.add(s);

		// perform Depth-First Search
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
