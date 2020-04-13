package fa.nfa;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;

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
	/**
	 * Set of states
	 */
	private Set<NFAState> states;

	/**
	 * Initial state
	 */
	private NFAState start;

	/**
	 * Valid transition characters (excluding e)
	 */
	private Set<Character> ordAbc;

	/**
	 * Default constructor
	 */
	public NFA() {
		states = new LinkedHashSet<NFAState>();
		ordAbc = new LinkedHashSet<Character>();
	}

	/**
	 * Add a new state as the start state
	 * @param name the state name
	 */
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

	/**
	 * Add a new non-start, non-final state
	 * @param name the state name
	 */
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

	/**
	 * Add a new final state
	 * @param name the state name
	 */
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

	/**
	 * Private helper method for state added.
	 * @param name the state name
	 */
	private void addState(NFAState s) {
		states.add(s);
	}

	/**
	 * Add transition on a character between two existing states
	 * @param fromState the origin state
	 * @param onSymb alphabet character (will be added to ordAbc)
	 * @param toState the destination state
	 */
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

		if (!ordAbc.contains(onSymb) && onSymb != 'e') {
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

	/**
	 * Get all states currently in the NFA
	 * @return Set of existing states
	 */
	@Override
	public Set<NFAState> getStates() {
		return states;
	}

	/**
	 * Get all final states in the NFA
	 * @return Set of all final states in the NFA
	 */
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

	/**
	 * Get NFA start state
	 * @return NFAState marked as the initial state
	 */
	@Override
	public NFAState getStartState() {
		return start;
	}

	/**
	 * Get states transitioned to from `from` on `onSymb`
	 * @param from current NFA state
	 * @param onSymb the input symbol
	 * @return Set of destination states for this symbol
	 */
	@Override
	public Set<NFAState> getToState(NFAState from, char onSymb) {
		return from.getTo(onSymb);
	}

	private Set<NFAState> getToState(Set<NFAState> from, char onSymb) {
		Set<NFAState> ret = new HashSet<NFAState>();

		for (NFAState nfaState : from) {
			ret.addAll(nfaState.getTo(onSymb));
		}

		return ret;
	}

	/**
	 * Get alphabet
	 * @return Set of all valid transition characters, except e.
	 */
	@Override
	public Set<Character> getABC() {
		return ordAbc;
	}

	/**
	 * Convert the NFA into a DFA object
	 * @return object representing the DFA after conversion
	 */
	@Override
	public DFA getDFA() {
		DFA dfa = new DFA();
		// dfa.addStartState("foo");
		// dfa.addFinalState("foo");
		// dfa.addState(name);
		Set<Set<NFAState>> addedStates = new HashSet<Set<NFAState>>();

		dfa.addStartState(eClosure(start).toString());

		Queue<Set<NFAState>> queue = new LinkedList<Set<NFAState>>();
		queue.add(eClosure(start));

		String startStateName = eClosure(start).toString();
		dfa.addStartState(startStateName);
		if (isFinal(eClosure(start)))
			dfa.addFinalState(startStateName);

		addedStates.add(eClosure(start));

		while (queue.peek() != null) {
			Set<NFAState> s = queue.poll();

			for (Character symb : ordAbc) {
				Set<NFAState> toState = getToState(s, symb);
				toState = eClosure(toState);

				if (!addedStates.contains(toState)) {
					if (isFinal(toState))
						dfa.addFinalState(toState.toString());
					else
						dfa.addState(toState.toString());

					addedStates.add(toState);
					queue.add(toState);
				}

				dfa.addTransition(s.toString(), symb, toState.toString());
			}
		}

		return dfa;
	}

	/**
	 * A helper method to determine if any of the given states are final.
	 * 
	 * @param s   The states to check
	 * @return True if any of the given states are final.
	 */
	private boolean isFinal(Set<NFAState> s) {
		for (NFAState nfaState : s) {
			if (nfaState.isFinal())
				return true;
		}

		return false;
	}

	/**
	 * A helper method to perform recursive DFS search on multiple states.
	 * 
	 * @param s   The states to perform DFS search from.
	 * @return The results of the DFS search on all given states.
	 */
	private Set<NFAState> eClosure(Set<NFAState> s) {
		Set<NFAState> ret = new HashSet<NFAState>();

		for (NFAState nfaState : s) {
			ret.addAll(eClosure(nfaState));
		}

		return ret;
	}

	/**
	 * Get the epsilon closure from a given state s.
	 * @return Set of all states accessible by following only 'e'
	 * 		   transitions from `s`.
	 */
	@Override
	public Set<NFAState> eClosure(NFAState s) {

		Set<NFAState> ret = new LinkedHashSet<NFAState>();
		ret = eClosureDFS(s, ret);

		return ret;
	}

	/**
	 * A helper method to perform recursive DFS search from a given state.
	 * 
	 * @param s   The state to perform DFS search from.
	 * @param ret The set of values that the results should be added to.
	 * @return The results of the DFS search added to the values in ret.
	 */
	private Set<NFAState> eClosureDFS(NFAState s, Set<NFAState> ret) {
		if (!ret.contains(s)) {
			ret.add(s);

			// perform Depth-First Search
			Set<NFAState> states = getToState(s, 'e');
			for (NFAState state : states) {
				eClosureDFS(state, ret);
			}
		}

		return ret;
	}
}
