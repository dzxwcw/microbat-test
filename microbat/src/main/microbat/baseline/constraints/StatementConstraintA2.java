package microbat.baseline.constraints;

import microbat.model.trace.TraceNode;

/**
 * Statement constraint A2
 * If the control dominator is correct, but at least one write variable and read variable is wrong
 * at the same time, then the trace node is still likely to be correct
 * 
 * Condition: The given node should have both read and written variable
 * @author David
 *
 */
public class StatementConstraintA2 extends StatementConstraint {
	
	private static int count = 0;
	
	public StatementConstraintA2(TraceNode node, double propProbability) {
		super(node, propProbability, StatementConstraintA2.genID(), StatementConstraintA2.getWriteStartIndex(node));
		if (Constraint.countReadVars(node)==0 || Constraint.countWrittenVars(node) == 0) {
			throw new WrongConstraintConditionException("Node: " + node.getOrder() + " do not have both read and written variable. Cannot construct Statement Constraint A2");
		}
		this.setVarsID(node);
	}
	
//	public StatementConstraintA2(BitRepresentation varsIncluded, int conclusionIndex, double propProbability,
//			int writeVarStarintIdx, int statementOrder) {
//		super(varsIncluded, conclusionIndex, propProbability, writeVarStarintIdx, StatementConstraintA2.genID(), statementOrder);
//	}
//	
//	public StatementConstraintA2(BitRepresentation varsIncluded, int conclusionIndex, double propProbability,
//			int writeVarStarintIdx, int statementOrder, String controlDomID) {
//		super(varsIncluded, conclusionIndex, propProbability, writeVarStarintIdx, StatementConstraintA2.genID(), statementOrder, controlDomID);
//	}
	
	@Override
	protected double calProbability(int caseNo) {
		
		BitRepresentation binValue = this.filter(caseNo);
		double prob = -1;
		
		for (int conclusionIndex : this.conclusionIndexes) {

			boolean haveWrongWriteVar = this.checkWrongWriteVars(caseNo);
			boolean haveWrongReadVar = this.checkWrongReadVars(caseNo);
			
			/*
			 * For A2, the invalid case is that, when the control dominator is correct and 
			 * there are write variable is wrong and at least one of the read variable is wrong, the statement is wrong. 
			 * All the other cases are valid.
			 * 
			 * If the control dominator is wrong, then we can directly say that this constraint is correct
			 */
			
			if (this.haveControlDom()) {
				boolean correctControlDom = binValue.get(this.predIdx);
				if (!correctControlDom) {
					prob = this.propProbability;
					return prob;
				}
			}
			
			if (haveWrongWriteVar && haveWrongReadVar) {
				if (binValue.get(conclusionIndex)) {
					prob = this.propProbability;
				} else {
					prob = 1 - this.propProbability;
				}
			} else {
				prob = this.propProbability;
			}		
		}
		return prob;
	}
	
	public static int getWriteStartIndex(TraceNode node) {
		final int readLen = Constraint.countReadVars(node);
		return readLen;
	}
	
	@Override
	public String toString() {
		return "Stat constraint A2 " + super.toString();
	}
	
	private static String genID() {
		return "SC2_" + StatementConstraintA2.count++;
	}
	
	public static void resetID() {
		StatementConstraintA2.count = 0;
	}
}
