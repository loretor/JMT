package jmt.jmva.analytical.solvers.linearSystem.btf.macroBlocks;

import jmt.jmva.analytical.solvers.basis.CoMoMBasis;
import jmt.jmva.analytical.solvers.dataStructures.QNModel;
import jmt.jmva.analytical.solvers.exceptions.InconsistentLinearSystemException;
import jmt.jmva.analytical.solvers.exceptions.InternalErrorException;
import jmt.jmva.analytical.solvers.linearSystem.btf.Position;
import jmt.jmva.analytical.solvers.linearSystem.btf.microBlocks.B2MicroBlock;
import jmt.jmva.analytical.solvers.linearSystem.btf.microBlocks.MicroBlock;

/**
 * This class implements the Macro Blocks of the B2 Block of the B matrix.
 * 
 * The Factory Methods newMicroBlock and subBlockCopy are implemented to return the correct 
 * block type in the parallel hierarchy.
 * 
 * Type 1 Micro Blocks are selected for lower class linear systems
 * 
 * @author Jack Bradshaw
 */
public class B2MacroBlock extends MacroBlock {

	public B2MacroBlock(QNModel qnm, CoMoMBasis basis, Position position, int h) throws InternalErrorException, InconsistentLinearSystemException {
		super(qnm, basis, position, h);	
		selection_policy = new TypeOneBlocks(qnm, this); 
	}

	public B2MacroBlock(MacroBlock full_block, int current_class) {
		super(full_block, current_class);
	}
	
	@Override
	protected MacroBlock subBlockCopy(int current_class) {
		return new B2MacroBlock(this, current_class);
	}

	@Override
	protected MicroBlock newMicroBlock(Position block_position, int h) {
		return new B2MicroBlock(qnm, basis, block_position, h);
	}

	public void update(int current_class_population) {
		for (int i = 0; i < micro_blocks.length; i++) {
			((B2MicroBlock) micro_blocks[i]).update(current_class_population);
		}		
	}
}
