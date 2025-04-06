package demo;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;

public class Demo1 {

    abstract static class Expression extends Node {

        abstract int execute(int[] arguments);

    }

    static class Add extends Expression {
        @Child Expression left;
        @Child Expression right;

        Add(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        int execute(int[] args) {
            return left.execute(args) + right.execute(args);
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
        public Object execute(VirtualFrame frame) {
            return body.execute((int[]) frame.getArguments()[0]);
        }
        
        @Override
        public String getName() {
            return "Demo1";
        }
    }

    public static void main(String[] args) {
        // Sample Program: (args[0] + args[1]) + args[2]
        Function sample = new Function(new Add(new Add(new Arg(0), new Arg(1)), new Arg(2)));
        CallTarget target = sample.getCallTarget();
        for (int i = 0; i < 1000; i++) {
            target.call(new int[] { 10, 11, 21 });
        }
        System.out.println("done");
    }

}
