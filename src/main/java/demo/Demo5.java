package demo;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.bytecode.BytecodeConfig;
import com.oracle.truffle.api.bytecode.BytecodeRootNode;
import com.oracle.truffle.api.bytecode.GenerateBytecode;
import com.oracle.truffle.api.bytecode.Operation;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.RootNode;

public class Demo5 {

    @GenerateBytecode(languageClass = MyLanguage.class)
    static abstract class Function extends RootNode implements BytecodeRootNode {

        protected Function(MyLanguage language, FrameDescriptor frameDescriptor) {
            super(language, frameDescriptor);
        }
        
        @Operation
        static final class Add {
            @Specialization
            static int doInt(int a, int b) {
                return a + b;
            }
        }

        @Operation
        static final class Abs {

            @Specialization(guards = "operand >= 0")
            static int doPositive(int operand) {
                return operand;
            }

            @Specialization(guards = "operand < 0")
            static int doNegative(int operand) {
                return -operand;
            }

        }

        @Override
        public String getName() {
            return "Demo5";
        }
    }

    public static void main(String[] args) {
        // Sample Program: (args[0] + args[1]) + args[2]
        Function sample = FunctionGen.create(null, BytecodeConfig.DEFAULT, (b) -> {
            b.beginRoot();
            b.beginAdd();
            b.beginAdd();
            b.emitLoadArgument(0);
            b.emitLoadArgument(1);
            b.endAdd();
            b.emitLoadArgument(2);
            b.endAdd();
            b.endRoot();
        }).getNode(0);
        System.out.println(sample.dump());
        CallTarget target = sample.getCallTarget();
        for (int i = 0; i < 1000; i++) {
            target.call(10, 11, 21);
        }
        System.out.println("done");
    }

    static abstract class MyLanguage extends TruffleLanguage<Env> {
    }
}
