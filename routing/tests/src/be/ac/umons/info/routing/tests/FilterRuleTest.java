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

package be.ac.umons.info.routing.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import be.ac.umons.info.routing.Filter;
import be.ac.umons.info.routing.FilterRule;
import be.ac.umons.info.routing.IFilterRule;
import be.ac.umons.info.routing.actions.IAction;
import be.ac.umons.info.routing.actions.atomic.PrefSet;
import be.ac.umons.info.routing.automata.ActionAlphabet;
import be.ac.umons.info.routing.automata.FilterAutomaton;
import be.ac.umons.info.routing.automata.LabelPair;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import be.ac.umons.info.routing.predicates.IPredicate;
import be.ac.umons.info.routing.predicates.atomic.CommIn;
import traul.ranked.terms.ITerm;


public class FilterRuleTest extends TestCase{
	
	/**
	 * A filter with one predicate and one action.
	 */
	public void testFullFilterRule() {
		final int comValue = 30;
		final int newPref = 140;
		final IPredicate inCom = new CommIn(comValue);
		final IAction prefChange = new PrefSet(newPref);
		final IFilterRule fRule = new FilterRule(
			inCom, Collections.singletonList(prefChange));
		final Filter filter = new Filter(
			Collections.singletonList(fRule));

		final RouteAlphabet alphabet = filter.filterAlphabet(); 
		final FilterAutomaton automaton = 
			filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
				Filter.DEFAULT_LOCAL_PREF, newPref);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.integerinteger(comValue, comValue));
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
			destList, pathList, prefVal, comList, acceptVal,
			new ActionAlphabet(alphabet));

		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));

		// predicate is false but the localpref changed: should reject
		List<LabelPair> comList2 = new ArrayList<LabelPair>();
		comList2.add(ActionAlphabet.COMCOM);
		
		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
			destList, pathList, prefVal, comList2, acceptVal,
			new ActionAlphabet(alphabet));

		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree2));

		// predicate is true but the localpref unchanged: should reject
		LabelPair prefVal2 = ActionAlphabet.integerinteger(
				Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		ITerm<LabelPair> tree3 = RoutingTree.getRoutingTree(
				destList, pathList, prefVal2, comList, acceptVal,
				new ActionAlphabet(alphabet));

			assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree3));

		// predicate is false: should accept the unchanged tree
		ITerm<LabelPair> tree4 = RoutingTree.getRoutingTree(
			destList, pathList, prefVal2, comList2, acceptVal,
			new ActionAlphabet(alphabet));

		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree4));
}
	
}
