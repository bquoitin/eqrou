/*
routing-equiv: testing the equivalence of routing policies
Copyright (C) 2013 routing-equiv team

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

package be.ac.umons.info.routing.predicates.atomic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import traul.ranked.nta.labels.ILabel;
import traul.ranked.nta.rules.BasicRule;
import traul.ranked.nta.rules.IRule;
import traul.ranked.nta.states.BasicState;
import traul.ranked.nta.states.IState;
import be.ac.umons.info.routing.automata.IntegerLabel;
import be.ac.umons.info.routing.automata.PredicateAutomaton;
import be.ac.umons.info.routing.automata.QuasiRoutes;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import be.ac.umons.info.routing.predicates.IPredicate;

/**
 * This predicate checks whether a given value is in the communities.
 */
public class CommIn implements IPredicate {
	
	private final int elemValue;
	
	/**
	 * Constructor
	 */
	public CommIn(final int elemValue) {
		this.elemValue = elemValue;
	}
	
	/**
	 * Returns the automaton recognizing the language of this predicate.
	 */
	@Override
	public PredicateAutomaton automaton(final RouteAlphabet routeAlphabet) {
		
		// states
		Set<IState> states = new HashSet<IState>();
		final IState q0 = new BasicState("q0");
		final IState q1 = new BasicState("q1");
		final IState qTrue = new BasicState("qTrue");
		final IState qAcc = new BasicState("qAcc");
		final IState sink = new BasicState("sink");
		states.add(q0);
		states.add(q1);
		states.add(qTrue);
		states.add(qAcc);
		states.add(sink);
		// final states
		final Set<IState> finalStates = Collections.singleton(qAcc);
		
		// rules for COM
		Set<IRule<ILabel,IState>> rulesCom = 
			new HashSet<IRule<ILabel,IState>>();
		rulesCom.add(new BasicRule<ILabel,IState>(RouteAlphabet.COM, q1));
		rulesCom.add(new BasicRule<ILabel,IState>(
			RouteAlphabet.integer(elemValue), qTrue, q1));		
		for (int i : routeAlphabet.comAlphabetInt()) {
			if (i != elemValue) {
				rulesCom.add(new BasicRule<ILabel,IState>(
					RouteAlphabet.integer(i), q1, q1));
			}
			rulesCom.add(new BasicRule<ILabel,IState>(
				RouteAlphabet.integer(i), qTrue, qTrue));
		}
		
		// rules at the root
		Set<IRule<ILabel,IState>> rulesRoot = 
				new HashSet<IRule<ILabel,IState>>();
		rulesRoot.add(new BasicRule<ILabel,IState>(
			RouteAlphabet.R, qAcc, q0, q0, q0, qTrue, q0));
		rulesRoot.add(new BasicRule<ILabel,IState>(
			RouteAlphabet.R, sink, q0, q0, q0, q1, q0));
		
		return new PredicateAutomaton(
				routeAlphabet, states, finalStates, 
				QuasiRoutes.destBranch(routeAlphabet, q0), 
				QuasiRoutes.pathBranch(routeAlphabet, q0), 
				QuasiRoutes.prefBranch(routeAlphabet, q0),
				rulesCom, 
				QuasiRoutes.modBranch(routeAlphabet, q0), 
				rulesRoot, sink);
	}
	
	/**
	 * Returns the set of symbols to be included in the alphabet. More 
	 * precisely, the RouteAlphabet is the set of constant values in predicates
	 * (except for the pref branch) and actions, while the Set<IntegerLabel> is
	 * the set of values in this predicate and related to the pref-branch.
	 */
	@Override
	public RouteAlphabet filterAlphabet() {
		return new RouteAlphabet(
			new HashSet<IntegerLabel>(), new HashSet<IntegerLabel>(), 
			new HashSet<IntegerLabel>(), 
			Collections.singleton(new IntegerLabel(elemValue)));
	}

	/**
	 * Number of atomic predicates contained in this predicate.
	 */
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public String toString() {
		return "Communities contains " + this.elemValue;
	}
}
