package demo;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;

public class Demo2 {

    abstract static class Expression extends Node {

        abstract int execute(int[] arguments);

    }

    static class Abs extends Expression {

        @Child Expression operand;
        @CompilationFinal boolean seenNegative;

        Abs(Expression operand) {
            this.operand = operand;
        }

        @Override
        int execute(int[] arguments) {
            int value = operand.execute(arguments);
            if (value >= 0) {
                return value;
            } else {
                if (!seenNegative) {
                    CompilerDirectives.transferToInterpreterAndInvalidate();
                    seenNegative = true;
                }
                return -value;
            }
        }

    }

    static class Arg extends Expression {

        final int index;

        Arg(int index) {
            this.index = index;
        }

        @Override
        int execute(int[] args) {
            return args[index];
        }

    }

    static class Function extends RootNode {

        @Child private Expression body;

        Function(Expression body) {
            super(null);
            this.body = body;
        }

        @Override
        public String getName() {
            return "Demo2";
        }

        @Override
        public Object execute(VirtualFrame frame) {
            return body.execute((int[]) frame.getArguments()[0]);
        }
    }

    public static void main(String[] args) {
        // Sample Program: abs(args[0])
        Function sample = new Function(new Abs(new Arg(0)));
        CallTarget target = sample.getCallTarget();

        for (int i = 0; i < 1000; i++) {
            target.call(new int[] { 10 });
        }
        for (int i = 0; i < 1000; i++) {
            target.call(new int[] { -10 });
        }
        System.out.println("done");
    }

}
