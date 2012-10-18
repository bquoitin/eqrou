/*
routing-equiv: testing the equivalence of routing policies
Copyright (C) 2011 routing-equiv team

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package be.ac.umons.info.routing.automata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import traul.ranked.nta.INTA;
import traul.ranked.nta.NTA;
import traul.ranked.nta.rules.BasicRule;
import traul.ranked.nta.rules.IRule;
import traul.ranked.nta.states.IState;
import traul.ranked.terms.ITerm;
import traul.ranked.terms.Term;

/**
 * This class implements automata that we use to encode a filter. It is also 
 * used internally to encode actions and rules.
 */
public class FilterAutomaton extends NTA<LabelPair, FilterState> {

	private final Set<IRule<LabelPair, FilterState>> rulesDest;
	private final Set<IRule<LabelPair, FilterState>> rulesPath;
	private final Set<IRule<LabelPair, FilterState>> rulesCom;
	private final Set<IRule<LabelPair, FilterState>> rulesPref;
	private final Set<IRule<LabelPair, FilterState>> rulesMod;
	private final Set<IRule<LabelPair, FilterState>> rulesRoot;
	private final ActionAlphabet actionAlphabet;

	/**
	 * Constructor
	 */
	public FilterAutomaton(
			final ActionAlphabet alphabet, 
			final Set<FilterState> states,
			final Set<FilterState> finalStates,
			final FilterState sinkState, 
			final Set<IRule<LabelPair, FilterState>> rulesDest, 
			final Set<IRule<LabelPair, FilterState>> rulesPath, 
			final Set<IRule<LabelPair, FilterState>> rulesPref,
			final Set<IRule<LabelPair, FilterState>> rulesCom, 
			final Set<IRule<LabelPair, FilterState>> rulesMod,
			final Set<IRule<LabelPair, FilterState>> rulesRoot) {
		
		super(alphabet, states, finalStates, 
			unionOfBranchRules(rulesDest, rulesPath, rulesPref,
					rulesCom, rulesMod, rulesRoot), 
			sinkState);
		this.rulesDest = rulesDest;
		this.rulesPath = rulesPath;
		this.rulesPref = rulesPref;
		this.rulesCom = rulesCom;
		this.rulesMod = rulesMod;
		this.rulesRoot = rulesRoot;
		this.actionAlphabet = alphabet;
	}
	
	private static Set<IRule<LabelPair,FilterState>> unionOfBranchRules(
			final Set<IRule<LabelPair, FilterState>> rulesDest, 
			final Set<IRule<LabelPair, FilterState>> rulesPath, 
			final Set<IRule<LabelPair, FilterState>> rulesPref,
			final Set<IRule<LabelPair, FilterState>> rulesCom, 
			final Set<IRule<LabelPair, FilterState>> rulesMod,
			final Set<IRule<LabelPair, FilterState>> rulesRoot) {
		Set<IRule<LabelPair,FilterState>> allRules = 
			new HashSet<IRule<LabelPair,FilterState>>();
		allRules.addAll(rulesDest);
		allRules.addAll(rulesPath);
		allRules.addAll(rulesPref);
		allRules.addAll(rulesCom);
		allRules.addAll(rulesMod);
		allRules.addAll(rulesRoot);
		return allRules;
	}
	
	/**
	 * Returns the ActionAlphabet, while getAlphabet returns the same alphabet
	 * typed as Alphabet<LabelPair>.
	 */
	public ActionAlphabet getActionAlphabet() {
		return this.actionAlphabet;
	}
	
	/**
	 * Simple type conversion.
	 */
	public INTA<LabelPair, FilterState> convertToINTA() {
		return this;
	}
	
	/**
	 * Computes the composition of this action (resp. rule) with an action 
	 * (resp. rule)'s automata. The automaton given as parameter is the first
	 * transformation to be applied, while 'this' object will be the second.
	 * @param automaton the action's automata
	 * @return an automaton recognizing the composition's language
	 */
	public FilterAutomaton compose(final FilterAutomaton automaton)	{
		
		final FilterAutomaton automaton1 = automaton.addDiamondRules();
		final FilterAutomaton automaton2 = this.addDiamondRules();
		
		final ActionAlphabet alphabet = 
			(ActionAlphabet)automaton.getAlphabet();
		
		// compose rules of DEST
		final Set<IRule<LabelPair, FilterState>> rulesDestResult = 
			computeCompositionRules(alphabet, 
					automaton1.getRulesDest(), automaton2.getRulesDest());
				
		// compose rules of AS-path
		final Set<IRule<LabelPair, FilterState>> rulesPathResult = 
			computeCompositionRules(alphabet, 
					automaton1.getRulesPath(), automaton2.getRulesPath());
		
		// compose rules of PREF
		Set<IRule<LabelPair, FilterState>> rulesPrefResult = 
			computeCompositionRules(alphabet, 
					automaton1.getRulesPref(), automaton2.getRulesPref());
		
		// compose rules of COM
		Set<IRule<LabelPair, FilterState>> rulesComResult = 
			computeCompositionRules(alphabet, 
					automaton1.getRulesCom(), automaton2.getRulesCom());
		
		// compose rules of MOD
		Set<IRule<LabelPair, FilterState>> rulesModResult = 
			computeCompositionRules(alphabet, 
					automaton1.getRulesMod(), automaton2.getRulesMod());
				
		// compose rules of Root
		Set<IRule<LabelPair, FilterState>> rulesRootResult = 
			computeCompositionRules(alphabet, 
					automaton1.getRulesRoot(), automaton2.getRulesRoot());
		
		// states
		Set<FilterState> statesResult = new HashSet<FilterState>();
		statesResult.addAll(statesInRules(rulesDestResult));
		statesResult.addAll(statesInRules(rulesPathResult));
		statesResult.addAll(statesInRules(rulesPrefResult));
		statesResult.addAll(statesInRules(rulesComResult));
		statesResult.addAll(statesInRules(rulesModResult));
		statesResult.addAll(statesInRules(rulesRootResult));
		
		final FilterState sinkResult = 
			new FilterState(automaton1.sinkState(), automaton2.sinkState());
		statesResult.add(sinkResult);
		// finalStates
		Set<FilterState> finalStatesResult = new HashSet<FilterState>();
		for (FilterState finalState1 : automaton1.getFinalStates()) {
			for (FilterState finalState2 : automaton2.getFinalStates()) {
				final FilterState candidate =
					findInStates(finalState1, finalState2, statesResult);
				if (candidate != null) {
					finalStatesResult.add(candidate);
				}
			}
		}		
		return new FilterAutomaton(alphabet, statesResult, finalStatesResult, 
				sinkResult, rulesDestResult, rulesPathResult,
				rulesPrefResult, rulesComResult, rulesModResult, 
				rulesRootResult);
	}

	private Set<FilterState> statesInRules(
			final Set<IRule<LabelPair,FilterState>> rules) {
		Set<FilterState> states = new HashSet<FilterState>();
		for (IRule<LabelPair, FilterState> rule : rules) {
			states.add(rule.rightState());
			states.addAll(rule.leftStates());
		}
		return states;
	}
	

	/**
	 * Ugly fix to replace the test 
	 * statesResult.contains(new FilterState(state1, state2))
	 * The problem is that the embedding of FilterStates might differ, 
	 * even between two pairs (state1,state2)...
	 */
	private FilterState findInStates(final FilterState state1, 
		    final FilterState state2, final Set<FilterState> statesResult) {
		FilterState result = null;
		FilterState candidate = new FilterState(state1, state2);
		final Iterator<FilterState> it = statesResult.iterator();
		while (result == null && it.hasNext()) {
			final FilterState state = it.next();
			if (candidate.toString().equals(state.toString())) { // brrrr....
				result = state;
			}
		}
		return result;
	}

	/**
	 * Computes the composition of two sets of rules
	 * @param rules1 first set of rules
	 * @param rules2 second set of rules
	 * @return set of rules resulting from the composition
	 */
	private Set<IRule<LabelPair, FilterState>> computeCompositionRules(
			final ActionAlphabet alphabet, 
			final Set<IRule<LabelPair, FilterState>> rules1,
			final Set<IRule<LabelPair, FilterState>> rules2) {
		
		Set<IRule<LabelPair, FilterState>> rulesResult = 
			new HashSet<IRule<LabelPair, FilterState>>();
		
		for (IRule<LabelPair, FilterState> rule1 : rules1) {
			for (IRule<LabelPair, FilterState> rule2 : rules2) {
				if (rule1.label().right().equals(rule2.label().left()) && (
						rule1.leftStates().size()== rule2.leftStates().size()||
						rule1.leftStates().size()==0 && 
							rule2.leftStates().size()==1||
						rule1.leftStates().size()==1 && 
							rule2.leftStates().size()==0)) {
					List<FilterState> leftStatesResult = 
						new ArrayList<FilterState>();
					if (rule1.leftStates().size()==0 && 
							rule2.leftStates().size()==1) {
						leftStatesResult.add(new FilterState(
								new FilterState("qDiamond"),
								rule2.leftStates().get(0)));
					} else if (rule1.leftStates().size()==1 && 
							rule2.leftStates().size()==0) {
						leftStatesResult.add(new FilterState(
								rule1.leftStates().get(0),
								new FilterState("qDiamond")));
					} else {
						for (int i=0;i<rule1.leftStates().size();i++) {
							leftStatesResult.add(
									new FilterState(rule1.leftStates().get(i),
									rule2.leftStates().get(i)));
						}
					}
					final LabelPair label = 
						new LabelPair(
								rule1.label().left(), rule2.label().right());
					if (alphabet.arity(label)==leftStatesResult.size()) {
						rulesResult.add(new BasicRule<LabelPair, FilterState>(
								leftStatesResult, label,
								new FilterState(rule1.rightState(),
										rule2.rightState())));
					} else if (alphabet.arity(label)==0)	{
						rulesResult.add(new BasicRule<LabelPair, FilterState>(
								new ArrayList<FilterState>(), label,
								new FilterState(rule1.rightState(),
										rule2.rightState())));
					}
				}
			}
		}
		return this.cleanDiamondRules(alphabet, rulesResult);
	}

	/**
	 * Efficient implementation of equivalence, for automata encoding filters.
	 * We assume here that accepted trees are not simple leaves (this is 
	 * always the case for filter automata).
	 * @param otherFilter the other filter automaton to be compared with
	 * @return true iff both filters are equivalent
	 */
	public boolean equivalent(final FilterAutomaton otherFilter) {
		return synthesizeSeparationTerm(otherFilter, false)==null;
	}

	/**
	 * If otherFilter is equivalent to this filter, returns null.
	 * Otherwise, returns a pair (t,t') of routes where t' is the image of t 
	 * by this filter, but has a different image by otherFilter.
	 * @param otherFilter the other filter automaton to be compared with
	 * @return a pair (t,t') proving non-equivalence, or null oherwise.
	 */
	public ITerm<LabelPair> separationRoute(final FilterAutomaton otherFilter){
		return synthesizeSeparationTerm(otherFilter, true);
	}
	
	/**
	 * Efficient implementation of equivalence, for automata encoding filters.
	 * We also use this test to build terms (t,t') where t' is the image of t
	 * by this automaton, but has a different image by otherFilter.
	 * If both are equivalent, this returns null.
	 * @param otherFilter the other filter automaton to be compared with
	 * @param computeSepTerm if true, computes a tree that proves the
	 *   non-equivalence of both automata, if they are not equivalent.
	 *   If false, just builds a dummy non-null term whenever filters are not 
	 *   equivalent.
	 * @return null if this filter and otherFilter are equivalent.
	 *   Otherwise, if computeSepTerm==true: 
	 *   returns a tree (t,t') where t' is the image of t by this 
	 *   filter, but has a different image by otherFilter.
	 *   If computeSepTerm==false, returns a dummy non-null term.
	 */
	private ITerm<LabelPair> synthesizeSeparationTerm(
			final FilterAutomaton otherFilter,
			final boolean computeSepTerm) {
		// the separation term is built by associating a term to each 
		// equivalence term
		ITerm<LabelPair> sepTerm = null;
		Map<EquivalenceState,ITerm<LabelPair>> sepMap =
			new HashMap<EquivalenceState,ITerm<LabelPair>>();
		// first add diamond rules
		final FilterAutomaton automaton1 = this.addDiamondRules();
		final FilterAutomaton automaton2 = otherFilter.addDiamondRules();
		// then look for a counterexample to equivalence
		Set<EquivalenceState> agenda = new HashSet<EquivalenceState>();
		boolean equivalent = true;
		Set<EquivalenceState> reachedStates = 
			reachedStatesAtLeaves(
				automaton1, automaton2, computeSepTerm, sepMap);
		agenda.addAll(reachedStates);
		while (equivalent && !agenda.isEmpty()) {
			final EquivalenceState state = agenda.iterator().next();
			agenda.remove(state);
			for (IRule<LabelPair,FilterState> rule1 : 
				automaton1.getRulesUsingLeftState(state.filterState1())) {
				for (IRule<LabelPair,FilterState> rule2 :
					automaton2.getRulesUsingLeftState(state.filterState2())) {
					final EquivalenceState equivState = 
						equivalenceStateForRules(rule1, rule2, 
							automaton1, automaton2, reachedStates,
							computeSepTerm, sepMap);
					if (equivState != null) {
						reachedStates.add(equivState);
						agenda.add(equivState);
						if (equivState.differs()) {
							equivalent = !equivState
								.provesNonEquiv(automaton1, automaton2);
							if (computeSepTerm && !equivalent) {
								sepTerm = sepMap.get(equivState);
							}
						}
					}
				}
			}
		}
		if (!equivalent && !computeSepTerm) {
			// a dummy non-null term
			sepTerm = new Term<LabelPair>(
				this.getAlphabet(),
				new ArrayList<ITerm<LabelPair>>(), 
				ActionAlphabet.REJREJ);
		}
		return sepTerm;
	}

	/**
	 * Returns an EquivalenceState (q,p,v) when given two matching automata 
	 * rules. This is the case when rules are:<br/>
	 * 
	 * (q1,...,qn) -- (a,b) --> q <br/>
	 * (p1,...,pn) -- (a,c) --> p <br/>
	 * 
	 * and all (q1,p1),...,(qn,pn) are in reachedStates but not (q,p). The
	 * truth value of v is given by (b different from c) or ((qi,pi,false) is 
	 * in the reachedStates for some i).
	 * If this EquivalenceState (q,p,v) is such that v==false and (q,p,v) is 
	 * already in reachedStates, then returns null.
	 */
	private EquivalenceState equivalenceStateForRules(
			final IRule<LabelPair,FilterState> rule1,
			final IRule<LabelPair,FilterState> rule2,
			final FilterAutomaton automaton1,
			final FilterAutomaton automaton2,
			final Set<EquivalenceState> reachedStates,
			final boolean computeSepTerm,
			Map<EquivalenceState,ITerm<LabelPair>> sepMap) {

		EquivalenceState equivState = null;
		final EquivalenceState newReachedDiff =
			new EquivalenceState(rule1.rightState(),rule2.rightState(),true);								
		final EquivalenceState newReachedNoDiff =
			new EquivalenceState(rule1.rightState(),rule2.rightState(),false);
		final int arity = automaton1.getAlphabet().arity(rule1.label());
		final boolean rightDiff = 
			!rule1.label().right().equals(rule2.label().right());
		if (rule2.label().left().equals(rule1.label().left())
			&& automaton2.getAlphabet().arity(rule2.label())==arity
			&& (!reachedStates.contains(newReachedDiff))) {
			// check whether all states on the left-hand side have been reached
			// so far
			boolean match = true;
			boolean leftDiff = false;
			List<ITerm<LabelPair>> sepTermChildren = 
				new ArrayList<ITerm<LabelPair>>();
			int i = 0;
			while (i<arity && match) {
				final EquivalenceState qNoDiff =
					new EquivalenceState(rule1.leftStates().get(i),
							rule2.leftStates().get(i), false);
				final EquivalenceState qDiff =
					new EquivalenceState(rule1.leftStates().get(i),
							rule2.leftStates().get(i), true);
				match &= reachedStates.contains(qNoDiff) ||
					reachedStates.contains(qDiff);
				leftDiff |= reachedStates.contains(qDiff);
				if (computeSepTerm && match) {
					if (reachedStates.contains(qDiff)) {
						sepTermChildren.add(sepMap.get(qDiff));
					} else {
						sepTermChildren.add(sepMap.get(qNoDiff));						
					}
				}
				i++;
			}
			if (match) {
				if (leftDiff || rightDiff) {
					equivState = newReachedDiff;
				} else {
					if (!reachedStates.contains(newReachedNoDiff)) {
						equivState = newReachedNoDiff;
					}
				}
				if (computeSepTerm && equivState != null) {
					sepMap.put(equivState,
						new Term<LabelPair>(this.actionAlphabet, 
							sepTermChildren, rule1.label()));
				}
			}
		}
		return equivState;
	}
	
	/**
	 * Builds the set of EquivalenceState reached for symbols of arity 0.
	 */
	private Set<EquivalenceState> reachedStatesAtLeaves(
			final FilterAutomaton automaton1, 
			final FilterAutomaton automaton2,
			final boolean computeSeparationTerm,
			Map<EquivalenceState,ITerm<LabelPair>> separationMap) {
		Set<EquivalenceState> reached = new HashSet<EquivalenceState>();
		for (LabelPair leafLabel1 : 
			automaton1.getAlphabet().getSymbolsOfArity(0)) {
			for (IRule<LabelPair, FilterState> rule1 : 
				automaton1.getRulesWithLabel(leafLabel1, false)) {
				for (LabelPair leafLabel2 : 
					automaton2.getAlphabet().getSymbolsOfArity(0)) {
					if (leafLabel1.left().equals(leafLabel2.left())) {
						for (IRule<LabelPair, FilterState> rule2 : 
							automaton2.getRulesWithLabel(leafLabel2, false)) {
							EquivalenceState eqState = new EquivalenceState(
									rule1.rightState(), rule2.rightState(), 
									!leafLabel1.right().equals(
										leafLabel2.right()));
							reached.add(eqState);
							if (computeSeparationTerm) {
							separationMap.put(eqState, 
								new Term<LabelPair>(
									automaton1.getAlphabet(), 
									new ArrayList<ITerm<LabelPair>>(), 
									leafLabel1));
							}
						}
					}
				}
			}
		}
		return reached;
	}

	/**
	 * Class encoding a state used when testing equivalence.
	 */
	protected class EquivalenceState implements IState {
		private final FilterState filterState1;
		private final FilterState filterState2;
		private final boolean differs;
		
		public EquivalenceState(final FilterState fState1, 
				final FilterState fState2, final boolean different) {
			this.filterState1 = fState1;
			this.filterState2 = fState2;
			this.differs = different;
		}
		
		public FilterState filterState1() {	return this.filterState1; }
		public FilterState filterState2() {	return this.filterState2; }
		public boolean differs() { return this.differs; }
		
		/**
		 * True iff it proves non-equivalence of two automata.
		 */
		public boolean provesNonEquiv(final FilterAutomaton automaton1, 
				final FilterAutomaton automaton2) {
			return automaton1.getFinalStates().contains(filterState1)
				&& automaton2.getFinalStates().contains(filterState2)
				&& differs;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("(").append(filterState1.toString());
			sb.append(",").append(filterState2.toString());
			sb.append(")").append(differs?"x":"v");
			return sb.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (differs ? 1231 : 1237);
			result = prime * result
					+ ((filterState1 == null) ? 0 : filterState1.hashCode());
			result = prime * result
					+ ((filterState2 == null) ? 0 : filterState2.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EquivalenceState other = (EquivalenceState) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (differs != other.differs)
				return false;
			if (filterState1 == null) {
				if (other.filterState1 != null)
					return false;
			} else if (!filterState1.equals(other.filterState1))
				return false;
			if (filterState2 == null) {
				if (other.filterState2 != null)
					return false;
			} else if (!filterState2.equals(other.filterState2))
				return false;
			return true;
		}

		private FilterAutomaton getOuterType() {
			return FilterAutomaton.this;
		}
		
	}
	
	/**
	 * Adds the rules allowing pairs of diamond to the leaves	 
	 * @param automaton the automata where diamond-rules should be added
	 * @return the automaton with diamond-rules added
	 */
	public final FilterAutomaton addDiamondRules() {
		
		ActionAlphabet alphabet = (ActionAlphabet)this.getAlphabet();
		Set<FilterState> finalStates = this.getFinalStates();
		Set<IRule<LabelPair,FilterState>> rulesDestD = this.getRulesDest();
		Set<IRule<LabelPair,FilterState>> rulesPrefD = this.getRulesPref();
		Set<IRule<LabelPair,FilterState>> rulesModD = this.getRulesMod();
		Set<IRule<LabelPair,FilterState>> rulesRootD = this.getRulesRoot();
		final FilterState sink = this.sinkState();
		
		// states
		Set<FilterState> states = new HashSet<FilterState>();
		states.addAll(this.getStates());
		final FilterState qDiamond = new FilterState("qDiamond");
		states.add(qDiamond);
		
		// rules
		Set<IRule<LabelPair,FilterState>> rulesComD = 
			new HashSet<IRule<LabelPair,FilterState>>();
		Set<IRule<LabelPair,FilterState>> rulesPathD = 
			new HashSet<IRule<LabelPair,FilterState>>();
		
		rulesComD.addAll(this.getRulesCom());
		rulesPathD.addAll(this.getRulesPath());
		
		final BasicRule<LabelPair, FilterState> firstRuleToAdd = 
			new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.DIAMONDDIAMOND, qDiamond); 
		final BasicRule<LabelPair, FilterState> secondRuleToAdd = 
			new BasicRule<LabelPair,FilterState>(
				ActionAlphabet.DIAMONDDIAMOND1, qDiamond, qDiamond);
		
		rulesPathD.add(firstRuleToAdd);
		rulesComD.add(firstRuleToAdd);
		
		rulesPathD.add(secondRuleToAdd);
		rulesComD.add(secondRuleToAdd);
		
		for (IRule<LabelPair,FilterState> initialRule: 
			this.getInitialRulesCom()) {
			final FilterState rightState = initialRule.rightState();
			final LabelPair labelArityOne = 
				new UnaryVersionLabelPair(initialRule.label());
			rulesComD.add(new BasicRule<LabelPair,FilterState>(
				labelArityOne, rightState, qDiamond));
		}
		
		for(IRule<LabelPair,FilterState> initialRule: 
			this.getInitialRulesPath()){
			final FilterState rightState = initialRule.rightState();
			final LabelPair labelArityOne = 
				new UnaryVersionLabelPair(initialRule.label());
			rulesPathD.add(new BasicRule<LabelPair,FilterState>(
				labelArityOne, rightState, qDiamond));
		}
		return new FilterAutomaton(
			alphabet, states, finalStates, sink, rulesDestD, rulesPathD, 
			rulesPrefD, rulesComD, rulesModD, rulesRootD);
	}

	/**
	 * Removes the rules allowing (diamond,diamond) labels below leaves.
	 */
	public final Set<IRule<LabelPair, FilterState>> cleanDiamondRules(
			final ActionAlphabet alphabet, 
			final Set<IRule<LabelPair, FilterState>> rules) {
		Set<IRule<LabelPair, FilterState>> rulesResult = 
			new HashSet<IRule<LabelPair, FilterState>>();
		
		for (IRule<LabelPair, FilterState> rule : rules) {
			if (!rule.label().equals(ActionAlphabet.DIAMONDDIAMOND) && 
				!rule.label().equals(ActionAlphabet.DIAMONDDIAMOND1)) {
				if (rule.label() instanceof UnaryVersionLabelPair) {
					final LabelPair label0 = 
						((UnaryVersionLabelPair)rule.label())
						.getBooleanVersion();
					rulesResult.add(new BasicRule<LabelPair, FilterState>(
						new ArrayList<FilterState>(), 
						label0,rule.rightState()));								
				} else{
					rulesResult.add(rule);
				}
			}
		}
		return rulesResult;
	}
	
	/**
	 * Returns the same automaton, where inaccessible states have been removed.
	 */
	public final FilterAutomaton cleanInaccessibleStates() {
		// we have to clean by branch, so we build an NTA for each branch,
		// clean it, and then rebuild a FilterAutomaton.
		final INTA<LabelPair,FilterState> destNta = 
			ntaForBranch(this.rulesDest).cleanInaccessibleStates();
		final INTA<LabelPair,FilterState> pathNta = 
			ntaForBranch(this.rulesPath).cleanInaccessibleStates();
		final INTA<LabelPair,FilterState> prefNta = 
			ntaForBranch(this.rulesPref).cleanInaccessibleStates();
		final INTA<LabelPair,FilterState> comNta = 
			ntaForBranch(this.rulesCom).cleanInaccessibleStates();
		final INTA<LabelPair,FilterState> modNta = 
			ntaForBranch(this.rulesMod).cleanInaccessibleStates();
		// states
		Set<FilterState> accessibleStates = new HashSet<FilterState>();
		accessibleStates.addAll(destNta.getStates());
		accessibleStates.addAll(pathNta.getStates());
		accessibleStates.addAll(prefNta.getStates());
		accessibleStates.addAll(comNta.getStates());
		accessibleStates.addAll(modNta.getStates());
		// rules at the root
		Set<IRule<LabelPair,FilterState>> rootRules = 
			new HashSet<IRule<LabelPair,FilterState>>();
		for (LabelPair rootLabel : ActionAlphabet.rootLabels()) {
			for (IRule<LabelPair,FilterState> rootRule :
				this.getRulesWithLabel(rootLabel, false)) {
				boolean leftStatesAccessible = true;
				int i=0;
				while (leftStatesAccessible && 
					i<this.getAlphabet().arity(rootLabel)) {
					leftStatesAccessible = 
						accessibleStates.contains(
							rootRule.leftHandSide().states().get(i));
					i++;
				}
				if (leftStatesAccessible) {
					rootRules.add(rootRule);
					accessibleStates.add(rootRule.rightState());
				}
			}
		}
		// final states
		Set<FilterState> newFinalStates = new HashSet<FilterState>();
		newFinalStates.addAll(accessibleStates);
		newFinalStates.retainAll(this.getFinalStates());
		return new FilterAutomaton(this.getActionAlphabet(), 
			accessibleStates, newFinalStates, this.sinkState(), 
			destNta.getRules(false), pathNta.getRules(false), 
			prefNta.getRules(false), comNta.getRules(false), 
			modNta.getRules(false), rootRules);
	}
	
	/**
	 * Builds an NTA for a single branch.
	 */
	private INTA<LabelPair,FilterState> ntaForBranch(
			final Set<IRule<LabelPair,FilterState>> branchRules) {
		return new NTA<LabelPair,FilterState>(
			this.getAlphabet(), this.getStates(), this.getFinalStates(), 
			branchRules, false);
	}
	
	/**
	 * Returns an equivalent automaton, where input trees are only valid 
	 * routes.
	 * @param routeAlphabet the alphabet of this new automaton
	 * @return an automaton for this filter, considering only valid routes
	 */
	public FilterAutomaton automatonForValidRoutes(
			final RouteAlphabet routeAlphabet) {
		final FilterAutomaton onlyRoutes = 
			ValidRoutePairs.routePairs(routeAlphabet);
		return this.compose(onlyRoutes);
	}
	
	/**
	 * Returns rules in the COM branch with no left-state
	 */
	public Set<IRule<LabelPair, FilterState>> getInitialRulesCom() {
		Set<IRule<LabelPair, FilterState>> initialRulesCom = 
			new HashSet<IRule<LabelPair, FilterState>>();
		for (IRule<LabelPair,FilterState> ruleCom : rulesCom) {
			if (ruleCom.leftStates().size()==0) {
				initialRulesCom.add(ruleCom);
			}
		}
		return initialRulesCom;
	}
	
	/**
	 * Returns rules in the AS-path branch with no left-state
	 */
	public Set<IRule<LabelPair, FilterState>> getInitialRulesPath() {
		Set<IRule<LabelPair, FilterState>> initialRulesPath = 
			new HashSet<IRule<LabelPair, FilterState>>();
		for (IRule<LabelPair,FilterState> rulePath : rulesPath) {
			if (rulePath.leftStates().size()==0) {
				initialRulesPath.add(rulePath);
			}
		}
		return initialRulesPath;
	}
	
	/**
	 * Returns rules in the COM branch
	 */
	public Set<IRule<LabelPair, FilterState>> getRulesCom() {
		return rulesCom;
	}

	/**
	 * Returns rules in the AS-path branch
	 */
	public Set<IRule<LabelPair, FilterState>> getRulesPath() {
		return rulesPath;
	}

	/**
	 * Returns rules in the PREF branch
	 */
	public Set<IRule<LabelPair, FilterState>> getRulesPref() {
		return rulesPref;
	}

	/**
	 * Returns rules at the Root
	 */
	public Set<IRule<LabelPair, FilterState>> getRulesRoot() {
		return rulesRoot;
	}
	
	/**
	 * Returns rules in the MODIFIED branch
	 */
	public Set<IRule<LabelPair, FilterState>> getRulesMod() {
		return rulesMod;
	}

	/**
	 * Returns rules in the DEST branch
	 */
	public Set<IRule<LabelPair, FilterState>> getRulesDest() {
		return rulesDest;
	}
}
