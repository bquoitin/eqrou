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

package be.ac.umons.info.routing.predicates.atomic;

import java.util.Arrays;
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
 * This predicate tests whether a given sequence of ASNs is in the AS path.
 */
public class PathSub implements IPredicate {

	private final int[] sequence;
	
	/**
	 * Constructor
	 * @param seq sequence of ASNs to be found in AS-list. This sequence is 
	 * given in the usual order, even though internally AS branches have their
	 * first elem at the bottom.
	 */
	public PathSub(final int[] seq) {
		this.sequence = seq;
	}
	
	@Override
	public PredicateAutomaton automaton(RouteAlphabet routeAlphabet) {
		// we use an automaton similar to the Knuth Morris-Pratt algorithm
		int n = this.sequence.length;
		// states
		IState[] intStates = new IState[n+1];
		for (int i=0; i<n; i++) {
			intStates[i] = new BasicState("kmp"+i);
		}
		Set<IState> states = new HashSet<IState>(Arrays.asList(intStates));
		final IState q0 = new BasicState("q0");
		final IState qAcc = new BasicState("qAcc");
		final IState sink = new BasicState("sink");
		states.add(q0);
		states.add(qAcc);
		states.add(sink);
		// final states
		final Set<IState> finalStates = Collections.singleton(qAcc);

		// rules for AS-Path
		Set<IRule<ILabel,IState>> rulesPath = 
			new HashSet<IRule<ILabel,IState>>();
		rulesPath.add(
			new BasicRule<ILabel,IState>(RouteAlphabet.PATH, intStates[0]));
		// case i<n: go to largest border
		for (int i=0; i<n-1; i++) {
			IState srcState = intStates[i];
			for (IntegerLabel label : routeAlphabet.asPathAlphabet()) {
				int[] prefix = Arrays.copyOf(this.sequence, i+1);
				prefix[i] = label.intValue();
				int borderLength = borderLength(prefix, this.sequence);
				IState destState = intStates[borderLength];
				
				rulesPath.add(new BasicRule<ILabel,IState>(
					label, destState, srcState));		
			}
		}
		// case n: self-loops on final state
		for (IntegerLabel label : routeAlphabet.asPathAlphabet()) {
			IState state = intStates[n-1];
			rulesPath.add(new BasicRule<ILabel,IState>(label, state, state));		
		}
				
		// rules at the root
		Set<IRule<ILabel,IState>> rulesRoot = 
			new HashSet<IRule<ILabel,IState>>();
		rulesRoot.add(new BasicRule<ILabel,IState>(
			RouteAlphabet.R, qAcc, q0, intStates[n-1], q0, q0, q0));
		for (int i=0; i<n-1; i++) {
			rulesRoot.add(new BasicRule<ILabel,IState>(
				RouteAlphabet.R, sink, q0, intStates[i], q0, q0, q0));			
		}		
		return new PredicateAutomaton(
				routeAlphabet, states, finalStates, 
				QuasiRoutes.destBranch(routeAlphabet, q0), 
				rulesPath, 
				QuasiRoutes.prefBranch(routeAlphabet, q0),
				QuasiRoutes.comBranch(routeAlphabet, q0), 
				QuasiRoutes.modBranch(routeAlphabet, q0), 
				rulesRoot, sink);
	}

	/**
	 * Returns the length of the largest suffix of p being also a prefix of w.
	 */
	public int borderLength(int[] p, int[] w) {
		int i=Math.max(0, p.length-w.length);
		boolean match = false;
		while (!match && i<p.length) {
			// test whether p[i:] is a suffix of w
			int j=i;
			match = true;
			while (match && j<p.length) {
				match = (p[j] == w[j-i]);
				j++;
			}
			if (!match) {
				i++;
			}
		}
		return p.length-i;
	}
	
	/**
	 * Returns the set of symbols to be included in the alphabet. More 
	 * precisely, the RouteAlphabet is the set of constant values in predicates
	 * (except for the pref branch) and actions, while the Set<IntegerLabel> is
	 * the set of values in this predicate and related to the pref-branch.
	 */
	@Override
	public RouteAlphabet filterAlphabet() {
		Set<IntegerLabel> intLabels = new HashSet<IntegerLabel>();
		for (Integer i : this.sequence) {
			intLabels.add(RouteAlphabet.integer(i));
		}
		return new RouteAlphabet(
				new HashSet<IntegerLabel>(), intLabels,
				new HashSet<IntegerLabel>(), new HashSet<IntegerLabel>());
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
		StringBuffer sb = new StringBuffer("AS-path contains sequence [");
		int i=0;
		while (i<this.sequence.length) {
			sb.append(this.sequence[i]);
			if (i<this.sequence.length-1) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
