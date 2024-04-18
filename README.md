# **COMP3021 Spring 2024 Java Programming Assignment 3 (PA3)**

## **Python AST Management System**

[AST (Abstract Syntax Tree)](https://en.wikipedia.org/wiki/Abstract_syntax_tree) is a tree representation that represents the syntactic structure of source code. It is widely used in compilers and interpreters to reason about relationships between program elements. In this project, you are required to implement your own management system, named ASTManager, to parse and analyze Python ASTs.

### **Grading System**

PA3 aims to practice the multithreading and parallel programming. 
Specifically, the goal of PA3 is to get you familiar with work dividing to speed up and work ordering based on the degree of their overlaps to avoid redundant computation.  
**ASTManager** should be enhanced to support the following additional functionalities:

- Task 1: Parallel importing of XML files
- Task 2: Efficient Query Processing
- Bonus: 

Similar to PA1 and PA2, each input is an XML file that represents a Python AST. The XML files used to test logics of PA1 are resided in `resources/pythonxmlPA1` while those for ten code patterns and bonus tasks are located in `resources/pythonxml`. Before task specification, we first explain the grading policy as follows for your reference so that you will get it.

| Item                                               | Ratio | Notes                                                                    |
|----------------------------------------------------| ----- |--------------------------------------------------------------------------|
| Keeping your GitHub repository private             | 5%    | You must keep your repository **priavte** at all times.                  |
| Having at least three commits on different days    | 5%    | You should commit three times during different days in your repository   |
| Code style                                         | 10%   | You get 10% by default, and every 5 warnings from CheckStyle deducts 1%. |
| Public test cases (Task 1 + Task 2 + Bonus Task)   | 30%   | (# of passing tests / # of provided tests) * 30%                         |
| Hidden test cases (Task 1 + Task 2 + Bonus Task)   | 50%   | (# of passing tests / # of provided tests) * 50%                         |

### Task Description

The specifications of each task are shown below.

#### Task 1: Parallel Import of XML Files (30%)


#### Task 2: Support Customized Parallelization on Query Processing



#### Bonus Task: Implement an API misuse bug detector (10%)

### What YOU need to do

We have marked the methods you need to implement using `TODO` in the skeleton. Specifically, please

- Learn three collectors, design, and implement them in `ASTElement`.
- Fully implement the lambda expressions in the class `QueryOnNode`.
- Fully implement the lambda expressions in the class `QueryOnMethod`.
- Fully implement the lambda expressions in the class `QueryOnClass`.
- Fully implement the lambda expressions in the class `BugDetector`.

**Note**: You can add more methods or even classes into the skeleton in your solution, but **DO NOT** modify existing code.

You need to follow the comments on the methods to be implemented in the provided skeleton. We have provided detailed descriptions and even several hints for these methods. To convenience the testing and debugging, you can just run the `main` method of `ASTManager` to interact with the system.

We use the JUnit test to verify the functionalities of all methods you implement. Please do not modify the type signatures of these functions.

### How to TEST

Public test cases are released in `src/test/java/hk.ust.comp3021/query` and `src/test/java/hk.ust.comp3021/misc`. Please try to test your code with `./gradlew test` before submission to ensure your implementation can pass all public test cases.

We use JUnit test to validate the correctness of individual methods that you need to implement. The mapping between public test cases and methods to be tested is shown below.

| Test Case      | Target Method                                  |
| ----------- |------------------------------------------------|
| `ASTElementTest`        | Three collectors for AST in `ASTElement`       |
| `QueryOnNodeTest`        | Methods in `QueryOnNode`                       |
| `QueryOnMethodTest`   | Methods in `QueryOnMethod`                     |
| `QueryOnClassTest` | Methods in `QueryOnClass`                      |
| `BugDetectorTest` | Methods in `BugDetector` (For Bonus Task Only) |

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

