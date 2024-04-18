# **COMP3021 Spring 2024 Java Programming Assignment 3 (PA3)**

## **Python AST Management System**

[AST (Abstract Syntax Tree)](https://en.wikipedia.org/wiki/Abstract_syntax_tree) is a tree representation that represents the syntactic structure of source code. It is widely used in compilers and interpreters to reason about relationships between program elements. In this project, you are required to implement your own management system, named ASTManager, to parse and analyze Python ASTs.

### **Grading System**

PA3 aims to practice the multithreading and parallel programming. 
Specifically, the goal of PA3 is to get you familiar with work dividing to speed up and work ordering based on the degree of their overlaps to avoid redundant computation.  
**ASTManager** should be enhanced to support the following additional functionalities:

- Task 1: Parallel importing of XML files
- Task 2: Efficient Query Processing
- Task 3: Mixture XML Importing and Query

Similar to PA1 and PA2, each input is an XML file that represents a Python AST. The XML files used to test logics of PA1 are resided in `resources/pythonxmlPA1` while those for ten code patterns are located in `resources/pythonxml`. Before task specification, we first explain the grading policy as follows for your reference so that you will get it.

| Item                                               | Ratio | Notes                                                                    |
|----------------------------------------------------| ----- |--------------------------------------------------------------------------|
| Keeping your GitHub repository private             | 5%    | You must keep your repository **priavte** at all times.                  |
| Having at least three commits on different days    | 5%    | You should commit three times during different days in your repository   |
| Code style                                         | 10%   | You get 10% by default, and every 5 warnings from CheckStyle deducts 1%. |
| Public test cases (Task 1 + Task 2 + Bonus Task)   | 30%   | (# of passing tests / # of provided tests) * 30%                         |
| Hidden test cases (Task 1 + Task 2 + Bonus Task)   | 50%   | (# of passing tests / # of provided tests) * 50%                         |

Note that we would also check the performance of your implementation. Without finishing the task within given time, you will not get full marks even with correct results.

### Task Description

The specifications of each task are shown below. We have released the implementations of core functionalities in the form of `jar` package, i.e., XML loading and querying mentioned in PA1 and PA2. The focus of PA3 is to build a framework to parallelize existing functionalities, thus you can treat the given implementations as blackboxes. 

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
- 

#### Task 3: Mixture XML Import and Query

#### Bonus Task: Implement an API misuse bug detector (10%)

### What YOU need to do

We have marked the methods you need to implement using `TODO` in the skeleton. Specifically, please

- Fully implement the TODOs in the class `RapidASTManagerEngine`.
- Fully implement the TODOs in the class `ParserWorker`.
- Fully implement the TODOs in the class `QueryWorkder`.

**Note**: You can add more methods into the above three classes in your solution, but **DO NOT** modify other classes.

You need to follow the comments on the methods to be implemented in the provided skeleton. We have provided detailed descriptions and even several hints for these methods. To convenience the testing and debugging, you can just run the `main` method of `ASTManager` to interact with the system.

We use the JUnit test to verify the functionalities of all methods you implement. Please do not modify the type signatures of these functions.

### How to TEST

Public test cases are released in `src/test/java/hk.ust.comp3021/query` and `src/test/java/hk.ust.comp3021/misc`. Please try to test your code with `./gradlew test` before submission to ensure your implementation can pass all public test cases.

We use JUnit test to validate the correctness of individual methods that you need to implement. The mapping between public test cases and methods to be tested is shown below.

| Test Case           | Target Method                                   |
|---------------------|-------------------------------------------------|
| `ASTElementTest`    | Three collectors for AST in `ASTElement`        |
| `QueryOnNodeTest`   | Methods in `QueryOnNode`                        |
| `QueryOnMethodTest` | Methods in `QueryOnMethod`                      |
| `QueryOnClassTest`  | Methods in `QueryOnClass`                       |
| `BugDetectorTest`   | Methods in `BugDetector` (For Bonus Task Only)  |

You can fix the problem of your implementation based on the failed test cases.


### Submission Policy

Please submit your code on Canvas before the deadline **April 13, 2024, 23:59:59.** You should submit a single text file specified as follows:

- A file named `<itsc-id>.txt` containing the URL of your private repository at the first line. We will ask you to add the TAs' accounts as collaborators near the deadline.

For example, a student CHAN, Tai Man with ITSC ID `tmchanaa` having a repository at `https://github.com/tai-man-chan/COMP3021-PA2` should submit a file named `tmchanaa.txt` with the following content:

```txt
https://github.com/tai-man-chan/COMP3021-PA2
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

If you have any questions on the PA2, please email TA Wei Chen via wei.chen@connect.ust.hk

---

Last Update: March 24, 2024

