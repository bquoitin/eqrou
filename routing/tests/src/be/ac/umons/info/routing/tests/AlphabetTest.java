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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import be.ac.umons.info.routing.Filter;
import be.ac.umons.info.routing.actions.IAction;
import be.ac.umons.info.routing.actions.atomic.PathPrepend;
import be.ac.umons.info.routing.actions.atomic.PrefSet;
import be.ac.umons.info.routing.automata.IntegerLabel;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import be.ac.umons.info.routing.predicates.IPredicate;
import be.ac.umons.info.routing.predicates.atomic.PathIn;

/**
 * Tests over the alphabet inference.
 */
public class AlphabetTest extends TestCase {

	
	public void testPredicateAlphabet() {
		
		final int asValue = 20;
		final IPredicate predicate = new PathIn(asValue);

		RouteAlphabet predicateAlphabet = predicate.filterAlphabet();
		
		RouteAlphabet correctAlphabet = 
			new RouteAlphabet(
				new HashSet<IntegerLabel>(),
				Collections.singleton(new IntegerLabel(asValue)),
				new HashSet<IntegerLabel>(),
				new HashSet<IntegerLabel>());
		assertEquals("Both alphabets should be equal.", 
			correctAlphabet, predicateAlphabet);

		RouteAlphabet incorrectRouteAlphabet = 
			new RouteAlphabet(
				new HashSet<IntegerLabel>(),
				Collections.singleton(new IntegerLabel(asValue+1)),
				new HashSet<IntegerLabel>(),
				new HashSet<IntegerLabel>());
		assertFalse("Alphabets should differ",
				incorrectRouteAlphabet.equals(predicateAlphabet));
	}
	
	public void testActionAlphabet() {

		final int asValue = 20;
		final IAction action = new PathPrepend(asValue);

		RouteAlphabet routeAlphabet = action.filterAlphabet();
		
		RouteAlphabet correctRouteAlphabet = 
			new RouteAlphabet(
				new HashSet<IntegerLabel>(),
				Collections.singleton(new IntegerLabel(asValue)),
				new HashSet<IntegerLabel>(),
				new HashSet<IntegerLabel>());
		assertEquals("Both alphabets should be equal.", 
			correctRouteAlphabet, routeAlphabet);

		RouteAlphabet incorrectRouteAlphabet = 
			new RouteAlphabet(
				new HashSet<IntegerLabel>(),
				Collections.singleton(new IntegerLabel(asValue+1)),
				new HashSet<IntegerLabel>(),
				new HashSet<IntegerLabel>());
		assertFalse("Alphabets should differ",
				incorrectRouteAlphabet.equals(routeAlphabet));
	}
	
	public void testActionsAlphabet() {

		final int asValue1 = 20;
		final int asValue2 = 30;
		final IAction action1 = new PathPrepend(asValue1);
		final IAction action2 = new PathPrepend(asValue2);
		List<IAction> actions = new ArrayList<IAction>();
		actions.add(action1);
		actions.add(action2);
		
		RouteAlphabet routeAlphabet = action1.filterAlphabet();
		routeAlphabet = routeAlphabet.union(action2.filterAlphabet());
				
		Set<IntegerLabel> pathLabels = new HashSet<IntegerLabel>();
		pathLabels.add(new IntegerLabel(asValue1));
		pathLabels.add(new IntegerLabel(asValue2));		
		RouteAlphabet correctRouteAlphabet = 
			new RouteAlphabet(
				new HashSet<IntegerLabel>(),
				pathLabels,
				new HashSet<IntegerLabel>(),
				new HashSet<IntegerLabel>());
		assertEquals("Both alphabets should be equal.", 
			correctRouteAlphabet, routeAlphabet);

		Set<IntegerLabel> pathLabels2 = new HashSet<IntegerLabel>();
		pathLabels.add(new IntegerLabel(asValue1));
		RouteAlphabet incorrectRouteAlphabet = 
			new RouteAlphabet(
				new HashSet<IntegerLabel>(),
				pathLabels2,
				new HashSet<IntegerLabel>(),
				new HashSet<IntegerLabel>());
		assertFalse("Alphabets should differ.", 
			incorrectRouteAlphabet.equals(routeAlphabet));
}
	
	public void testFilterAlphabet1() {

		final int asValue1 = 20;
		final int asValue2 = 30;
		Filter filter1 = 
			new Filter(new PathPrepend(asValue1), new PathPrepend(asValue2));
		
		RouteAlphabet routeAlphabet = filter1.filterAlphabet();

		Set<IntegerLabel> pathLabels = new HashSet<IntegerLabel>();
		pathLabels.add(new IntegerLabel(asValue1));
		pathLabels.add(new IntegerLabel(asValue2));		
		RouteAlphabet correctRouteAlphabet = 
			new RouteAlphabet(
				new HashSet<IntegerLabel>(),
				pathLabels,
				Collections.singleton(
					new IntegerLabel(Filter.DEFAULT_LOCAL_PREF)),
				new HashSet<IntegerLabel>());
		assertEquals("Both alphabets should be equal.", 
			correctRouteAlphabet, routeAlphabet);
		
		Filter filter2 = 
			new Filter(new PathPrepend(asValue2), new PathPrepend(asValue1));
		
		routeAlphabet = routeAlphabet.union(filter2.filterAlphabet());
		assertEquals("Both alphabets should be equal.", 
				correctRouteAlphabet, routeAlphabet);		
	}
	
	public void testFilterAlphabet2() {
		
		final int prefIncr = 20;
		final int prefDecr = 10;
		final int prefVal = 200;
		
		final Filter filter1 = FilterProvider.relativePref(prefIncr, prefDecr);
		final Filter filter2 = new Filter(new PrefSet(prefVal));
		
		RouteAlphabet routeAlphabet = filter1.filterAlphabet();
		routeAlphabet = routeAlphabet.union(filter2.filterAlphabet());

		Set<IntegerLabel> prefLabels = new HashSet<IntegerLabel>();
		prefLabels.add(new IntegerLabel(Filter.DEFAULT_LOCAL_PREF));
		prefLabels.add(new IntegerLabel(Filter.DEFAULT_LOCAL_PREF + prefIncr));
		prefLabels.add(new IntegerLabel(Filter.DEFAULT_LOCAL_PREF - prefDecr));
		prefLabels.add(new IntegerLabel(
				Filter.DEFAULT_LOCAL_PREF + prefIncr - prefDecr));
		prefLabels.add(new IntegerLabel(prefVal));
		RouteAlphabet correctRouteAlphabet = 
			new RouteAlphabet(
				new HashSet<IntegerLabel>(), new HashSet<IntegerLabel>(),
				prefLabels,	new HashSet<IntegerLabel>());
		assertEquals("Both alphabets should be equal.", 
			correctRouteAlphabet, routeAlphabet);
	}

}
