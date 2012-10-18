package be.ac.umons.info.routing.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import traul.ranked.nta.labels.ILabel;
import traul.ranked.terms.ITerm;
import traul.ranked.terms.Term;
import be.ac.umons.info.routing.automata.RouteAlphabet;

/**
 * Builds a tree over RouteAlphabet, from its branches.
 */
public class PredicateTree {
	
	/**
	 * Builds the tree and returns it. If it uses a label not in the alphabet,
	 * it returns null.
	 */
	public static ITerm<ILabel> getPredicateTree(
			final List<ILabel> destList,
			final List<ILabel> pathList, final ILabel prefVal, 
			final List<ILabel> comList, final ILabel modif, 
			final RouteAlphabet alphabet) {	

		// dest branch
		final ITerm<ILabel> nodeDest = listToTerm(destList, alphabet);
		
		// AS branch
		final ITerm<ILabel> nodePath = listToTerm(pathList, alphabet);
		
		// Pref	branch
		List<ILabel> prefList = new ArrayList<ILabel>();
		prefList.add(prefVal);
		prefList.add(RouteAlphabet.PREF);
		final ITerm<ILabel> nodePref = listToTerm(prefList, alphabet);
		
		// Com branch
		final ITerm<ILabel> nodeCom = listToTerm(comList, alphabet);

		// Mod branch
		final ITerm<ILabel> nodeMod = 
			new Term<ILabel>(alphabet, null, modif);
		
		// root
		List<ITerm<ILabel>> childrenRoot = 
			new ArrayList<ITerm<ILabel>>();
		childrenRoot.add(nodeDest);
		childrenRoot.add(nodePath);
		childrenRoot.add(nodePref);
		childrenRoot.add(nodeCom);		
		childrenRoot.add(nodeMod);
		
		ITerm<ILabel> term = null;
		if (nodeDest!=null && nodePath!=null && nodePref!=null && nodeCom!=null
				&& nodeMod!=null) {
			term = new Term<ILabel>(alphabet, childrenRoot, RouteAlphabet.R);
		}
		return term;
	}

	/**
	 * Converts a list of labels to a term with unary labels and a leaf.
	 */
	private static ITerm<ILabel> listToTerm(final List<ILabel> list,
			final RouteAlphabet alphabet) {
		List<ITerm<ILabel>> children = new ArrayList<ITerm<ILabel>>();
		int i = list.size()-1;
		while (i>=0 && children != null) {
			final ITerm<ILabel> node = 
				new Term<ILabel>(alphabet, children, list.get(i));
			children = Collections.singletonList(node);
			i--;
		}
		return children.get(0);
	}
	
}
