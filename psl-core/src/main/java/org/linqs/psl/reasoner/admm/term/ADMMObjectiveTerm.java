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

import org.linqs.psl.reasoner.term.Term;

import java.util.List;

// TODO(eriq): In general, we have swapped from [] to List<>. Ponder this some more.

/**
 * A term in the objective to be optimized by an {@link ADMMReasoner}.
 */
public abstract class ADMMObjectiveTerm implements Term {
	protected final List<LocalVariable> variables;

	/**
	 * Caller releases control of |variables|.
	 */
	public ADMMObjectiveTerm(List<LocalVariable> variables) {
		this.variables = variables;
	}
	
	/**
	 * Updates x to the solution of <br />
	 * argmin f(x) + stepSize / 2 * \|x - z + y / stepSize \|_2^2 <br />
	 * for the objective term f(x)
	 */
	public abstract void minimize(double stepSize, double[] consensusValues);
	
	public void updateLagrange(double stepSize, double[] consensusValues) {
		for (LocalVariable variable : variables) {
			variable.setLagrange(variable.getLagrange() + stepSize * (variable.getValue() - consensusValues[variable.getGlobalId()]));
		}
	}
}
