package demo;

import static com.oracle.truffle.api.CompilerDirectives.shouldNotReachHere;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.ExplodeLoop.LoopExplosionKind;
import com.oracle.truffle.api.nodes.RootNode;

public class Demo4 {

    static final byte CODE_ADD = 1;
    static final byte CODE_ARG = 2;
    static final byte CODE_RETURN = 3;

    static class Function extends RootNode {

        @CompilationFinal(dimensions = 1) private byte[] bytecodes;

        Function(byte[] bytecodes, int stackSize) {
            super(null, createDescriptor(stackSize));
            this.bytecodes = bytecodes;
        }

        private static FrameDescriptor createDescriptor(int stackSize) {
            var builder = FrameDescriptor.newBuilder();
            builder.addSlots(stackSize);
            return builder.build();
        }

        @Override
        @ExplodeLoop(kind = LoopExplosionKind.MERGE_EXPLODE)
        public Object execute(VirtualFrame frame) {
            int[] args = (int[]) frame.getArguments()[0];
            int bytecodeIndex = 0;
            int stackPointer = -1;
            byte[] bc = this.bytecodes;
            while (bytecodeIndex < bc.length) {
                switch (bc[bytecodeIndex]) {
                case CODE_ADD:
                    int a = frame.getInt(stackPointer - 1);
                    int b = frame.getInt(stackPointer);
                    int result = a + b;
                    frame.setInt(stackPointer - 1, result);
                    stackPointer--;
                    bytecodeIndex++;
                    break;
                case CODE_ARG:
                    int index = bc[bytecodeIndex + 1];
                    frame.setInt(stackPointer + 1, args[index]);
                    stackPointer++;
                    bytecodeIndex += 2;
                    break;
                case CODE_RETURN:
                    return frame.getInt(stackPointer);
                default:
                    throw shouldNotReachHere();
                }
            }
            throw shouldNotReachHere();
        }

        @Override
        public String getName() {
            return "Demo4";
        }
    }

    public static void main(String[] args) {
        // Sample Program: (args[0] + args[1]) + args[2]
        byte[] bytes = new byte[] { CODE_ARG, 0, CODE_ARG, 1, CODE_ADD, CODE_ARG, 2, CODE_ADD, CODE_RETURN };
        int stackSize = 2;
        Function sample = new Function(bytes, stackSize);
        CallTarget target = sample.getCallTarget();
        for (int i = 0; i < 1000; i++) {
            target.call(new int[] { 10, 11, 21 });
        }
        System.out.println("done");
    }

}
