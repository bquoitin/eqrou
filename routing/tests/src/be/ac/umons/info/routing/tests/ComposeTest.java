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
import java.util.List;

import junit.framework.TestCase;
import be.ac.umons.info.routing.Filter;
import be.ac.umons.info.routing.actions.atomic.ComAdd;
import be.ac.umons.info.routing.actions.atomic.ComRemove;
import be.ac.umons.info.routing.actions.atomic.PathPrepend;
import be.ac.umons.info.routing.actions.atomic.PrefAdd;
import be.ac.umons.info.routing.actions.atomic.PrefSet;
import be.ac.umons.info.routing.automata.ActionAlphabet;
import be.ac.umons.info.routing.automata.FilterAutomaton;
import be.ac.umons.info.routing.automata.LabelPair;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import traul.ranked.terms.ITerm;

public class ComposeTest extends TestCase {
	
	/**
	 * Tests the composition of prefIncrement after prefChange on a tree to be
	 * accepted.
	 */
	public void testComposePrefChangePrefIncrementAccept() {
		
		final int newPref = 130;	
		final int increment = 20;
		PrefSet prefChange = new PrefSet(newPref);
		PrefAdd prefIncrement = new PrefAdd(increment);

		final Filter filter = new Filter(prefChange, prefIncrement);
		final RouteAlphabet alphabet = filter.filterAlphabet();
		final FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, newPref+increment);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;
		
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));

		assertTrue("The composition automaton should accept the routing tree.",
				automaton.accepts(tree));

		//prefval2
		LabelPair prefVal2 = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
				destList, pathList, prefVal2, comList, acceptVal,
				new ActionAlphabet(alphabet));

		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree2));
}
	
	/**
	 * Tests the composition of comRemove after comAdd: add and remove the same
	 * element.
	 */
	public void testComposeComAddComRemove1() {

		// add and remove the same element
		final int comValue = 40;
		final int otherElement1 = 10;
		final int otherElement2 = 50;
		ComAdd comAdd = new ComAdd(comValue);
		ComRemove comRemove = new ComRemove(comValue);
		ComAdd comAddOther1 = new ComAdd(otherElement1);
		ComAdd comAddOther2 = new ComAdd(otherElement2);

		final Filter filter = new Filter(comAdd, comRemove);
		final RouteAlphabet alphabet = FilterProvider.alphabetForActions(
			comAdd, comRemove, comAddOther1, comAddOther2);
		final FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(
			ActionAlphabet.integerinteger(otherElement1, otherElement1));
		comList.add(
			ActionAlphabet.integerinteger(otherElement2, otherElement2));
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;

		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
			destList, pathList, prefVal, comList, acceptVal,
			new ActionAlphabet(alphabet));
			
		assertTrue("The composition automaton should accept the routing tree.",
			automaton.accepts(tree));

		//comList2
		List<LabelPair> comList2 = new ArrayList<LabelPair>();
		comList2.add(ActionAlphabet.integerinteger(otherElement1, comValue));
		comList2.add(
			ActionAlphabet.integerinteger(otherElement2, otherElement2));
		comList2.add(ActionAlphabet.COMCOM);

		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
			destList, pathList, prefVal, comList2, acceptVal,
			new ActionAlphabet(alphabet));
			
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree2));
	}

	/**
	 * Tests the composition of comRemove after comAdd: add and remove different
	 * elements (with a tree to be accepted).
	 */
	public void testComposeComAddComRemove2() {
		
		final int comValueAdd = 40;	
		final int comValueRemove = 50;
		final int comValueOther = 80;
		ComAdd comAdd = new ComAdd(comValueAdd);
		ComRemove comRemove = new ComRemove(comValueRemove);
		ComAdd comOther = new ComAdd(comValueOther);
		
		Filter filter = new Filter(comAdd, comRemove);
		RouteAlphabet alphabet = 
			FilterProvider.alphabetForActions(comAdd, comRemove, comOther);
		FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.integerinteger(comValueRemove,comValueAdd));
		comList.add(
			ActionAlphabet.integerinteger(comValueOther, comValueOther));
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;

		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
			destList, pathList, prefVal, comList, acceptVal,
			new ActionAlphabet(alphabet));
			
		assertTrue("The composition automaton should accept the routing tree.",
			automaton.accepts(tree));

		//comList2
		List<LabelPair> comList2 = new ArrayList<LabelPair>();
		comList2.add(
			ActionAlphabet.integerinteger(comValueRemove,comValueRemove));
		comList2.add(
			ActionAlphabet.integerinteger(comValueOther, comValueOther));
		comList2.add(ActionAlphabet.COMCOM);

		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
			destList, pathList, prefVal, comList2, acceptVal,
			new ActionAlphabet(alphabet));
			
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree2));
	}
	
	/**
	 * Tests the composition of comRemove after comAdd: add an existing element
	 * and remove another one.
	 */
	public void testComposeComAddComRemove3() {

		final int comValueAdd = 50;	
		final int comValueRemove = 80;

		ComAdd comAdd = new ComAdd(comValueAdd);
		ComRemove comRemove = new ComRemove(comValueRemove);
		
		Filter filter = new Filter(comAdd, comRemove);
		RouteAlphabet alphabet = filter.filterAlphabet();
		FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.integerinteger(comValueAdd, comValueAdd));
		comList.add(ActionAlphabet.integercom(comValueRemove));
		comList.add(ActionAlphabet.COMDIAMOND);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;

		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
			
		assertTrue("The composition automaton should accept the routing tree.",
				automaton.accepts(tree));
		
		//comList2
		List<LabelPair> comList2 = new ArrayList<LabelPair>();
		comList2.add(ActionAlphabet.integerinteger(comValueAdd, comValueAdd));
		comList2.add(
			ActionAlphabet.integerinteger(comValueRemove,comValueRemove));
		comList2.add(ActionAlphabet.COMDIAMOND);

		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
			destList, pathList, prefVal, comList2, acceptVal,
			new ActionAlphabet(alphabet));
			
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree2));
	}

	/**
	 * Tests the composition of comRemove after comAdd: add an element and 
	 * remove another one that is not in.
	 */
	public void testComposeComAddComRemove4() {
		
		final int comValueAdd = 40;	
		final int comValueRemove = 30;
		final int comValueOther1 = 50;
		final int comValueOther2 = 80;
		
		ComAdd comAdd = new ComAdd(comValueAdd);
		ComRemove comRemove = new ComRemove(comValueRemove);		
		
		Filter filter = new Filter(comAdd, comRemove);
		RouteAlphabet alphabet = FilterProvider.alphabetForActions(
			comAdd, comRemove,
			new ComAdd(comValueOther1), new ComAdd(comValueOther2));
		FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.integerinteger(comValueOther1,comValueAdd));
		comList.add(
			ActionAlphabet.integerinteger(comValueOther2, comValueOther1));
		comList.add(ActionAlphabet.cominteger(comValueOther2));
		comList.add(ActionAlphabet.DIAMONDCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;

		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
			
		assertTrue("The composition automaton should accept the routing tree.",
				automaton.accepts(tree));
		
		//comList2
		List<LabelPair> comList2 = new ArrayList<LabelPair>();
		comList2.add(
			ActionAlphabet.integerinteger(comValueOther1, comValueOther1));
		comList2.add(
			ActionAlphabet.integerinteger(comValueOther2, comValueOther2));
		comList2.add(ActionAlphabet.COMCOM);

		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList2, acceptVal,
				new ActionAlphabet(alphabet));
			
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree2));
	}

	/**
	 * Tests the composition of comRemove after comAdd: add an element already 
	 * in, and remove the same.
	 */
	public void testComposeComAddComRemove5() {
		
		final int comValueAdd = 30;	
		final int comValueRemove = 30;
		
		ComAdd comAdd = new ComAdd(comValueAdd);
		ComRemove comRemove = new ComRemove(comValueRemove);
		
		Filter filter = new Filter(comAdd, comRemove);
		RouteAlphabet alphabet = filter.filterAlphabet();
		FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.integercom(comValueRemove));
		comList.add(ActionAlphabet.COMDIAMOND);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;

		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
			destList, pathList, prefVal, comList, acceptVal,
			new ActionAlphabet(alphabet));
			
		assertTrue("The composition automaton should accept the routing tree.",
			automaton.accepts(tree));
		
		//comList2
		List<LabelPair> comList2 = new ArrayList<LabelPair>();
		comList2.add(
			ActionAlphabet.integerinteger(comValueRemove, comValueRemove));
		comList2.add(ActionAlphabet.COMCOM);

		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
			destList, pathList, prefVal, comList2, acceptVal,
			new ActionAlphabet(alphabet));
			
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree2));
	}

	/**
	 * Tests the composition of two insertions in AS-path.
	 */
	public void testComposeASAS() {

		final int asValueAdd1 = 10;	
		final int asValueAdd2 = 30;
		final int asValueOther1 = 20;
		final int asValueOther2 = 40;
		
		PathPrepend asAdd = new PathPrepend(asValueAdd1);
		PathPrepend asAdd2 = new PathPrepend(asValueAdd2);
		
		Filter filter = new Filter(asAdd, asAdd2);
		RouteAlphabet alphabet = FilterProvider.alphabetForActions(
			asAdd, asAdd2, 
			new PathPrepend(asValueOther1), new PathPrepend(asValueOther2));
		FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(
			ActionAlphabet.integerinteger(asValueOther1, asValueOther1));
		pathList.add(
			ActionAlphabet.integerinteger(asValueOther2, asValueOther2));
		pathList.add(ActionAlphabet.pathinteger(asValueAdd1));
		pathList.add(ActionAlphabet.diamondinteger(asValueAdd2));
		pathList.add(ActionAlphabet.DIAMONDPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.COMCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;

		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
			
		assertTrue("The composition automaton should accept the routing tree.",
				automaton.accepts(tree));

		//pathList2
		List<LabelPair> pathList2 = new ArrayList<LabelPair>();
		pathList2.add(
			ActionAlphabet.integerinteger(asValueOther1, asValueOther1));
		pathList.add(
			ActionAlphabet.integerinteger(asValueOther2, asValueOther2));
		pathList2.add(ActionAlphabet.diamondinteger(asValueAdd2));
		pathList2.add(ActionAlphabet.DIAMONDPATH);

		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
				destList, pathList2, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
			
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree2));
	}

	/**
	 * Tests the composition of two insertions in COM.
	 */
	public void testComposeComAddComAdd() {
		
		final int comValueAdd1 = 40;	
		final int comValueAdd2 = 90;
		final int comValueOther1 = 50;
		final int comValueOther2 = 80;
		
		ComAdd comAdd1 = new ComAdd(comValueAdd1);
		ComAdd comAdd2 = new ComAdd(comValueAdd2);
		
		Filter filter = new Filter(comAdd1, comAdd2);
		RouteAlphabet alphabet = FilterProvider.alphabetForActions(
			comAdd1, comAdd2,
			new ComAdd(comValueOther1), new ComAdd(comValueOther2));
		FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
		
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(ActionAlphabet.integerinteger(comValueOther1, comValueAdd1));
		comList.add(ActionAlphabet.integerinteger(comValueOther2, comValueOther1));
		comList.add(ActionAlphabet.cominteger(comValueOther2));
		comList.add(ActionAlphabet.diamondinteger(comValueAdd2));
		comList.add(ActionAlphabet.DIAMONDCOM);
		
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;

		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet));
			
		assertTrue("The composition automaton should accept the routing tree.",
				automaton.accepts(tree));
		
		//comList2
		List<LabelPair> comList2 = new ArrayList<LabelPair>();
		comList2.add(ActionAlphabet.integerinteger(comValueOther1, comValueAdd1));
		comList2.add(ActionAlphabet.integerinteger(comValueOther2, comValueOther1));
		comList2.add(ActionAlphabet.cominteger(comValueOther2));
		comList2.add(ActionAlphabet.DIAMONDCOM);

		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList2, acceptVal,
				new ActionAlphabet(alphabet));
			
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree2));
	}
	
	/**
	 * Tests the composition of two removals in COM.
	 */
	public void testComposeComRemoveComRemove() {
		
		// add an element and remove an element not in
		final int comValueRemove1 = 40;	
		final int comValueRemove2 = 90;
		final int comValueOther1 = 50;
		final int comValueOther2 = 80;
		
		ComRemove comRemove1 = new ComRemove(comValueRemove1);
		ComRemove comRemove2 = new ComRemove(comValueRemove2);		

		Filter filter = new Filter(comRemove1, comRemove2);
		RouteAlphabet alphabet = FilterProvider.alphabetForActions(
			comRemove1, comRemove2,
			new ComAdd(comValueOther1), new ComAdd(comValueOther2));
		FilterAutomaton automaton = filter.automaton(alphabet);
		
		//destList
		List<LabelPair> destList = new ArrayList<LabelPair>();
		destList.add(ActionAlphabet.DESTDEST);

		//pathList
		List<LabelPair> pathList = new ArrayList<LabelPair>();
		pathList.add(ActionAlphabet.PATHPATH);
		
		//prefval
		LabelPair prefVal = ActionAlphabet.integerinteger(
			Filter.DEFAULT_LOCAL_PREF, Filter.DEFAULT_LOCAL_PREF);
				
		//accept val
		LabelPair acceptVal = ActionAlphabet.MODMOD;

		{
		//comList
		List<LabelPair> comList = new ArrayList<LabelPair>();
		comList.add(
			ActionAlphabet.integerinteger(comValueRemove1, comValueOther1));
		comList.add(
			ActionAlphabet.integerinteger(comValueOther1, comValueOther2));
		comList.add(ActionAlphabet.integercom(comValueOther2));
		comList.add(ActionAlphabet.integerdiamond(comValueRemove2));
		comList.add(ActionAlphabet.COMDIAMOND);
		ITerm<LabelPair> tree = RoutingTree.getRoutingTree(
			destList, pathList, prefVal, comList, acceptVal,
			new ActionAlphabet(alphabet));
			
		assertTrue("The composition automaton should accept the routing tree.",
				automaton.accepts(tree));
		}
		{	
		//comList2
		List<LabelPair> comList2 = new ArrayList<LabelPair>();
		comList2.add(
			ActionAlphabet.integerinteger(comValueOther1, comValueOther1));
		comList2.add(
			ActionAlphabet.integerinteger(comValueOther2, comValueOther2));
		comList2.add(ActionAlphabet.integercom(comValueRemove2));
		comList2.add(ActionAlphabet.COMDIAMOND);

		ITerm<LabelPair> tree2 = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList2, acceptVal,
				new ActionAlphabet(alphabet));
			
		assertTrue("The composition automaton should accept the routing tree.",
				automaton.accepts(tree2));
		}
		{
		//comList3
		List<LabelPair> comList3 = new ArrayList<LabelPair>();
		comList3.add(
			ActionAlphabet.integerinteger(comValueOther1, comValueOther1));
		comList3.add(ActionAlphabet.integercom(comValueOther2));
		comList3.add(ActionAlphabet.integerdiamond(comValueRemove2));
		comList3.add(ActionAlphabet.COMDIAMOND);

		ITerm<LabelPair> tree3 = RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList3, acceptVal,
				new ActionAlphabet(alphabet));
			
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree3));
		}
	}
}
