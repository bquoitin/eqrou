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

package be.ac.umons.info.routing.automata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import traul.ranked.nta.labels.Alphabet;

/**
 * Alphabet of actions, made of pairs (a,b) of simple labels.
 */
public class ActionAlphabet extends Alphabet<LabelPair>{
	
	private final RouteAlphabet routeAlphabet;

	public static final LabelPair DESTDEST = 
		new LabelPair(RouteAlphabet.DEST, RouteAlphabet.DEST);
	public static final LabelPair PATHPATH = 
		new LabelPair(RouteAlphabet.PATH, RouteAlphabet.PATH);
	public static final LabelPair PATHPATH1 = 
		new UnaryVersionLabelPair(PATHPATH);
	public static final LabelPair DIAMONDPATH = 
		new LabelPair(RouteAlphabet.DIAMOND, RouteAlphabet.PATH);
	public static final LabelPair DIAMONDPATH1 = 
		new UnaryVersionLabelPair(DIAMONDPATH);
	public static final LabelPair PREFPREF = 
		new LabelPair(RouteAlphabet.PREF, RouteAlphabet.PREF);
	public static final LabelPair COMCOM = 
		new LabelPair(RouteAlphabet.COM, RouteAlphabet.COM);
	public static final LabelPair COMCOM1 = 
		new UnaryVersionLabelPair(COMCOM);
	public static final LabelPair COMDIAMOND = 
		new LabelPair(RouteAlphabet.COM, RouteAlphabet.DIAMOND);
	public static final LabelPair COMDIAMOND1 = 
		new UnaryVersionLabelPair(COMDIAMOND);
	public static final LabelPair DIAMONDCOM = 
		new LabelPair(RouteAlphabet.DIAMOND, RouteAlphabet.COM);
	public static final LabelPair DIAMONDCOM1 = 
		new UnaryVersionLabelPair(DIAMONDCOM);
	public static final LabelPair ACCACC = 
		new LabelPair(RouteAlphabet.ACCEPTED, RouteAlphabet.ACCEPTED);
	public static final LabelPair REJREJ = 
		new LabelPair(RouteAlphabet.REJECTED, RouteAlphabet.REJECTED);
	public static final LabelPair MODMOD = 
		new LabelPair(RouteAlphabet.MODIFIED, RouteAlphabet.MODIFIED);
	public static final LabelPair MODACC = 
		new LabelPair(RouteAlphabet.MODIFIED, RouteAlphabet.ACCEPTED);
	public static final LabelPair ACCMOD = 
			new LabelPair(RouteAlphabet.ACCEPTED, RouteAlphabet.MODIFIED);
	public static final LabelPair MODREJ = 
		new LabelPair(RouteAlphabet.MODIFIED, RouteAlphabet.REJECTED);
	public static final LabelPair DIAMONDDIAMOND = 
		new LabelPair(RouteAlphabet.DIAMOND, RouteAlphabet.DIAMOND);
	public static final LabelPair DIAMONDDIAMOND1 = 
		new UnaryVersionLabelPair(DIAMONDDIAMOND);
	public static final LabelPair RR = 
		new LabelPair(RouteAlphabet.R, RouteAlphabet.R);
	public static final LabelPair R_RBAR = 
		new LabelPair(RouteAlphabet.R, RouteAlphabet.R_BAR);
	public static final LabelPair RBAR_R = 
		new LabelPair(RouteAlphabet.R_BAR, RouteAlphabet.R);
	
	/**
	 * Constructor parameterized by a RouteAlphabet.
	 */
	public ActionAlphabet(final RouteAlphabet routeAlphabet) {
		super(ActionAlphabet.actionSymbols(routeAlphabet, false));
		this.routeAlphabet = routeAlphabet;
	}
	
	/**
	 * Constructor parameterized by a RouteAlphabet.
	 */
	public ActionAlphabet(final RouteAlphabet routeAlphabet, 
		final boolean withAccMod) {
		super(ActionAlphabet.actionSymbols(routeAlphabet, withAccMod));
		this.routeAlphabet = routeAlphabet;
	}
	
	/**
	 * The RouteAlphabet from which this ActionAlphabet is built.
	 */
	public RouteAlphabet routeAlphabet() {
		return this.routeAlphabet;
	}
	
	private static Map<LabelPair,Integer> actionSymbols(
		final RouteAlphabet routeAlphabet,
		final boolean withAccMod) {
		Map<LabelPair,Integer> actionSymbols = new HashMap<LabelPair,Integer>();

		// root
		actionSymbols.put(RR, 5);
		actionSymbols.put(R_RBAR, 5);
		actionSymbols.put(RBAR_R, 5);

		// dest branch
		actionSymbols.put(DESTDEST, 0);
		for (IntegerLabel label : routeAlphabet.destAlphabet()) {
			final int i = ((IntegerLabel)label).intValue();
			actionSymbols.put(integerinteger(i, i), 1);
		}
		// path branch
		actionSymbols.put(PATHPATH, 0);
		actionSymbols.put(PATHPATH1, 1);
		actionSymbols.put(DIAMONDPATH, 0);
		actionSymbols.put(DIAMONDPATH1, 1);
		for (int i : routeAlphabet.asPathAlphabetInt()) {
			actionSymbols.put(pathinteger(i), 1);
			actionSymbols.put(diamondinteger(i), 1);
			for (int j : routeAlphabet.asPathAlphabetInt()) {
				actionSymbols.put(integerinteger(i,j), 1);
			}
		}
		// pref branch
		actionSymbols.put(PREFPREF, 0);
		for (IntegerLabel label : routeAlphabet.prefAlphabet()) {
			final int i = ((IntegerLabel)label).intValue();
			for (IntegerLabel labelj : routeAlphabet.prefAlphabet()) {
				final int j = ((IntegerLabel)labelj).intValue();
				actionSymbols.put(integerinteger(i, j), 1);
			}
		}
		// com branch
		actionSymbols.put(COMCOM, 0);
		actionSymbols.put(COMCOM1, 1);
		actionSymbols.put(COMDIAMOND, 0);
		actionSymbols.put(COMDIAMOND1, 1);
		actionSymbols.put(DIAMONDCOM, 0);
		actionSymbols.put(DIAMONDCOM1, 1);		
		for (IntegerLabel label : routeAlphabet.comAlphabet()) {
			final int i = ((IntegerLabel)label).intValue();
			actionSymbols.put(cominteger(i), 1);
			actionSymbols.put(integercom(i), 1);
			actionSymbols.put(diamondinteger(i), 1);
			actionSymbols.put(integerdiamond(i), 1);
			for (IntegerLabel labelj : routeAlphabet.comAlphabet()) {
				final int j = ((IntegerLabel)labelj).intValue();
				actionSymbols.put(integerinteger(i, j), 1);
			}
		}
		// mod branch
		actionSymbols.put(MODMOD, 0);
		actionSymbols.put(MODACC, 0);
		actionSymbols.put(MODREJ, 0);
		actionSymbols.put(ACCACC, 0);
		actionSymbols.put(REJREJ, 0);
		if (withAccMod) {
			actionSymbols.put(ACCMOD, 0);
		}
		
		// diamonds
		actionSymbols.put(DIAMONDDIAMOND, 0);
		actionSymbols.put(DIAMONDDIAMOND1, 1);
		
		return actionSymbols;
	}
	
	/**
	 * Returns all labels of a given arity
	 */
	public Set<LabelPair> getLabelsOfArity(final int arity) {
		Set<LabelPair> setLabel = new HashSet<LabelPair>();
		Set<LabelPair> symbols = getSymbols().keySet();
		for (LabelPair symbol : symbols) {
			if (arity(symbol)==arity) {
				setLabel.add(symbol);
			}
		}
		return setLabel;
	}
	
	/**
	 * Returns label (COM,i)
	 */
	public static LabelPair cominteger(final int i){
		return new LabelPair(RouteAlphabet.COM, RouteAlphabet.integer(i));
	}
	
	/**
	 * Returns label (i,COM)
	 */
	public static LabelPair integercom(final int i){
		return new LabelPair(RouteAlphabet.integer(i), RouteAlphabet.COM);
	}
	
	/**
	 * Returns label (PATH,i)
	 */
	public static LabelPair pathinteger(final int i){
		return new LabelPair(RouteAlphabet.PATH, RouteAlphabet.integer(i));
	}
		
	/**
	 * Returns label (DIAMOND,i)
	 */
	public static LabelPair diamondinteger(final int i){
		return new LabelPair(RouteAlphabet.DIAMOND, RouteAlphabet.integer(i));
	}	
	
	/**
	 * Returns label (i,DIAMOND)
	 */
	public static LabelPair integerdiamond(final int i){
		return new LabelPair(RouteAlphabet.integer(i), RouteAlphabet.DIAMOND);
	}
	
	/**
	 * Returns label (i,j)
	 */
	public static LabelPair integerinteger(final int i, final int j){
		return new LabelPair(
				RouteAlphabet.integer(i), RouteAlphabet.integer(j));
	}
	
	/**
	 * Returns all labels that can be used at the root of valid routes.
	 */
	public static Set<LabelPair> rootLabels() {
		Set<LabelPair> rootLabels = new HashSet<LabelPair>();
		rootLabels.add(RR);
		rootLabels.add(RBAR_R);
		rootLabels.add(R_RBAR);
		return rootLabels;
	}
}
