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

package be.ac.umons.info.routing.actions.atomic;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import traul.ranked.nta.rules.BasicRule;
import traul.ranked.nta.rules.IRule;
import be.ac.umons.info.routing.actions.IAction;
import be.ac.umons.info.routing.automata.ActionAlphabet;
import be.ac.umons.info.routing.automata.FilterAutomaton;
import be.ac.umons.info.routing.automata.FilterState;
import be.ac.umons.info.routing.automata.IntegerLabel;
import be.ac.umons.info.routing.automata.LabelPair;
import be.ac.umons.info.routing.automata.QuasiRoutes;
import be.ac.umons.info.routing.automata.RouteAlphabet;

/**
 * Removes a community.
 */
public final class ComRemove implements IAction{
	
	private final int comValue;
	
	/**
	 * Constructor
	 */
	public ComRemove(final int comValue) {
		this.comValue = comValue;
	}
	
	/**
	 * Returns the automaton recognizing the language of this action.
	 */
	@Override
	public FilterAutomaton automaton(final RouteAlphabet routeAlphabet,
			final ActionAlphabet actionAlphabet) {

		// states
		Set<FilterState> states = new HashSet<FilterState>();
		final FilterState q0 = new FilterState("q0");
		final FilterState q1 = new FilterState("q1");
		final FilterState q2 = new FilterState("q2");
		final FilterState q3 = new FilterState("q3");
		final FilterState qMod = new FilterState("qMod");
		final FilterState qFix = new FilterState("qFix");
		final FilterState qOK = new FilterState("qOK");
		final FilterState sink = new FilterState("sink");
		states.add(q0);
		states.add(q1);
		states.add(q2);
		states.add(q3);
		states.add(qMod);
		states.add(qFix);
		states.add(qOK);
		states.add(sink);
		Map<Integer,FilterState> stateForInteger = 
			new HashMap<Integer,FilterState>(); 
		for (int intLabel : routeAlphabet.comAlphabetInt()) {
			final FilterState qInt = new FilterState("q["+intLabel+"]");
			states.add(qInt);
			stateForInteger.put(intLabel, qInt);
		}
		// final states
		Set<FilterState> finalStates = new HashSet<FilterState>();
		finalStates.add(qOK);

		// pairs (t,t) of quasi-routes on all branches
		final Set<IRule<LabelPair,FilterState>> rulesDest =
			QuasiRoutes.destPairBranch(routeAlphabet, q0);
		final Set<IRule<LabelPair,FilterState>> rulesPath =
			QuasiRoutes.pathPairBranch(routeAlphabet, q0);
		final Set<IRule<LabelPair,FilterState>> rulesPref =
			QuasiRoutes.prefPairBranch(routeAlphabet, q0);
		Set<IRule<LabelPair,FilterState>> rulesCom =
			QuasiRoutes.comPairBranch(routeAlphabet, q0);
		final Set<IRule<LabelPair,FilterState>> rulesMod =
			QuasiRoutes.modPairBranch(routeAlphabet, qMod, qFix);
		
		// rules in Com branch
		rulesCom.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.COMDIAMOND, q1));		
		rulesCom.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.integercom(comValue), q2, q1));
		rulesCom.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.COMCOM, q3));
		for (int i : routeAlphabet.comAlphabetInt()) {
			if (i!=comValue) {
				rulesCom.add(new BasicRule<LabelPair,FilterState>(
					ActionAlphabet.integerinteger(i, i), q3, q3));				
			}
			if (i>comValue) {
				rulesCom.add(new BasicRule<LabelPair,FilterState>(
					ActionAlphabet.integercom(i), stateForInteger.get(i), q1));
				for (int j : routeAlphabet.comAlphabetInt()) {
					if (j>i) {
						rulesCom.add(new BasicRule<LabelPair,FilterState>(
							ActionAlphabet.integerinteger(i,j),
							stateForInteger.get(i),	stateForInteger.get(j)));
					}
				}
				rulesCom.add(new BasicRule<LabelPair,FilterState>(
					ActionAlphabet.integerinteger(comValue,i),
					q2, stateForInteger.get(i)));
			}
			if (i<comValue) {
				rulesCom.add(new BasicRule<LabelPair,FilterState>(
					ActionAlphabet.integerinteger(i, i), q2, q2));
			}
		}
		
		// rules at the root
		Set<IRule<LabelPair,FilterState>> rulesRoot =
			new HashSet<IRule<LabelPair,FilterState>>();
		rulesRoot.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.RR, qOK, q0, q0, q0, q2, qMod));
		rulesRoot.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.RR, qOK, q0, q0, q0, q3, qMod));
		rulesRoot.add(new BasicRule<LabelPair,FilterState>(
			ActionAlphabet.RR, qOK, q0, q0, q0, q0, qFix));

		// automaton
		return new FilterAutomaton(
			actionAlphabet, states, finalStates, sink,
			rulesDest, rulesPath, rulesPref, rulesCom, rulesMod, rulesRoot);
	}

	/**
	 * Returns the set of labels to be included in the alphabet.
	 */
	@Override
	public RouteAlphabet filterAlphabet() {
		return new RouteAlphabet(new HashSet<IntegerLabel>(), 
			new HashSet<IntegerLabel>(), new HashSet<IntegerLabel>(), 
			Collections.singleton(new IntegerLabel(this.comValue)));
	}

	/**
	 * Returns the set of labels used by the action. This may differ from 
	 * {@link IAction.filterAlphabet} because we may use values during the
	 * composition, that won't be used outside the action.
	 * @param alphabet alphabet already inferred before this action
	 */
	@Override
	public RouteAlphabet internalAlphabet(final RouteAlphabet alphabet) {
		return alphabet;
	}
	
	/**
	 * Returns the output local pref value, for a given input local pref value.
	 */
	@Override
	public int localPrefImage(final int inputLocalPref) {
		return inputLocalPref;
	}

	@Override
	public String toString() {
		return "Remove community " + this.comValue;
	}
}
