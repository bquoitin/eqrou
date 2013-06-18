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

package be.ac.umons.info.routing.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import be.ac.umons.info.routing.Filter;
import be.ac.umons.info.routing.FilterRule;
import be.ac.umons.info.routing.IFilterRule;
import be.ac.umons.info.routing.actions.IAction;
import be.ac.umons.info.routing.actions.atomic.Accept;
import be.ac.umons.info.routing.actions.atomic.ComAdd;
import be.ac.umons.info.routing.actions.atomic.ComRemove;
import be.ac.umons.info.routing.actions.atomic.PathPrepend;
import be.ac.umons.info.routing.actions.atomic.Reject;
import be.ac.umons.info.routing.automata.FilterAutomaton;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import be.ac.umons.info.routing.predicates.IPredicate;
import be.ac.umons.info.routing.predicates.atomic.CommIn;
import be.ac.umons.info.routing.predicates.atomic.PathNei;

public class FilterEquivalenceTest extends TestCase{
	
	/**
	 * First test: two equivalent series of actions: the second one is obtained
	 * from the first one by stopping after the accept action.
	 */
	public void testFilterEquivalent1() {
		
		Filter filter1 = new Filter(
			new PathPrepend(30), new PathPrepend(20), 
			new Accept(), new PathPrepend(50));
		Filter filter2 = new Filter(
			new PathPrepend(30), new PathPrepend(20), new Accept());
		
		// check automata, just in case...
		RouteAlphabet routeAlphabet = filter1.filterAlphabet();
		routeAlphabet = routeAlphabet.union(filter2.filterAlphabet());
		FilterAutomaton automaton1 = filter1.automaton(routeAlphabet);
		FilterAutomaton automaton2 = filter2.automaton(routeAlphabet);
		automaton1.checkIntegrity();
		automaton2.checkIntegrity();
		assertTrue("The filters should be equivalent.",
				filter1.equivalent(filter2));
		assertNull("There should be no separating route.",
				filter1.separatingRoute(filter2));
		
	}
	
	public void testFilterEquivalent2() {
		
		Filter filter1 = new Filter(new PathPrepend(30), new ComAdd(20));
		Filter filter2 = new Filter(new ComAdd(20), new PathPrepend(30));
		
		assertTrue("The filters should be equivalent.",
				filter1.equivalent(filter2));
		assertNull("There should be no separating route.",
				filter1.separatingRoute(filter2));
	}
	
	public void testFilterNonEquivalent2() {

		Filter filter1 = new Filter(new PathPrepend(30));
		Filter filter2 = new Filter(new PathPrepend(30), new PathPrepend(40));
		assertFalse("The filters should be non equivalent.",
			filter1.equivalent(filter2));
		assertNotNull("There should be a separating route.",
			filter1.separatingRoute(filter2));
	}
	
	public void testComAddAccept() {

		final int asValue = 10;
		final int comValue = 20;
		
		assertTrue("These two filters should be equivalent",
			FilterProvider.simpleComAddAccept(comValue).equivalent(
				FilterProvider.complexComAddAccept(asValue, comValue)));
	}

	/**
	 * Second test: it should be equivalent to add+remove 30, and to just 
	 * remove 30 in COM.
	 */
	public void testAddRemoveEquivalent() {

		Filter filter1 = new Filter(new ComRemove(30));
		Filter filter2 = new Filter(new ComAdd(30), new ComRemove(30));
		assertTrue("The filters should be equivalent.",
			filter1.equivalent(filter2));
	}
	
	/**
	 * Comparing filters with predicates.
	 */
	public void testFilterWithPredicate1() {

		// first filter
		List<IFilterRule> filter1rules = new ArrayList<IFilterRule>();
		
		IPredicate rule1predicate = new CommIn(30);
		IAction rule1action = new ComAdd(40);
		IFilterRule rule1 = new FilterRule(rule1predicate,
			Collections.singletonList(rule1action));
		filter1rules.add(rule1);
		
		IPredicate rule2predicate = new CommIn(40);
		IAction rule2action = new ComRemove(30);
		IFilterRule rule2 = new FilterRule(rule2predicate,
			Collections.singletonList(rule2action));
		filter1rules.add(rule2);
		
		Filter filter1 = new Filter(filter1rules);
		
		// second filter
		IPredicate filter2predicate = new CommIn(30);
		List<IAction> filter2actions = new ArrayList<IAction>();
		filter2actions.add(new ComAdd(40));
		filter2actions.add(new ComRemove(30));
		IFilterRule filter2rule = 
			new FilterRule(filter2predicate, filter2actions);
		
		Filter filter2 = 
			new Filter(Collections.singletonList(filter2rule));
		
		assertTrue("These two filters should be equivalent",
			filter1.equivalent(filter2));
		
		// third filter
		Filter filter3 = new Filter(new ComAdd(40));
		
		assertFalse("These two filters should not be equivalent.",
			filter1.equivalent(filter3));
	}

	/**
	 * Test related to a bug mentioned by Laurent Vanbever on feb 20th 2012.
	 */
	public void testAddingRule(){
		// filter 1
		IPredicate predicate1 = new PathNei(3561);
		List<IAction> actions1 = new ArrayList<IAction>();
		actions1.add(new Reject());
		IFilterRule filterRule1 = new FilterRule(predicate1, actions1);
		List<IFilterRule> rules1 = new ArrayList<IFilterRule>();
		rules1.add(filterRule1);
		Filter filter1 = new Filter(rules1);
		// filter 2
		IPredicate predicate2 = new PathNei(3561);
		List<IAction> actions2 = new ArrayList<IAction>();
		actions2.add(new Reject());
		IFilterRule filterRule2 = new FilterRule(predicate2, actions2);
		IPredicate predicate3 = new PathNei(1200);
	    List<IAction> actions3 = new ArrayList<IAction>();
	    actions3.add(new Accept());
	    IFilterRule filterRule3 = new FilterRule(predicate3, actions3);
		List<IFilterRule> rules2 = new ArrayList<IFilterRule>();
		rules2.add(filterRule2);
		rules2.add(filterRule3);
		Filter filter2 = new Filter(rules2);
		
		assertFalse("These two filters should not be equivalent.",
				filter1.equivalent(filter2));
	}

}
