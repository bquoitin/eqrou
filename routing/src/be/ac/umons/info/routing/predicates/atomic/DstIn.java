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
import java.util.Iterator;
import java.util.Set;

import traul.ranked.nta.labels.ILabel;
import traul.ranked.nta.rules.BasicRule;
import traul.ranked.nta.rules.IRule;
import traul.ranked.nta.states.BasicState;
import traul.ranked.nta.states.IState;
import be.ac.umons.info.routing.IPPrefix;
import be.ac.umons.info.routing.automata.IntegerLabel;
import be.ac.umons.info.routing.automata.PredicateAutomaton;
import be.ac.umons.info.routing.automata.QuasiRoutes;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import be.ac.umons.info.routing.predicates.IPredicate;

/**
 * Destination inclusion: this predicate checks whether the route's destination
 * prefix is included in a given IP prefix.
 */
public class DstIn implements IPredicate {

	final IPPrefix prefix;
	
	/**
	 * Constructor, where the prefix is given by a String like
	 * "192.168.128.0/17".
	 */
	public DstIn(final String prefix) {
		this.prefix = new IPPrefix(prefix);
	}
	
	@Override
	public PredicateAutomaton automaton(RouteAlphabet routeAlphabet) {
		// states and rules
		Set<IState> states = new HashSet<IState>();
		Set<IRule<ILabel,IState>> rulesDest = 
			new HashSet<IRule<ILabel,IState>>();
		// init
		IState qInit = new BasicState("qInit"); 
		states.add(qInit);
		rulesDest.add(new BasicRule<ILabel,IState>(
				RouteAlphabet.DEST, qInit));
		// sink
		IState sink = new BasicState("sink"); 
		states.add(sink);
		rulesDest.add(new BasicRule<ILabel,IState>(
				new IntegerLabel(0), sink, sink));
		rulesDest.add(new BasicRule<ILabel,IState>(
				new IntegerLabel(1), sink, sink));
		// prefix
		int i=0;
		IState qPrevious = qInit;
		Iterator<Integer> iterator = this.prefix.intList().iterator();
		while (iterator.hasNext()) {
			final int bit = iterator.next();
			final IState q = new BasicState("q-"+i++);
			states.add(q);
			rulesDest.add(new BasicRule<ILabel,IState>(
					new IntegerLabel(bit), q, qPrevious));
			rulesDest.add(new BasicRule<ILabel,IState>(
					new IntegerLabel(1-bit), sink, qPrevious));
			qPrevious = q;
		}
		// allow longer prefix
		rulesDest.add(new BasicRule<ILabel,IState>(
			new IntegerLabel(0), qPrevious, qPrevious));
		rulesDest.add(new BasicRule<ILabel,IState>(
			new IntegerLabel(1), qPrevious, qPrevious));		
		// final states
		final IState qAcc = new BasicState("qAcc");
		final Set<IState> finalStates = Collections.singleton(qAcc);
		
		// rules at the root
		IState q0 = new BasicState("q0");
		Set<IRule<ILabel,IState>> rulesRoot = 
				new HashSet<IRule<ILabel,IState>>();
		rulesRoot.add(new BasicRule<ILabel,IState>(
			RouteAlphabet.R, qAcc, qPrevious, q0, q0, q0, q0));
		rulesRoot.add(new BasicRule<ILabel,IState>(
			RouteAlphabet.R, sink, sink, q0, q0, q0, q0));
		
		return new PredicateAutomaton(
				routeAlphabet, states, finalStates, 
				rulesDest, 
				QuasiRoutes.pathBranch(routeAlphabet, q0), 
				QuasiRoutes.prefBranch(routeAlphabet, q0),
				QuasiRoutes.comBranch(routeAlphabet, q0), 
				QuasiRoutes.modBranch(routeAlphabet, q0), 
				rulesRoot, sink);
	}

	@Override
	public RouteAlphabet filterAlphabet() {
		Set<IntegerLabel> destLabels = new HashSet<IntegerLabel>();
		destLabels.add(new IntegerLabel(0));
		destLabels.add(new IntegerLabel(1));
		return new RouteAlphabet(
			destLabels,	new HashSet<IntegerLabel>(), 
			new HashSet<IntegerLabel>(), new HashSet<IntegerLabel>());
	}

	/**
	 * Number of atomic predicates contained in this predicate.
	 */
	@Override
	public int size() {
		return 1;
	}

}
