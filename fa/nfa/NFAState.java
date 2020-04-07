package fa.nfa;

import java.util.HashMap;

import fa.State;
import java.util.Set;
import java.util.HashSet;

/**
 * Jan 19, 2017
 * Implementation of a DFA state, which
 * mainly contains the information of its
 * neighboring states.
 * @author elenasherman
 * @author geoffreymeier
 * @author parkererway
 */
public class NFAState extends State{
	

	private HashMap<Character,Set<NFAState>> delta;//delta
	private boolean isFinal;//remembers its type
	
	/**
	 * Default constructor
	 * @param name the state name
	 */
	public NFAState(String name){
		initDefault(name);
		isFinal = false;
	}
	
	/**
	 * Overlaoded constructor that sets the state type
	 * @param name the state name
	 * @param isFinal the type of state: true - final, false - nonfinal.
	 */
	public NFAState(String name, boolean isFinal){
		initDefault(name);
		this.isFinal = isFinal;
	}
	
	private void initDefault(String name ){
		this.name = name;
		delta = new HashMap<Character, Set<NFAState>>();
	}
	
	/**
	 * Accessor for the state type
	 * @return true if final and false otherwise
	 */
	public boolean isFinal(){
		return isFinal;
	}
	

	/**
	 * Add the transition from <code> this </code> object
	 * @param onSymb the alphabet symbol
	 * @param toState to NFA state
	 */
	public void addTransition(char onSymb, NFAState toState){
		if (!delta.containsKey(onSymb)) {
			delta.put(onSymb, new HashSet<NFAState>());
		}

		delta.get(onSymb).add(toState);
	}
	
	/**
	 * Retrieves the state that <code>this</code> transitions to
	 * on the given symbol
	 * @param symb - the alphabet symbol
	 * @return the new state 
	 */
	public Set<NFAState> getTo(char symb){
		Set<NFAState> ret = delta.get(symb);
		if(ret == null){
			return new HashSet<NFAState>();
		}
		
		return delta.get(symb);
	}
	
	// private Set<NFAState> recursiveGetTo(char symb, Set<NFAState> alreadySeen) {
	// 	Set<NFAState> ret = delta.get(symb);
		
	// 	Iterator<NFAState> i = ret.iterator();
	// 	while (i.hasNext()) {
	// 		NFAState nfaState = i.next();

	// 		if (alreadySeen.contains(nfaState)) {
	// 			i.remove();
	// 		}

	// 		alreadySeen.addAll(ret);
	// 		ret.addAll(nfaState.recursiveGetTo(symb, alreadySeen));
	// 	}

	// 	return ret;
	// }
	
}
