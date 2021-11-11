# REGEL-kt

Kotlin CLI for Regel  that runs Regel in an interactive (AKA Customized) mode. It uses `so`(StackOverflow) pretrained model. The work is inspired by [utopia-group/regel](https://github.com/utopia-group/regel/). 

## Prerequisites

Before Running the CLI make sure that following dependencies are installed. 

- [Z3](https://github.com/Z3Prover/z3). Make sure you have Z3 installed with the Java binding.
- `ant` to compile the java files.+
- `java` 1.8.0

To build SEMPRE follow these steps:
```shell
  cd sempre
  ./pull-dependencies core
  ./pull-dependencies corenlp
  ./pull-dependencies freebase
  ./pull-dependencies tables
```

## Running REGEL-kt
### Using IDEA IDE
1. `runConfiguations` are included in `.idea`. So after opening the project in IDEA. Just run `MainKt` configuration/

### Using Gradle
```bash
./gradlew run
```

### Using Docker Build

```bash
 docker build -t regel-kt:v1 .
 docker run -it regel-kt:v1
 ```
 
## Limitation
1. Resnax(Synthesiser) runs on a Multithread Pool. I tried different approaches like `Executors`, `Streams` and `flowOf{}`, but wasn't able to reciprocate the desired results. 
2. Resnax stucks on the main thread. Possible reasons are:
   1. No Multithreading implemented.
   2. Kotlin Overhead to port Java code(Highly Unlikely).
3. Resnax runs on shorter examples.

## Results

## References
1. ["Multi-modal Synthesis of Regular Expressions"](https://arxiv.org/abs/1908.03316).
2. [utopia-group/regel](https://github.com/utopia-group/regel/). 
