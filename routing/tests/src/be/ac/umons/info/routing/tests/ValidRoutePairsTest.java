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

import be.ac.umons.info.routing.automata.ActionAlphabet;
import be.ac.umons.info.routing.automata.FilterAutomaton;
import be.ac.umons.info.routing.automata.IntegerLabel;
import be.ac.umons.info.routing.automata.LabelPair;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import be.ac.umons.info.routing.automata.ValidRoutePairs;
import traul.ranked.terms.ITerm;
import junit.framework.TestCase;

/**
 * Unit test for {@link ValidRoutePairs.routePairs} method.
 */
public class ValidRoutePairsTest extends TestCase {

	public void testNotIdentity() {
		// route
		final List<LabelPair> destList = convertToLabelPairList(
			ActionAlphabet.integerinteger(0, 0),
			ActionAlphabet.integerinteger(1, 1),
			ActionAlphabet.DESTDEST);
		final List<LabelPair> pathList = convertToLabelPairList(
			ActionAlphabet.integerinteger(20, 10),
			ActionAlphabet.pathinteger(20),
			ActionAlphabet.diamondinteger(10),
			ActionAlphabet.DIAMONDPATH);
		final LabelPair prefValue = ActionAlphabet.integerinteger(10, 20);
		final List<LabelPair> comList = convertToLabelPairList(
				ActionAlphabet.integerinteger(40, 10),
				ActionAlphabet.cominteger(20),
				ActionAlphabet.diamondinteger(30),
				ActionAlphabet.DIAMONDCOM);
		final LabelPair modValue = ActionAlphabet.ACCACC;
		final RouteAlphabet alphabet = 
			alphabetFromTerm(destList, pathList, prefValue,	comList);
		final ITerm<LabelPair> routePair =
			RoutingTree.getRoutingTree(
				destList, pathList, prefValue, comList, modValue,
				new ActionAlphabet(alphabet));
		// automaton
		final FilterAutomaton onlyRoutes = 
			ValidRoutePairs.routePairs(alphabet);
		onlyRoutes.checkIntegrity();
		// test
		assertFalse(
				"This pair (t,t') should not be accepted: t differs from t'.",
				onlyRoutes.accepts(routePair));
	}

	public void testDiamondDiamond() {
		// route
		final List<LabelPair> destList = convertToLabelPairList(
				ActionAlphabet.integerinteger(0, 0),
				ActionAlphabet.integerinteger(1, 1),
				ActionAlphabet.DESTDEST);
		final List<LabelPair> pathList = convertToLabelPairList(
			ActionAlphabet.integerinteger(20, 20),
			ActionAlphabet.integerinteger(10, 10),
			ActionAlphabet.PATHPATH1,
			ActionAlphabet.DIAMONDDIAMOND);
		final LabelPair prefValue = ActionAlphabet.integerinteger(10, 10);
		final List<LabelPair> comList = convertToLabelPairList(
				ActionAlphabet.integerinteger(10, 10),
				ActionAlphabet.COMCOM);
		final LabelPair modValue = ActionAlphabet.ACCACC;
		final RouteAlphabet alphabet = 
			alphabetFromTerm(destList, pathList, prefValue,	comList);
		final ITerm<LabelPair> routePair =
			RoutingTree.getRoutingTree(
				destList, pathList, prefValue, comList, modValue,
				new ActionAlphabet(alphabet));
		// automaton
		final FilterAutomaton onlyRoutes = 
			ValidRoutePairs.routePairs(alphabet);
		// test
		assertFalse(
				"This pair (t,t) should not be accepted as it contains a leaf"
				+ " (diamond,diamond)",
				onlyRoutes.accepts(routePair));		
	}
	
	public void testMixingASandCOM() {
		// route
		final List<LabelPair> destList = convertToLabelPairList(
				ActionAlphabet.integerinteger(0, 0),
				ActionAlphabet.integerinteger(1, 1),
				ActionAlphabet.DESTDEST);
		final List<LabelPair> pathList = convertToLabelPairList(
			ActionAlphabet.integerinteger(20, 20),
			ActionAlphabet.integerinteger(10, 10),
			ActionAlphabet.PATHPATH);
		final LabelPair prefValue = ActionAlphabet.integerinteger(10, 10);
		final List<LabelPair> comList = convertToLabelPairList(
				ActionAlphabet.integerinteger(10, 10),
				ActionAlphabet.integerinteger(20, 20),
				ActionAlphabet.integerinteger(30, 30),
				ActionAlphabet.COMCOM);
		final LabelPair modValue = ActionAlphabet.ACCACC;
		final RouteAlphabet alphabet = 
			alphabetFromTerm(destList, pathList, prefValue,	comList);
		final ITerm<LabelPair> routePair =
			RoutingTree.getRoutingTree(
				destList, comList, prefValue, pathList, modValue,
				new ActionAlphabet(alphabet));
		// automaton
		final FilterAutomaton onlyRoutes = 
			ValidRoutePairs.routePairs(alphabet);
		// test
		assertFalse(
				"This pair (t,t) should not be accepted: branches AS and COM"
				+" are swapped.",
				onlyRoutes.accepts(routePair));		
	}

	public void testEmptyRoute() {
		// route
		final List<LabelPair> destList = convertToLabelPairList(
				ActionAlphabet.integerinteger(0, 0),
				ActionAlphabet.integerinteger(1, 1),
				ActionAlphabet.DESTDEST);
		final List<LabelPair> pathList = convertToLabelPairList(
			ActionAlphabet.PATHPATH);
		final LabelPair prefValue = ActionAlphabet.integerinteger(10, 10);
		final List<LabelPair> comList = convertToLabelPairList(
				ActionAlphabet.COMCOM);
		final LabelPair modValue = ActionAlphabet.ACCACC;
		final RouteAlphabet alphabet = 
			alphabetFromTerm(destList, pathList, prefValue,	comList);
		final ITerm<LabelPair> routePair =
			RoutingTree.getRoutingTree(
				destList, pathList, prefValue, comList, modValue,
				new ActionAlphabet(alphabet));
		// automaton
		final FilterAutomaton onlyRoutes = 
			ValidRoutePairs.routePairs(alphabet);
		// test
		assertTrue("This pair (t,t) should be accepted, as t is empty.",
				onlyRoutes.accepts(routePair));		
	}

	public void testValidRoute() {
		// route
		final List<LabelPair> destList = convertToLabelPairList(
				ActionAlphabet.integerinteger(0, 0),
				ActionAlphabet.integerinteger(1, 1),
				ActionAlphabet.DESTDEST);
		final List<LabelPair> pathList = convertToLabelPairList(
			ActionAlphabet.integerinteger(20, 20),
			ActionAlphabet.integerinteger(10, 10),
			ActionAlphabet.PATHPATH);
		final LabelPair prefValue = ActionAlphabet.integerinteger(10, 10);
		final List<LabelPair> comList = convertToLabelPairList(
				ActionAlphabet.integerinteger(10, 10),
				ActionAlphabet.integerinteger(20, 20),
				ActionAlphabet.integerinteger(30, 30),
				ActionAlphabet.COMCOM);
		final LabelPair modValue = ActionAlphabet.ACCACC;
		final RouteAlphabet alphabet = 
			alphabetFromTerm(destList, pathList, prefValue,	comList);
		final ITerm<LabelPair> routePair =
			RoutingTree.getRoutingTree(
				destList, pathList, prefValue, comList, modValue,
				new ActionAlphabet(alphabet));
		// automaton
		final FilterAutomaton onlyRoutes = 
			ValidRoutePairs.routePairs(alphabet);
		// test
		assertTrue("This pair (t,t) should be accepted.",
				onlyRoutes.accepts(routePair));
	}
	
	public void testUnorderedCOM() {
		// route
		final List<LabelPair> destList = convertToLabelPairList(
				ActionAlphabet.integerinteger(0, 0),
				ActionAlphabet.integerinteger(1, 1),
				ActionAlphabet.DESTDEST);
		final List<LabelPair> pathList = convertToLabelPairList(
			ActionAlphabet.integerinteger(20, 20),
			ActionAlphabet.integerinteger(10, 10),
			ActionAlphabet.PATHPATH);
		final LabelPair prefValue = ActionAlphabet.integerinteger(10, 10);
		final List<LabelPair> comList = convertToLabelPairList(
				ActionAlphabet.integerinteger(20, 20),
				ActionAlphabet.integerinteger(10, 10),
				ActionAlphabet.integerinteger(30, 30),
				ActionAlphabet.COMCOM);
		final LabelPair modValue = ActionAlphabet.ACCACC;
		final RouteAlphabet alphabet = 
			alphabetFromTerm(destList, pathList, prefValue,	comList);
		final ITerm<LabelPair> routePair =
			RoutingTree.getRoutingTree(
				destList, pathList, prefValue, comList, modValue,
				new ActionAlphabet(alphabet));
		// automaton
		final FilterAutomaton onlyRoutes = 
			ValidRoutePairs.routePairs(alphabet);
		// test
		assertFalse("This pair (t,t) should not be accepted: branch COM is"
				+ " unordered.",
				onlyRoutes.accepts(routePair));		
	}
	
	private List<LabelPair> convertToLabelPairList(LabelPair... labelPairs) {
		List<LabelPair> labelList = new ArrayList<LabelPair>();
		for (LabelPair labelPair : labelPairs) {
			labelList.add(labelPair);
		}
		return labelList;
	}	

	/**
	 * Returns the alphabet used in branches of a term.
	 */
	private RouteAlphabet alphabetFromTerm(
			final List<LabelPair> destList,
			final List<LabelPair> pathList,
			final LabelPair prefValue,
			final List<LabelPair> comList) {
		return new RouteAlphabet(
				intLabelsFromBranch(destList), 
				intLabelsFromBranch(pathList), 
				intLabelsFromBranch(Collections.singletonList(prefValue)),
				intLabelsFromBranch(comList));
	}

	/**
	 * Returns the alphabet used in a branch.
	 */
	private Set<IntegerLabel> intLabelsFromBranch(final List<LabelPair> branch) {
		Set<IntegerLabel> intLabels = new HashSet<IntegerLabel>();
		for (LabelPair labelPair : branch) {
			if (labelPair.left() instanceof IntegerLabel) {
				intLabels.add((IntegerLabel)labelPair.left());
			}
			if (labelPair.right() instanceof IntegerLabel) {
				intLabels.add((IntegerLabel)labelPair.right());
			}
		}
		return intLabels;
	}
}
