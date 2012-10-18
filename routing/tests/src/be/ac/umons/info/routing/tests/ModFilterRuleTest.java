package be.ac.umons.info.routing.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.ac.umons.info.routing.Filter;
import be.ac.umons.info.routing.IFilterRule;
import be.ac.umons.info.routing.ModFilterRule;
import be.ac.umons.info.routing.automata.ActionAlphabet;
import be.ac.umons.info.routing.automata.FilterAutomaton;
import be.ac.umons.info.routing.automata.LabelPair;
import be.ac.umons.info.routing.automata.RouteAlphabet;
import traul.ranked.terms.ITerm;
import junit.framework.TestCase;

public class ModFilterRuleTest extends TestCase {

	public void testModAcc() {

		IFilterRule modRule = new ModFilterRule();

		final Filter filter = new Filter(Collections.singletonList(modRule));
		RouteAlphabet alphabet = filter.filterAlphabet();
		FilterAutomaton automaton = modRule.automaton(alphabet);
		
		ITerm<LabelPair> tree = 
			emptyTreeWithModBranch(ActionAlphabet.MODACC, alphabet);
		
		assertFalse("The automaton should not accept the routing tree.",
			automaton.accepts(tree));
	}

	public void testAccMod() {

		IFilterRule modRule = new ModFilterRule();

		final Filter filter = new Filter(Collections.singletonList(modRule));
		RouteAlphabet alphabet = filter.filterAlphabet();
		FilterAutomaton automaton = modRule.automaton(alphabet);
		
		ITerm<LabelPair> tree = 
			emptyTreeWithModBranch(ActionAlphabet.ACCMOD, alphabet);
		
		assertTrue("The automaton should accept the routing tree.",
			automaton.accepts(tree));
	}

	public void testAccAcc() {

		IFilterRule modRule = new ModFilterRule();

		final Filter filter = new Filter(Collections.singletonList(modRule));
		RouteAlphabet alphabet = filter.filterAlphabet();
		FilterAutomaton automaton = modRule.automaton(alphabet);
		
		ITerm<LabelPair> tree = 
			emptyTreeWithModBranch(ActionAlphabet.ACCACC, alphabet);
		
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree));
	}


	public void testRejRej() {

		IFilterRule modRule = new ModFilterRule();

		final Filter filter = new Filter(Collections.singletonList(modRule));
		RouteAlphabet alphabet = filter.filterAlphabet();
		FilterAutomaton automaton = modRule.automaton(alphabet);
		
		ITerm<LabelPair> tree = 
			emptyTreeWithModBranch(ActionAlphabet.REJREJ, alphabet);
		
		assertTrue("The automaton should accept the routing tree.",
				automaton.accepts(tree));
	}

	public void testModRej() {

		IFilterRule modRule = new ModFilterRule();

		final Filter filter = new Filter(Collections.singletonList(modRule));
		RouteAlphabet alphabet = filter.filterAlphabet();
		FilterAutomaton automaton = modRule.automaton(alphabet);
		
		ITerm<LabelPair> tree = 
			emptyTreeWithModBranch(ActionAlphabet.MODREJ, alphabet);
		
		assertFalse("The automaton should not accept the routing tree.",
				automaton.accepts(tree));
	}


	/**
	 * Builds a tree with empty branches except for the mod branch, 
	 * set to acceptVal.
	 */
	private ITerm<LabelPair> emptyTreeWithModBranch(
			final LabelPair acceptVal, final RouteAlphabet alphabet) {

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
		comList.add(ActionAlphabet.COMCOM);
		
		return RoutingTree.getRoutingTree(
				destList, pathList, prefVal, comList, acceptVal,
				new ActionAlphabet(alphabet, true));
	}
	
}
