# **COMP3021 Spring 2024 Java Programming Assignment 3 (PA3)**

## **Python AST Management System**

[AST (Abstract Syntax Tree)](https://en.wikipedia.org/wiki/Abstract_syntax_tree) is a tree representation that represents the syntactic structure of source code. It is widely used in compilers and interpreters to reason about relationships between program elements. In this project, you are required to implement your own management system, named ASTManager, to parse and analyze Python ASTs.

### **Grading System**

PA3 aims to practice multithreading and parallel programming. Specifically, the goal of PA3 is to familiarize you with dividing and scheduling work to speed up and avoid redundant computation of AST loading and query. **ASTManager** should be enhanced to support the following additional functionalities:

- Task 1: Parallel importing of XML files
- Task 2: Efficient Query Processing
- Task 3: Mixture XML Importing and Query
- Bonus Task: 

Similar to PA1 and PA2, each input is an XML file that represents a Python AST. The XML files used to test logics of PA1 are resided in `resources/pythonxmlPA1` while those for ten code patterns are located in `resources/pythonxml`. Before task specification, we first explain the grading policy as follows for your reference so that you will get it.

| Item                                            | Ratio | Notes                                                        |
| ----------------------------------------------- | ----- | ------------------------------------------------------------ |
| Keeping your GitHub repository private          | 5%    | You must keep your repository **priavte** at all times.      |
| Having at least three commits on different days | 5%    | You should commit three times during different days in your repository |
| Code style                                      | 10%   | You get 10% by default, and every 5 warnings from CheckStyle deducts 1%. |
| Public test cases (Task 1-3 + Bonus Task)       | 30%   | (# of passing tests / # of provided tests) * 30%             |
| Hidden test cases (Task 1-3 + Bonus Task)       | 50%   | (# of passing tests / # of provided tests) * 50%             |

Note that we would check 1) the correctness, 2) the performance, as well as 3) the number of active threads for your implementation. Without finishing the task within given time or if the number of threads is not correct, you will not get full marks even with correct results.

### Task Description

The specifications of each task are shown below. 

In PA2, we have mentioned 14 kinds of queries on AST, including 4 queries on AST node (`QueryOnNode`), 5 queries on method (`QueryOnMethod`) and 5 queries on class (`QueryOnClass`). In PA3, we are still working on these 14 kinds of queries. To cope with multi-thread programming, we have slightly modified the signatures of these methods, which will be detailed later.

The implementations of these 14 queries are given in the form of `jar` package. You do not need to re-implement them. The focus of PA3 is to build a framework to parallelize existing functionalities, thus you can treat the given implementations as blackboxes. 

In PA3, we establish a new parallel framework `RapidASTManagerEngine` under directory `parallel` to replace the original `ASTManagerEngine`.
The class has two objects, `id2ASTModules` organizing the mapping between the ID to corresponding parsed AST and `allResults` stores the results of a bunch of query you need to process in parallel.

#### Task 1: Build Parallel Framework and Support Parallel Import of XML Files 

In task 1, to support parallel import of XML files, we use `ParserWorkers` to organize essential information for XML loading, including the ID of AST to be loaded `xmlID`, the absolute path of XML file directory `xmlDirPath`, and the mapping to store the loaded AST `id2ASTModules`.

You can notice `ParserWorker` is a subclass of interface `Runnable`. Please implement the method `run` of `Runnable` interface to load AST of given ID and store the results to `id2ASTModules`. You can invoke `ASTParser#parser` but please caution on the concurrent writing to the global mapping. 

```Java
public class ParserWorker implements Runnable {
    @Override
    public void run() {
        // Loading XML file in Parallel
    }
}
```

After finishing the `ParserWorder`, please implement `processXMLParsing` method of `RapidASTManagerEngine` to launch threads and manage them with a threading pool using `ExecutorService`.


#### Task 2: Support Customized Parallelization on Query Processing

 So far you have implemented three kinds of queries, i.e., `queryOnNode`, `queryOnMethod` and `queryOnClass`.
In the previous PAs, each time you need to process on query command at a time.
But in task 2, you are requested to process numbers of queries in parallel.

We use `QueryWorker` under `parallel` to universally manage the different commands and their outputs. The meaning of each field is outlined below.
- `id2ASTModules`: global mapping managing all loaded AST so far
- `queryID`: the index of current query inside total queries
- `astID`: the ID of AST to be queried
- `queryName`: the name of query, including 14 queries you writen in lambda, from `findFuncWithArgGtN` to `findClassesWithMain`.
- `args`: the inputs of query, its size depends on the specific query to be conducted, for instance, `answerIfACalledB` query has two inputs.
- `result`: universal structure to store the query results, which depends on the specific query to be conducted, for instance, `answerIfACalledB` returns boolean data.
- `mode`: three query mode, which will be elaborated on later.

There are in total 3 execution modes. 
```Java
public class QueryWorker implements Runnable {
    public void run() {
        // Implement the following three modes
        if (mode == 0) {
            runSerial();
        } else if (mode == 1) {
            runParallel();
        } else if (mode == 2) {
            runParallelWithOrder();
        }
    }
}
```
1. `0` means sequential execution. You need to implemente `runSerial`.
2. `1` means
3. mode `2` enhances mode `1` with the goal of reducing redundant tree traversal when querying class information.
   As you can observe, the one query on classes could rely on the result of another. For instance, `find Overriding Methods` depend on the class inheritance hierarchy computed by `findSuperClasses`. Please consider the query dependence relation based on your understanding and implement `runParallelWithOrder` with as less tree traversals as possible. We would grade your code based on the times of traversal.


For mode 2, the dependences among the given five classes are shown below:
```
haveSuperClass invokes findSuperClasses
findOverridingMethods invokes findSuperClasses
findAllMethods invokes findSuperClasses
findClassesWithMain invokes findAllMethods
```

Once `run` method of `QueryWorker` is finished, please finish the `processCommands` method of `RapidASTManagerEngine`. The method creates a worker based on the given commands and schedules these workers based on the execution modes. 

```Java
public List<Object> processCommands(List<Object[]> commands, int executionMode) {
    // schedule workers based on commands and execution mode
    return allResults;
}
```

A list of commands is sampled below:

```Java
// query id, ast id, query name, query args
1, 18, findClassesWithMain, {}
2, 19, findClassesWithMain, {}
...
```

The expected performance is mode 2 > mode 1 > mode 0. We would count the time elapsed to check if the performance of your implementations for three modes is correct.

#### Task 3: Interleaved XML Import and Query

In task 1 and 2, the XML import and query is handled separately. In task 3, you will receive a list of commands with XML import and query mixture. Noticed that for each AST, its loading commands is not guaranteed to be issued earlier than its query, whereas your implementation should take charges of this part. A list of commands is sampled below:

```Java
// query id, ast id, command name, command args
1, 18, findClassesWithMain, {}
2, 19, findClassesWithMain, {}
3, 18, processXMLParsering, {"resources/pythonxml/"}
...
```


### What YOU need to do

We have marked the methods you need to implement using `TODO` in the skeleton. Specifically, please

- Fully implement the TODOs in the class `RapidASTManagerEngine`.
- Fully implement the TODOs in the class `ParserWorker`.
- Fully implement the TODOs in the class `QueryWorkder`.

**Note**: You can add more methods into the above three classes in your solution, but **DO NOT** modify other classes.

You need to follow the comments on the methods to be implemented in the provided skeleton. We have provided detailed descriptions and even several hints for these methods. To convenience the testing and debugging, you can just run the `main` method of `ASTManager` to interact with the system.

We use the JUnit test to verify the functionalities of all methods you implement. Please do not modify the type signatures of these functions.

### How to TEST

We use JUnit test to validate the correctness of individual methods that you need to implement. Public test cases are released in `src/test/java/hk.ust.comp3021/parallel`. The mapping between public test cases and methods to be tested is shown below. Please try to test your code with `./gradlew test` before submission to ensure your implementation can pass all public test cases.

| Test Case                         | Target Method                                                |
| --------------------------------- | ------------------------------------------------------------ |
| `testParallelLoadingPool`         | `processXMLParsingPool` in Task 1                            |
| `testParallelLoadingDivide`       | `processXMLParsingDivide` in Task 1                          |
| `testSerialExecution`             | `processCommands` with mode 0 (`executeCommandsSerial`) in Task 2 |
| `testParallelExecution`           | `processCommands` with mode 1 (`executeCommandsParallel`) in Task 2 |
| `testParallelExecutionWithOrder`  | `processCommands` with mode 2 (`executeCommandsParallelWithOrder`) in Task 2 |
| `testInterleavedImportQuery`      | `processCommandsInterLeaved` in Task 3                       |
| `testInterleavedImportQueryTwo`   | `processCommandsInterLeavedTwoThread` in Task 3              |
| `testInterleavedImportQueryBonus` | `processCommandsInterLeavedFixedThread` in Task 3 (Bonus Task Only) |

You can fix the problem of your implementation based on the failed test cases.


### Submission Policy

Please submit your code on Canvas before the deadline **May 11, 2024, 23:59:59.** You should submit a single text file specified as follows:

- A file named `<itsc-id>.txt` containing the URL of your private repository at the first line. We will ask you to add the TAs' accounts as collaborators near the deadline.

For example, a student CHAN, Tai Man with ITSC ID `tmchanaa` having a repository at `https://github.com/tai-man-chan/COMP3021-PA3` should submit a file named `tmchanaa.txt` with the following content:

```txt
https://github.com/tai-man-chan/COMP3021-PA3
```

Note that we are using automatic scripts to process your submission on test cases rather than testing via the console manually. **DO NOT add extra explanation** to the file; otherwise, they will prevent our scripts from correctly processing your submission. 

**We will grade your submission based on the latest committed version before the deadline.** Please make sure all the amendments are made before the deadline and do not make changes after the deadline.

We have pre-configured a gradle task to check style for you. You can run `./gradlew checkstyleMain` in the integrated terminal of IntelliJ to check style.

Before submission, please make sure that: 

1. Your code can be complied with successfully. Please try to compile your code with `./gradlew build` before submission. You will not get any marks for public/hidden test cases if your code cannot be successfully compiled.

2. Your implementation can pass the public test cases we provided in `src/test`.

3. Your implementation should not yield too many errors when running `./gradlew checkstyleMain`.

### Academic Integrity

We trust that you are familiar with the Honor Code of HKUST. If not, refer to [this page](https://course.cse.ust.hk/comp3021/#policy).

### Contact US

If you have any questions on the PA3, please email TA Wei Chen via wei.chen@connect.ust.hk

---

Last Update: April 21, 2024

