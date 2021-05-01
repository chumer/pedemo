
***Installation Instructions***

1. Download latest GraalVM: https://www.graalvm.org/docs/reference-manual/
2. Download IGV: https://www.oracle.com/downloads/graalvm-downloads.html
3. set GRAALVM_HOME to the unpacked GraalVM home folder
4. Clone this repository `git clone git@github.com:chumer/pedemo.git`
5a. Eclipse: Install eclipse m2e plugin and use existing eclipse config by importing it as existing project.
5b. Maven or other IDE: Import Maven project into your IDE or build with mvn package. 
6. Open IGV
7. Run demos. e.g. `./demo1 -dump` to dump to igv or `./demo1 -disassemble` to see the assembly.

