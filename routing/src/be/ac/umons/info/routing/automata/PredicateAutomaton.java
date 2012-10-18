package be.ac.umons.info.routing.automata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import traul.ranked.nta.BinaryOperators;
import traul.ranked.nta.DTA;
import traul.ranked.nta.IBinaryOperators;
import traul.ranked.nta.IDTA;
import traul.ranked.nta.labels.ILabel;
import traul.ranked.nta.rules.BasicRule;
import traul.ranked.nta.rules.IRule;
import traul.ranked.nta.states.ComposedState;
import traul.ranked.nta.states.IState;

public class PredicateAutomaton extends DTA<ILabel, IState> {
	
	private final Set<IRule<ILabel, IState>> rulesDest;
	private final Set<IRule<ILabel, IState>> rulesPath;
	private final Set<IRule<ILabel, IState>> rulesPref;
	private final Set<IRule<ILabel, IState>> rulesCom;
	private final Set<IRule<ILabel, IState>> rulesMod;
	private final Set<IRule<ILabel, IState>> rulesRoot;
	private final RouteAlphabet routeAlphabet;

	/**
	 * Constructor
	 */
	public PredicateAutomaton(final RouteAlphabet alphabet, 
			final Set<IState> states,
			final Set<IState> finalStates,
			final Set<IRule<ILabel, IState>> rulesDest,
			final Set<IRule<ILabel, IState>> rulesPath,
			final Set<IRule<ILabel, IState>> rulesPref,
			final Set<IRule<ILabel, IState>> rulesCom, 
			final Set<IRule<ILabel, IState>> rulesMod,
			final Set<IRule<ILabel, IState>> rulesRoot, 
			final IState sinkState) {
		
		super(alphabet, states, finalStates, 
			unionOfBranchRules(rulesDest, rulesPath, rulesPref, rulesCom,
				rulesMod, rulesRoot), sinkState);
		this.routeAlphabet = alphabet;
		this.rulesDest = rulesDest;
		this.rulesPath = rulesPath;
		this.rulesPref = rulesPref;
		this.rulesCom = rulesCom;
		this.rulesMod = rulesMod;
		this.rulesRoot = rulesRoot;
	}
	
	private static Set<IRule<ILabel,IState>> unionOfBranchRules(
			final Set<IRule<ILabel, IState>> rulesDest, 
			final Set<IRule<ILabel, IState>> rulesPath, 
			final Set<IRule<ILabel, IState>> rulesPref,
			final Set<IRule<ILabel, IState>> rulesCom, 
			final Set<IRule<ILabel, IState>> rulesMod,
			final Set<IRule<ILabel, IState>> rulesRoot) {
		Set<IRule<ILabel,IState>> allRules = 
			new HashSet<IRule<ILabel,IState>>();
		allRules.addAll(rulesDest);
		allRules.addAll(rulesPath);
		allRules.addAll(rulesPref);
		allRules.addAll(rulesCom);
		allRules.addAll(rulesMod);
		allRules.addAll(rulesRoot);
		return allRules;
	}

	/**
	 * Computes the synchronized product between this PredicateAutomaton and
	 * another one. We use the standard product of tree automata, on every 
	 * branch.
	 * @param other the other automaton for the product
	 * @param intersection if true, computes the intersection, otherwise 
	 * compute the union.
	 */
	private PredicateAutomaton synchronizedProduct(
			final PredicateAutomaton other, boolean intersection) {
		
		// automata for branches
		final IDTA<ILabel,ComposedState<IState,IState>> destAut =
			syncBranchAutomaton(other, 
				this.getRulesDest(), other.getRulesDest());
		final IDTA<ILabel,ComposedState<IState,IState>> pathAut =
			syncBranchAutomaton(other, 
				this.getRulesPath(), other.getRulesPath());
		final IDTA<ILabel,ComposedState<IState,IState>> prefAut =
			syncBranchAutomaton(other, 
				this.getRulesPref(), other.getRulesPref());
		final IDTA<ILabel,ComposedState<IState,IState>> comAut =
			syncBranchAutomaton(other, 
				this.getRulesCom(), other.getRulesCom());
		final IDTA<ILabel,ComposedState<IState,IState>> modAut =
			syncBranchAutomaton(other, 
				this.getRulesMod(), other.getRulesMod());

		// root rules and final states
		final IBinaryOperators<ILabel, IState, IState> binOps =
			new BinaryOperators<ILabel, IState, IState>();
		final IDTA<ILabel,ComposedState<IState,IState>> rootAut;
		if (intersection) {
			rootAut = binOps.intersection(this, other);
		} else {
			rootAut = binOps.synchronizedUnion(this, other);
		}
		Set<IRule<ILabel,IState>> rootRules =
			new HashSet<IRule<ILabel,IState>>();
		for (IRule<ILabel,ComposedState<IState,IState>> rule : 
			rootAut.getRulesWithLabel(RouteAlphabet.R, false)) {
			rootRules.add(convertRuleToIState(rule));
		}
		// final states
		Set<IState> finalStates = new HashSet<IState>();
		for (ComposedState<IState,IState> cState : rootAut.getFinalStates()) {
			finalStates.add(cState);
		}
		// sink state
		IState sinkState = rootAut.sinkState();
		
		Set<IState> states = new HashSet<IState>();
		states.addAll(destAut.getStates());
		states.addAll(pathAut.getStates());
		states.addAll(prefAut.getStates());
		states.addAll(comAut.getStates());
		states.addAll(modAut.getStates());

		return new PredicateAutomaton(this.routeAlphabet(), states, 
			finalStates, 
			convertRulesToIState(destAut.getRules(false)), 
			convertRulesToIState(pathAut.getRules(false)), 
			convertRulesToIState(prefAut.getRules(false)),
			convertRulesToIState(comAut.getRules(false)), 
			convertRulesToIState(modAut.getRules(false)), rootRules, 
			sinkState);
	}
	
	private IDTA<ILabel,ComposedState<IState,IState>> syncBranchAutomaton(
		final PredicateAutomaton other,
		final Set<IRule<ILabel,IState>> branchRulesThis,
		final Set<IRule<ILabel,IState>> branchRulesOther) {
		
		final IBinaryOperators<ILabel, IState, IState> binOps =
			new BinaryOperators<ILabel, IState, IState>();
		final IDTA<ILabel,IState> branchAutThis = 
			new DTA<ILabel,IState>(this.getAlphabet(), this.getStates(), 
				this.getFinalStates(), branchRulesThis, this.sinkState());
		final IDTA<ILabel,IState> branchAutOther =
			new DTA<ILabel,IState>(other.getAlphabet(), other.getStates(), 
				other.getFinalStates(), branchRulesOther, other.sinkState());
		return binOps.intersection(branchAutThis, branchAutOther);
	}
	
	/**
	 * Computes the intersection between this PredicateAutomaton and another 
	 * one. We use the standard intersection of tree automata, on every branch.
	 */
	public PredicateAutomaton intersection(final PredicateAutomaton other) {
		return this.synchronizedProduct(other, true);
	}
		
	/**
	 * Computes the synchronized union between this PredicateAutomaton and 
	 * another one.
	 */
	public PredicateAutomaton syncUnion(final PredicateAutomaton other) {
		return this.synchronizedProduct(other, false);
	}
	
	/** 
	 * Type conversion.
	 */
	private Set<IRule<ILabel,IState>> convertRulesToIState(
			final Set<IRule<ILabel,ComposedState<IState,IState>>> rules) {
		Set<IRule<ILabel,IState>> newRules = 
			new HashSet<IRule<ILabel,IState>>();
		for (IRule<ILabel,ComposedState<IState,IState>> rule : rules) {
			newRules.add(convertRuleToIState(rule));
		}
		return newRules;
	}

	/** 
	 * Type conversion.
	 */
	private IRule<ILabel,IState> convertRuleToIState(
			final IRule<ILabel,ComposedState<IState,IState>> rule) {
		return new BasicRule<ILabel,IState>(
			convertLeftStatesToIState(rule.leftStates()), 
			rule.label(), rule.rightState());
	}
	
	/** 
	 * Type conversion.
	 */
	private List<IState> convertLeftStatesToIState(
			final List<ComposedState<IState,IState>> list){
		List<IState> newList = new ArrayList<IState>();
		for (IState state : list) {
			newList.add(state);
		}
		return newList;
	}
	
	public Set<IRule<ILabel, IState>> getRulesDest() {
		return this.rulesDest;
	}

	public Set<IRule<ILabel, IState>> getRulesPath() {
		return this.rulesPath;
	}

	public Set<IRule<ILabel, IState>> getRulesPref() {
		return this.rulesPref;
	}

	public Set<IRule<ILabel, IState>> getRulesCom() {
		return this.rulesCom;
	}

	public Set<IRule<ILabel, IState>> getRulesMod() {
		return this.rulesMod;
	}
	
	public Set<IRule<ILabel, IState>> getRulesRoot() {
		return this.rulesRoot;
	}

	public RouteAlphabet routeAlphabet() {
		return this.routeAlphabet;
	}
}
