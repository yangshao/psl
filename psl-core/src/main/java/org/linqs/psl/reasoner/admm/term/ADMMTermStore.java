/*
 * This file is part of the PSL software.
 * Copyright 2011-2015 University of Maryland
 * Copyright 2013-2015 The Regents of the University of California
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.linqs.psl.reasoner.admm.term;

import org.linqs.psl.reasoner.function.AtomFunctionVariable;
import org.linqs.psl.reasoner.term.MemoryTermStore;
import org.linqs.psl.reasoner.term.TermStore;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A TermStore specifically for ADMM terms.
 * The actually terms will just be held in a standard TermStore (a MemoryTermStore if not specified).
 * This class will focus on keeping track of the variables in the terms.
 */
public class ADMMTermStore implements TermStore<ADMMObjectiveTerm> {
	// Keep an internal store to hold the terms while this class focus on variables.
	private TermStore<ADMMObjectiveTerm> store;

	private Map<AtomFunctionVariable, Integer> variableIndexes;
	private List<List<LocalVariable>> localVariables;

	/**
	 * The total number of all local variables (the sum of the sizes of each listin |localVariables|.
	 */
	private int numLocalVariables;

	public ADMMTermStore() {
		store = new MemoryTermStore<ADMMObjectiveTerm>();
		variableIndexes = new HashMap<AtomFunctionVariable, Integer>();
		localVariables = new ArrayList<List<LocalVariable>>();
		numLocalVariables = 0;
	}

	public ADMMTermStore(TermStore<ADMMObjectiveTerm> store) {
		this.store = store;
	}

	/**
	 * Create a local variable and ensure that a global copy is registered.
	 */
	public LocalVariable createLocalVariable(AtomFunctionVariable atomVariable) {
		numLocalVariables++;

		int globalId;
		// Check if the global copy has already been registered.
		if (variableIndexes.containsKey(atomVariable)) {
			globalId = variableIndexes.get(atomVariable).intValue();
		} else {
			// If the global copy has not been registered, register it and prep it's local copies.
			globalId = variableIndexes.size();
			variableIndexes.put(atomVariable, globalId);
			localVariables.add(new ArrayList<LocalVariable>());
		}

		LocalVariable localVariable = new LocalVariable(globalId, atomVariable.getValue());
		localVariables.get(globalId).add(localVariable);

		return localVariable;
	}

	public int getNumLocalVariables() {
		return numLocalVariables;
	}

	public int getNumGlobalVariables() {
		return variableIndexes.size();
	}

	public List<LocalVariable> getLocalVariables(int globalId) {
		return localVariables.get(globalId);
	}

	/**
	 * Update the global variables (AtomFunctionVariables).
	 * The passed in values in indexed according to global id.
	 */
	public void updateVariables(double[] values) {
		for (Map.Entry<AtomFunctionVariable, Integer> entry : variableIndexes.entrySet()) {
			entry.getKey().setValue(values[entry.getValue().intValue()]);
		}
	}

	@Override
	public void add(ADMMObjectiveTerm term) {
		store.add(term);
	}

	@Override
	public void close() {
		store.close();
		store = null;

		variableIndexes.clear();
		variableIndexes = null;

		localVariables.clear();
		localVariables = null;
	}

	@Override
	public ADMMObjectiveTerm get(int index) {
		return store.get(index);
	}

	@Override
	public int size() {
		return store.size();
	}

	@Override
	public Iterator<ADMMObjectiveTerm> iterator() {
		return store.iterator();
	}
}
