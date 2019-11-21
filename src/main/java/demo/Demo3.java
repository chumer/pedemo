package demo;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;

import demo.Demo3Factory.AbsNodeGen;

/**
 * Same as Demo2 but now uses Truffle DSL for Abs.
 */
public class Demo3 {

    abstract static class Expression extends Node {

        abstract int execute(VirtualFrame frame);
        
    }
   
    @NodeChild("operand")
    static abstract class Abs extends Expression {
        
        @CompilationFinal boolean seenNegative;
        
        @Specialization(guards = "operand >= 0")
        int doPositive(int operand) {
            return operand;
        }
        
        @Specialization(guards = "operand < 0")
        int doNegative(int operand) {
            return -operand;
        }
        
    }

    static class Arg extends Expression {

        final int index;

        Arg(int index) {
            this.index = index;
        }

        @Override
        int execute(VirtualFrame frame) {
            return ((int[])frame.getArguments()[0])[index];
        }

    }

    static class Function extends RootNode {

        @Child private Expression body;

        Function(Expression body) {
            super(null);
            this.body = body;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            return body.execute(frame);
        }
    } 

    public static void main(String[] args) {
        // Sample Program: abs(args[0])
        Function sample = new Function(AbsNodeGen.create(new Arg(0)));
        CallTarget target = Truffle.getRuntime().createCallTarget(sample);
        for (int i = 0; i < 1000; i++) {
            target.call(new int[] { 10 });
        }
        for (int i = 0; i < 1000; i++) {
            target.call(new int[] { -10 });
        }
        System.out.println("done");
    }

}
